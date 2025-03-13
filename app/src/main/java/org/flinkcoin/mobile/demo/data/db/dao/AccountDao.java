package org.flinkcoin.mobile.demo.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.flinkcoin.mobile.demo.data.db.entity.Account;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;

@Dao
public abstract class AccountDao {

    @Query("SELECT * FROM `account` LIMIT 1")
    public abstract Maybe<Account> getAccount();

    @Query("SELECT * FROM `account` LIMIT 1")
    public abstract Flowable<Account> getAccountFlow();

    @Insert
    public abstract Completable insert(Account account);

    @Update
    public abstract Completable update(Account account);
}
