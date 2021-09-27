package org.flinkcoin.mobile.demo.ui.transactions.adapter;

public abstract class TransactionListItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_DATA = 1;

    abstract public int getType();
} 