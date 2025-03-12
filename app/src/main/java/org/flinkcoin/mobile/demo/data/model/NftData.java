package org.flinkcoin.mobile.demo.data.model;

import android.graphics.Bitmap;

import org.flinkcoin.mobile.demo.data.service.dto.WalletBlock;

public class NftData {

    private final String accountCode;
    private final String nftCode;
    private final String nftCodeBase32;
    private final String maskedNftCode;
    private final Bitmap nftPreview;
    private final WalletBlock walletBlock;

    public NftData(String accountCode, String nftCode, String nftCodeBase32, String maskedNftCode, Bitmap nftPreview, WalletBlock walletBlock) {
        this.accountCode = accountCode;
        this.nftCode = nftCode;
        this.nftCodeBase32 = nftCodeBase32;
        this.maskedNftCode = maskedNftCode;
        this.nftPreview = nftPreview;
        this.walletBlock = walletBlock;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public String getNftCode() {
        return nftCode;
    }

    public String getNftCodeBase32() {
        return nftCodeBase32;
    }

    public String getMaskedNftCode() {
        return maskedNftCode;
    }

    public Bitmap getNftPreview() {
        return nftPreview;
    }

    public WalletBlock getWalletBlock() {
        return walletBlock;
    }

}
