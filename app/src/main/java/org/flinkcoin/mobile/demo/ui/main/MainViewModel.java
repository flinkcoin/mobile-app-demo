package org.flinkcoin.mobile.demo.ui.main;

import androidx.lifecycle.ViewModel;

import org.flinkcoin.mobile.demo.data.db.entity.Account;
import org.flinkcoin.mobile.demo.data.repository.AccountRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final AccountRepository accountRepository;

    @Inject
    public MainViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public String getAccountIdBase32() {
        return accountRepository.getAccountData().getAccountIdBase32();
    }

    public String getAccountCode() {
        return accountRepository.getAccountData().getAccountCode();
    }

    public Flowable<Account> getAccount() {
        return accountRepository.flowAccount()//
                .subscribeOn(Schedulers.io());  // Run on background thread
    }
}
