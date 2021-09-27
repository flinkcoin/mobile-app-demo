package org.flinkcoin.mobile.demo.ui.transactions.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.flinkcoin.mobile.demo.R;

public class TransactionViewHolder extends RecyclerView.ViewHolder {

    private final ImageView transactionType;
    private final TextView transactionAccount;
    private final TextView transactionTimestamp;
    private final TextView transactionAmount;
    private final TextView transactionReferenceCode;

    public TransactionViewHolder(View view) {
        super(view);
        this.transactionType = view.findViewById(R.id.image_transaction_type);
        this.transactionAccount = view.findViewById(R.id.text_transaction_account);
        this.transactionTimestamp = view.findViewById(R.id.text_transaction_timestamp);
        this.transactionAmount = view.findViewById(R.id.text_transaction_amount);
        this.transactionReferenceCode = view.findViewById(R.id.text_transaction_reference_code);
    }

    public ImageView getTransactionType() {
        return transactionType;
    }

    public TextView getTransactionAccount() {
        return transactionAccount;
    }

    public TextView getTransactionTimestamp() {
        return transactionTimestamp;
    }

    public TextView getTransactionAmount() {
        return transactionAmount;
    }

    public TextView getTransactionReferenceCode() {
        return transactionReferenceCode;
    }
}
