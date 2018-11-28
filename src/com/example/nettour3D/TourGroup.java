package com.example.nettour3D;

import java.io.PrintWriter;
import java.util.ArrayList;

public class TourGroup {

    private ArrayList arrayList;

    public TourGroup() {
        arrayList = new ArrayList();
    }

    synchronized public void addPerson(String clientAddress, int port, PrintWriter printWriter) {
        arrayList.add(new TouristInfo(clientAddress, port, printWriter));
    }

    synchronized public void deletePerson(String clientAddress, int port, String byeMessage) {
        TouristInfo touristInfo;
        for (int i = 0; i < arrayList.size(); i++) {
            touristInfo = (TouristInfo) arrayList.get(i);
            if (touristInfo.matches(clientAddress, port)) {
                arrayList.remove(i);
                broadcast(clientAddress, port, byeMessage);
                break;
            }
        }
    }

    synchronized public void broadcast(String clientAddress, int port, String message) {
        TouristInfo touristInfo;
        for (int i = 0; i < arrayList.size(); i++) {
            touristInfo = (TouristInfo) arrayList.get(i);
            if (!touristInfo.matches(clientAddress, port)) {
                touristInfo.sendMessage(message);
            }
        }
    }

    synchronized public void sendTo(String clientAddress, int port, String message) {
        TouristInfo touristInfo;
        for (int i = 0; i < arrayList.size(); i++) {
            touristInfo = (TouristInfo) arrayList.get(i);
            if (touristInfo.matches(clientAddress, port)) {
                touristInfo.sendMessage(message);
                break;
            }
        }
    }
}
