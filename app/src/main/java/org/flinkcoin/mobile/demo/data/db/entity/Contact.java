package org.flinkcoin.mobile.demo.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "contact")
public class Contact {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "account_id")
    private final String accountId;

    @NonNull
    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;

    @ColumnInfo(name = "is_favorite")
    private boolean favorite;

    public Contact(@NonNull String accountId, @NonNull String firstName, String lastName, boolean favorite) {
        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.favorite = favorite;
    }

    @NonNull
    public String getAccountId() {
        return accountId;
    }

    @NonNull
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NonNull String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Ignore
    public String getFullName() {
        return lastName == null ? firstName : firstName + " " + lastName;
    }

    @Ignore
    public String getFirstCharacter() {
        return firstName.substring(0, 1).toUpperCase();
    }
}
