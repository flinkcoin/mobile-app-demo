package org.flinkcoin.mobile.demo.ui.nft;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.flinkcoin.data.proto.common.Common;
import org.flinkcoin.mobile.demo.data.model.TransactionData;
import org.flinkcoin.mobile.demo.data.model.TransactionType;
import org.flinkcoin.mobile.demo.data.repository.WalletRepository;
import org.flinkcoin.mobile.demo.data.service.dto.WalletBlock;
import org.flinkcoin.mobile.demo.ui.transactions.adapter.TransactionDataItem;
import org.flinkcoin.mobile.demo.ui.transactions.adapter.TransactionListItem;
import org.flinkcoin.mobile.demo.util.AccountIdUtils;
import org.flinkcoin.mobile.demo.util.CurrencyUtils;
import org.flinkcoin.mobile.demo.util.ReferenceCodeUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class NftViewModel extends ViewModel {

    private static final DateTimeFormatter TRANSACTION_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final WalletRepository walletRepository;

    private final CompositeDisposable compositeDisposable;
    private final MutableLiveData<WalletBlock> lastTransaction;
    private final MutableLiveData<List<TransactionListItem>> transactions;

    private Uri selectedImage;

    @Inject
    public NftViewModel(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;

        this.compositeDisposable = new CompositeDisposable();
        this.lastTransaction = new MutableLiveData<>();
        this.transactions = new MutableLiveData<>();

        init();
    }

    private void init() {
        compositeDisposable.add(walletRepository.getTransactions()
                .subscribeOn(Schedulers.io())
                .subscribe(walletBlocks -> {

                    List<WalletBlock> sendReceiveBlocks = walletBlocks.stream().
                            filter(walletBlock -> Common.Block.BlockType.ADD_NFT.equals(walletBlock.blockType) || Common.Block.BlockType.DEL_NFT.equals(walletBlock.blockType)).
                            collect(Collectors.toList());

                    List<TransactionListItem> items = new ArrayList<>();

                    for (WalletBlock walletBlock : sendReceiveBlocks) {

                        TransactionType type;
                        switch (walletBlock.blockType) {
                            case ADD_NFT:
                                type = TransactionType.ADD_NFT;
                                break;
                            case DEL_NFT:
                            default:
                                type = TransactionType.DEL_NFT;
                                break;
                        }

                        items.add(new TransactionDataItem(new TransactionData(type,
                                walletBlock.accountId,
                                AccountIdUtils.mask(walletBlock.accountId),
                                null,
                                CurrencyUtils.format(walletBlock.amount),
                                ZonedDateTime.ofInstant(Instant.ofEpochMilli(walletBlock.timestamp),
                                        ZoneId.systemDefault()).format(TRANSACTION_TIMESTAMP_FORMATTER),
                                ReferenceCodeUtils.format(walletBlock.referenceCode),
                                walletBlock)));
                    }

                    transactions.postValue(items);

                }, throwable -> {

                }));
    }

    public Uri getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(Uri selectedImage) {
        this.selectedImage = selectedImage;
    }

    public void createNft() {
        walletRepository.addNft(null, selectedImage);
    }

    public void requestData() {
        walletRepository.requestTransactions();
    }

    public MutableLiveData<List<TransactionListItem>> getTransactions() {
        return transactions;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

}
