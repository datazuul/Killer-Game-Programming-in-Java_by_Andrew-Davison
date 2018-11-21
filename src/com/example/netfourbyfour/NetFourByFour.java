package com.example.netfourbyfour;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetFourByFour extends JFrame {

    private static final int PORT = 1234;
    private static final String HOST = "localhost";

    private static final int MAX_PLAYERS = 2;
    private static final int PLAYER1 = 1;
    private static final int PLAYER2 = 2;

    private WrapNetFourByFour wrapNetFourByFour;

    private Socket socket;
    private PrintWriter printWriter;

    private int playerID;
    private String status;
    private int numPlayers;
    private int currPlayer;
    private boolean isDisabled;

    public NetFourByFour() {
        super("Net four by four");

        playerID = 0;
        status = null;
        numPlayers = 0;
        currPlayer = 1;
        isDisabled = false;

        makeContact();

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        wrapNetFourByFour = new WrapNetFourByFour(this);
        container.add(wrapNetFourByFour, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disable("exiting");
                System.exit(0);
            }
        });

        pack();
        setResizable(false);
        setVisible(true);
    }

    private void makeContact() {
        try {
            socket = new Socket(HOST, PORT);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream(), true);

            new Watcher(this, bufferedReader).start();
        } catch (Exception e) {
            System.out.println("Can't contact with the  server");
            System.exit(0);
        }
    }

    public void tryMove(int position) {
        if (!isDisabled) {
            if (numPlayers < MAX_PLAYERS) {
                setStatus("Waiting for player " + otherPlayer(playerID));
            } else if (playerID != currPlayer) {
                setStatus("Sorry, it is player " + currPlayer + "'s turn");
            } else if (numPlayers == MAX_PLAYERS) {
                printWriter.println("try " + position);
                doMove(position, playerID);
            } else {
                System.out.println("Error on processing position");
            }
        }
    }

    synchronized public void doMove(int position, int playerID) {
        wrapNetFourByFour.tryPosition(position, playerID);
        if (!isDisabled) {
            currPlayer = otherPlayer(currPlayer);
            if (currPlayer == playerID) {
                setStatus("It's your turn now");
            } else {
                setStatus("Player " + currPlayer + "'s turn");
            }
        }
    }

    private int otherPlayer(int id) {
        int otherID = ((id == PLAYER1) ? PLAYER2 : PLAYER1);
        return otherID;
    }

    synchronized public void disable(String message) {
        if (!isDisabled) {
            try {
                isDisabled = true;
                printWriter.println("disconnect");
                socket.close();
                setStatus("Game Over : " + message);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void gameWon(int playerID, int score) {
        if (playerID == this.playerID) {
            disable("You have won with score " + score);
        } else {
            disable("Player " + playerID + " has won with score " + score);
        }
    }

    synchronized public void setStatus(String message) {
        status = message;
    }

    synchronized public String getStatus() {
        return status;
    }

    public void addPlayer() {
        numPlayers++;
        if (numPlayers == MAX_PLAYERS) {
            if (currPlayer == playerID) {
                setStatus("Please start");
            } else {
                setStatus("Player " + currPlayer + " starts the game");
            }
        }
    }

    public void removePlayer() {
        numPlayers--;
        if (numPlayers < MAX_PLAYERS) {
            disable("Player " + otherPlayer(playerID) + " has left");
        }
    }

    public void setPlayerID(int id) {
        System.out.println("My player id : " + id);
        playerID = id;
        if (playerID == PLAYER1) {
            setTitle("Player 1 (red balls)");
        } else {
            setTitle("Player 2 (blue cubes)");
        }

        numPlayers = id;
        if (numPlayers == MAX_PLAYERS) {
            setStatus("Player1 starts the game");
        } else if (numPlayers < MAX_PLAYERS) {
            setStatus("Waiting for player " + otherPlayer(playerID));
        }
    }

    public static void main(String[] args) {
        new NetFourByFour();
    }
}
