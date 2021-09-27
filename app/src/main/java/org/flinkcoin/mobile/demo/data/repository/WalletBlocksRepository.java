package org.flinkcoin.mobile.demo.data.repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import org.flinkcoin.mobile.demo.data.service.WalletService;
import org.flinkcoin.mobile.demo.data.service.dto.WalletBlock;
import org.flinkcoin.mobile.demo.util.MoshiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;

@Singleton
public class WalletBlocksRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletBlocksRepository.class);

    private final WalletService walletService;

    private final Cache<String, List<WalletBlock>> walletBlocksCache;

    private List<WalletBlock> lastWalletBlocks;

    @Inject
    public WalletBlocksRepository(WalletService walletService) {
        this.walletService = walletService;

        this.walletBlocksCache = CacheBuilder
                .newBuilder()
                .maximumSize(1)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
    }

    public Observable<List<WalletBlock>> getWalletBlocks(String accountId) {
        return Observable
                .concat(getWalletBlockListCache(accountId), getWalletBlockListService(accountId))
                .firstElement()
                .map(walletBlocks -> lastWalletBlocks = walletBlocks)
                .toObservable();
    }

    private Observable<List<WalletBlock>> getWalletBlockListService(String accountId) {
        return walletService
                .listBlocks(accountId)
                .onErrorComplete()
                .map(walletBlock -> saveWalletBlocksToCache(accountId, walletBlock));
    }

    private Observable<List<WalletBlock>> getWalletBlockListCache(String accountId) {
        return Observable.create(emitter -> {
            List<WalletBlock> cachedWalletBlocks = walletBlocksCache.getIfPresent(accountId);
            if (Objects.nonNull(cachedWalletBlocks)) {
                LOGGER.debug("Got wallet blocks from cache: {}", MoshiHelper.writeValueAsFormattedString(cachedWalletBlocks));
                emitter.onNext(cachedWalletBlocks);
            }
            emitter.onComplete();
        });
    }

    public List<WalletBlock> saveWalletBlocksToCache(String accountId, List<WalletBlock> walletBlocks) {
        LOGGER.debug("Saving wallet blocks to cache");
        walletBlocksCache.put(accountId, walletBlocks);
        return walletBlocks;
    }

    public WalletBlock saveLastWalletBlock(String accountId, WalletBlock walletBlock) {

        if (Objects.nonNull(lastWalletBlocks) && lastWalletBlocks.size() > 0) {
            if (!lastWalletBlocks.get(0).hash.equals(walletBlock.hash)) {
                LOGGER.debug("Adding last wallet block to blocks");
                lastWalletBlocks.add(0, walletBlock);
            }
        }

        List<WalletBlock> cachedWalletBlocks = walletBlocksCache.getIfPresent(accountId);
        if (Objects.nonNull(cachedWalletBlocks) && cachedWalletBlocks.size() > 0) {
            if (!cachedWalletBlocks.get(0).hash.equals(walletBlock.hash)) {
                LOGGER.debug("Updating wallet blocks cache with last block");
                cachedWalletBlocks.add(0, walletBlock);
            }
        }

        return walletBlock;
    }

    public List<WalletBlock> getLastWalletBlocks() {
        return lastWalletBlocks;
    }
}
