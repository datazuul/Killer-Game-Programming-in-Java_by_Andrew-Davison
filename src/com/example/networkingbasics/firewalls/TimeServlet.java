package com.example.networkingbasics.firewalls;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SimpleDateFormat format = new SimpleDateFormat("d M yyyy HH:mm:ss");
        Date today = new Date();
        String todayStr = format.format(today);
        System.out.println("Today is : " + todayStr);

        PrintWriter printWriter = response.getWriter();
        printWriter.println(todayStr);
        printWriter.close();
    }
}
