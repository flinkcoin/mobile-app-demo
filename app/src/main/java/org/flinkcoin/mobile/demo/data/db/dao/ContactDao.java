package org.flinkcoin.mobile.demo.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import org.flinkcoin.mobile.demo.data.db.entity.Contact;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public abstract class ContactDao {

    @Query("SELECT * FROM `contact` ORDER BY is_favorite DESC, first_name COLLATE NOCASE ASC")
    public abstract Single<List<Contact>> getContacts();

    @Query("SELECT * FROM `contact` WHERE account_id=:accountId")
    public abstract Single<Contact> getContact(String accountId);

    @Insert
    public abstract Completable insert(Contact contact);

    @Delete
    public abstract Completable delete(Contact contact);
}
