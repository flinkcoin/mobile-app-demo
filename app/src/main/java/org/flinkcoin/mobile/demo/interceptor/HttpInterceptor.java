package org.flinkcoin.mobile.demo.interceptor;

import org.flinkcoin.mobile.demo.util.MoshiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class HttpInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpInterceptor.class);

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        logRequest(request);

        long t1 = System.nanoTime();
        try {
            Response response = chain.proceed(request);
            logResponse(response, (System.nanoTime() - t1) / 1e6d);
            return response;
        } catch (IOException ex) {
            logErrorResponse(request, ex, (System.nanoTime() - t1) / 1e6d);
            throw ex;
        }
    }

    private void logRequest(Request request) {

        LOGGER.info(String.format("Sending request %s %s", request.method(), request.url()));


        if (null != request.body() && MEDIA_TYPE_JSON.equals(request.body().contentType())) {
            LOGGER.info(String.format("Request body: \n%s", jsonBodyToString(request.body())));
        }
    }

    private void logResponse(Response response, double time) {

        LOGGER.info(String.format("Received response (%d) for request %s %s in %.1fms", response.code(), response.request().method(), response.request().url(), time));


        if (null != response.body() && MEDIA_TYPE_JSON.equals(response.body().contentType())) {
            LOGGER.info("Response body: {}", jsonBodyToString(response.body()));
        }
    }

    private void logErrorResponse(Request request, IOException ex, double time) {

        LOGGER.error(String.format("Received error for request %s %s in %.1fms: '%s'", request.method(), request.url(), time, getMessage(ex)));

    }

    private String getMessage(Throwable ex) {
        if (null == ex) {
            return null;
        }
        return ex.getMessage();
    }

    private String jsonBodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null) {
                copy.writeTo(buffer);
            } else {
                return null;
            }
            String string = buffer.readUtf8();
            return MoshiHelper.writeStringAsFormattedString(string);
        } catch (final IOException e) {
            LOGGER.error("Can not convert request body to string", e);
            return null;
        }
    }

    private String jsonBodyToString(final ResponseBody response) {
        try {
            final ResponseBody copy = response;
            BufferedSource source = response.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();

            String string = buffer.clone().readString(StandardCharsets.UTF_8);
            return MoshiHelper.writeStringAsFormattedString(string);
        } catch (final IOException e) {
            LOGGER.error("Can not convert response body to string", e);
            return null;
        }
    }

}
