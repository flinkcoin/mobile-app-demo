package org.flinkcoin.mobile.demo.data.ws;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

public class WebSocketListener extends okhttp3.WebSocketListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketListener.class);

    private final LinkedBlockingQueue<String> messages;
    private final LinkedBlockingQueue<WebSocketConnectionEvent> connectionEvents;

    public WebSocketListener(LinkedBlockingQueue<String> messages, LinkedBlockingQueue<WebSocketConnectionEvent> connectionEvents) {
        this.messages = messages;
        this.connectionEvents = connectionEvents;
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        addConnectionEvent(new WebSocketConnectionEvent(webSocket, WebSocketConnectionState.OPEN));
        LOGGER.info("onOpen response: {}", response.toString());
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        addConnectionEvent(new WebSocketConnectionEvent(webSocket, WebSocketConnectionState.CLOSING));
        LOGGER.info("onClosing code: {} reason: {}", code, reason);
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        addConnectionEvent(new WebSocketConnectionEvent(webSocket, WebSocketConnectionState.CLOSED));
        LOGGER.info("onClosed code: {} reason: {}", code, reason);
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        addConnectionEvent(new WebSocketConnectionEvent(webSocket, WebSocketConnectionState.CANCELED));
        LOGGER.info("onFailure: {}", t.getMessage());
        if (null != response) {
            LOGGER.info("response code: {}", response.code());
            LOGGER.info("response msg: {}", response.message());
        }
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        addMessage(text);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        LOGGER.warn("Binary message received! Ignoring.");
    }

    private void addConnectionEvent(WebSocketConnectionEvent connectionEvent) {
        try {
            connectionEvents.put(connectionEvent);
        } catch (InterruptedException ex) {
            LOGGER.error("Interrupted while putting event to queue", ex);
            Thread.currentThread().interrupt();
        }
    }

    private void addMessage(String message) {
        try {
            messages.put(message);
        } catch (InterruptedException ex) {
            LOGGER.error("Interrupted while putting message to queue", ex);
            Thread.currentThread().interrupt();
        }
    }

}
