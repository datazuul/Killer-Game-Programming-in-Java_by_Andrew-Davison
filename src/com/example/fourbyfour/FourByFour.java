package com.example.fourbyfour;

import javax.swing.*;
import java.awt.*;

public class FourByFour extends JFrame {

    private JTextField messageTF;

    public FourByFour() {
        super("Four by Four");

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        WrapFourByFour wrapFourByFour = new WrapFourByFour(this);
        container.add(wrapFourByFour, BorderLayout.CENTER);

        JLabel messageLabel = new JLabel("Messages : ");
        messageTF = new JTextField(30);
        messageTF.setText("Player 1's turn");
        messageTF.setEditable(false);
        JPanel jPanel1 = new JPanel();
        jPanel1.add(messageLabel);
        jPanel1.add(messageTF);
        container.add(jPanel1, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setVisible(true);
    }

    public void showMessage(String message) {
        messageTF.setText(message);
    }

    public static void main(String[] args) {
        new FourByFour();
    }
}
