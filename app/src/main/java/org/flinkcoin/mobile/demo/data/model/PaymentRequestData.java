package org.flinkcoin.mobile.demo.data.model;

import org.flinkcoin.mobile.demo.data.ws.dto.MessageDtl;

public class PaymentRequestData {

    private final long id;
    private final String accountId;
    private final String maskedAccountId;
    private final String accountContact;
    private final String amount;
    private final String timestamp;
    private final String referenceCode;
    private final MessageDtl.PaymentRequest request;

    public PaymentRequestData(long id, String accountId, String maskedAccountId, String accountContact, String amount, String timestamp, String referenceCode, MessageDtl.PaymentRequest request) {
        this.id = id;
        this.accountId = accountId;
        this.maskedAccountId = maskedAccountId;
        this.accountContact = accountContact;
        this.amount = amount;
        this.timestamp = timestamp;
        this.referenceCode = referenceCode;
        this.request = request;
    }

    public long getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getMaskedAccountId() {
        return maskedAccountId;
    }

    public String getAccountContact() {
        return accountContact;
    }

    public String getAmount() {
        return amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public MessageDtl.PaymentRequest getRequest() {
        return request;
    }

    public MessageDtl.PaymentRequest getWalletBlock() {
        return request;
    }
}
