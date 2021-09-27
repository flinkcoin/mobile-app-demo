package org.flinkcoin.mobile.demo.data.ws;

import com.google.common.base.Stopwatch;

import org.flinkcoin.mobile.demo.BuildConfig;
import org.flinkcoin.mobile.demo.data.ws.dto.MessageDtl;
import org.flinkcoin.mobile.demo.util.DateHelper;
import org.flinkcoin.mobile.demo.util.MoshiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

@Singleton
public class WebSocketHandler implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandler.class);

    private static final long MIN_RETRY_CONNECTION = TimeUnit.MILLISECONDS.toMillis(500);
    private static final long MAX_RETRY_CONNECTION = TimeUnit.SECONDS.toMillis(10);
    private static final long RETRY_CONNECTION_FACTOR = 2;
    private static final int MIN_RETRY_COUNT = 3;

    private final WebSocketConnectionManager webSocketConnectionManager;
    private final PublishSubject<MessageDtl.InfoResponse> publishSubject = PublishSubject.create();

    private final Stopwatch waitConnectionSw;

    private final AtomicBoolean running;

    private String accountId;

    private long retryConnectionStep;
    private int retryConnectionCount;

    @Inject
    public WebSocketHandler(WebSocketConnectionManager webSocketConnectionManager) {
        this.webSocketConnectionManager = webSocketConnectionManager;

        this.waitConnectionSw = Stopwatch.createUnstarted();
        this.retryConnectionStep = MIN_RETRY_CONNECTION;

        this.running = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        try {
            runLoop();
        } catch (InterruptedException ex) {
            LOGGER.error("Thread interrupted", ex);
            disconnect();
            Thread.currentThread().interrupt();
        }
        throw new RuntimeException("WebSocketHandler stopped working");
    }

    private void disconnect() {
        resetConnectionStep();
        try {
            webSocketConnectionManager.disconnect();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void runLoop() throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {

            if (!waitConnection()) {
                continue;
            }

            String message = webSocketConnectionManager.getMessage();
            if (null == message) {
                continue;
            }

            MessageDtl.InfoResponse infoResponse = MoshiHelper.readValueFromString(message, MessageDtl.InfoResponse.class);
            if (null == infoResponse) {
                continue;
            }

            handleMessage(infoResponse);
        }
    }

    private void handleMessage(MessageDtl.InfoResponse infoResponse) {
        LOGGER.info("Received WebSocket message {}", MoshiHelper.writeValueAsFormattedString(infoResponse));
        publishSubject.onNext(infoResponse);
    }

    private boolean waitConnection() throws InterruptedException {
        if (webSocketConnectionManager.isConnectionAlive()) {
            return true;
        }

        if (!waitConnectionSw.isRunning()) {
            waitConnectionSw.reset();
            waitConnectionSw.start();
        }

        WebSocketConnectionState connectionState = webSocketConnectionManager.connect("ws://" + BuildConfig.WALLET_PROXY_HOST + ":" + BuildConfig.WALLET_PROXY_PORT + "/message/" + accountId);

        if (WebSocketConnectionState.TIMEOUT == connectionState) {
            resetConnectionStep();
        }

        if (WebSocketConnectionState.OPEN != connectionState) {
            LOGGER.error("Could not get device connection!");
            Thread.sleep(retryConnectionStep);

            if (WebSocketConnectionState.CANCELED == connectionState) {
                increaseConnectionStep();
            }

            return false;
        }

        waitConnectionSw.stop();
        LOGGER.debug("Connected to server after {}", DateHelper.parseMilliSeconds(waitConnectionSw.elapsed(TimeUnit.MILLISECONDS)));

        resetConnectionStep();
        return true;
    }

    private void resetConnectionStep() {
        this.retryConnectionCount = 0;
        this.retryConnectionStep = MIN_RETRY_CONNECTION;
    }

    private void increaseConnectionStep() {
        if (retryConnectionCount++ < MIN_RETRY_COUNT) {
            return;
        }
        this.retryConnectionStep = retryConnectionStep * RETRY_CONNECTION_FACTOR > MAX_RETRY_CONNECTION
                ? MAX_RETRY_CONNECTION : retryConnectionStep * RETRY_CONNECTION_FACTOR;
    }

    public Observable<MessageDtl.InfoResponse> start(String accountId) {
        if (running.compareAndSet(false, true)) {
            this.accountId = accountId;
            Thread thread = new Thread(this);
            thread.setName("WebSocketHandler");
            thread.start();
        }
        return publishSubject.subscribeOn(Schedulers.io());
    }

    public Observable<MessageDtl.InfoResponse> getEvents() {
        return publishSubject.subscribeOn(Schedulers.io());
    }

}
