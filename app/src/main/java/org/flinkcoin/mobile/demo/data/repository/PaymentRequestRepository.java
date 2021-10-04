package org.flinkcoin.mobile.demo.data.repository;

import org.flinkcoin.data.proto.api.Api;
import org.flinkcoin.data.proto.common.Common;
import org.flinkcoin.helper.helpers.Base32Helper;

import org.flinkcoin.mobile.demo.data.model.PaymentRequest;
import org.flinkcoin.mobile.demo.data.service.WalletService;
import org.flinkcoin.mobile.demo.data.service.dto.WalletPaymentRequest;
import org.flinkcoin.mobile.demo.data.ws.WebSocketHandler;
import org.flinkcoin.mobile.demo.data.ws.dto.MessageDtl;
import org.flinkcoin.mobile.demo.util.AppTime;
import org.flinkcoin.mobile.demo.util.BlockHelper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import okhttp3.ResponseBody;

@Singleton
public class PaymentRequestRepository {

    private final WalletService walletService;
    private final WebSocketHandler webSocketHandler;
    private final AccountRepository accountRepository;

    private final List<PaymentRequest> paymentRequests;
    private final Subject<Integer> numberOfPaymentRequestsSubject;
    private final Subject<List<PaymentRequest>> paymentRequestsSubject;

    @Inject
    public PaymentRequestRepository(WalletService walletService, WebSocketHandler webSocketHandler, AccountRepository accountRepository) {
        this.walletService = walletService;
        this.webSocketHandler = webSocketHandler;
        this.accountRepository = accountRepository;
        this.paymentRequests = new ArrayList<>();
        this.numberOfPaymentRequestsSubject = BehaviorSubject.create();
        this.paymentRequestsSubject = BehaviorSubject.create();

        this.paymentRequestsSubject.onNext(paymentRequests);

        readPaymentRequests();
    }

    public void paymentRequest(String paymentRequestAccountId, long amount, String referenceCode) {
        paymentRequest(accountRepository.getAccountData().getAccountId(), Base32Helper.decode(paymentRequestAccountId), amount, referenceCode)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private Observable<ResponseBody> paymentRequest(byte[] fromAccountId, byte[] toAccountId, long amount, String referenceCode) {
        byte[] reference = null != referenceCode ? referenceCode.getBytes(StandardCharsets.UTF_8) : new byte[0];

        Common.PaymentRequest paymentRequestBlock = BlockHelper.createPaymentRequest(fromAccountId, toAccountId, amount, reference);

        WalletPaymentRequest walletPaymentRequest = new WalletPaymentRequest();
        walletPaymentRequest.encodedPaymentRequest = Base32Helper.encode(paymentRequestBlock.toByteArray());
        return walletService.paymentRequest(walletPaymentRequest)
                .onErrorComplete();
    }

    private Disposable readPaymentRequests() {
        return webSocketHandler.getEvents()
                .filter(infoResponse -> Api.InfoRes.InfoType.PAYMENT_REQUEST == infoResponse.infoType)
                .map(infoResponse -> infoResponse.paymentRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(this::savePaymentRequest);
    }

    private void savePaymentRequest(MessageDtl.PaymentRequest paymentRequest) {
        paymentRequests.add(new PaymentRequest(AppTime.getSystemTime(), paymentRequest));
        numberOfPaymentRequestsSubject.onNext(paymentRequests.size());
        paymentRequestsSubject.onNext(paymentRequests);
    }

    public void removePaymentRequest(Long paymentRequestId) {
        if (paymentRequestId == null) {
            return;
        }

        paymentRequests.removeIf(paymentRequest -> paymentRequest.getId() == paymentRequestId);
        numberOfPaymentRequestsSubject.onNext(paymentRequests.size());
        paymentRequestsSubject.onNext(paymentRequests);
    }

    public Subject<Integer> getNumberOfPaymentRequests() {
        return numberOfPaymentRequestsSubject;
    }

    public Subject<List<PaymentRequest>> getPaymentRequests() {
        return paymentRequestsSubject;
    }

}
