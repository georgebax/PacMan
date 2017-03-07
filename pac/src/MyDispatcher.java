import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class MyDispatcher implements KeyEventDispatcher
{
    /**
     * Reads the pressed arrow from the keyboard and changes Pac's wanted direction
     */
    Playground pg;
    MyDispatcher(Playground pg) {
        this.pg = pg;
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent e)
    {
        PacMan pac = pg.getPac();
        if (e.getKeyCode() == 38) //up key
            pac.setWantedDirection('u');
        else if (e.getKeyCode() == 40) //down key
            pac.setWantedDirection('d');
        else if (e.getKeyCode() == 39) //right key
            pac.setWantedDirection('r');
        else if (e.getKeyCode() == 37) //left key
            pac.setWantedDirection('l');

        return false;
    }
}