package org.flinkcoin.mobile.demo.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.flinkcoin.data.proto.common.Common;

import org.flinkcoin.mobile.demo.data.model.TransactionData;
import org.flinkcoin.mobile.demo.data.model.TransactionType;
import org.flinkcoin.mobile.demo.data.repository.ContactsRepository;
import org.flinkcoin.mobile.demo.data.repository.PaymentRequestRepository;
import org.flinkcoin.mobile.demo.data.repository.WalletRepository;
import org.flinkcoin.mobile.demo.data.service.dto.WalletBlock;
import org.flinkcoin.mobile.demo.ui.transactions.adapter.TransactionDataItem;
import org.flinkcoin.mobile.demo.ui.transactions.adapter.TransactionListItem;
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
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    private static final DateTimeFormatter TRANSACTION_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final WalletRepository walletRepository;
    private final PaymentRequestRepository paymentRequestRepository;
    private final ContactsRepository contactsRepository;

    private final CompositeDisposable compositeDisposable;

    private final MutableLiveData<WalletBlock> lastTransaction;
    private final MutableLiveData<List<TransactionListItem>> transactions;
    private final MutableLiveData<Integer> numberOfPaymentRequests;

    @Inject
    public HomeViewModel(WalletRepository walletRepository, PaymentRequestRepository paymentRequestRepository, ContactsRepository contactsRepository) {
        this.walletRepository = walletRepository;
        this.paymentRequestRepository = paymentRequestRepository;
        this.contactsRepository = contactsRepository;
        this.compositeDisposable = new CompositeDisposable();
        this.lastTransaction = new MutableLiveData<>();
        this.transactions = new MutableLiveData<>();
        this.numberOfPaymentRequests = new MutableLiveData<>();

        init();
    }

    private void init() {
        compositeDisposable.add(walletRepository.getLastTransaction()
                .subscribeOn(Schedulers.io())
                .subscribe(lastTransaction::postValue, throwable -> {

                }));

        compositeDisposable.add(walletRepository.getTransactions()
                .subscribeOn(Schedulers.io())
                .subscribe(walletBlocks -> {

                    List<WalletBlock> sendReceiveBlocks = walletBlocks.stream().
                            filter(walletBlock -> Common.Block.BlockType.SEND.equals(walletBlock.blockType) || Common.Block.BlockType.RECEIVE.equals(walletBlock.blockType)).
                            collect(Collectors.toList());

                    List<TransactionListItem> items = new ArrayList<>();

                    for (WalletBlock walletBlock : sendReceiveBlocks) {

                        TransactionType type;
                        switch (walletBlock.blockType) {
                            case SEND:
                                type = TransactionType.SEND;
                                break;
                            case RECEIVE:
                            default:
                                type = TransactionType.RECEIVE;
                                break;
                        }

                        AtomicReference<String> account = new AtomicReference<>();

                        compositeDisposable.add(contactsRepository.getContact(walletBlock.sendAccountId)
                                .subscribe(contact -> account.set(contact.getFullName())
                                        , throwable -> account.set(null)));

                        items.add(new TransactionDataItem(new TransactionData(type,
                                walletBlock.sendAccountId,
                                AccountIdUtils.mask(walletBlock.sendAccountId),
                                account.get(),
                                CurrencyUtils.format(walletBlock.amount),
                                ZonedDateTime.ofInstant(Instant.ofEpochMilli(walletBlock.timestamp),
                                        ZoneId.systemDefault()).format(TRANSACTION_TIMESTAMP_FORMATTER),
                                ReferenceCodeUtils.format(walletBlock.referenceCode),
                                walletBlock)));
                    }

                    transactions.postValue(items);

                }, throwable -> {

                }));

        compositeDisposable.add(paymentRequestRepository.getNumberOfPaymentRequests()
                .subscribeOn(Schedulers.io())
                .subscribe(numberOfPaymentRequests::postValue));
    }

    public void requestData() {
        walletRepository.requestLastTransaction();
        walletRepository.requestTransactions();
    }

    public MutableLiveData<WalletBlock> getLastTransaction() {
        return lastTransaction;
    }

    public MutableLiveData<List<TransactionListItem>> getTransactions() {
        return transactions;
    }

    public MutableLiveData<Integer> getNumberOfPaymentRequests() {
        return numberOfPaymentRequests;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
