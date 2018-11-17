package com.example.networkingbasics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

public class ScoreClient extends JFrame implements ActionListener {

    private static final int PORT = 1234;
    private static final String HOST = "localhost";

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private JTextArea jtaMessages;
    private JTextField jtfName, jtfScore;
    private JButton jbGetScores;

    public ScoreClient() {
        super("High Score Client");

        initializeGUI();
        makeContact();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeLink();
            }
        });

        setSize(300, 450);
        setVisible(true);
    }

    private void initializeGUI() {
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        jtaMessages = new JTextArea(7, 7);
        jtaMessages.setEditable(false);
        JScrollPane jScrollPane = new JScrollPane(jtaMessages);
        container.add(jScrollPane, "Center");

        JLabel jlName = new JLabel("Name : ");
        jtfName = new JTextField(10);

        JLabel jlScore = new JLabel("Score : ");
        jtfScore = new JTextField(5);
        jtfScore.addActionListener(this);

        jbGetScores = new JButton("Get Scores");
        jbGetScores.addActionListener(this);

        JPanel jPanel1 = new JPanel(new FlowLayout());
        jPanel1.add(jlName);
        jPanel1.add(jtfName);
        jPanel1.add(jlScore);
        jPanel1.add(jtfScore);

        JPanel jPanel2 = new JPanel(new FlowLayout());
        jPanel2.add(jbGetScores);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.add(jPanel1);
        jPanel.add(jPanel2);

        container.add(jPanel, "South");
    }

    private void closeLink() {
        try {
            out.println("bye");
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        System.exit(0);
    }

    private void makeContact() {
        try {
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jbGetScores) {
            sendGet();
        } else if (e.getSource() == jtfScore) {
            sendScore();
        }
    }

    private void sendGet() {
        try {
            out.println("get");
            String line = in.readLine();
            System.out.println(line);
            if ((line.length() >= 7) && (line.substring(0, 6).equals("HIGH$$"))) {
                showHigh(line.substring(6).trim());
            } else {
                jtaMessages.append(line + "\n");
            }
        } catch (Exception e) {
            jtaMessages.append("Problem obtaining high scores\n");
            System.out.println(e);
        }
    }

    private void showHigh(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line, "&");
        String name;
        int i, score;
        i = 1;
        try {
            while (tokenizer.hasMoreTokens()) {
                name = tokenizer.nextToken().trim();
                score = Integer.parseInt(tokenizer.nextToken().trim());
                jtaMessages.append("" + i + ". " + name + " : " + score + "\n");
                i++;
            }
            jtaMessages.append("\n");
        } catch (Exception e) {
            jtaMessages.append("Problem parsing high scores\n");
            System.out.println("Parsing error with high scores : \n" + e);
        }
    }

    private void sendScore() {
        String name = jtfName.getText().trim();
        String score = jtfScore.getText().trim();

        if ((name.equals("")) && (score.equals(""))) {
            JOptionPane.showMessageDialog(null, "No name and score entered", "Send Score Error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (name.equals("")) {
            JOptionPane.showMessageDialog(null, "No name entered", "Send Score Error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (score.equals("")) {
            JOptionPane.showMessageDialog(null, "No score entered", "Send Score Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            out.println("score " + name + " & " + score + " & ");
            jtaMessages.append("Sent " + name + " & " + score + "\n");
        }
    }

    public static void main(String[] args) {
        new ScoreClient();
    }
}
