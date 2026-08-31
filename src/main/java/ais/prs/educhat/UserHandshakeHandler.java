package ais.prs.educhat;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

// Turns the userId stashed by UserHandshakeInterceptor into the STOMP session Principal.
// Required for convertAndSendToUser(...) and /user/queue/** subscriptions to route.
public class UserHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        String userId = (String) attributes.get("userId");
        return () -> userId; // Principal::getName returns the userId
    }
}
