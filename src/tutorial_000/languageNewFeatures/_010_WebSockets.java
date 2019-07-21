package tutorial_000.languageNewFeatures;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket.Builder;
import java.net.http.WebSocket.Listener;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class _010_WebSockets {
	/*
	 * In this class, we will see a quick review on new java.net's websocket API. The WebSocket protocol is used in real-time web applications to provide 
	 * client-server communication with low message overhead.The examples won't necessary be launchable (with unexisting server's addresses for example). 
	 * The goal is only to get a rapid overview on this new API's syntaxes.
	 */

	public static void main(String[] args) {
		CompletableFuture<WebSocket> server_cf = HttpClient.newHttpClient()
														.newWebSocketBuilder()
														// In order to build a WebSocket client, we have to implement the WebSocket.Listener 
														// interface first. That's what WebSocketClient do.
														.buildAsync(URI.create("ws://localhost:4567/echo"),new WebSocketClient());

		WebSocket server = server_cf.join();
        server.sendText("Hello!", true);
        
        System.out.println("=====================================");
        
        /*
         * The next example will show how to use an HttpClient to create a WebSocket that connects to an URI, sends messages for one second, and then closes its 
         * output. The API also makes use of asynchronous calls that return CompletableFuture. 
         */
        ExecutorService executor = Executors.newFixedThreadPool(6);
        HttpClient httpClient = HttpClient.newBuilder().executor(executor).build();
        
        Builder webSocketBuilder = httpClient.newWebSocketBuilder();
        
        WebSocket webSocket = webSocketBuilder.buildAsync(URI.create("wss://echo.websocket.org"), new WebSocket.Listener() {

            @Override
            public void onOpen(WebSocket webSocket) {
                System.out.println("CONNECTED");
                webSocket.sendText("This is a message", true);
                Listener.super.onOpen(webSocket);
            }

            @Override
            public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            	System.out.println("onText received with data " + data);
                if(!webSocket.isOutputClosed()) {
                    webSocket.sendText("This is a message", true);
                }
                return Listener.super.onText(webSocket, data, last);
            }

            @Override
            public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            	System.out.println("Closed with status " + statusCode + ", reason: " + reason);
                executor.shutdown();
                return Listener.super.onClose(webSocket, statusCode, reason);
            }
        }).join();

        System.out.println("WebSocket created");
        
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

        webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "ok").thenRun(() -> System.out.println("Sent close"));
	}

}
