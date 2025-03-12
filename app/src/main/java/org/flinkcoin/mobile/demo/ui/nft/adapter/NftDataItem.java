package org.flinkcoin.mobile.demo.ui.nft.adapter;

import org.flinkcoin.mobile.demo.data.model.NftData;

public class NftDataItem {

    private final NftData nftData;

    public NftDataItem(NftData nftData) {
        this.nftData = nftData;
    }

    public NftData getNftData() {
        return nftData;
    }

}