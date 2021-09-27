package org.flinkcoin.mobile.demo.data.model;

import org.flinkcoin.mobile.demo.data.ws.dto.MessageDtl;

import java.util.Objects;

public class PaymentRequest {

    private final long id;
    private final MessageDtl.PaymentRequest request;

    public PaymentRequest(long id, MessageDtl.PaymentRequest request) {
        this.id = id;
        this.request = request;
    }

    public long getId() {
        return id;
    }

    public MessageDtl.PaymentRequest getRequest() {
        return request;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentRequest that = (PaymentRequest) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
