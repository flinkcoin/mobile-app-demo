package org.flinkcoin.mobile.demo.ui.transactions;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.flinkcoin.data.proto.common.Common;

import org.flinkcoin.mobile.demo.data.model.TransactionData;
import org.flinkcoin.mobile.demo.data.model.TransactionType;
import org.flinkcoin.mobile.demo.data.repository.AccountRepository;
import org.flinkcoin.mobile.demo.data.repository.ContactsRepository;
import org.flinkcoin.mobile.demo.data.repository.WalletRepository;
import org.flinkcoin.mobile.demo.data.service.dto.WalletBlock;
import org.flinkcoin.mobile.demo.ui.transactions.adapter.TransactionDataItem;
import org.flinkcoin.mobile.demo.ui.transactions.adapter.TransactionHeaderItem;
import org.flinkcoin.mobile.demo.ui.transactions.adapter.TransactionListItem;
import org.flinkcoin.mobile.demo.util.AccountIdUtils;
import org.flinkcoin.mobile.demo.util.CurrencyUtils;
import org.flinkcoin.mobile.demo.util.ReferenceCodeUtils;

import java.time.Instant;
import java.time.LocalDate;
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
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class TransactionsViewModel extends ViewModel {

    private static final DateTimeFormatter TRANSACTION_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final WalletRepository walletRepository;
    private final ContactsRepository contactsRepository;
    private final AccountRepository accountRepository;

    private final CompositeDisposable compositeDisposable;

    private final MutableLiveData<List<TransactionListItem>> transactions;

    private TransactionData detailsTransactionData;

    @Inject
    public TransactionsViewModel(WalletRepository walletRepository, ContactsRepository contactsRepository, AccountRepository accountRepository) {
        this.walletRepository = walletRepository;
        this.contactsRepository = contactsRepository;
        this.accountRepository = accountRepository;
        this.compositeDisposable = new CompositeDisposable();
        this.transactions = new MutableLiveData<>();

        init();
    }

    private void init() {

        Disposable disposable = walletRepository.getTransactions()
                .subscribeOn(Schedulers.io())
                .map(walletBlocks -> walletBlocks.stream().
                        filter(walletBlock -> Common.Block.BlockType.SEND.equals(walletBlock.blockType) || Common.Block.BlockType.RECEIVE.equals(walletBlock.blockType)).
                        collect(Collectors.toList()))
                .subscribe(walletBlocks -> {

                    List<TransactionListItem> items = new ArrayList<>();
                    LocalDate current = null;

                    for (WalletBlock walletBlock : walletBlocks) {

                        ZonedDateTime timestamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(walletBlock.timestamp), ZoneId.systemDefault());
                        LocalDate localDate = timestamp.toLocalDate();

                        if (!localDate.equals(current)) {
                            items.add(new TransactionHeaderItem(localDate));
                            current = localDate;
                        }

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

                });
        compositeDisposable.add(disposable);

    }

    public void requestTransactions() {
        walletRepository.requestTransactions();
    }

    public MutableLiveData<List<TransactionListItem>> getTransactions() {
        return transactions;
    }

    public TransactionData getDetailsTransactionData() {
        return detailsTransactionData;
    }

    public void setDetailsTransactionData(TransactionData detailsTransactionData) {
        this.detailsTransactionData = detailsTransactionData;
    }

    public String getAccountIdBase32() {
        return accountRepository.getAccountData().getAccountIdBase32();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
