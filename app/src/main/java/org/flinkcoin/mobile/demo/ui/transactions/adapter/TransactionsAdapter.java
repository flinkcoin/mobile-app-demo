package org.flinkcoin.mobile.demo.ui.transactions.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.flinkcoin.mobile.demo.R;
import org.flinkcoin.mobile.demo.data.model.TransactionData;
import org.flinkcoin.mobile.demo.data.model.TransactionType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class TransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final DateTimeFormatter TRANSACTION_GROUP_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Consumer<TransactionData> onClickConsumer;

    private List<TransactionListItem> itemsDataSet;

    public TransactionsAdapter(Consumer<TransactionData> onClickConsumer) {
        this.onClickConsumer = onClickConsumer;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TransactionListItem.TYPE_HEADER) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.transaction_row_header, viewGroup, false);
            return new TransactionHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.transaction_row_item, viewGroup, false);
            return new TransactionViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        TransactionListItem transactionListItem = itemsDataSet.get(position);
        int type = getItemViewType(position);

        if (type == TransactionListItem.TYPE_HEADER) {
            LocalDate localDate = ((TransactionHeaderItem) transactionListItem).getLocalDate();
            TransactionHeaderViewHolder transactionHeaderViewHolder = (TransactionHeaderViewHolder) viewHolder;

            transactionHeaderViewHolder.getTransactionDate().setText(localDate.format(TRANSACTION_GROUP_TIMESTAMP_FORMATTER));
        } else {
            TransactionData transactionData = ((TransactionDataItem) transactionListItem).getTransactionData();
            TransactionViewHolder transactionViewHolder = (TransactionViewHolder) viewHolder;

            if (TransactionType.SEND == transactionData.getType()) {
                transactionViewHolder.getTransactionType().setImageResource(R.drawable.ic_send_transaction);
            } else if (TransactionType.RECEIVE == transactionData.getType()) {
                transactionViewHolder.getTransactionType().setImageResource(R.drawable.ic_receive_transaction);
            } else {
                return;
            }

            transactionViewHolder.getTransactionAccount().setText(transactionData.getAccountContact() != null ? transactionData.getAccountContact() : transactionData.getMaskedAccountId());
            transactionViewHolder.getTransactionTimestamp().setText(transactionData.getTimestamp());
            transactionViewHolder.getTransactionAmount().setText(transactionData.getAmount());
            transactionViewHolder.getTransactionReferenceCode().setText(transactionData.getReferenceCode());

            if (onClickConsumer != null) {
                viewHolder.itemView.setOnClickListener(v -> onClickConsumer.accept(transactionData));
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        return itemsDataSet.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return itemsDataSet == null ? 0 : itemsDataSet.size();
    }

    public void setItems(List<TransactionListItem> items) {
        this.itemsDataSet = items;
        notifyDataSetChanged();
    }
}
