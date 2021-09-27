package org.flinkcoin.mobile.demo.data.ws;

import org.flinkcoin.mobile.demo.util.DateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

@Singleton
public class WebSocketConnectionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketConnectionManager.class);

    private static final long CONNECTION_TIMEOUT = 15;
    private static final TimeUnit CONNECTION_TIMEOUT_UNIT = TimeUnit.SECONDS;

    private static final int CONNECTION_CLOSED_WAIT = 2;
    private static final TimeUnit CONNECTION_CLOSED_WAIT_UNIT = TimeUnit.SECONDS;

    private static final int MESSAGE_READ_WAIT = 50;
    private static final TimeUnit MESSAGE_READ_WAIT_UNIT = TimeUnit.MILLISECONDS;

    private final LinkedBlockingQueue<String> messages;
    private final LinkedBlockingQueue<WebSocketConnectionEvent> connectionEvents;
    private final WebSocketListener webSocketListener;
    private final OkHttpClient okHttpClient;

    private boolean connectionAlive;
    private WebSocket webSocket;

    @Inject
    public WebSocketConnectionManager() {
        this.messages = new LinkedBlockingQueue<>();
        this.connectionEvents = new LinkedBlockingQueue<>();
        this.webSocketListener = new WebSocketListener(messages, connectionEvents);
        this.okHttpClient = new OkHttpClient.Builder()
                .readTimeout(2L, TimeUnit.MINUTES)
                .writeTimeout(2L, TimeUnit.MINUTES)
                .connectTimeout(CONNECTION_TIMEOUT, CONNECTION_TIMEOUT_UNIT)
                .pingInterval(30L, TimeUnit.SECONDS)
                .build();

        this.connectionAlive = false;
    }

    public WebSocketConnectionState connect(String wsUrl) throws InterruptedException {
        readConnectionEvents();
        if (isConnectionAlive()) {
            return WebSocketConnectionState.OPEN;
        }

        LOGGER.debug("Creating new WebSocket connection: " + wsUrl);
        Request request = new Request.Builder()
                .url(wsUrl)
                .build();

        this.connectionEvents.clear();
        this.webSocket = okHttpClient.newWebSocket(request, webSocketListener);

        WebSocketConnectionState connectionState = waitConnectionState(WebSocketConnectionState.OPEN, CONNECTION_TIMEOUT, CONNECTION_TIMEOUT_UNIT);
        setConnectionStatus(WebSocketConnectionState.OPEN == connectionState);
        if (!isConnectionAlive()) {
            this.webSocket.cancel();
            LOGGER.warn("WebSocket connection timeout on open,cancel socket!");
        }

        return connectionState;
    }

    public void disconnect() throws InterruptedException {
        if (this.webSocket == null) {
            return;
        }
        this.webSocket.close(1000, "Finished.");
        if (!isConnectionAlive() || WebSocketConnectionState.CLOSED != waitConnectionState(WebSocketConnectionState.CLOSED, CONNECTION_CLOSED_WAIT, CONNECTION_CLOSED_WAIT_UNIT)) {
            this.webSocket.cancel();
        }
        setConnectionStatus(false);
    }

    private void readConnectionEvents() {
        List<WebSocketConnectionEvent> events = new ArrayList<>();
        connectionEvents.drainTo(events);

        for (WebSocketConnectionEvent event : events) {

            if (!Objects.equals(this.webSocket, event.getWebSocket())) {
                LOGGER.debug("Got drain event for different session!");
                continue;
            }

            switch (event.getConnectionState()) {
                case CLOSED:
                case CANCELED:
                    LOGGER.debug("WebSocket connection closed because of failure!");
                    this.webSocket.close(1000, "Connection close because of failure!");
                    setConnectionStatus(false);
                    break;
                case CLOSING:
                    LOGGER.debug("WebSocket connection closed from server!");
                    this.webSocket.close(1000, "Connection close from server!");
                    setConnectionStatus(false);
                    break;
            }
        }
    }

    private WebSocketConnectionState waitConnectionState(WebSocketConnectionState connectionState, long timeout, TimeUnit unit) throws InterruptedException {
        ZonedDateTime now = DateHelper.now();
        do {
            WebSocketConnectionEvent connectionEvent = connectionEvents.poll(timeout, unit);

            if (connectionEvent == null) {
                break;
            }

            if (!Objects.equals(this.webSocket, connectionEvent.getWebSocket())) {
                LOGGER.debug("Got event for different session!");
                continue;
            }

            if (connectionState == connectionEvent.getConnectionState()) {
                LOGGER.debug("Wait on {} event end.", connectionState);
                return connectionState;
            }

            if (WebSocketConnectionState.CANCELED == connectionEvent.getConnectionState()) {
                LOGGER.error("Error while waiting {} event!", connectionState);
                return WebSocketConnectionState.CANCELED;
            }
        } while (now.plusSeconds(timeout).isAfter(DateHelper.now()));
        return WebSocketConnectionState.TIMEOUT;
    }

    public String getMessage() throws InterruptedException {
        return messages.poll(MESSAGE_READ_WAIT, MESSAGE_READ_WAIT_UNIT);
    }

    private void setConnectionStatus(boolean connected) {
        this.connectionAlive = connected;
    }

    public boolean isConnectionAlive() {
        readConnectionEvents();
        return connectionAlive;
    }

}
