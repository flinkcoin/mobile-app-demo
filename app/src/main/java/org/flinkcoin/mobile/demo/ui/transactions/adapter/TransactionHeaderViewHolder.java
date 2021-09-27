package org.flinkcoin.mobile.demo.ui.transactions.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.flinkcoin.mobile.demo.R;

public class TransactionHeaderViewHolder extends RecyclerView.ViewHolder {

    private final TextView transactionDate;

    public TransactionHeaderViewHolder(View view) {
        super(view);
        this.transactionDate = view.findViewById(R.id.text_transaction_date);
    }

    public TextView getTransactionDate() {
        return transactionDate;
    }
}
