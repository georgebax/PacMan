import javax.swing.*;
import java.awt.*;

/**
 * Created by gbax on 3/3/17.
 */
public class StartScreen extends JLabel{
    public StartScreen() {
        super("Welcome! Press Start to begin!!!", SwingConstants.CENTER);
        setForeground(Color.CYAN);
        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
    }
}
