package org.flinkcoin.mobile.demo.ui.transactions.adapter;

import java.time.LocalDate;

public class TransactionHeaderItem extends TransactionListItem {

    private final LocalDate localDate;

    public TransactionHeaderItem(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }

}