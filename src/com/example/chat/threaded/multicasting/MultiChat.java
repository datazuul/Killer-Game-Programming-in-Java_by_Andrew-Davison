package com.example.chat.threaded.multicasting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.*;

public class MultiChat extends JFrame implements ActionListener {

    private static final int TIME_OUT = 5000;
    private static final int PACKET_SIZE = 1024;

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 1234;

    private static final int GROUP_PORT = 5555;

    private DatagramSocket clientSocket;
    private InetAddress serverAddress;

    private MulticastSocket groupSocket;
    private InetAddress groupAddress;

    private String userName;

    private JTextArea jtaMessages;
    private JTextField jtfMessage;
    private JButton jbWho;

    public MultiChat(String name) {
        super("Multicasting chat client for " + name);
        userName = name;
        initializeGUI();

        makeClientSocket();
        sendServerMessage("hi " + userName);
        checkHiResponse();

        joinChatGroup();
        sendPacket("hi");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sayBye();
            }
        });

        setSize(300, 450);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jbWho) {
            doWho();
        } else if (e.getSource() == jtfMessage) {
            sendMessage();
        }
    }

    private void showMessage(final String message) {
        Runnable updateMessagesText = new Runnable() {
            @Override
            public void run() {
                jtaMessages.append(message);
                jtaMessages.setCaretPosition(jtaMessages.getText().length());
            }
        };
        SwingUtilities.invokeLater(updateMessagesText);
    }

    private void makeClientSocket() {
        try {
            clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(TIME_OUT);
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
    }

    private void checkHiResponse() {
        String hiResponse = readServerMessage();

        if (hiResponse.equals("no")) {
            System.out.println("Login rejected, exiting...");
            System.exit(0);
        } else {
            try {
                groupAddress = InetAddress.getByName(hiResponse);
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

    private void doWho() {
        sendServerMessage("who");
        String whoResponse = readServerMessage();
        if (whoResponse == null) {
            showMessage("NameServer Problem : no who info available\n");
        } else {
            showMessage(whoResponse);
        }
    }

    private void sayBye() {
        try {
            sendPacket("bye");
            sendServerMessage("bye" + userName);
        } catch (Exception e) {
            System.out.println(e);
        }
        System.exit(0);
    }

    private void sendServerMessage(String message) {
        try {
            DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(), serverAddress,
                    SERVER_PORT);
            clientSocket.send(datagramPacket);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private String readServerMessage() {
        String message = null;
        try {
            byte[] data = new byte[PACKET_SIZE];
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length);

            clientSocket.receive(datagramPacket);
            message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
        } catch (IOException e) {
            System.out.println(e);
        }
        return message;
    }

    private void joinChatGroup() {
        try {
            groupSocket = new MulticastSocket(GROUP_PORT);
            groupSocket.joinGroup(groupAddress);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void sendMessage() {
        String message = jtfMessage.getText().trim();

        if (message.equals("")) {
            JOptionPane.showMessageDialog(null, "No message entered", "Send message error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            sendPacket(message);
        }
    }

    private void sendPacket(String message) {
        String labelledMessage = "(" + userName + ") : " + message;
        try {
            DatagramPacket datagramPacket = new DatagramPacket(labelledMessage.getBytes(), labelledMessage.length(),
                    groupAddress, GROUP_PORT);
            groupSocket.send(datagramPacket);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void waitForPackets() {
        DatagramPacket datagramPacket;
        byte data[];

        try {
            while (true) {
                data = new byte[PACKET_SIZE];
                datagramPacket = new DatagramPacket(data, data.length);
                groupSocket.receive(datagramPacket);
                processPacket(datagramPacket);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void processPacket(DatagramPacket datagramPacket) {
        String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
        if (isVisibleMessage(message, userName)) {
            showMessage(message + "\n");
        }
    }

    private boolean isVisibleMessage(String message, String name) {
        int index = message.indexOf("/");
        if (index == -1) {
            return true;
        }

        String toName = message.substring(index + 1).trim();
        if (toName.equals(name)) {
            return true;
        } else {
            if (message.startsWith("(" + name)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("OK");
            System.exit(0);
        }
        new MultiChat(args[0]);
    }
}
