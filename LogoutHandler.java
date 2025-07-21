import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

import static util.ServerUtil.parseQueryParams;

public class LogoutHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQueryParams(query);
        String username = params.get("user");

        if (username == null || !RegisterHandler.isAuthorized(exchange, username)) {
            VirtualThreadHttpServer.sendResponse(exchange, 401, "Unauthorized");
            return;
        }

        RegisterHandler.authStore.remove(username);
        VirtualThreadHttpServer.sendResponse(exchange, 200, "Logged out " + username);

    }
}
