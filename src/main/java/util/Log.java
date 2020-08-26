package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    public Log() {
    }

    public static void log(String data) {
        data = String.format("[*] Time:%s ThreadId:%s Message: %s", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()), Thread.currentThread().getId(), data);
        System.out.println(data);
    }

    public static void error(Exception exception) {
        String stackTrace = "";
        StackTraceElement[] elements = exception.getStackTrace();

        for (StackTraceElement element : elements) {
            stackTrace = stackTrace + element + "->";
        }

        stackTrace = stackTrace.substring(0, stackTrace.length() - 2);
        String data = String.format("[!] Time:%s ThreadId:%s Message:%s stackTrace: %s", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()), Thread.currentThread().getId(), exception.getMessage(), stackTrace);
        System.out.println(data);
    }

    public static void error(String data) {
        data = String.format("[!] Time:%s ThreadId:%s Message: %s ", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()), Thread.currentThread().getId(), data);
        System.out.println(data);
    }
}
