package org.flinkcoin.mobile.demo.ui.transactions.adapter;

import org.flinkcoin.mobile.demo.data.model.TransactionData;

public class TransactionDataItem extends TransactionListItem {

    private final TransactionData transactionData;

    public TransactionDataItem(TransactionData transactionData) {
        this.transactionData = transactionData;
    }

    public TransactionData getTransactionData() {
        return transactionData;
    }

    @Override
    public int getType() {
        return TYPE_DATA;
    }

}