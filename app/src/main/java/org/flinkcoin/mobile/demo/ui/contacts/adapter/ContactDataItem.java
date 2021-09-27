package org.flinkcoin.mobile.demo.ui.contacts.adapter;

import org.flinkcoin.mobile.demo.data.db.entity.Contact;

public class ContactDataItem extends ContactListItem {

    public ContactDataItem(Contact contact) {
        super(contact);
    }

    @Override
    public int getType() {
        return TYPE_DATA;
    }

}