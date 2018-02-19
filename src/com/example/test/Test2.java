package com.example.test;

public class Test2 {
    public static void main(String[] args) {
        long count1 = System.nanoTime();
        long count2 = System.nanoTime();
        long diff = (count2 - count1);

        System.out.println("count1 : " + count1);
        System.out.println("count2 : " + count2);
        System.out.println("diff : " + diff);
    }
}
