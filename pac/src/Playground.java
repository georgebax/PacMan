import sun.applet.Main;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Timer;

public class Playground extends JPanel {
    /**
     * The current stage
     */
    private static final int BLOCK_SIZE = 24;

    private final int DEFAULT_LIVES = 3;
    private PlayerInfo playerInfo;
    private GameInfo gameInfo;
    private int lives, totalCookies, eatenCookies;
    private PacMan pac;
    private ArrayList<Ghost> ghosts = new ArrayList<>();
    private ArrayList<String> pg = new ArrayList<>();
    private Timer pacTimer;
    private TimerTask refreshPac, refreshGhosts, changeGhostPics, changePacPic;
    private MainWindow root;

    private ArrayList<ArrayList<Shape>> level = new ArrayList<>();

    Playground(String fileName, PlayerInfo pi, GameInfo gi, MainWindow root) {
        pg = getTextFromFile(fileName);
        this.playerInfo = pi;
        this.gameInfo   = gi;
        this.lives = DEFAULT_LIVES;
        this.eatenCookies = 0;
        this.root = root;

        makeLevel(pg, level);
    }

    private ArrayList<String> getTextFromFile(String fileName) {
        ArrayList<String> list = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() < 22)
                    for (int i = line.length()-1; i < 22; i++)
                        line = line + " "; // fix trailing whitespaces
                list.add(line);
            }
            reader.close();
        } catch (IOException e) {
            System.err.format("Exception occurred trying to read '%s'!", fileName);
            e.printStackTrace();
        }
        return list;
    }


    private void makeLevel(ArrayList<String> pg, ArrayList<ArrayList<Shape>> level) {
        /**
         * Every model of the stage is saved in a 2D ArrayList, so that it would be fast to redraw them
         * This is done circa 60 times per second
         */
        for (int i = 0; i < 22; i++) { // 22 x 19 size
            ArrayList<Shape> temp = new ArrayList<>();
            for (int j = 0; j < pg.get(i).length(); j++) {
                switch (pg.get(i).charAt(j)) {
                    case (' '):
                        temp.add(new Space(BLOCK_SIZE * j, BLOCK_SIZE * i, BLOCK_SIZE, BLOCK_SIZE));
                        break;
                    case ('#'):
                        temp.add(new Rectangle(BLOCK_SIZE * j, BLOCK_SIZE * i, BLOCK_SIZE, BLOCK_SIZE));
                        break;
                    case ('.'):
                        totalCookies++;
                        temp.add(new Circle(BLOCK_SIZE * j + 6, BLOCK_SIZE * i + 6, BLOCK_SIZE / 2, BLOCK_SIZE / 2));
                        break;
                    case ('o'):
                        temp.add(new Circle(BLOCK_SIZE * j + 2, BLOCK_SIZE * i + 2, BLOCK_SIZE - 4, BLOCK_SIZE - 4));
                        break;
                    case ('-'):
                        temp.add(new Rectangle(BLOCK_SIZE * j, BLOCK_SIZE * i + 12, BLOCK_SIZE, BLOCK_SIZE/6));
                        break;
                    case ('F'):
                        Ghost g = new Ghost(BLOCK_SIZE * j, BLOCK_SIZE * i, BLOCK_SIZE, BLOCK_SIZE, ghosts.size(), this);
                        g.setDefaultX(BLOCK_SIZE * j);
                        g.setDefaultY(BLOCK_SIZE * i);
                        ghosts.add(g);
                        temp.add(ghosts.get(ghosts.size()-1)); // add last element (ghost.size() >= 1 here!)
                    case ('P'):
                        pac = new PacMan(BLOCK_SIZE * j, BLOCK_SIZE * i, BLOCK_SIZE, BLOCK_SIZE, this);
                        pac.setDefaultX(BLOCK_SIZE * j);
                        pac.setDefaultY(BLOCK_SIZE * i);
                        temp.add(pac); // we need to refer to pac later!
                        break;
                }
            }
            level.add(temp);
        }
    }

    private void drawPac(Graphics g, char direction) {
        int im = pac.getImageModifier();
        switch (direction) {
            case ('u'):
                g.drawImage(pac.getImage().get(im), pac.getX(), pac.getY(), this);
                break;
            case ('d'):
                g.drawImage(pac.getImage().get(4+im), pac.getX(), pac.getY(), this);
                break;
            case ('l'):
                g.drawImage(pac.getImage().get(8+im), pac.getX(), pac.getY(), this);
                break;
            case ('r'):
                g.drawImage(pac.getImage().get(12+im), pac.getX(), pac.getY(), this);
                break;
            case ('s'): // ROOKIE MISTAKE...
                g.drawImage(pac.getImage().get(im), pac.getX(), pac.getY(), this);
                break;
        }
    }

    private void drawGhosts(Graphics g, ArrayList<Ghost> ghosts) {
        for (Ghost ghost : ghosts) {
            int im = ghost.getImageModifier() + ghost.getImageModifierOffset();
            g.drawImage(ghost.getImage().get(im), ghost.getX(), ghost.getY(), this);
        }
    }

    public ArrayList<Ghost> getGhosts() {
        return ghosts;
    }

    public void pacDeath() {
        setLives(this.lives - 1);
        pac.setX(pac.getDefaultX());
        pac.setY(pac.getDefaultY());
        for (Ghost g : ghosts) {
            g.setX(g.getDefaultX());
            g.setY(g.getDefaultY());
            g.setInitialMovement(true);
            g.setDirection('u');
            g.setWantedDirection('r');
        }
        if (this.lives == 0) {
            getRoot().declareFailure();
        }
        GameInfo myGi = getGameInfo();
        JPanel ll = myGi.makeLivesLabel(this.lives);
        myGi.removeAll();
        myGi.add(ll);
        myGi.add(myGi.makeButton());
        myGi.revalidate();
        myGi.repaint();
    }

    public void run() {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher(this));
        pacTimer = new Timer();
        Playground temp = this;
        refreshPac = new TimerTask() {
            @Override
            public void run() {
                pac.update();
                temp.revalidate();
                temp.repaint();
            }
        };
        refreshGhosts = new TimerTask() {
            @Override
            public void run() {
                for (Ghost g : ghosts) g.update();
                temp.revalidate();
                temp.repaint();
            }
        };
        changeGhostPics = new TimerTask() {
            @Override
            public void run() {
                for (Ghost g : ghosts) {
                    int im = g.getImageModifier();
                    g.setImageModifier((im + 1) % 2);
                }
            }
        };
        changePacPic = new TimerTask() {
            @Override
            public void run() {
//                if (pac.getDirection() != 's') {
                int im = pac.getImageModifier();
                pac.setImageModifier((im + 1) % 4); // 4 images for each pac direction
//                } // don't change pics if pac's still
            }
        };
        pacTimer.schedule(refreshPac, 17, 17); // 17ms -> 60fps smooth
        pacTimer.schedule(refreshGhosts, 17, 17); // 17ms -> 60fps smooth
        pacTimer.schedule(changePacPic, 70, 70); // refresh pic every 70ms
        pacTimer.schedule(changeGhostPics, 1000, 1000); // refresh pics every sec
    }

    public MainWindow getRoot() {
        return root;
    }

    public PacMan getPac() { // one pac two pac three pac four
        return pac;
    }

    public ArrayList<ArrayList<Shape>> getLevel() {
        return level;
    }

    public ArrayList<String> getPg() {
        return pg;
    }

    public Timer getPacTimer() {
        return pacTimer;
    }

    public int getLives() {
        return lives;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public int getTotalCookies() {
        return totalCookies;
    }

    public int getEatenCookies() {
        return eatenCookies;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setEatenCookies(int eatenCookies) {
        this.eatenCookies = eatenCookies;
    }

    @Override
    public void paintComponent(Graphics g) {
        for (ArrayList<Shape> sl: level)
            for (Shape s : sl)
                s.draw(g);
        drawPac(g, pac.getDirection());
        drawGhosts(g, ghosts);
    }
}
