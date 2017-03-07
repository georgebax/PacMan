import java.awt.Graphics;
/**
 * Created by gbax on 12/27/16.
 */

public abstract class Shape {
    /**
     * The class used for every model of this game, be it Pac, Ghosts, or stage elements!
     */
    private int x;
    private int y;
    private int width;
    private int height;
    private int defaultY;
    private int defaultX;

    private Playground stage;

    public Shape() { // just for there to be a default
        this(0, 0, 1, 1);
    } // default constructor calls "normal" one

    public Shape(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void draw(Graphics g);

    public double distance(Shape s1, Shape s2) {
        return Math.sqrt((Math.pow((double) s1.getX() - (double) s2.getX(), 2)) + (Math.pow((double) s1.getY() - (double) s2.getY(), 2)));
    }

    public Playground getStage() {
        return stage;
    }

    public void setStage(Playground stage) {
        this.stage = stage;
    }

    public int getX() {return x;}

    public void setX(int x) {this.x = x;}

    public int getDefaultX() {
        return defaultX;
    }

    public int getDefaultY() {
        return defaultY;
    }

    public int getY() {return y;}

    public void setY(int y) {this.y = y;}

    public int getWidth() {return width;}

    public void setWidth(int width) {this.width = width;}

    public int getHeight() {return height;}

    public void setHeight(int height) {this.height = height;}

    public void setDefaultY(int defaultY) {
        this.defaultY = defaultY;
    }

    public void setDefaultX(int defaultX) {
        this.defaultX = defaultX;
    }
}