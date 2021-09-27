package org.flinkcoin.mobile.demo.ui.contacts.adapter;

import org.flinkcoin.mobile.demo.data.db.entity.Contact;

public class ContactHeaderItem extends ContactListItem {

    public ContactHeaderItem(Contact contact) {
        super(contact);
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }

}