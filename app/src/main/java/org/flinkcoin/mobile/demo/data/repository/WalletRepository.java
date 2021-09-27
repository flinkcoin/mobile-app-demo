package org.flinkcoin.mobile.demo.data.repository;

import com.flick.crypto.CryptoException;
import com.flick.crypto.KeyGenerator;
import com.flick.crypto.KeyPair;
import com.flick.data.proto.api.Api;
import com.flick.data.proto.common.Common;
import com.flick.helper.helpers.Base32Helper;

import org.flinkcoin.mobile.demo.data.db.entity.Account;
import org.flinkcoin.mobile.demo.data.model.GeneratedAccountData;
import org.flinkcoin.mobile.demo.data.service.MacgyverService;
import org.flinkcoin.mobile.demo.data.service.WalletService;
import org.flinkcoin.mobile.demo.data.service.dto.WalletBlock;
import org.flinkcoin.mobile.demo.data.service.dto.WalletTransaction;
import org.flinkcoin.mobile.demo.data.ws.WebSocketHandler;
import org.flinkcoin.mobile.demo.data.ws.dto.MessageDtl;
import org.flinkcoin.mobile.demo.util.BlockHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

@Singleton
public class WalletRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletRepository.class);

    private final WalletService walletService;
    private final MacgyverService macgyverService;
    private final WebSocketHandler webSocketHandler;
    private final AccountRepository accountRepository;
    private final LastWalletBlockRepository lastWalletBlockRepository;
    private final WalletBlocksRepository walletBlocksRepository;
    private final UnclaimedWalletBlocksRepository unclaimedWalletBlocksRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    private final PublishSubject<WalletBlock> lastWalletBlockPublishSubject = PublishSubject.create();
    private final Subject<List<WalletBlock>> transactionsSubject = PublishSubject.create();

    @Inject
    public WalletRepository(WalletService walletService, MacgyverService macgyverService, WebSocketHandler webSocketHandler,
                            AccountRepository accountRepository, LastWalletBlockRepository lastWalletBlockRepository, WalletBlocksRepository walletBlocksRepository,
                            UnclaimedWalletBlocksRepository unclaimedWalletBlocksRepository, WalletTransactionRepository walletTransactionRepository) {
        this.walletService = walletService;
        this.macgyverService = macgyverService;
        this.webSocketHandler = webSocketHandler;
        this.accountRepository = accountRepository;
        this.lastWalletBlockRepository = lastWalletBlockRepository;
        this.walletBlocksRepository = walletBlocksRepository;
        this.unclaimedWalletBlocksRepository = unclaimedWalletBlocksRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    public Single<Boolean> saveWallet(GeneratedAccountData generatedAccountData, String pin) {
        return Single.create(emitter -> {
            emitter.onSuccess(saveWalletAndCreateBlock(generatedAccountData, pin));
        });
    }

    private boolean saveWalletAndCreateBlock(GeneratedAccountData generatedAccountData, String pin) {

        try {

            KeyPair keyPair = KeyGenerator.getKeyPairFromSeed(generatedAccountData.getKeySeed());

            long timestamp = System.currentTimeMillis();
            byte[] accountId = generatedAccountData.getAccountId();

            String encodedCreateBlock = BlockHelper.createCreateBlock(timestamp, accountId, keyPair);

            WalletTransaction createTransaction = new WalletTransaction();
            createTransaction.encodedBlock = encodedCreateBlock;

            Call<ResponseBody> createTransactionCall = walletService.createTransactionCall(createTransaction);

            Response<ResponseBody> response = createTransactionCall.execute();

            if (!response.isSuccessful()) {
                return false;
            }

            Call<WalletBlock> lastBlockCall = walletService.lastBlockCall(Base32Helper.encode(accountId));

            Response<WalletBlock> lastBlockResponse = lastBlockCall.execute();

            if (!lastBlockResponse.isSuccessful()) {
                return false;
            }

            WalletBlock lastBlock = lastBlockResponse.body();

            if (Objects.isNull(lastBlock)) {
                return false;
            }

            //TODO compare block hash
            if (!(Common.Block.BlockType.CREATE.equals(lastBlock.blockType) && timestamp == lastBlock.timestamp)) {
                return false;
            }

            Account account = new Account(Base32Helper.encode(generatedAccountData.getAccountId()), generatedAccountData.getAccountIdPhrase(),
                    generatedAccountData.getKeySeedPhrase(), generatedAccountData.getKeySeed(), pin);
            accountRepository.insert(account).subscribe();

            Call<ResponseBody> payToResponse = macgyverService.payTo(Base32Helper.encode(accountId), "100000000000000");
            Response<ResponseBody> execute = payToResponse.execute();

            return true;
        } catch (SignatureException | NoSuchAlgorithmException | CryptoException | InvalidKeyException | IOException ex) {
            //TODO
        }

        return false;
    }

    public Single<Boolean> login(String pin) {
        return Single.create(emitter -> {

            Account account = accountRepository.readAccount().blockingGet();
            if (account == null) {
                emitter.onSuccess(false);
                return;
            }

            if (account.getPin().equals(pin)) {
                emitter.onSuccess(true);

                Observable<MessageDtl.InfoResponse> webSocketMessageObserver = webSocketHandler.start(accountRepository.getAccountData().getAccountIdBase32());

                unclaimedWalletBlocksRepository
                        .claimWalletBlocksPeriodic(accountRepository.getAccountData())
                        .subscribeOn(Schedulers.io())
                        .subscribe(lastWalletBlock -> {
                            LOGGER.info("block claimed: interval");
                            updateLastWalletBlock(lastWalletBlock);
                        });

                webSocketMessageObserver
                        .filter(infoResponse -> Api.InfoRes.InfoType.PAYMENT_RECEIVED == infoResponse.infoType)
                        .map(infoResponse -> infoResponse.paymentReceived)
                        .concatMap(paymentReceived -> unclaimedWalletBlocksRepository.claimWalletBlockEvent(accountRepository.getAccountData(), paymentReceived.blockHash))
                        .subscribeOn(Schedulers.io())
                        .subscribe(lastWalletBlock -> {
                            LOGGER.info("block claimed: event");
                            updateLastWalletBlock(lastWalletBlock);
                        });

                return;
            }

            emitter.onSuccess(false);
        });
    }

    private void updateLastWalletBlock(WalletBlock walletBlock) {
        lastWalletBlockRepository.saveLastWalletBlockToCache(accountRepository.getAccountData().getAccountIdBase32(), walletBlock);
        publishLastWalletBlock(walletBlock);

        walletBlocksRepository.saveLastWalletBlock(accountRepository.getAccountData().getAccountIdBase32(), walletBlock);
        List<WalletBlock> lastWalletBlocks = walletBlocksRepository.getLastWalletBlocks();
        if (null != lastWalletBlocks) {
            publishWalletBlocks(lastWalletBlocks);
        }
    }

    public void send(String sendAccountId, long amount, String referenceCode) {
        lastWalletBlockRepository
                .getLastWalletBlock(accountRepository.getAccountData().getAccountIdBase32())
                .map(lastBlock -> {
                    long timestamp = System.currentTimeMillis();
                    byte[] accountId = accountRepository.getAccountData().getAccountId();
                    KeyPair keyPair = accountRepository.getAccountData().getKeyPair();

                    byte[] reference = null != referenceCode ? referenceCode.getBytes(StandardCharsets.UTF_8) : new byte[0];

                    LOGGER.info("creating send block");
                    Common.Block sendBlock = BlockHelper.createSendBlock(timestamp,
                            Base32Helper.decode(lastBlock.hash),
                            accountId,
                            lastBlock.balance - amount,
                            amount,
                            Base32Helper.decode(sendAccountId),
                            reference,
                            keyPair
                    );

                    return sendBlock;
                }).concatMap(sendBlock -> walletTransactionRepository.addWalletTransaction(sendBlock))
                .subscribeOn(Schedulers.io())
                .subscribe(lastWalletBlock -> {
                    LOGGER.info("block added");
                    updateLastWalletBlock(lastWalletBlock);
                });

    }

    public void requestTransactions() {
        LOGGER.debug("requesting transactions");
        getWalletBlocks()
                .onErrorComplete()
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public Subject<List<WalletBlock>> getTransactions() {
        return transactionsSubject;
    }

    private Observable<List<WalletBlock>> getWalletBlocks() {
        return walletBlocksRepository
                .getWalletBlocks(accountRepository.getAccountData().getAccountIdBase32())
                .map(walletBlocks -> publishWalletBlocks(walletBlocks));
    }

    private List<WalletBlock> publishWalletBlocks(List<WalletBlock> walletBlocks) {
        transactionsSubject.onNext(walletBlocks);
        return walletBlocks;
    }

    public void requestLastTransaction() {
        LOGGER.debug("requesting last transaction");
        getLastBlock()
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public Observable<WalletBlock> getLastTransaction() {
        return lastWalletBlockPublishSubject.subscribeOn(Schedulers.io());
    }

    private Observable<WalletBlock> getLastBlock() {
        return lastWalletBlockRepository
                .getLastWalletBlock(accountRepository.getAccountData().getAccountIdBase32())
                .map(walletBlock -> publishLastWalletBlock(walletBlock));
    }

    private WalletBlock publishLastWalletBlock(WalletBlock walletBlock) {
        lastWalletBlockPublishSubject.onNext(walletBlock);
        return walletBlock;
    }

}
