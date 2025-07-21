import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import record.AuthInfo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterHandler implements HttpHandler {

    // username -> AuthInfo
    public static final Map<String, AuthInfo> authStore = new ConcurrentHashMap<>();
    private static final long TOKEN_EXPIRY_SECONDS = 3600; // 1 hour

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            // Handle registration logic here
            // For simplicity, we will just return a success message
            String username = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8).trim();
            if(username.isEmpty()){
                VirtualThreadHttpServer.sendResponse(exchange, 400, "Username cannot be empty");
                return;
            }
            String token = UUID.randomUUID().toString();
            AuthInfo auth = new AuthInfo(token, Instant.now());
            authStore.put(username, auth);

            VirtualThreadHttpServer.sendResponse(exchange, 200, "Registered " + username + "\nToken: " + token); // OK

        } else {
            String error = "Method not allowed";
            exchange.sendResponseHeaders(405, error.getBytes().length); // Method Not Allowed
            try (var os = exchange.getResponseBody()) {
                os.write(error.getBytes());
            }
        }
    }



    public static boolean isAuthorized(HttpExchange exchange, String username) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;

        String token = authHeader.substring("Bearer ".length()).trim();
        AuthInfo auth = authStore.get(username);

        if (auth == null || !auth.token().equals(token)) return false;

        Instant now = Instant.now();
        return now.isBefore(auth.issuedAt().plusSeconds(TOKEN_EXPIRY_SECONDS));
    }
}
