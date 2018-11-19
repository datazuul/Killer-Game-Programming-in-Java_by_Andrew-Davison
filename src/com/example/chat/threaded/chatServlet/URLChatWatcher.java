package com.example.chat.threaded.chatServlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class URLChatWatcher extends Thread {

    private static final int SLEEP_TIME = 2000;
    private static final String SERVER = "localhost";

    private URLChat urlChatClient;
    private String userName;
    private String cookieString = null;

    public URLChatWatcher(URLChat urlChatClient, String userName, String cookieString) {
        this.urlChatClient = urlChatClient;
        this.userName = userName;
        this.cookieString = cookieString;
    }

    public void run() {
        URL url;
        URLConnection urlConnection;
        BufferedReader bufferedReader;
        String line, response;
        StringBuffer stringBuffer;

        try {
            String readRequest = SERVER + "?cmd=read&name=" + URLEncoder.encode(userName, "UTF-8");
            while (true) {
                Thread.sleep(SLEEP_TIME);

                url = new URL(readRequest);
                urlConnection = url.openConnection();
                urlConnection.setRequestProperty("Cookie", cookieString);

                bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                stringBuffer = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                    if (!fromClient(line)) {
                        stringBuffer.append(line + "\n");
                    }
                }
                bufferedReader.close();

                response = stringBuffer.toString();
                if ((response != null) && !response.equals("\n")) {
                    urlChatClient.showMessage(response);
                }
            }
        } catch (Exception e) {
            urlChatClient.showMessage("Servlet Error : watching terminated\n");
            System.out.println(e);
        }
    }

    private boolean fromClient(String line) {
        if (line.startsWith("(" + userName)) {
            return true;
        }
        return false;
    }
}
