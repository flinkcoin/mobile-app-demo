package org.flinkcoin.mobile.demo.ui.payment.request;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.flinkcoin.helper.helpers.Base32Helper;

import org.flinkcoin.mobile.demo.data.model.PaymentRequest;
import org.flinkcoin.mobile.demo.data.model.PaymentRequestData;
import org.flinkcoin.mobile.demo.data.repository.ContactsRepository;
import org.flinkcoin.mobile.demo.data.repository.PaymentRequestRepository;
import org.flinkcoin.mobile.demo.util.AccountIdUtils;
import org.flinkcoin.mobile.demo.util.CurrencyUtils;
import org.flinkcoin.mobile.demo.util.ReferenceCodeUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class PaymentRequestsViewModel extends ViewModel {

    private static final DateTimeFormatter TRANSACTION_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final PaymentRequestRepository paymentRequestRepository;
    private final ContactsRepository contactsRepository;

    private final CompositeDisposable compositeDisposable;

    private final MutableLiveData<List<PaymentRequestData>> paymentRequests;

    @Inject
    public PaymentRequestsViewModel(PaymentRequestRepository paymentRequestRepository, ContactsRepository contactsRepository) {
        this.paymentRequestRepository = paymentRequestRepository;
        this.contactsRepository = contactsRepository;
        this.compositeDisposable = new CompositeDisposable();
        this.paymentRequests = new MutableLiveData<>();
    }

    public void requestData() {
        compositeDisposable.add(paymentRequestRepository
                .getPaymentRequests()
                .subscribeOn(Schedulers.io())
                .subscribe(requests -> {
                    List<PaymentRequestData> items = new ArrayList<>();

                    for (PaymentRequest request : requests) {


                        AtomicReference<String> account = new AtomicReference<>();

                        compositeDisposable.add(contactsRepository.getContact(request.getRequest().accountId)
                                .subscribe(contact -> account.set(contact.getFullName())
                                        , throwable -> account.set(null)));

                        items.add(new PaymentRequestData(request.getId(),
                                request.getRequest().accountId,
                                AccountIdUtils.mask(request.getRequest().accountId),
                                account.get(),
                                CurrencyUtils.format(request.getRequest().amount),
                                ZonedDateTime.ofInstant(Instant.ofEpochMilli(request.getId()),
                                        ZoneId.systemDefault()).format(TRANSACTION_TIMESTAMP_FORMATTER),
                                ReferenceCodeUtils.format(new String(Base32Helper.decode(request.getRequest().referenceCode))),
                                request.getRequest()));
                    }

                    paymentRequests.postValue(items);
                }));
    }

    public MutableLiveData<List<PaymentRequestData>> getPaymentRequests() {
        return paymentRequests;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void removePaymentRequest(Long paymentRequestId) {
        paymentRequestRepository.removePaymentRequest(paymentRequestId);
    }
}
