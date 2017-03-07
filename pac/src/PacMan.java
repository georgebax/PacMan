import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PacMan extends Shape {
    /**
     * Class which "controls" most of the in game happenings!
     * Extends shape so that it's easier to make the level when setting up the stage
     * If the game is paused or Pac eats a big cookie, the Timers within this class control the movements
     * This was done so that the calls to "ancestor" classes, such as Playground, would be minimized
     * @Note The TimerTasks are declared more than one times in this class because when timer.cancel()
     *       called, the TimerTasks are finalized, so they need to be redeclared
     * @Note The ghosts' speeds are controlled by the slightly different refresh rates!
     *       However, it's always close to 60fps so it's crystal smooth ;)
     */
    private ArrayList<BufferedImage> image;
    private char direction;
    private char wantedDirection;
    private int imageModifier;
    private int horOffset;
    private int verOffset;
    private int scoreMultiplier;
    private Timer huntTimer, normalTimer, newTimer;
    private boolean hunting;

    public PacMan(int x, int y, int width, int height, Playground stage) {
        super(x, y, width, height);
//        this.state = state;
        this.horOffset = 0;
        this.verOffset = 0;
        this.imageModifier = 0;
        this.direction = 's'; // stand still
        this.wantedDirection = 's'; // stand still
        this.setStage(stage);
        image = new ArrayList<>();
        try {
            this.image.add( 0, ImageIO.read(new File("PM0.gif")));
            this.image.add( 1, ImageIO.read(new File("PMup1.gif")));
            this.image.add( 2, ImageIO.read(new File("PMup2.gif")));
            this.image.add( 3, ImageIO.read(new File("PMup3.gif")));
            this.image.add( 4, ImageIO.read(new File("PM0.gif")));
            this.image.add( 5, ImageIO.read(new File("PMdown1.gif")));
            this.image.add( 6, ImageIO.read(new File("PMdown2.gif")));
            this.image.add( 7, ImageIO.read(new File("PMdown3.gif")));
            this.image.add( 8, ImageIO.read(new File("PM0.gif")));
            this.image.add( 9, ImageIO.read(new File("PMleft1.gif")));
            this.image.add(10, ImageIO.read(new File("PMleft2.gif")));
            this.image.add(11, ImageIO.read(new File("PMleft3.gif")));
            this.image.add(12, ImageIO.read(new File("PM0.gif")));
            this.image.add(13, ImageIO.read(new File("PMright1.gif")));
            this.image.add(14, ImageIO.read(new File("PMright2.gif")));
            this.image.add(15, ImageIO.read(new File("PMright3.gif")));
        } catch (IOException e) {
            System.out.println("Error loading images! " + e.getMessage());
        }
    }


    private boolean checkEdges(char direction, ArrayList<String> stage) {
        int pacX, pacY;
        char checked;
        switch (direction) {
            case ('u') :
                pacX = this.getX() / 24;
                pacY = (this.getY() - 1) / 24;
                checked = stage.get(pacY).charAt(pacX);
                if (checked == '#' || checked == '-') return false; // check upper left
                pacX = (this.getX() + 23) / 24;
                pacY = (this.getY() - 1) / 24;
                checked = stage.get(pacY).charAt(pacX);
                if (checked == '#' || checked == '-') return false; // check upper right
                break;
            case ('d') :
                pacX = this.getX() / 24;
                pacY = (this.getY() + 24) / 24;
                checked = stage.get(pacY).charAt(pacX);
                if (checked == '#' || checked == '-') return false; // check lower left
                pacX = (this.getX() + 23) / 24;
                pacY = (this.getY() + 24) / 24;
                checked = stage.get(pacY).charAt(pacX);
                if (stage.get(pacY).charAt(pacX) == '#') return false; // check lower right
                break;
            case ('l') :
                pacX = (this.getX() - 1) / 24;
                pacY = this.getY() / 24;
                checked = stage.get(pacY).charAt(pacX);
                if (stage.get(pacY).charAt(pacX) == '#') return false; // check upper left
                pacX = (this.getX() - 1) / 24;
                pacY = (this.getY() + 23) / 24;
                checked = stage.get(pacY).charAt(pacX);
                if (stage.get(pacY).charAt(pacX) == '#') return false; // check lower left
                break;
            case ('r') :
                pacX = (this.getX() + 24) / 24;
                pacY = this.getY() / 24;
                checked = stage.get(pacY).charAt(pacX);
                if (stage.get(pacY).charAt(pacX) == '#') return false; // check upper right
                pacX = (this.getX() + 24) / 24;
                pacY = (this.getY() + 23) / 24;
                checked = stage.get(pacY).charAt(pacX);
                if (stage.get(pacY).charAt(pacX) == '#') return false; // check lower right
                break;
            case ('s') :
                return true;
        }
        return true;
    }

    public void stopTimers() {
        Timer myTimer = getStage().getPacTimer();
        myTimer.cancel();
        myTimer.purge();
        if (normalTimer != null) {
            normalTimer.cancel();
            normalTimer.purge();
        }
        if (newTimer != null) {
            newTimer.cancel();
            newTimer.purge();
        }
        if (huntTimer != null) {
            huntTimer.cancel();
            huntTimer.purge();
        }
        if (newTimer != null) {
            newTimer.cancel();
            newTimer.purge();
        }
    }

    private void startHunt() {
        scoreMultiplier = 1;
        for (Ghost g : getStage().getGhosts()) {
            g.setHunted(true);
            g.setImageModifierOffset(2);
        }
        stopTimers();
        Playground temp = getStage();
        TimerTask refreshPac = new TimerTask() {
            @Override
            public void run() {
                update();
                temp.revalidate();
                temp.repaint();
            }
        };
        TimerTask refreshGhosts = new TimerTask() {
            @Override
            public void run() {
                for (Ghost g : getStage().getGhosts()) g.update();
                temp.revalidate();
                temp.repaint();
            }
        };
        TimerTask changeGhostPics = new TimerTask() {
            @Override
            public void run() {
                for (Ghost g : getStage().getGhosts()) {
                    int im = g.getImageModifier();
                    g.setImageModifier((im + 1) % 2);
                }
            }
        };
        TimerTask changePacPic = new TimerTask() {
            @Override
            public void run() {
                int im = getImageModifier();
                setImageModifier((im + 1) % 4); // 4 images for each pac direction
            }
        };

        huntTimer = new Timer();
        huntTimer.schedule(refreshPac, 17, 17); // normal speed
        huntTimer.schedule(refreshGhosts, 19, 19); // 10% slower -> .9 * normal_speed -> refresh per 19ms
        huntTimer.schedule(changePacPic, 70, 70); // refresh pic every 70ms
        huntTimer.schedule(changeGhostPics, 1000, 1000); // refresh pics every sec
    }

    private void endHunt() {
        hunting = false;
        for (Ghost g : getStage().getGhosts()) {
            g.setHunted(false);
            g.setImageModifierOffset(0);
        }
        stopTimers();
        Playground temp = getStage();
        TimerTask refreshPac = new TimerTask() {
            @Override
            public void run() {
                update();
                temp.revalidate();
                temp.repaint();
            }
        };
        TimerTask refreshGhosts = new TimerTask() {
            @Override
            public void run() {
                for (Ghost g : getStage().getGhosts()) g.update();
                temp.revalidate();
                temp.repaint();
            }
        };
        TimerTask changeGhostPics = new TimerTask() {
            @Override
            public void run() {
                for (Ghost g : getStage().getGhosts()) {
                    int im = g.getImageModifier();
                    g.setImageModifier((im + 1) % 2);
                }
            }
        };
        TimerTask changePacPic = new TimerTask() {
            @Override
            public void run() {
//                if (pac.getDirection() != 's') {
                int im = getImageModifier();
                setImageModifier((im + 1) % 4); // 4 images for each pac direction
//                } // don't change pics if pac's still
            }
        };

        normalTimer = new Timer();
        normalTimer.schedule(refreshPac, 17, 17); // normal speed
        if (getStage().getEatenCookies() >= Math.round(.6 * getStage().getTotalCookies()))
            normalTimer.schedule(refreshGhosts, 13, 13); // 30% faster if close to end
        else
            normalTimer.schedule(refreshGhosts, 17, 17); // normal speed
        normalTimer.schedule(changePacPic, 70, 70); // refresh pic every 70ms
        normalTimer.schedule(changeGhostPics, 1000, 1000); // refresh pics every sec
    }

    public void startTimers() {
        Playground temp = getStage();
        TimerTask refreshPac = new TimerTask() {
            @Override
            public void run() {
                update();
                temp.revalidate();
                temp.repaint();
            }
        };
        TimerTask refreshGhosts = new TimerTask() {
            @Override
            public void run() {
                for (Ghost g : getStage().getGhosts()) g.update();
                temp.revalidate();
                temp.repaint();
            }
        };
        TimerTask changeGhostPics = new TimerTask() {
            @Override
            public void run() {
                for (Ghost g : getStage().getGhosts()) {
                    int im = g.getImageModifier();
                    g.setImageModifier((im + 1) % 2);
                }
            }
        };
        TimerTask changePacPic = new TimerTask() {
            @Override
            public void run() {
                int im = getImageModifier();
                setImageModifier((im + 1) % 4); // 4 images for each pac direction
            }
        };

        normalTimer = new Timer();
        normalTimer.schedule(refreshPac, 17, 17); // normal speed
        if (getStage().getEatenCookies() >= Math.round(.6 * getStage().getTotalCookies()))
            normalTimer.schedule(refreshGhosts, 13, 13); // 30% faster if close to end
        else
            normalTimer.schedule(refreshGhosts, 17, 17); // normal speed
        normalTimer.schedule(changePacPic, 70, 70); // refresh pic every 70ms
        normalTimer.schedule(changeGhostPics, 1000, 1000); // refresh pics every sec
    }

    private void checkCookies(char direction, ArrayList<String> stage, ArrayList<ArrayList<Shape>> level) {
        int pacX, pacY;
        char[] temp;
        MainWindow rootWindow;

        pacX = (this.getX() + 12) / 24; pacY = (this.getY() + 12) / 24; // middle of the pac block

        if (stage.get(pacY).charAt(pacX) == '.') {
            temp = stage.get(pacY).toCharArray();
            temp[pacX] = ' ';
            stage.set(pacY, String.valueOf(temp));
            Space sp = new Space(24 * pacX, 24 * pacY, 23, 23);
            level.get(pacY).set(pacX, sp); // place Space in place of cookie (Circle)
            PlayerInfo tmpPi = getStage().getPlayerInfo(); // increases score when simple cookie is eaten
            tmpPi.setScore(tmpPi.getScore() + 10); // small cookies are worth 10 points each
            getStage().setEatenCookies(getStage().getEatenCookies() + 1);
            if (getStage().getEatenCookies() == Math.round(.6 * getStage().getTotalCookies()))
                accelerateGhosts();

            rootWindow = getStage().getRoot();
            if (getStage().getEatenCookies() == getStage().getTotalCookies()) {
//            if (getStage().getEatenCookies() == 3) {
                if (rootWindow.getCurrentStage() == rootWindow.getNumOfLevels()) {
                    rootWindow.declareSuccess();
                    return;
                }
                rootWindow.setCurrentStage(rootWindow.getCurrentStage() + 1); // next stage number
                rootWindow.loadStage(rootWindow.getCurrentStage() + 1); // success, load next
            }

        }
        if (stage.get(pacY).charAt(pacX) == 'o') {
            temp = stage.get(pacY).toCharArray();
            temp[pacX] = ' ';
            stage.set(pacY, String.valueOf(temp));
            Space sp = new Space(24 * pacX, 24 * pacY, 23, 23);
            level.get(pacY).set(pacX, sp); // place Space in place of cookie (Circle)
            PlayerInfo tmpPi = getStage().getPlayerInfo(); // increases score when simple cookie is eaten
            tmpPi.setScore(tmpPi.getScore() + 50); // big cookies are worth 50 points each
            startHunt(); // get huntin
            Timer huntTimer = new Timer();
            huntTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    endHunt();
                }
            }, 7000); // end hunt after 7 seconds!
        }
    }

    private void accelerateGhosts() {
        stopTimers();
        Playground temp = getStage();
        TimerTask refreshPac = new TimerTask() {
            @Override
            public void run() {
                update();
                temp.revalidate();
                temp.repaint();
            }
        };
        TimerTask refreshGhosts = new TimerTask() {
            @Override
            public void run() {
                for (Ghost g : getStage().getGhosts()) g.update();
                temp.revalidate();
                temp.repaint();
            }
        };
        TimerTask changeGhostPics = new TimerTask() {
            @Override
            public void run() {
                for (Ghost g : getStage().getGhosts()) {
                    int im = g.getImageModifier();
                    g.setImageModifier((im + 1) % 2);
                }
            }
        };
        TimerTask changePacPic = new TimerTask() {
            @Override
            public void run() {
//                if (pac.getDirection() != 's') {
                int im = getImageModifier();
                setImageModifier((im + 1) % 4); // 4 images for each pac direction
//                } // don't change pics if pac's still
            }
        };

        newTimer = new Timer();
        newTimer.schedule(refreshPac, 17, 17); // normal speed
        newTimer.schedule(refreshGhosts, 13, 13); // 30% faster -> 1.3 * normal_speed -> refresh per 13ms
        newTimer.schedule(changePacPic, 70, 70); // refresh pic every 70ms
        newTimer.schedule(changeGhostPics, 1000, 1000); // refresh pics every sec
    }

    private void setOffSets(char wantedDirection) {
        switch (wantedDirection) {
            case ('u') :
                setHorOffset(0);
                setVerOffset(-1);
                break;
            case ('d') :
                setHorOffset(0);
                setVerOffset(+1);
                break;
            case ('l') :
                setHorOffset(-1);
                setVerOffset(0);
                break;
            case ('r') :
                setHorOffset(+1);
                setVerOffset(0);
                break;
            case ('s') :
                setHorOffset(0);
                setVerOffset(0);
        }

    }

    public void update() {
        int x = this.getX();
        int y = this.getY();
        if (checkEdges(this.wantedDirection, getStage().getPg())) {
            this.setDirection(this.wantedDirection);
            setOffSets(this.wantedDirection);
        }
        else if (checkEdges(this.direction, getStage().getPg()))
            setOffSets(this.direction);
        else {
            this.setDirection('s');
            setOffSets(this.direction);
        }
        this.setX(x + horOffset);
        this.setY(y + verOffset);
        checkCookies(this.direction, getStage().getPg(), getStage().getLevel());
    }

    public int getScoreMultiplier() {
        return scoreMultiplier;
    }

    public void setScoreMultiplier(int scoreMultiplier) {
        this.scoreMultiplier = scoreMultiplier;
    }

    public ArrayList<BufferedImage> getImage() {
        return image;
    }

    public char getDirection() {
        return direction;
    }

    public int getImageModifier() {
        return imageModifier;
    }

    public void setImageModifier(int imageModifier) {
        this.imageModifier = imageModifier;
    }

    public void setDirection(char direction) {
        this.direction = direction;
    }

    public void setWantedDirection(char wantedDirection) {
        this.wantedDirection = wantedDirection;
    }

    public void setVerOffset(int verOffset) {
        this.verOffset = verOffset;
    }

    public void setHorOffset(int horOffset) {
        this.horOffset = horOffset;
    }

    @Override
    public void draw(Graphics g) {} // empty because we just need to draw the pacman image!
}
