import java.awt.*;

/**
 * Created by gbax on 12/27/16.
 */

public class Circle extends Shape {
    public Circle(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public Circle() {
        super();
    } // default constructor calls Shape constructor which calls the "normal" one

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.pink);
        g.fillOval(getX(), getY(), getWidth(), getHeight());
    }
}