package org.flinkcoin.mobile.demo.ui.payment.request.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import org.flinkcoin.mobile.demo.R;
import org.flinkcoin.mobile.demo.data.model.PaymentRequestData;

import java.util.List;
import java.util.function.Consumer;

public class PaymentRequestsAdapter extends RecyclerView.Adapter<PaymentRequestViewHolder> {

    private final Consumer<PaymentRequestData> onClickConsumer;

    private List<PaymentRequestData> paymentRequestsDataSet;

    public PaymentRequestsAdapter(Consumer<PaymentRequestData> onClickConsumer) {
        this.onClickConsumer = onClickConsumer;
    }

    @NonNull
    @Override
    public PaymentRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.payment_request_row, viewGroup, false);
        return new PaymentRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentRequestViewHolder paymentRequestViewHolder, int position) {
        PaymentRequestData paymentRequest = paymentRequestsDataSet.get(position);

        paymentRequestViewHolder.getPaymentRequestAccount().setText(paymentRequest.getAccountContact() != null ? paymentRequest.getAccountContact() : paymentRequest.getMaskedAccountId());
        paymentRequestViewHolder.getPaymentRequestTimestamp().setText(paymentRequest.getTimestamp());
        paymentRequestViewHolder.getPaymentRequestAmount().setText(paymentRequest.getAmount());
        paymentRequestViewHolder.getPaymentRequestReferenceCode().setText(paymentRequest.getReferenceCode());

        paymentRequestViewHolder.itemView.setOnClickListener(v -> onClickConsumer.accept(paymentRequest));
    }

    @Override
    public int getItemCount() {
        return paymentRequestsDataSet == null ? 0 : paymentRequestsDataSet.size();
    }

    public void setPaymentRequests(List<PaymentRequestData> paymentRequests) {
        this.paymentRequestsDataSet = paymentRequests;
        notifyDataSetChanged();
    }
}
