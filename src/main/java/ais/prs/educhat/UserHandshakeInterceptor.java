package ais.prs.educhat;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

// Prototype-only identity: trusts a ?userId= query param on the /ws handshake.
// When real auth is added, this is the single class that changes (verify a
// token/session instead of trusting the param) - handlers depend on Principal, not this.
public class UserHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String userId = UriComponentsBuilder.fromUri(request.getURI())
                .build().getQueryParams().getFirst("userId");

        if (userId == null || userId.isBlank()) {
            return false; // reject the handshake - no identity, no socket
        }

        attributes.put("userId", userId.strip());
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {}
}
