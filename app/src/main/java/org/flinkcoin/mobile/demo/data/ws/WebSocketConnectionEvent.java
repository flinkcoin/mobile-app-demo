package org.flinkcoin.mobile.demo.data.ws;

import org.flinkcoin.mobile.demo.util.DateHelper;

import java.time.ZonedDateTime;

import okhttp3.WebSocket;

public class WebSocketConnectionEvent {

    private final WebSocket webSocket;
    private final WebSocketConnectionState connectionState;
    private final ZonedDateTime eventTime;

    public WebSocketConnectionEvent(WebSocket webSocket, WebSocketConnectionState connectionState) {
        this.webSocket = webSocket;
        this.connectionState = connectionState;
        this.eventTime = DateHelper.now();
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public WebSocketConnectionState getConnectionState() {
        return connectionState;
    }

    public ZonedDateTime getEventTime() {
        return eventTime;
    }
}
