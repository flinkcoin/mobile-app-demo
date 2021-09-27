package org.flinkcoin.mobile.demo.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "account")
public class Account {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "account_id")
    private final String accountId;

    @NonNull
    @ColumnInfo(name = "account_id_mnemonic_phrase")
    private final String accountIdMnemonicPhrase;

    @NonNull
    @ColumnInfo(name = "seed_mnemonic_phrase")
    private final String seedMnemonicPhrase;

    @ColumnInfo(name = "seed", typeAffinity = ColumnInfo.BLOB)
    private final byte[] seed;

    @NonNull
    @ColumnInfo(name = "pin")
    private final String pin;

    public Account(@NonNull String accountId, @NonNull String accountIdMnemonicPhrase, @NonNull String seedMnemonicPhrase, byte[] seed, @NonNull String pin) {
        this.accountId = accountId;
        this.accountIdMnemonicPhrase = accountIdMnemonicPhrase;
        this.seedMnemonicPhrase = seedMnemonicPhrase;
        this.seed = seed;
        this.pin = pin;
    }

    @NonNull
    public String getAccountId() {
        return accountId;
    }

    @NonNull
    public String getAccountIdMnemonicPhrase() {
        return accountIdMnemonicPhrase;
    }

    @NonNull
    public String getSeedMnemonicPhrase() {
        return seedMnemonicPhrase;
    }

    public byte[] getSeed() {
        return seed;
    }

    @NonNull
    public String getPin() {
        return pin;
    }
}
