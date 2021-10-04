package org.flinkcoin.mobile.demo.data.repository;

import org.flinkcoin.data.proto.api.Api;
import org.flinkcoin.data.proto.common.Common;
import org.flinkcoin.helper.helpers.Base32Helper;

import org.flinkcoin.mobile.demo.data.service.WalletService;
import org.flinkcoin.mobile.demo.data.service.dto.WalletBlock;
import org.flinkcoin.mobile.demo.data.service.dto.WalletTransaction;
import org.flinkcoin.mobile.demo.data.ws.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;

@Singleton
public class WalletTransactionRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletTransactionRepository.class);

    private final WebSocketHandler webSocketHandler;
    private final WalletService walletService;

    @Inject
    public WalletTransactionRepository(WebSocketHandler webSocketHandler, WalletService walletService) {
        this.webSocketHandler = webSocketHandler;
        this.walletService = walletService;
    }

    public Observable<WalletBlock> addWalletTransaction(Common.Block block) {
        WalletTransaction walletTransaction = new WalletTransaction();
        walletTransaction.encodedBlock = Base32Helper.encode(block.toByteArray());
        LOGGER.debug("adding wallet transaction");
        return walletService
                .addTransaction(walletTransaction)
                .onErrorComplete()
                .concatMap(responseBody -> waitBlockConfirmed(block));
    }

    private Observable<WalletBlock> waitBlockConfirmed(Common.Block block) {
        String blockHash = Base32Helper.encode(block.getBlockHash().getHash().toByteArray());
        LOGGER.info("waiting block confirmation: {}", blockHash);
        return webSocketHandler.getEvents()
                .onErrorComplete()
                .filter(infoResponse -> Api.InfoRes.InfoType.BLOCK_CONFIRM == infoResponse.infoType)
                .map(infoResponse -> infoResponse.blockConfirm)
                .filter(blockConfirm -> blockConfirm.blockHash.equals(blockHash))
                .timeout(5, TimeUnit.SECONDS, Observable.empty())
                .onErrorComplete()
                .take(1)
                .map(blockConfirm -> {
                    LOGGER.info("block confirmed: {}", blockHash);
                    WalletBlock confirmedWalletBlock = new WalletBlock(block);
                    return confirmedWalletBlock;
                });
    }


}
