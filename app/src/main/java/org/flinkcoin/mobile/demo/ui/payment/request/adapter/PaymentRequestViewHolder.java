package org.flinkcoin.mobile.demo.ui.payment.request.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.flinkcoin.mobile.demo.R;

public class PaymentRequestViewHolder extends RecyclerView.ViewHolder {

    private final TextView paymentRequestAccount;
    private final TextView paymentRequestTimestamp;
    private final TextView paymentRequestAmount;
    private final TextView paymentRequestReferenceCode;

    public PaymentRequestViewHolder(View view) {
        super(view);
        this.paymentRequestAccount = view.findViewById(R.id.text_payment_request_account);
        this.paymentRequestTimestamp = view.findViewById(R.id.text_payment_request_timestamp);
        this.paymentRequestAmount = view.findViewById(R.id.text_payment_request_amount);
        this.paymentRequestReferenceCode = view.findViewById(R.id.text_payment_request_reference_code);
    }

    public TextView getPaymentRequestAccount() {
        return paymentRequestAccount;
    }

    public TextView getPaymentRequestTimestamp() {
        return paymentRequestTimestamp;
    }

    public TextView getPaymentRequestAmount() {
        return paymentRequestAmount;
    }

    public TextView getPaymentRequestReferenceCode() {
        return paymentRequestReferenceCode;
    }
}
