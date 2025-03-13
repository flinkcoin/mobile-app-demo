package org.flinkcoin.mobile.demo.data.db;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migrations {

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE account ADD COLUMN account_code TEXT DEFAULT ''");
        }
    };

    public static final Migration[] ALL_MIGRATIONS = new Migration[]{MIGRATION_1_2};

}
