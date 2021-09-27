package org.flinkcoin.mobile.demo.data.model;

import com.flick.crypto.KeyGenerator;
import com.flick.crypto.KeyPair;
import com.flick.helper.helpers.Base32Helper;

public class AccountData {

    private final byte[] accountId;
    private final String accountIdBase32;
    private final KeyPair keyPair;

    public AccountData(String accountIdBase32, byte[] keySeed) {
        this.accountIdBase32 = accountIdBase32;
        this.accountId = Base32Helper.decode(accountIdBase32);
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
}
