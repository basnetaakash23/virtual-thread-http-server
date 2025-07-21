import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            // Handle login logic here
            // For simplicity, we will just return a success message
            String response = "Login successful!";
            exchange.sendResponseHeaders(200, response.getBytes().length); // OK
            try (var os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } else {
            String error = "Method not allowed";
            exchange.sendResponseHeaders(405, error.getBytes().length); // Method Not Allowed
            try (var os = exchange.getResponseBody()) {
                os.write(error.getBytes());
            }
        }
    }
}
