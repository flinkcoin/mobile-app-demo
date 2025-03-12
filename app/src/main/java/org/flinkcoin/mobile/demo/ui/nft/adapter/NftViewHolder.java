package org.flinkcoin.mobile.demo.ui.nft.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.flinkcoin.mobile.demo.R;

public class NftViewHolder extends RecyclerView.ViewHolder {

    private final ImageView transactionType;
    private final TextView nftCode;
    private final TextView accountCode;
    private final ImageView nftPreview;

    public NftViewHolder(View view) {
        super(view);
        this.transactionType = view.findViewById(R.id.image_transaction_type);
        this.nftCode = view.findViewById(R.id.text_nft_code);
        this.accountCode = view.findViewById(R.id.text_account_code);
        this.nftPreview = view.findViewById(R.id.image_nft_preview);
    }

    public ImageView getTransactionType() {
        return transactionType;
    }

    public TextView getNftCode() {
        return nftCode;
    }

    public TextView getAccountCode() {
        return accountCode;
    }

    public ImageView getNftPreview() {
        return nftPreview;
    }
}
