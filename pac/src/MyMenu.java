import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;

public class MyMenu extends JMenuBar {
    /**
     * Top menu of this game
     */
    public MyMenu(MainWindow root) {
        JMenu fileMenu = new JMenu("Game");
        add(fileMenu);

        JMenuItem startAction = new JMenuItem("Start");
        JMenuItem showScores  = new JMenuItem("Highscores");
        JMenuItem about = new JMenuItem("About");
        JMenuItem endAction = new JMenuItem("Exit");

        fileMenu.add(startAction);
        fileMenu.add(showScores);
        fileMenu.add(about);
        fileMenu.add(endAction);
        startAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (root.getCurrentStage() == 0) {
                    root.loadStage(root.getCurrentStage() + 1); // means game is not running yet
                    root.getPg().run();
                }
                else {
                    root.setCurrentStage(0);
                    root.getPi().setScore(0);
                    root.loadStage(1);
                }
            }
        });
        endAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0); // just shuttin down
            }
        });
        showScores.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                showHighScores();
            }
        });
        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                showAbout();
            }
        });
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(null, "This project was created with love by George Baxopoulos " +
                "for the NTUA Multimedia course!");
    }

    private void showHighScores() {
        Scanner sc = null;
        StringBuilder sb = new StringBuilder();
        try {
            sc = new Scanner(new File("highscores.txt"));
        } catch (FileNotFoundException e) {e.printStackTrace();}
        sc.nextLine();
        sb.append("Highscores!\n");
        while(sc.hasNextLine()) {
            sb.append(sc.nextLine());
            sb.append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }
}
