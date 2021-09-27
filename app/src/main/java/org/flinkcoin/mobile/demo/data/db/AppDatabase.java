package org.flinkcoin.mobile.demo.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import org.flinkcoin.mobile.demo.data.db.dao.AccountDao;
import org.flinkcoin.mobile.demo.data.db.dao.ContactDao;
import org.flinkcoin.mobile.demo.data.db.entity.Account;
import org.flinkcoin.mobile.demo.data.db.entity.Contact;

@Database(entities = {Account.class, Contact.class}, version = AppDatabase.VERSION)
public abstract class AppDatabase extends RoomDatabase {

    static final int VERSION = 1;

    public abstract AccountDao accountDao();

    public abstract ContactDao contactDao();

}
