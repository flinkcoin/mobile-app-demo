package org.flinkcoin.mobile.demo.ui.nft;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NftViewModel extends ViewModel {

    private Uri selectedImage;

    @Inject
    public NftViewModel() {
        //empty
    }

    public Uri getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(Uri selectedImage) {
        this.selectedImage = selectedImage;
    }
}
