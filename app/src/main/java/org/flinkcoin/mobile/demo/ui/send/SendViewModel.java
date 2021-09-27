package org.flinkcoin.mobile.demo.ui.send;

import androidx.lifecycle.ViewModel;

import org.flinkcoin.mobile.demo.data.repository.AccountRepository;
import org.flinkcoin.mobile.demo.data.repository.WalletRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SendViewModel extends ViewModel {

    private final WalletRepository walletRepository;
    private final AccountRepository accountRepository;

    private String sendContactName;
    private String sendAccountId;
    private Long sendAmount;
    private String sendReferenceCode;
    private Long paymentRequestId;

    @Inject
    public SendViewModel(WalletRepository walletRepository, AccountRepository accountRepository) {
        this.walletRepository = walletRepository;
        this.accountRepository = accountRepository;
    }

    public void send() {
        walletRepository.send(getSendAccountId(), getSendAmount(), getSendReferenceCode());
    }

    public String getAccountIdBase32() {
        return accountRepository.getAccountData().getAccountIdBase32();
    }

    public String getSendContactName() {
        return sendContactName;
    }

    public void setSendContactName(String sendContactName) {
        this.sendContactName = sendContactName;
    }

    public String getSendAccountId() {
        return sendAccountId;
    }

    public void setSendAccountId(String sendAccountId) {
        this.sendAccountId = sendAccountId;
    }

    public Long getSendAmount() {
        return sendAmount;
    }

    public void setSendAmount(Long sendAmount) {
        this.sendAmount = sendAmount;
    }

    public String getSendReferenceCode() {
        return sendReferenceCode;
    }

    public void setSendReferenceCode(String sendReferenceCode) {
        this.sendReferenceCode = sendReferenceCode;
    }

    public Long getPaymentRequestId() {
        return paymentRequestId;
    }

    public void setPaymentRequestId(Long paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }

    public void clearValues() {
        this.sendContactName = null;
        this.sendAccountId = null;
        this.sendAmount = null;
        this.sendReferenceCode = null;
        this.paymentRequestId = null;
    }
}
