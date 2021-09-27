package org.flinkcoin.mobile.demo.data.ws.dto;

import com.flick.data.proto.api.Api;

public class MessageDtl {

    public static class InfoResponse {

        public Api.InfoRes.InfoType infoType;
        public String accountId;

        public BlockConfirm blockConfirm;
        public PaymentReceived paymentReceived;
        public PaymentRequest paymentRequest;

    }

    public static class BlockConfirm {

        public String blockHash;

        public BlockConfirm() {
        }

        public BlockConfirm(String blockHash) {
            this.blockHash = blockHash;
        }

    }

    public static class PaymentReceived {

        public String blockHash;

        public PaymentReceived() {
        }

        public PaymentReceived(String blockHash) {
            this.blockHash = blockHash;
        }

    }

    public static class PaymentRequest {

        public String accountId;
        public long amount;
        public String referenceCode;

        public PaymentRequest() {
        }

        public PaymentRequest(String accountId, long amount, String referenceCode) {
            this.accountId = accountId;
            this.amount = amount;
            this.referenceCode = referenceCode;
        }

    }
}