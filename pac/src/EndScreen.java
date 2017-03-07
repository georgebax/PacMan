import javax.swing.*;
import java.awt.*;

/**
 * Created by gbax on 3/3/17.
 */
public class EndScreen extends JPanel {
    public EndScreen(int score, String result) {
        /**
         * The screen the player sees when he runs out of lives
         */
        super(new GridLayout(2, 0));
        JLabel l1;
        if (result.equals("success"))
            l1 = new JLabel("You Won !!! Thanks for playing!", SwingConstants.CENTER);
        else
            l1 = new JLabel("You Lost, but you could always try again ;)", SwingConstants.CENTER);
        l1.setForeground(Color.cyan);
        l1.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        JLabel l2 = new JLabel("Your score: " + score, SwingConstants.CENTER);
        l2.setForeground(Color.yellow);
        l2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

        add(l2, SwingConstants.CENTER);
        add(l1, SwingConstants.CENTER);
    }
}
