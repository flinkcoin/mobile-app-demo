package org.flinkcoin.mobile.demo.ui.main;

import androidx.lifecycle.ViewModel;

import org.flinkcoin.mobile.demo.data.repository.AccountRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

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
}
