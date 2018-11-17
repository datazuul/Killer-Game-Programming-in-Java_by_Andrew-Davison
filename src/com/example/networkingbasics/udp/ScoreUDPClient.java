package com.example.networkingbasics.udp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.StringTokenizer;

public class ScoreUDPClient extends JFrame implements ActionListener {

    private static final int SERVER_PORT = 1234;
    private static final String SERVER_HOST = "localhost";

    private static final int BUFSIZE = 1024;

    private DatagramSocket socket;
    private InetAddress serverAddress;

    private JTextArea jtaMessages;
    private JTextField jtfName, jtfScore;
    private JButton jbGetScores;

    public ScoreUDPClient() {
        super("High Score UDP Client");

        initializeGUI();

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            serverAddress = InetAddress.getByName(SERVER_HOST);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 450);
        setResizable(false);
        setVisible(true);

        waitForPackets();
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jbGetScores) {
            sendMessage(serverAddress, SERVER_PORT, "get");
            jtaMessages.append("Sent a get command\n");
        } else if (e.getSource() == jtfScore) {
            sendScore();
        }
    }

    private void sendScore() {
        String name = jtfName.getText().trim();
        String score = jtfScore.getText().trim();

        if ((name.equals("")) && (score.equals(""))) {
            JOptionPane.showMessageDialog(null, "No name and score entered", "Send score error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (name.equals("")) {
            JOptionPane.showMessageDialog(null, "No name entered", "Send name error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (score.equals("")) {
            JOptionPane.showMessageDialog(null, "No score entered", "Send score error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            sendMessage(serverAddress, SERVER_PORT, "score " + name + " & " + score + " & ");
            jtaMessages.append("Sent " + name + " & " + score + "\n");
        }
    }

    private void sendMessage(InetAddress serverAddress, int serverPort, String message) {
        byte messageData[] = message.getBytes();
        try {
            DatagramPacket datagramPacket = new DatagramPacket(messageData, messageData.length, serverAddress,
                    serverPort);
            socket.send(datagramPacket);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void waitForPackets() {
        DatagramPacket datagramPacket;
        byte data[];

        try {
            while (true) {
                data = new byte[BUFSIZE];
                datagramPacket = new DatagramPacket(data, data.length);

                System.out.println("Waiting for a packet...");
                socket.receive(datagramPacket);

                processServer(datagramPacket);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void processServer(DatagramPacket datagramPacket) {
        InetAddress inetAddress = datagramPacket.getAddress();
        int serverPort = datagramPacket.getPort();
        String serverMessage = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

        System.out.println("Server packet from " + serverAddress + ", " + serverPort);
        System.out.println("Server message : " + serverMessage);

        showResponse(serverMessage);
    }

    private void showResponse(String message) {
        if ((message.length() >= 7) && (message.substring(0, 6).equals("HIGH$$"))) {
            showHigh(message.substring(6).trim());
        } else {
            jtaMessages.append(message + "\n");
        }
    }

    private void showHigh(String message) {
        StringTokenizer tokenizer = new StringTokenizer(message, "&");
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

    public static void main(String[] args) {
        new ScoreUDPClient();
    }
}
