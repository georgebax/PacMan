import java.awt.*;

public class Space extends Shape {
    public Space(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public Space() {
        super();
    } // default constructor calls Shape constructor which calls the "normal" one

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(getX(), getY(), getWidth(), getHeight());
    }
}