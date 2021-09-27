package org.flinkcoin.mobile.demo.data.repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import org.flinkcoin.mobile.demo.data.service.WalletService;
import org.flinkcoin.mobile.demo.data.service.dto.WalletBlock;
import org.flinkcoin.mobile.demo.util.MoshiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;

@Singleton
public class LastWalletBlockRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(LastWalletBlockRepository.class);

    private final WalletService walletService;

    private final Cache<String, WalletBlock> lastWalletBlockCache;

    @Inject
    public LastWalletBlockRepository(WalletService walletService) {
        this.walletService = walletService;

        this.lastWalletBlockCache = CacheBuilder
                .newBuilder()
                .maximumSize(1)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
    }

    public Observable<WalletBlock> getLastWalletBlock(String accountId) {
        return Observable
                .concat(getLastWalletBlockCache(accountId), getLastWalletBlockService(accountId))
                .firstElement()
                .toObservable();
    }

    private Observable<WalletBlock> getLastWalletBlockService(String accountId) {
        return walletService
                .lastBlock(accountId)
                .onErrorComplete()
                .map(walletBlock -> saveLastWalletBlockToCache(accountId, walletBlock));
    }

    private Observable<WalletBlock> getLastWalletBlockCache(String accountId) {
        return Observable.create(emitter -> {
            WalletBlock cachedWalletBlock = lastWalletBlockCache.getIfPresent(accountId);
            if (Objects.nonNull(cachedWalletBlock)) {
                LOGGER.debug("Got last wallet block from cache: {}", MoshiHelper.writeValueAsFormattedString(cachedWalletBlock));
                emitter.onNext(cachedWalletBlock);
            }
            emitter.onComplete();
        });
    }

    public WalletBlock saveLastWalletBlockToCache(String accountId, WalletBlock walletBlock) {
        LOGGER.debug("Saving last wallet block to cache");
        lastWalletBlockCache.put(accountId, walletBlock);
        return walletBlock;
    }
}
