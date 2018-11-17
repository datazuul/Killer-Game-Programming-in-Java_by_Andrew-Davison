package com.example.networkingbasics.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class ClientInfo {

    private static final int BUFSIZE = 1024;

    private SocketChannel socketChannel;
    private SelectScoreServer selectScoreServer;
    private ByteBuffer inBuffer;

    private Charset charset;
    private CharsetDecoder decoder;

    public ClientInfo(SocketChannel socketChannel, SelectScoreServer selectScoreServer) {
        this.socketChannel = socketChannel;
        this.selectScoreServer = selectScoreServer;
        inBuffer = ByteBuffer.allocateDirect(BUFSIZE);
        inBuffer.clear();

        charset = Charset.forName("ISO-8859-1");
        decoder = charset.newDecoder();

        showClientDetails();
    }

    private void showClientDetails() {
        Socket socket = socketChannel.socket();
        InetAddress inetAddress = socket.getInetAddress();
        System.out.println("Client address : " + inetAddress.getHostAddress());
        System.out.println("Client name : " + inetAddress.getHostName());
        System.out.println("Client port : " + socket.getPort());
    }

    public void closeDown() {
        try {
            socketChannel.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public String readMessage() {
        String inputMsg = null;
        try {
            int numBytesRead = socketChannel.read(inBuffer);
            if (numBytesRead == -1) {
                socketChannel.close();
                selectScoreServer.removeChannel(socketChannel);
            } else {
                inputMsg = getMessage(inBuffer);
            }
        } catch (IOException e) {
            System.out.println("rm : " + e);
            selectScoreServer.removeChannel(socketChannel);
        }
        return inputMsg;
    }

    private String getMessage(ByteBuffer byteBuffer) {
        String msg = null;
        int posn = byteBuffer.position();
        int limit = byteBuffer.limit();

        byteBuffer.position(0);
        byteBuffer.limit(posn);
        try {
            CharBuffer charBuffer = decoder.decode(byteBuffer);
            msg = charBuffer.toString();
        } catch (CharacterCodingException e) {
            System.out.println(e);
        }

        System.out.println("Current msg : " + msg);
        byteBuffer.limit(limit);
        byteBuffer.position(posn);

        if (msg.endsWith("\n")) {
            byteBuffer.clear();
            return msg;
        }

        return null;
    }

    public boolean sendMessage(String message) {
        String fullMessage = message + "\r\n";

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFSIZE);
        byteBuffer.clear();
        byteBuffer.put(fullMessage.getBytes());
        byteBuffer.flip();

        boolean messageSent = false;
        try {
            while (byteBuffer.hasRemaining()) {
                socketChannel.write(byteBuffer);
            }
            messageSent = true;
        } catch (IOException e) {
            System.out.println(e);
            selectScoreServer.removeChannel(socketChannel);
        }

        return messageSent;
    }
}
