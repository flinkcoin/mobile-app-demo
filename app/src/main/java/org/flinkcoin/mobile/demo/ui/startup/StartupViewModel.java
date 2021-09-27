package org.flinkcoin.mobile.demo.ui.startup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.flinkcoin.mobile.demo.data.repository.AccountRepository;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class StartupViewModel extends ViewModel {

    private final AccountRepository accountRepository;
    private final MutableLiveData<Boolean> accountExist;
    private CompositeDisposable compositeDisposable;

    @Inject
    public StartupViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;

        this.compositeDisposable = new CompositeDisposable();
        this.accountExist = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getAccountExist() {
        return accountExist;
    }

    public void checkAccountExist() {
        compositeDisposable.add(accountRepository
                .readAccount()
                .delay(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(account -> accountExist.postValue(true),
                        throwable -> accountExist.postValue(false),
                        () -> accountExist.postValue(false))
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
            compositeDisposable = null;
        }
    }
}
