package org.flinkcoin.mobile.demo.ui.init;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.flinkcoin.mobile.demo.data.model.GeneratedAccountData;
import org.flinkcoin.mobile.demo.data.repository.AccountRepository;
import org.flinkcoin.mobile.demo.data.repository.WalletRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class CreateWalletViewModel extends ViewModel {

    private final AccountRepository accountRepository;
    private final WalletRepository walletRepository;
    private final MutableLiveData<GeneratedAccountData> walletData = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String pin;

    @Inject
    public CreateWalletViewModel(AccountRepository accountRepository, WalletRepository walletRepository) {
        this.accountRepository = accountRepository;
        this.walletRepository = walletRepository;
        this.pin = null;
    }

    public void generateWalletData() {
        Disposable disposable = accountRepository.generateAccount().
                subscribeOn(Schedulers.io()).
                subscribe(wd -> {
                    walletData.postValue(wd);
                });
        compositeDisposable.add(disposable);
    }

    public Single<Boolean> saveWallet() {
        return walletRepository.saveWallet(walletData.getValue(), pin).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread());
    }

    public MutableLiveData<GeneratedAccountData> getWalletData() {
        return walletData;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }


}
