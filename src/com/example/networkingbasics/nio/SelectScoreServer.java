package com.example.networkingbasics.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;

public class SelectScoreServer {

    private static final int PORT_NUMBER = 1234;

    private HighScores highScores;
    private HashMap clients;

    public SelectScoreServer() {
        highScores = new HighScores();
        clients = new HashMap();
        try {
            System.out.println("Listening on port " + PORT_NUMBER);

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);

            ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(PORT_NUMBER));

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Iterator iterator = selector.selectedKeys().iterator();
                SelectionKey selectionKey;
                while (iterator.hasNext()) {
                    selectionKey = (SelectionKey) iterator.next();
                    iterator.remove();
                    if (selectionKey.isAcceptable()) {
                        newChannel(selectionKey, selector);
                    } else if (selectionKey.isReadable()) {
                        readFromChannel(selectionKey);
                    } else {
                        System.out.println("Did not process key : " + selectionKey);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void newChannel(SelectionKey selectionKey, Selector selector) {
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);

            clients.put(socketChannel, new ClientInfo(socketChannel, this));
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void readFromChannel(SelectionKey selectionKey) {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ClientInfo clientInfo = (ClientInfo) clients.get(socketChannel);
        if (clientInfo == null) {
            System.out.println("No client info for channel " + socketChannel);
        } else {
            String message = clientInfo.readMessage();
            if (message != null) {
                System.out.println("Read message : " + message);
                if (message.trim().equals("bye")) {
                    clientInfo.closeDown();
                    clients.remove(socketChannel);
                } else {
                    doRequest(message, clientInfo);
                }
            }
        }
    }

    private void doRequest(String line, ClientInfo clientInfo) {
        if (line.trim().toLowerCase().equals("get")) {
            System.out.println("Processing 'get'");
            clientInfo.sendMessage(highScores.toString());
        } else if ((line.length() >= 6) && (line.substring(0, 5).toLowerCase().equals("score"))) {
            System.out.println("Processing 'score'");
            highScores.addScore(line.substring(5));
        } else {
            System.out.println("Ignoring input line");
        }
    }

    public void removeChannel(SocketChannel socketChannel) {
        clients.remove(socketChannel);
    }

    public static void main(String[] args) {
        new SelectScoreServer();
    }
}
