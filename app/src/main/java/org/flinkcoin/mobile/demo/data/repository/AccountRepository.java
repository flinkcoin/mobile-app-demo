package org.flinkcoin.mobile.demo.data.repository;

import com.flick.crypto.CryptoException;
import com.flick.crypto.mnemonic.Language;
import com.flick.crypto.mnemonic.MnemonicGenerator;

import org.flinkcoin.mobile.demo.data.db.dao.AccountDao;
import org.flinkcoin.mobile.demo.data.db.entity.Account;
import org.flinkcoin.mobile.demo.data.model.AccountData;
import org.flinkcoin.mobile.demo.data.model.GeneratedAccountData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class AccountRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountRepository.class);

    private static final int ENTROPY_LENGTH = 128;
    private static final int ACCOUNT_ID_ENTROPY_LENGTH = 64;
    private static final Language DEFAULT_LANGUAGE = Language.english;

    private final AccountDao accountDao;
    private final MnemonicGenerator mnemonicGenerator;

    private AccountData accountData;

    @Inject
    public AccountRepository(AccountDao accountDao) {
        this.accountDao = accountDao;
        this.mnemonicGenerator = new MnemonicGenerator();
        this.accountData = null;
    }

    public Maybe<Account> readAccount() {
        return accountDao.getAccount()
                .map(account -> {
                    this.accountData = new AccountData(account.getAccountId(), account.getSeed());
                    return account;
                });
    }

    public AccountData getAccountData() {
        return accountData;
    }

    public Completable insert(Account account) {
        return accountDao.insert(account);
    }

    public Single<GeneratedAccountData> generateAccount() {
        return Single.create(emitter -> emitter.onSuccess(createAccount()));
    }

    private GeneratedAccountData createAccount() {
        try {
            String accountIdPhrase = generateAccountIdMnemonicPhrase();
            byte[] accountId = mnemonicGenerator.getSeedFromWordlist(accountIdPhrase, "", Language.english, 128);

            String keySeedPhrase = generateKeySeedMnemonicPhrase();
            byte[] keySeed = mnemonicGenerator.getSeedFromWordlist(keySeedPhrase, "", Language.english);

            return new GeneratedAccountData(accountIdPhrase, accountId, keySeedPhrase, keySeed);

        } catch (CryptoException ex) {
            LOGGER.error("Can not generate account data", ex);
            throw new RuntimeException(ex);
        }
    }

    private String generateKeySeedMnemonicPhrase() throws CryptoException {
        return generateMnemonicPhrase(ENTROPY_LENGTH);
    }

    private String generateAccountIdMnemonicPhrase() throws CryptoException {
        return generateMnemonicPhrase(ACCOUNT_ID_ENTROPY_LENGTH);
    }

    private String generateMnemonicPhrase(int entropyLength) throws CryptoException {
        return mnemonicGenerator.getWordlist(entropyLength, AccountRepository.DEFAULT_LANGUAGE);
    }
}
