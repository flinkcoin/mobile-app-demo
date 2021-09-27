package org.flinkcoin.mobile.demo.di;

import android.content.Context;

import androidx.room.Room;

import org.flinkcoin.mobile.demo.data.db.AppDatabase;
import org.flinkcoin.mobile.demo.data.db.Migrations;
import org.flinkcoin.mobile.demo.data.db.dao.AccountDao;
import org.flinkcoin.mobile.demo.data.db.dao.ContactDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class RoomModule {

    private final static String DB_NAME = "flink";

    @Provides
    @Singleton
    AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                .createFromAsset("database/flink")
                .addMigrations(Migrations.ALL_MIGRATIONS)
                .build();
    }

    @Provides
    AccountDao provideAccountDao(AppDatabase appDatabase) {
        return appDatabase.accountDao();
    }

    @Provides
    ContactDao provideContactDao(AppDatabase appDatabase) {
        return appDatabase.contactDao();
    }

}
