package com.bensiebert.dynoc.logging;

import java.time.format.DateTimeFormatter;

public class Logger {

    public static String ASCII_RED = "\u001B[31m";
    public static String ASCII_GREEN = "\u001B[32m";
    public static String ASCII_YELLOW = "\u001B[33m";
    public static String ASCII_BLUE = "\u001B[34m";
    public static String ASCII_RESET = "\u001B[0m";

    public static String withTS(String message) {
        return "[" + java.time.LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        ) + "] " + message;
    }

    public static String withStatus(String message, int errorLevel) {
        String status = "";
        switch (errorLevel) {
            case -1:
                status = "[" + ASCII_BLUE + "DEBUG" + ASCII_RESET + "] ";
                break;
            case 1:
                status = "[" + ASCII_YELLOW + "WARN" + ASCII_RESET + "] ";
                break;
            case 2:
                status = "[" + ASCII_RED + "ERROR" + ASCII_RESET + "] ";
                break;
            default:
                status = "[" + ASCII_GREEN + "INFO" + ASCII_RESET + "] ";
                break;
        }
        return status + message;
    }

    public static void log(String message) {
        System.out.println(withTS(withStatus(message, 0)));
    }

    public static void log(String message, int errorLevel) {
        System.out.println(withTS(withStatus(message, errorLevel)));
    }

    public static void debug(String message) {
        System.out.println(withTS(withStatus(message, -1)));
    }

    public static void info(String message) {
        System.out.println(withTS(withStatus(message, 0)));
    }

    public static void warn(String message) {
        System.out.println(withTS(withStatus(message, 1)));
    }

    public static void error(String message) {
        System.out.println(withTS(withStatus(message, 2)));
    }
}
