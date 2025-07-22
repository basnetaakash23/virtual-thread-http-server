import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import record.AuthInfo;
import record.Message;
import util.ServerUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static util.LogSystemStats.logSystemStats;
import static util.ServerUtil.parseQueryParams;

public class VirtualThreadHttpServer {

    private static final List<Message> messages = new CopyOnWriteArrayList<>();
    private static final Map<String, List<Message>> inboxes = new ConcurrentHashMap<>();



    public static void main(String[] args) throws IOException {
            // This is a placeholder for the main method of your HTTP server.
            // You can implement your server logic here.
            System.out.println("HTTP Server is running...");
            HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);
            server.createContext("/send", new SendMessageHandler());
            server.createContext("/messages", new GetMessageHandler());
            server.createContext("/login", new LoginHandler());
            server.createContext("/logout", new LogoutHandler());
            server.createContext("/register", new RegisterHandler());
            server.createContext("/ping", new PingPongHandler());


        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
            server.setExecutor(executor);
            server.start();
            System.out.println("Server started on port 8082");

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


    }

    static class SendMessageHandler implements HttpHandler{

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // Read the message from the request body
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = parseQueryParams(query);

                String to = params.get("to");
                String from = params.get("from");
                if(to == null || from == null) {
                    String error = "Missing 'to' or 'from' parameter in the request.";
                    exchange.sendResponseHeaders(400, error.getBytes().length); // Bad Request
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(error.getBytes());
                    }
                    return;
                }
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Message message = new Message(from, body, Instant.now());
                inboxes.computeIfAbsent(to, k -> new CopyOnWriteArrayList<>()).add(message);
                sendResponse(exchange, 200, "Message sent to "+to); // OK

            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }


    }

    static class GetMessageHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {

                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = parseQueryParams(query);

                String user = params.get("user");
                if(user == null){
                    sendResponse(exchange, 400, "Missing 'user' parameter");
                    return;
                }

                List<Message> userMessages = inboxes.getOrDefault(user, List.of());
                StringBuilder responseBuilder = new StringBuilder();
                userMessages.forEach(message -> {
                    responseBuilder.append(message.toString()).append("\n");
                });
                sendResponse(exchange, 200, responseBuilder.toString());

            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }



    public static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }




}
