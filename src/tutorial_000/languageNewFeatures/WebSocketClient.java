package tutorial_000.languageNewFeatures;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;

public class WebSocketClient implements WebSocket.Listener {
	/*
	 * When implementing WebSocket.Listener, we can override several callback methods to get the desired behaviour.
	 */

    @Override
    public void onOpen(WebSocket webSocket) {
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
       return null;
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
       return null;
    }

    @Override
    public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
       return null;
    }

    @Override
    public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
       return null;
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
       return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
    }
}
