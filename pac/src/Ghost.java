import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by gbax on 1/1/17.
 */
public class Ghost extends Shape {
    private int imageModifier;
    private ArrayList<BufferedImage> image = new ArrayList<>();
    private String state;
    private char wantedDirection;
    private char direction;
    private int horOffset;
    private int verOffset;
    private boolean initialMovement;
    private boolean hunted;
    private int imageModifierOffset; // used when in scared/hunted condition
    private long step;

    public Ghost(int x, int y, int width, int height, int id, Playground stage) {
        super(x, y, width, height);
        this.horOffset = 0;
        this.verOffset = 0;
        this.imageModifier = 0;
        this.direction = 'u';
        this.wantedDirection = 'u'; // move up at the beginning
        this.hunted = false;
        this.initialMovement = true; // determines that ghosts are trying to exit their cave
        this.setStage(stage);
        this.step = 1; // default speed
        image = new ArrayList<>();
        try {
            this.image.add(0, ImageIO.read(new File("Ghost" + Integer.toString(1) + ".gif"))); // determine which ghost it is
            this.image.add(1, ImageIO.read(new File("Ghost" + Integer.toString(2) + ".gif")));
            this.image.add(2, ImageIO.read(new File("GhostScared" + Integer.toString(1) + ".gif")));
            this.image.add(3, ImageIO.read(new File("GhostScared" + Integer.toString(2) + ".gif")));
        } catch (IOException e) {e.printStackTrace();}
    }

    public Ghost() {
        super();
    } // default constructor calls default Shape constructor which calls the "normal" one

    public ArrayList<BufferedImage> getImage() {
        return image;
    }

    public void setState(String state) {
        this.state = new String(state); // to make sure that the string is gonna change
    }

    private boolean checkEdges(char direction, ArrayList<String> stage) {
        /**
         * Like in PacMan class, this checks if the ghost can move in the "this.direction" direction
         */
        int gX, gY;
        switch (direction) {
            case ('u') :
                gX = this.getX() / 24;
                gY = (this.getY() - 1) / 24;
                if (stage.get(gY).charAt(gX) == '#' || (stage.get(gY).charAt(gX) == '-' && !initialMovement))
                    return false; // check upper left
                gX = (this.getX() + 23) / 24;
                gY = (this.getY() - 1) / 24;
                if (stage.get(gY).charAt(gX) == '#' || (stage.get(gY).charAt(gX) == '-' && !initialMovement))
                    return false; // check upper right
                break;
            case ('d') :
                gX = this.getX() / 24;
                gY = (this.getY() + 24) / 24;
                if (stage.get(gY).charAt(gX) == '#' || (stage.get(gY).charAt(gX) == '-' && !initialMovement))
                    return false; // check lower left
                gX = (this.getX() + 23) / 24;
                gY = (this.getY() + 24) / 24;
                if (stage.get(gY).charAt(gX) == '#' || (stage.get(gY).charAt(gX) == '-' && !initialMovement))
                    return false; // check lower right
                break;
            case ('l') :
                gX = (this.getX() - 1) / 24;
                gY = this.getY() / 24;
                if (stage.get(gY).charAt(gX) == '#' || (stage.get(gY).charAt(gX) == '-' && !initialMovement))
                    return false; // check upper left
                gX = (this.getX() - 1) / 24;
                gY = (this.getY() + 23) / 24;
                if (stage.get(gY).charAt(gX) == '#' || (stage.get(gY).charAt(gX) == '-' && !initialMovement))
                    return false; // check lower left
                break;
            case ('r') :
                gX = (this.getX() + 24) / 24;
                gY = this.getY() / 24;
                if (stage.get(gY).charAt(gX) == '#' || (stage.get(gY).charAt(gX) == '-' && !initialMovement))
                    return false; // check upper right
                gX = (this.getX() + 24) / 24;
                gY = (this.getY() + 23) / 24;
                if (stage.get(gY).charAt(gX) == '#' || (stage.get(gY).charAt(gX) == '-' && !initialMovement))
                    return false; // check lower right
                break;
        }
        return true;
    }

    private void setOffSets(char wantedDirection) {
        /**
         * Offsets used to update ghosts' positions
         */
        switch (wantedDirection) {
            case ('u'):
                setHorOffset(0);
                setVerOffset(-1);
                break;
            case ('d'):
                setHorOffset(0);
                setVerOffset(+1);
                break;
            case ('l'):
                setHorOffset(-1);
                setVerOffset(0);
                break;
            case ('r'):
                setHorOffset(+1);
                setVerOffset(0);
                break;
            case ('s'):
                setHorOffset(0);
                setVerOffset(0);
        }

    }

    public void setWantedDirection(char wantedDirection) {
        this.wantedDirection = wantedDirection;
    }

