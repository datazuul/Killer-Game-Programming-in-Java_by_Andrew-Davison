package com.example.test;

public class Test4 {
    public static boolean isAvailable(String className) {
        boolean isFound = false;

        try {
            Class.forName(className, false, null);
            isFound = true;
        } catch (ClassNotFoundException e) {
            isFound = false;
        }
        return isFound;
    }

    public static boolean isJava3dAvailable() {
        return isAvailable("javax.media.j3d.View");
    }

    public static void main(String[] args) {
        System.out.println("Java3d " + (Test4.isJava3dAvailable() ? "present" : "missing"));
    }
}
