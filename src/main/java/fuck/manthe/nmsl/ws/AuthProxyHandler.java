package fuck.manthe.nmsl.ws;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class AuthProxyHandler extends TextWebSocketHandler {

    private final WebSocketClient client = new StandardWebSocketClient();
    private WebSocketSession targetSession;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        // Connect to the target WebSocket server
        client.execute(new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                targetSession = session;
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                if (targetSession != null && targetSession.isOpen()) {
                    targetSession.sendMessage(message);
                }
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) {
                // Handle errors
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
                // Handle connection close
            }

            @Override
            public boolean supportsPartialMessages() {
                return false;
            }
        }, "wss://vape.gg");
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (targetSession != null && targetSession.isOpen()) {
            targetSession.sendMessage(message);
        }
    }
}