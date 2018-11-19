package com.example.chat.threaded;

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

public class ChatClient extends JFrame implements ActionListener {

    private static final int PORT = 1234;
    private static final String HOST = "localhost";

    private Socket socket;
    private PrintWriter printWriter;

    private JTextArea jtaMessages;
    private JTextField jtfMessage;
    private JButton jbWho;

    public ChatClient() {
        super("Chat Client");
        initializeGUI();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeLink();
            }
        });

        setSize(300, 450);
        setVisible(true);

        makeContact();
    }

    private void initializeGUI() {
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        jtaMessages = new JTextArea(7, 7);
        jtaMessages.setEditable(false);
        JScrollPane jScrollPane = new JScrollPane(jtaMessages);
        container.add(jScrollPane, "Center");

        JLabel jlMessage = new JLabel("Message : ");
        jtfMessage = new JTextField(15);
        jtfMessage.addActionListener(this);

        jbWho = new JButton("Who");
        jbWho.addActionListener(this);

        JPanel jPanel1 = new JPanel(new FlowLayout());
        jPanel1.add(jlMessage);
        jPanel1.add(jtfMessage);

        JPanel jPanel2 = new JPanel(new FlowLayout());
        jPanel2.add(jbWho);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.add(jPanel1);
        jPanel.add(jPanel2);

        container.add(jPanel, "South");
    }

    private void closeLink() {
        try {
            printWriter.println("bye");
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.exit(0);
    }

    private void makeContact() {
        try {
            socket = new Socket(HOST, PORT);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream(), true);

            new ChatWatcher(this, bufferedReader).start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jbWho) {
            printWriter.println("who");
        } else if (e.getSource() == jtfMessage) {
            sendMessage();
        }
    }

    private void sendMessage() {
        String message = jtfMessage.getText().trim();

        if (message.equals("")) {
            JOptionPane.showMessageDialog(null, "No message entered", "Send message error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            printWriter.println(message);
        }
    }

    public void showMessage(final String message) {
        Runnable updateMessagesText = new Runnable() {
            @Override
            public void run() {
                jtaMessages.append(message);
                jtaMessages.setCaretPosition(jtaMessages.getText().length());
            }
        };
        SwingUtilities.invokeLater(updateMessagesText);
    }

    public static void main(String[] args) {
        new ChatClient();
    }
}
