package com.example.test;

import sun.misc.Perf;

public class Test1 {
    public static void main(String[] args) {
        Perf perf = Perf.getPerf();
        long countFreq = perf.highResFrequency();
        long count1 = perf.highResCounter();
        long count2 = perf.highResCounter();
        long diff = (count2 - count1) * 1000000000L / countFreq;

        System.out.println("perf : " + perf);
        System.out.println("countFreq : " + countFreq);
        System.out.println("count1 : " + count1);
        System.out.println("count2 ; " + count2);
        System.out.println("diff : " + diff);
    }
}