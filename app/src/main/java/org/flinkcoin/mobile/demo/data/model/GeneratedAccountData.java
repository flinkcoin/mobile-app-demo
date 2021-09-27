package org.flinkcoin.mobile.demo.data.model;

public class GeneratedAccountData {

    private final String accountIdPhrase;
    private final byte[] accountId;
    private final String keySeedPhrase;
    private final byte[] keySeed;

    private final String[] words;

    public GeneratedAccountData(String accountIdPhrase, byte[] accountId, String keySeedPhrase, byte[] keySeed) {
        this.accountIdPhrase = accountIdPhrase;
        this.accountId = accountId;
        this.keySeedPhrase = keySeedPhrase;
        this.keySeed = keySeed;

        this.words = (accountIdPhrase + " " + keySeedPhrase).split("\\s+");
    }

    public String getAccountIdPhrase() {
        return accountIdPhrase;
    }

    public byte[] getAccountId() {
        return accountId;
    }

    public String getKeySeedPhrase() {
        return keySeedPhrase;
    }

    public byte[] getKeySeed() {
        return keySeed;
    }

    public String[] getAllWords() {
        return words;
    }
}


