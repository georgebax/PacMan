import javax.swing.*;
import java.awt.*;

public class PlayerInfo extends JPanel {
    /**
     * Provides info about the current game!
     */
    private int score;
    private HighscoreLabel hsLabel;
    private ScoreLabel scoreLabel;

    PlayerInfo(int highscore) {
        setLayout(new GridLayout(0, 2));
        this.score = 0;
        hsLabel = new HighscoreLabel(highscore);
        scoreLabel  = new ScoreLabel(score);
        this.add(hsLabel, SwingConstants.CENTER);
        this.add(scoreLabel, SwingConstants.CENTER);
    }

    private class HighscoreLabel extends JLabel {
        HighscoreLabel(int h) {
            super("High Score: " + h, SwingConstants.CENTER);
            this.setOpaque(true);
            this.setForeground(Color.magenta);
            this.setBackground(Color.blue);
        }
    }

    private class ScoreLabel extends JLabel {
        ScoreLabel(int sc) {
            super("Score: " + sc, SwingConstants.CENTER);
            this.setOpaque(true);
            this.setForeground(Color.blue);
            this.setBackground(Color.magenta);
        }
    }

    public int getScore() {return score;}
    public void setScore(int score) {
        this.score = score;
        scoreLabel.setText("Score: " + score);
    }
}
