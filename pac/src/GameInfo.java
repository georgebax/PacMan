import sun.applet.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by gbax on 1/1/17.
 */
public class GameInfo extends JPanel{
    private JPanel livesLabel;
    private MainWindow root;

    public GameInfo(int lives, MainWindow root) {
        super(new GridLayout(0, 2));
        this.root = root;
//        add(new JLabel(Integer.toString(lives), SwingConstants.CENTER));
        livesLabel = makeLivesLabel(lives);
        add(livesLabel);
        add(makeButton()); // add start button
    }

    public JPanel makeLivesLabel(int lives) {
        /**
         * @return a Label with as many PacMan icons as the remaining lives
         * Public because it's called from the Playground class when Pac dies
         */
        JPanel panel = new JPanel();
        for (int i = 0; i < lives; i++) {
            ImageIcon icon = null;
            try { icon = new ImageIcon(ImageIO.read(new File("PMright1.gif"))); }
            catch (IOException e) { e.printStackTrace(); }
            panel.add(new JLabel(icon));
        }
        return panel;
    }

    public JButton makeButton() {
        /**
         * @return A JButton with the functionality of starting the game upon being pressed for the first time,
         * and then pausing and resuming it
         * Public because it's called from the Playground class when Pac dies
         */
        JButton b = new JButton();
        b.setText("Start!");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (b.getText().equalsIgnoreCase("Start!") || b.getText().equalsIgnoreCase("Resume!")) {
                    b.setText("Pause");
                    if (root.getCurrentStage() == 0) {
                        root.loadStage(root.getCurrentStage() + 1); // means game is not running yet
                        root.setCurrentStage(root.getCurrentStage() + 1);
                        root.getPg().run();
                    }
                    else
                        root.getPg().getPac().startTimers();
                }
                else { //pause
                    if (root.getCurrentStage() == 0)
                        b.setText("Start!");
                    else
                        b.setText("Resume!");
                    root.getPg().getPac().stopTimers();
                }
            }
        });
        return b;
    }

    public JPanel getLivesLabel() {
        return livesLabel;
    }

}
