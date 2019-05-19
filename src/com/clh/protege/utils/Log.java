package com.clh.protege.utils;

public class Log {
    public static int loglevel = 1;
    public static void Debug(String log) {
        if (loglevel > 2) {
            System.out.println(log);
        }
    }
}
