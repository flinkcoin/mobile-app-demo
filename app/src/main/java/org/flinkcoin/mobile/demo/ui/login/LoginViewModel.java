package org.flinkcoin.mobile.demo.ui.login;

import androidx.lifecycle.ViewModel;

import org.flinkcoin.mobile.demo.data.repository.WalletRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class LoginViewModel extends ViewModel {

    private final WalletRepository walletRepository;

    @Inject
    public LoginViewModel(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Single<Boolean> login(String pin) {
        return walletRepository.login(pin).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread());
    }
}
