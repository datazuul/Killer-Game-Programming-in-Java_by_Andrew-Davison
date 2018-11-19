package com.example.chat.threaded.chatServlet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Properties;

public class URLChat extends JFrame implements ActionListener {

    private static final String SERVER = "localhost";

    private String userName;
    private String cookieString = null;

    private JTextArea jtaMessages;
    private JTextField jtfMessage;
    private JButton jbWho;

    public URLChat(String name) {
        super("URL Chat Client for : " + name);

        userName = name;
        initializeGUI();

        Properties properties = System.getProperties();
        properties.put("sun.net.client.defaultConnectTimeout", "2000");
        properties.put("sun.net.client.defaultReadTimeout", "2000");
        System.setProperties(properties);

        sayHi();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sayBye();
            }
        });

        setSize(300, 450);
        setVisible(true);

        new URLChatWatcher(this, userName, cookieString).start();
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

        jbWho = new JButton("who");
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

    private void sayHi() {
        try {
            URL url = new URL(SERVER + "?cmd=hi&name=" + URLEncoder.encode(userName, "UTF-8"));
            URLConnection urlConnection = url.openConnection();
            cookieString = urlConnection.getHeaderField("Set-Cookie");

            System.out.println("Received cookie : " + cookieString);
            if (cookieString != null) {
                int index = cookieString.indexOf(";");
                if (index != -1) {
                    cookieString = cookieString.substring(0, index);
                }
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String response = bufferedReader.readLine().trim();
            bufferedReader.close();

            if (response.equals("ok") && (cookieString != null)) {
                showMessage("Server login successful\n");
            } else {
                System.out.println("Server rejected login");
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private void sayBye() {
        try {
            URL url = new URL(SERVER + "?cmd=bye&name=" + URLEncoder.encode(userName, "UTF-8"));
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("Cookie", cookieString);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String response = bufferedReader.readLine().trim();
            bufferedReader.close();

            if (response.equals("ok")) {
                System.out.println("Server logout successful");
            } else {
                System.out.println("Server rejected logout");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jbWho) {
            askWho();
        } else if (e.getSource() == jtfMessage) {
            sendMessage();
        }
    }

    private void askWho() {
        try {
            URL url = new URL(SERVER + "?cmd=who");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            StringBuffer stringBuffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }
            bufferedReader.close();

            String response = stringBuffer.toString();
            if (response.equals("no")) {
                showMessage("Server rejected who request\n");
            } else {
                showMessage(response);
            }
        } catch (Exception e) {
            showMessage("Servlet Error : who button not processed\n");
            System.out.println(e);
        }
    }

    private void sendMessage() {
        String message = jtfMessage.getText().trim();

        if (message.equals("")) {
            JOptionPane.showMessageDialog(null, "No message entered", "Send message error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            sendURLMessage(message);
        }
    }

    private void sendURLMessage(String message) {
        try {
            URL url = new URL(SERVER + "?cmd=msg&name=" + URLEncoder.encode(userName, "UTF-8") + "&msg=" +
                    URLEncoder.encode(message, "UTF-8"));
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("Cookie", cookieString);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String response = bufferedReader.readLine().trim();
            bufferedReader.close();

            if (!response.equals("ok")) {
                showMessage("Message rejected\n");
            } else {
                showMessage("(" + userName + ")" + message + "\n");
            }
        } catch (Exception e) {
            showMessage("Servlet Error : did not send : " + message + "\n");
            System.out.println(e);
        }
    }

    public void showMessage(final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                jtaMessages.append(message);
                jtaMessages.setCaretPosition(jtaMessages.getText().length());
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("ok");
            System.exit(0);
        }
        new URLChat(args[0]);
    }
}
