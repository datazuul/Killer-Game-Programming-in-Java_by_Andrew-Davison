package com.example.chat.threaded.chatServlet;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ChatServlet extends HttpServlet {

    private ChatGroup chatGroup;

    public void init() throws ServletException {
        chatGroup = new ChatGroup();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String command = request.getParameter("cmd");
        System.out.println("Command : " + command);

        if (command.equals("hi")) {
            processHi(request, response);
        } else if (command.equals("bye")) {
            processBye(request, response);
        } else if (command.equals("who")) {
            processWho(response);
        } else if (command.equals("message")) {
            processMessage(request, response);
        } else if (command.equals("read")) {
            processRead(request, response);
        } else {
            System.out.println("Did not understand command : " + command);
        }
    }

    private void processHi(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = -1;
        String userName = request.getParameter("name");

        if (userName != null) {
            userId = chatGroup.addUser(userName);
        }

        if (userId != -1) {
            Cookie cookie = new Cookie("userId", "" + userId);
            response.addCookie(cookie);
        }

        PrintWriter printWriter = response.getWriter();
        if (userId != -1) {
            printWriter.println("ok");
        } else {
            printWriter.println("no");
        }
        printWriter.close();
    }

    private void processBye(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isDeleted = false;
        String userName = request.getParameter("name");

        if (userName != null) {
            int userId = getUserIdFromCookie(request);
            isDeleted = chatGroup.deleteUser(userName, userId);
        }

        PrintWriter printWriter = response.getWriter();
        if (isDeleted) {
            printWriter.println("ok");
        } else {
            printWriter.println("no");
        }
        printWriter.close();
    }

    private int getUserIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Cookie cookie;
        for (int i = 0; i < cookies.length; i++) {
            cookie = cookies[i];
            if (cookie.getName().equals("userId")) {
                try {
                    return Integer.parseInt(cookie.getValue());
                } catch (Exception e) {
                    System.out.println(e);
                    return -1;
                }
            }
        }
        return -1;
    }

    private void processWho(HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        printWriter.print(chatGroup.who());
        printWriter.close();
    }

    private void processMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isStored = false;
        String userName = request.getParameter("name");
        String message = request.getParameter("message");

        System.out.println("Message : " + message);

        if ((userName != null) && (message != null)) {
            int userId = getUserIdFromCookie(request);
            isStored = chatGroup.storeMessage(userName, userId, message);
        }

        PrintWriter printWriter = response.getWriter();
        if (isStored) {
            printWriter.println("ok");
        } else {
            printWriter.println("no");
        }
        printWriter.close();
    }

    private void processRead(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = -1;
        String userName = request.getParameter("name");

        if (userName != null) {
            userId = getUserIdFromCookie(request);
        }

        PrintWriter printWriter = response.getWriter();
        if (userId != -1) {
            printWriter.print(chatGroup.read(userName, userId));
            printWriter.flush();
        } else {
            printWriter.println("no");
        }
        printWriter.close();
    }
}
