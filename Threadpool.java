import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


import static util.LogSystemStats.logSystemStats;

public class Threadpool {

    public static void main(String[] args) throws IOException {
        System.out.println("HTTP Server is running...");

        HttpServer server = HttpServer.create(new InetSocketAddress(8084), 0);

        server.createContext("/send", new VirtualThreadHttpServer.SendMessageHandler());
        server.createContext("/messages", new VirtualThreadHttpServer.GetMessageHandler());
        server.createContext("/login", new LoginHandler());
        server.createContext("/logout", new LogoutHandler());
        server.createContext("/register", new RegisterHandler());

        // 🔁 Use traditional thread pool (pre-Java 21)
        int poolSize = Runtime.getRuntime().availableProcessors() * 2;
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        server.setExecutor(executor);
        server.start();

        System.out.println("Server started on port 8082 with thread pool size: " + poolSize);

        ScheduledExecutorService statsLogger = Executors.newSingleThreadScheduledExecutor();

        new Thread(() -> {
            while (true) {
                logSystemStats();
                try {
                    Thread.sleep(5000); // 5 seconds in milliseconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
        // every 5 seconds

    }



}
