package org.flinkcoin.mobile.demo.data.model;

import org.flinkcoin.mobile.demo.data.service.dto.WalletBlock;

public class NftData {

    private final String accountCode;
    private final String nftCode;
    private final String nftCodeBase32;
    private final String maskedNftCode;
    private final WalletBlock walletBlock;

    public NftData(String accountCode, String nftCode, String nftCodeBase32, String maskedNftCode, WalletBlock walletBlock) {
        this.accountCode = accountCode;
        this.nftCode = nftCode;
        this.nftCodeBase32 = nftCodeBase32;
        this.maskedNftCode = maskedNftCode;
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

    public WalletBlock getWalletBlock() {
        return walletBlock;
    }

}
