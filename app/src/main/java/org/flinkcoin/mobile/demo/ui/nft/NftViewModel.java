package org.flinkcoin.mobile.demo.ui.nft;

import static org.flinkcoin.data.proto.common.Common.Block.BlockType.ADD_NFT;
import static org.flinkcoin.data.proto.common.Common.Block.BlockType.DEL_NFT;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.flinkcoin.data.proto.common.Common;
import org.flinkcoin.mobile.demo.data.model.NftData;
import org.flinkcoin.mobile.demo.data.repository.WalletRepository;
import org.flinkcoin.mobile.demo.data.service.dto.WalletBlock;
import org.flinkcoin.mobile.demo.ui.nft.adapter.NftDataItem;
import org.flinkcoin.mobile.demo.util.AccountCodeUtils;
import org.flinkcoin.mobile.demo.util.NftCodeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class NftViewModel extends ViewModel {

    private final Context context;
    private final WalletRepository walletRepository;

    private final CompositeDisposable compositeDisposable;
    private final MutableLiveData<List<NftDataItem>> nfts;

    private Uri selectedImage;

    @Inject
    public NftViewModel(@ApplicationContext Context context,
                        WalletRepository walletRepository) {
        this.context = context;
        this.walletRepository = walletRepository;

        this.compositeDisposable = new CompositeDisposable();
        this.nfts = new MutableLiveData<>();

        init();
    }

    private void init() {
        compositeDisposable.add(walletRepository.getTransactions()
                .subscribeOn(Schedulers.io())
                .subscribe(walletBlocks -> {

                    List<WalletBlock> nftBlocks = walletBlocks.stream().
                            filter(walletBlock -> Common.Block.BlockType.ADD_NFT.equals(walletBlock.blockType) || DEL_NFT.equals(walletBlock.blockType)).
                            collect(Collectors.toList());

                    Map<String, NftDataItem> items = new HashMap<>();
                    Set<String> deleted = new HashSet<>();

                    for (WalletBlock walletBlock : nftBlocks) {

                        if (DEL_NFT == walletBlock.blockType) {
                            deleted.add(walletBlock.nftCode);
                        } else if (ADD_NFT == walletBlock.blockType) {

                            String nftCodeBase32 = walletBlock.nftCode;

                            items.put(walletBlock.nftCode, new NftDataItem(new NftData(
                                    AccountCodeUtils.format(walletBlock.accountCode),
                                    walletBlock.nftCode,
                                    nftCodeBase32,
                                    NftCodeUtils.mask(nftCodeBase32),
                                    getPreview(nftCodeBase32),
                                    walletBlock)));

                        }
                    }
                    deleted.forEach(items::remove);
                    nfts.postValue(new ArrayList<>(items.values()));

                }, throwable -> {
                    throwable.printStackTrace();
                    nfts.postValue(new ArrayList<>());
                }));
    }

    private Bitmap getPreview(String nftCodeBase32) {
        File directory = new File(context.getExternalFilesDir(null), "previews");
        File file = new File(directory, nftCodeBase32 + "_preview.png");

        if (file.exists()) {
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        }

        return null;
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

    public MutableLiveData<List<NftDataItem>> getNfts() {
        return nfts;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

}
