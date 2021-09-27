package org.flinkcoin.mobile.demo.ui.contacts;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.flinkcoin.mobile.demo.data.db.entity.Contact;
import org.flinkcoin.mobile.demo.data.repository.ContactsRepository;
import org.flinkcoin.mobile.demo.ui.contacts.adapter.ContactDataItem;
import org.flinkcoin.mobile.demo.ui.contacts.adapter.ContactHeaderItem;
import org.flinkcoin.mobile.demo.ui.contacts.adapter.ContactListItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class ContactsViewModel extends ViewModel {

    private final ContactsRepository contactsRepository;

    private final MutableLiveData<List<ContactListItem>> contacts = new MutableLiveData<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private String contactAccountId;
    private Contact viewContact;

    @Inject
    public ContactsViewModel(ContactsRepository contactsRepository) {
        this.contactsRepository = contactsRepository;
    }

    public void loadContacts() {
        compositeDisposable.add(contactsRepository.getContacts()
                .subscribeOn(Schedulers.io())
                .subscribe(contactsList -> {

                    List<ContactListItem> items = new ArrayList<>();
                    String current = null;

                    for (Contact contact : contactsList) {

                        String firstCharacter = contact.getFirstCharacter();
                        if (contact.isFavorite()) {
                            firstCharacter = "*";
                        }

                        if (!firstCharacter.equals(current)) {
                            items.add(new ContactHeaderItem(contact));
                            current = firstCharacter;
                            continue;
                        }
                        items.add(new ContactDataItem(contact));

                    }

                    contacts.postValue(items);

                }, throwable -> contacts.postValue(new ArrayList<>())));
    }

    public MutableLiveData<List<ContactListItem>> getContacts() {
        return contacts;
    }

    public Completable saveContact(String accountId, String firstName, String lastName) {
        return contactsRepository.save(accountId, firstName, lastName);
    }

    public Completable deleteContact(Contact contact) {
        return contactsRepository.delete(contact);
    }

    public String getContactAccountId() {
        return contactAccountId;
    }

    public void setContactAccountId(String contactAccountId) {
        this.contactAccountId = contactAccountId;
    }

    public Contact getViewContact() {
        return viewContact;
    }

    public void setViewContact(Contact contact) {
        this.viewContact = contact;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

}
