package org.flinkcoin.mobile.demo.ui.nft.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.flinkcoin.mobile.demo.R;
import org.flinkcoin.mobile.demo.data.model.NftData;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class NftsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Consumer<NftData> onClickConsumer;

    private List<NftDataItem> itemsDataSet;

    public NftsAdapter(Consumer<NftData> onClickConsumer) {
        this.onClickConsumer = onClickConsumer;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.nft_row_item, viewGroup, false);
        return new NftViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        NftDataItem nftDataItem = itemsDataSet.get(position);

        NftData nftData = nftDataItem.getNftData();
        NftViewHolder nftViewHolder = (NftViewHolder) viewHolder;

        nftViewHolder.getTransactionType().setImageResource(R.drawable.ic_receive_transaction);
        nftViewHolder.getNftCode().setText(nftData.getMaskedNftCode());

        if(Objects.isNull(nftData.getAccountCode())|| nftData.getAccountCode().isEmpty()){
            nftViewHolder.getAccountCode().setVisibility(View.GONE);
        } else {
            nftViewHolder.getAccountCode().setText(nftData.getAccountCode());
        }

        Bitmap nftPreview = nftData.getNftPreview();
        if (Objects.nonNull(nftPreview)) {
            nftViewHolder.getNftPreview().setImageBitmap(nftPreview);
        } else {
            nftViewHolder.getNftPreview().setImageResource(R.drawable.ic_question_mark);
        }

        if (onClickConsumer != null) {
            viewHolder.itemView.setOnClickListener(v -> onClickConsumer.accept(nftData));
        }
    }

    @Override
    public int getItemCount() {
        return itemsDataSet == null ? 0 : itemsDataSet.size();
    }

    public void setItems(List<NftDataItem> items) {
        this.itemsDataSet = items;
        notifyDataSetChanged();
    }
}
