package util;

import java.lang.management.ManagementFactory;

public class LogSystemStats {
    public static void logSystemStats() {
        int activeThreads = Thread.activeCount();
        long heapUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        int totalThreads = ManagementFactory.getThreadMXBean().getThreadCount();

        System.out.printf("[STATS] Active Java Threads: %d, Total Threads: %d, Heap Used: %.2f MB%n",
                activeThreads, totalThreads, heapUsed / (1024.0 * 1024.0));
    }

}
