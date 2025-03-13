package org.flinkcoin.mobile.demo.data.model;

import org.flinkcoin.crypto.KeyGenerator;
import org.flinkcoin.crypto.KeyPair;
import org.flinkcoin.helper.helpers.Base32Helper;

public class AccountData {

    private final byte[] accountId;
    private final String accountIdBase32;
    private final String accountCode;
    private final KeyPair keyPair;

    public AccountData(String accountIdBase32, String accountCode, byte[] keySeed) {
        this.accountIdBase32 = accountIdBase32;
        this.accountId = Base32Helper.decode(accountIdBase32);
        this.accountCode = accountCode;
        this.keyPair = KeyGenerator.getKeyPairFromSeed(keySeed);
    }

    public byte[] getAccountId() {
        return accountId;
    }

    public String getAccountIdBase32() {
        return accountIdBase32;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public String getAccountCode() {
        return accountCode;
    }
}
