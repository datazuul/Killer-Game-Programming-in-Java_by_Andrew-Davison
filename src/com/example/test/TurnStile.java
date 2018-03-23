/*
package com.example.test;

public class TurnStile {

    private static final int LOCKED = 0;
    private static final int UNLOCKED = 1;

    private static final int COIN = 2;
    private static final int PASS = 3;

    public static void main(String[] args) {
        int currentState = LOCKED;
        int event;
        while (true) {
            event = */
/**get the next event*//*
;
            currentState = makeTransition(currentState, event);
        }
    }

    private static int makeTransition(int state, int event) {
        if ((state == LOCKED) && (event == COIN)) {
            unlock();
            return UNLOCKED;
        } else if ((state == LOCKED) && (event == PASS)) {
            alarm();
            return LOCKED;
        } else if ((state == UNLOCKED) && (event == COIN)) {
            thanks();
            return UNLOCKED;
        } else if ((state == UNLOCKED) && (event == PASS)) {
            lock();
            return LOCKED;
        } else {
            System.out.println("Unknown state event");
            System.exit(0);
        }
    }
}
*/
