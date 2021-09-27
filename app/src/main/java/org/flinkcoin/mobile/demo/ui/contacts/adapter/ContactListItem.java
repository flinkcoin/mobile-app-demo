package org.flinkcoin.mobile.demo.ui.contacts.adapter;

import org.flinkcoin.mobile.demo.data.db.entity.Contact;

public abstract class ContactListItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_DATA = 1;

    private final Contact contact;

    public ContactListItem(Contact contact) {
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }

    abstract public int getType();
} 