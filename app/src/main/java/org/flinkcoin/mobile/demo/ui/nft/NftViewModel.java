package org.flinkcoin.mobile.demo.ui.nft;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import org.flinkcoin.mobile.demo.data.repository.WalletRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NftViewModel extends ViewModel {

    private final WalletRepository walletRepository;

    private Uri selectedImage;

    @Inject
    public NftViewModel(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
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

}
