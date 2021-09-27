package org.flinkcoin.mobile.demo.data.repository;

import com.flick.crypto.KeyPair;
import com.flick.data.proto.common.Common;
import com.flick.helper.helpers.Base32Helper;

import org.flinkcoin.mobile.demo.data.model.AccountData;
import org.flinkcoin.mobile.demo.data.service.WalletService;
import org.flinkcoin.mobile.demo.data.service.dto.WalletBlock;
import org.flinkcoin.mobile.demo.util.BlockHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;

@Singleton
public class UnclaimedWalletBlocksRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnclaimedWalletBlocksRepository.class);

    private static final long CHECK_UNCLAIMED_WALLET_BLOCKS_INTERVAL = 1;
    private static final TimeUnit CHECK_UNCLAIMED_WALLET_BLOCKS_INTERVAL_UNIT = TimeUnit.MINUTES;

    private final WalletService walletService;
    private final LastWalletBlockRepository lastWalletBlockRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Inject
    public UnclaimedWalletBlocksRepository(WalletService walletService, LastWalletBlockRepository lastWalletBlockRepository,
                                           WalletTransactionRepository walletTransactionRepository) {
        this.walletService = walletService;
        this.lastWalletBlockRepository = lastWalletBlockRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    public Observable<WalletBlock> claimWalletBlocksPeriodic(AccountData accountData) {
        return getUnclaimedWalletBlocks(accountData.getAccountIdBase32())
                .repeatWhen(objectFlowable -> objectFlowable.delay(CHECK_UNCLAIMED_WALLET_BLOCKS_INTERVAL, CHECK_UNCLAIMED_WALLET_BLOCKS_INTERVAL_UNIT))
                .concatMapIterable(walletBlocks -> walletBlocks)
                .concatMap(unclaimedWalletBlock -> claimBlock(accountData, unclaimedWalletBlock));
    }

    public Observable<WalletBlock> claimWalletBlockEvent(AccountData accountData, String blockHash) {
        return walletService
                .getBlock(blockHash)
                .onErrorComplete()
                .concatMap(unclaimedWalletBlock -> claimBlock(accountData, unclaimedWalletBlock));
    }

    private Observable<List<WalletBlock>> getUnclaimedWalletBlocks(String accountId) {
        return walletService
                .listUnclaimedBlocks(accountId)
                .onErrorComplete();
    }

    private Observable<WalletBlock> claimBlock(AccountData accountData, WalletBlock unclaimedWalletBlock) {
        LOGGER.info("claim block: {}", unclaimedWalletBlock.hash);
        return lastWalletBlockRepository.getLastWalletBlock(accountData.getAccountIdBase32())
                .map(lastBlock -> {
                    long timestamp = System.currentTimeMillis();
                    byte[] accountId = accountData.getAccountId();
                    KeyPair keyPair = accountData.getKeyPair();

                    LOGGER.info("creating receive block");
                    Common.Block receiveBlock = BlockHelper.createReceiveBlock(timestamp,
                            Base32Helper.decode(lastBlock.hash),
                            accountId,
                            lastBlock.balance + unclaimedWalletBlock.amount,
                            unclaimedWalletBlock.amount,
                            Base32Helper.decode(unclaimedWalletBlock.accountId),
                            Base32Helper.decode(unclaimedWalletBlock.hash),
                            unclaimedWalletBlock.referenceCode.getBytes(StandardCharsets.UTF_8),
                            keyPair);

                    return receiveBlock;
                })
                .concatMap(receiveBlock -> walletTransactionRepository.addWalletTransaction(receiveBlock));
    }

}