    public int getImageModifier() {
        return imageModifier;
    }

    public void setImageModifier(int imageModifier) {
        this.imageModifier = imageModifier;
    }

    private void changeWantedDirection(char direction) {
        char dir = direction;
        int randomNum = ThreadLocalRandom.current().nextInt(0, 4);
        switch (randomNum) { // TODO: 2/27/17 modify to chase pac
            case 0 :
                if (direction != 'd') dir = 'u';
                break;
            case 1 :
                if (direction != 'u') dir = 'd';
                break;
            case 2 :
                if (direction != 'r') dir = 'l';
                break;
            case 3 :
                if (direction != 'l') dir = 'r';
                break;
        }
        this.setWantedDirection(dir);
    }

    private boolean checkCollision(PacMan pac) {
        /**
         * If it looks stupid but it works, it ain't stupid!
         */
        return distance(this, pac) < 20;
    }

    private boolean checkProximity(PacMan pac) {
        /**
         * Based on whether the ghosts hunt or are hunted,
         * this changes their (wanted) directions based on Pac's position
         * This happens if they see him in a distance < (3 blocks)
         */
        if (this.getX() == pac.getX() && Math.abs(this.getY() - pac.getY()) <= 72) {
            if (this.getY() > pac.getY()) {
                if (hunted) {
                    direction = 'd';
                    wantedDirection = 'd';
                }
                else {
                    direction = 'u';
                    wantedDirection = 'u';
                }
            }
            if (this.getY() < pac.getY()) {
                if (hunted) {
                    direction = 'u';
                    wantedDirection = 'u';
                }
                else {
                    direction = 'd';
                    wantedDirection = 'd';
                }
            }
            return true;
        }
        if (this.getY() == pac.getY() && Math.abs(this.getX() - pac.getX()) <= 72) {
            if (this.getX() > pac.getX()) {
                if (hunted) {
                    direction = 'r';
                    wantedDirection = 'r';
                }
                else {
                    direction = 'l';
                    wantedDirection = 'l';
                }
            }
            if (this.getX() < pac.getX()) {
                if (hunted) {
                    direction = 'l';
                    wantedDirection = 'l';
                }
                else {
                    direction = 'r';
                    wantedDirection = 'r';
                }
            }
            return true;
        }
        return false;
    }

    private void ghostDeath() {
        /**
         * Each ghost is worth more in a particular hunt
         */
        int scoreMultiplier = getStage().getPac().getScoreMultiplier();
        setX(getDefaultX());
        setY(getDefaultY());
        initialMovement = true; // it's now in the cave!
        wantedDirection = 'u';
        direction = 'u';
        PlayerInfo tmpPi = getStage().getPlayerInfo(); // increases score when simple cookie is eaten
        tmpPi.setScore(tmpPi.getScore() + 400 * scoreMultiplier); // values of ghosts' lives increase through kills!
        getStage().getPac().setScoreMultiplier(scoreMultiplier + 1);
    }

    public void setInitialMovement(boolean initialMovement) {
        this.initialMovement = initialMovement;
    }

    public void update() {
        int x = this.getX();
        int y = this.getY();

        if (checkCollision(getStage().getPac())) { // collision check
            if (hunted) ghostDeath();
            else getStage().pacDeath();
            return;
        }

        if (hunted && getX() == getDefaultX() && getY() == getDefaultY())
            return; // do nothing if it's just eaten, wait until the hunt ends!


        if (initialMovement) {
            if (checkEdges(this.direction, getStage().getPg())) {
                setOffSets(this.direction);
            } else {
                initialMovement = false; // stop goin up
                changeWantedDirection(direction);
                direction = wantedDirection;
            }
        }
        else {
            boolean close = checkProximity(getStage().getPac());
            if (checkEdges(this.wantedDirection, getStage().getPg())) {
                this.setDirection(this.wantedDirection);
                setOffSets(this.wantedDirection);
                if (!close) { // far away from pac
                    changeWantedDirection(this.direction);
                }
            }
            else if (checkEdges(this.direction, getStage().getPg())) {
                setOffSets(this.direction);
            }
            else {
                if (!close) {
                    changeWantedDirection(direction);
                }
                return;
            }
        }
        setX(x + horOffset);
        setY(y + verOffset);
    }

    @Override
    public void draw(Graphics g) {;}

    public void setDirection(char direction) {
        this.direction = direction;
    }

    public void setHorOffset(int horOffset) {
        this.horOffset = horOffset;
    }

    public void setVerOffset(int verOffset) {
        this.verOffset = verOffset;
    }

    public void setHunted(boolean value) {
        hunted = value;
    }

    public void setImageModifierOffset(int imageModifierOffset) {
        this.imageModifierOffset = imageModifierOffset;
    }

    public int getImageModifierOffset() {
        return imageModifierOffset;
    }
}
