package org.flinkcoin.mobile.demo.data.model;

import org.flinkcoin.mobile.demo.data.service.dto.WalletBlock;

public class TransactionData {

    private final TransactionType type;
    private final String accountId;
    private final String maskedAccountId;
    private final String accountContact;
    private final String amount;
    private final String timestamp;
    private final String referenceCode;
    private final WalletBlock walletBlock;

    public TransactionData(TransactionType type, String accountId, String maskedAccountId, String accountContact, String amount, String timestamp, String referenceCode, WalletBlock walletBlock) {
        this.type = type;
        this.accountId = accountId;
        this.accountContact = accountContact;
        this.maskedAccountId = maskedAccountId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.referenceCode = referenceCode;
        this.walletBlock = walletBlock;
    }

    public TransactionType getType() {
        return type;
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

    public WalletBlock getWalletBlock() {
        return walletBlock;
    }
}
