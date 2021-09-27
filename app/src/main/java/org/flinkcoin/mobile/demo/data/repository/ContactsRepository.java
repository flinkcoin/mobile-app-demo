package org.flinkcoin.mobile.demo.data.repository;

import org.flinkcoin.mobile.demo.data.db.dao.ContactDao;
import org.flinkcoin.mobile.demo.data.db.entity.Contact;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class ContactsRepository {

    private final ContactDao contactDao;

    @Inject
    public ContactsRepository(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    public Single<List<Contact>> getContacts() {
        return contactDao.getContacts();
    }

    public Single<Contact> getContact(String accountId) {
        return contactDao.getContact(accountId);
    }

    public Completable save(String accountId, String firstName, String lastName) {
        return contactDao.insert(new Contact(accountId, firstName, lastName, false));
    }

    public Completable delete(Contact contact) {
        return contactDao.delete(contact);
    }
}
