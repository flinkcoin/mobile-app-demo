package org.flinkcoin.mobile.demo.ui.payment.request;

import androidx.lifecycle.ViewModel;

import org.flinkcoin.mobile.demo.data.repository.AccountRepository;
import org.flinkcoin.mobile.demo.data.repository.PaymentRequestRepository;
import org.flinkcoin.mobile.demo.data.repository.WalletRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PaymentRequestViewModel extends ViewModel {

    private final WalletRepository walletRepository;
    private final AccountRepository accountRepository;
    private final PaymentRequestRepository paymentRequestRepository;

    private String paymentRequestContactName;
    private String paymentRequestAccountId;
    private Long paymentRequestAmount;
    private String paymentRequestReferenceCode;

    @Inject
    public PaymentRequestViewModel(WalletRepository walletRepository, AccountRepository accountRepository, PaymentRequestRepository paymentRequestRepository) {
        this.walletRepository = walletRepository;
        this.accountRepository = accountRepository;
        this.paymentRequestRepository = paymentRequestRepository;
    }

    public void paymentRequest() {
        paymentRequestRepository.paymentRequest(paymentRequestAccountId, paymentRequestAmount, paymentRequestReferenceCode);
    }

    public String getAccountIdBase32() {
        return accountRepository.getAccountData().getAccountIdBase32();
    }

    public String getPaymentRequestContactName() {
        return paymentRequestContactName;
    }

    public void setPaymentRequestContactName(String paymentRequestContactName) {
        this.paymentRequestContactName = paymentRequestContactName;
    }

    public String getPaymentRequestAccountId() {
        return paymentRequestAccountId;
    }

    public void setPaymentRequestAccountId(String paymentRequestAccountId) {
        this.paymentRequestAccountId = paymentRequestAccountId;
    }

    public Long getPaymentRequestAmount() {
        return paymentRequestAmount;
    }

    public void setPaymentRequestAmount(Long paymentRequestAmount) {
        this.paymentRequestAmount = paymentRequestAmount;
    }

    public String getPaymentRequestReferenceCode() {
        return paymentRequestReferenceCode;
    }

    public void setPaymentRequestReferenceCode(String paymentRequestReferenceCode) {
        this.paymentRequestReferenceCode = paymentRequestReferenceCode;
    }

    public void clearValues() {
        this.paymentRequestContactName = null;
        this.paymentRequestAccountId = null;
        this.paymentRequestAmount = null;
        this.paymentRequestReferenceCode = null;
    }
}
