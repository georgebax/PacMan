import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class SubmitWindow extends JFrame{
    /**
     * A small window with the functionality of updating the highscores.txt file with the new score,
     * only if it's within the 5 top scores!
     */
    private JTextField field;
    private JButton sb;
    ArrayList<Integer> scores;
    ArrayList<String> names;


    public SubmitWindow(MainWindow root, int score) {
        super();
        field = new JTextField(10);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        sb = new JButton("Submit");
        sb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String name = new String(field.getText());
//                int score = root.getPi().getScore();
                root.getPi().setScore(0);
                checkScore(name, score);
            }
        });

        setTitle("Submit your name!");
        setLayout(new GridLayout(2, 0));
        setBounds(200, 200, 100, 100);
        setLocationRelativeTo(null); // center of screen regardless of monitor
        add(field);
        add(sb);
        setVisible(true);
    }

    private void checkScore(String name, int score) {
        BufferedReader reader;
        Scanner sc = null;
        try {
            sc = new Scanner(new File("highscores.txt"));
        } catch (FileNotFoundException e) {System.out.println(e.getMessage());}
        scores = new ArrayList<>();
        names = new ArrayList<>();
        sc.nextLine(); // skip header
        sc.useDelimiter("\\s");
        while (sc.hasNextLine()) {
            if (sc.hasNext()) names.add(sc.next());
            else break;
            Integer temp = new Integer(sc.nextInt());
            scores.add(temp);
        }
        for (int i = 0; i < scores.size(); i++) {
            if (score > scores.get(i)) {
                scores.add(i, score);
                names.add(i, name);
                break; // descending order kept
            }
        }
        try {
            updateHighscores();
        } catch (IOException e) {System.out.println(e.getMessage());}
    }

    private void updateHighscores() throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter("highscores.txt"));
        pw.write("Highscores!\n");
        for (int i = 0; i < 5; i++)
            pw.write(names.get(i) + " " + scores.get(i) + "\n");
        pw.close();
    }
}
