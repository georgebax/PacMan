/**
 * Created by gbax on 11/23/16.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Timer;

public class MainWindow extends JFrame implements Runnable{
    private static final int NUM_OF_LEVELS = 2;
    /**
     * Main Application Window, titled "Medialab Pacman", always placed at the center of the screen.
     * Set to run in the main thread.
     */
    private ArrayList<ArrayList<Shape>> list = new ArrayList<>();
    private GameInfo gi;
    private PlayerInfo pi;
    private Playground pg;
    private int currentStage;
    private StartScreen ss;
    private String pName;

    public MainWindow() {
        super();
        setTitle("MediaLab Pacman");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(0, 0, 22*24, 570);
        setLocationRelativeTo(null); // center of screen regardless of monitor
        setJMenuBar(new MyMenu(this));
        currentStage = 0;
    }

    private void createAndShowGui() {
        Scanner sc = null;
        try {
            sc = new Scanner(new File("highscores.txt"));
        } catch (FileNotFoundException e) {System.out.println(e.getMessage());}
        sc.nextLine();
        String[] temp = sc.nextLine().split(" ");
        int highscore = Integer.valueOf(temp[1]);
        gi = new GameInfo(3, this);
        pi = new PlayerInfo(highscore);
        ss = new StartScreen();
        add(ss, BorderLayout.CENTER);
        add(gi, BorderLayout.SOUTH);
        add(pi, BorderLayout.NORTH);
        setVisible(true);
//        this.setResizable(false);
    }

    public void loadStage(int stageNumber) {
        /**
         *  Public because it has to be called from the menu, which is another class
         */
        if (pg != null) {
            pg.getPac().stopTimers();
            remove(pg);
        }
        if (ss != null) remove(ss);

        if (currentStage > NUM_OF_LEVELS)
            declareSuccess();
        pg = new Playground("pg" + Integer.toString(stageNumber) + ".txt", pi, gi, this);
        add(pg, BorderLayout.CENTER);
//        pg.getPac().setX(pg.getPac().getDefaultX()); // initializing pacman
//        pg.getPac().setY(pg.getPac().getDefaultY());
        if (currentStage > 1) pg.run();
        list = pg.getLevel();
        setVisible(true);
    }

    public void declareSuccess() {
        /**
         *  Public because it has to be called from the PacMan class
         */
        MainWindow root = this;
        remove(pg);
        EndScreen es = new EndScreen(pi.getScore(), "success");
        add(es, BorderLayout.CENTER);
        System.out.println(pi.getScore());
        pg.getPac().stopTimers();
        setVisible(true);
        Timer endTimer = new Timer();
        int tempscore = pi.getScore();
        endTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SubmitWindow sw = new SubmitWindow(root, tempscore);
            }
        }, 2000);
        pi.setScore(0);
        currentStage = 0;
        remove(gi);
    }

    public void declareFailure() {
        /**
         *  Public because it has to be called from the PacMan class
         */
        MainWindow root = this;
        String pName;
        remove(pg);
        EndScreen es = new EndScreen(pi.getScore(), "failure");
        add(es, BorderLayout.CENTER);
        System.out.println(pi.getScore());
        int tempscore = pi.getScore();
        pg.getPac().stopTimers();
        Timer endTimer = new Timer();
        endTimer.schedule(new TimerTask() {
            @Override
            public void run() {
//                remove(es);
//                getTextFromField();
                SubmitWindow sw = new SubmitWindow(root, tempscore);
            }
        }, 2000);
        pi.setScore(0);
        currentStage = 0;
        setVisible(true);
    }

    @Override
    public void run() {
        createAndShowGui(); // first stage, next stage only if first one is completed
    }

    public static void main(String[] args) {
        try { // UI settings
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("OptionPane.background", Color.BLACK);
            UIManager.put("Panel.background", Color.BLACK);
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            System.out.println("Can't find appropriate Look and Feel: " + e.getMessage());
            System.exit(1);
        }
        try {
            Thread t = new Thread(new MainWindow());
            t.start(); // "run" method starts
        } catch (Exception e) {
            System.out.println("Can't open main Thread: " + e.getMessage());
            System.exit(1);
        }
//        SwingUtilities.invokeLater(w); // Schedules the application to be run at the correct time in the event queue.
    }

    //--------------------- SETTERS N GETTERS -----------------------------------------

    public Playground getPg() {
        return pg;
    }

    public GameInfo getGi() {
        return gi;
    }

    public PlayerInfo getPi() {
        return pi;
    }

    public StartScreen getSs() {
        return ss;
    }

    public static int getNumOfLevels() {
        return NUM_OF_LEVELS;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(int currentStage) {
        this.currentStage = currentStage;
    }
}
