package org.flinkcoin.mobile.demo.ui.contacts.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.flinkcoin.mobile.demo.R;

public class ContactViewHolder extends RecyclerView.ViewHolder {

    private final TextView contactNameTextView;
    private final TextView contactAccountIdTextView;
    private final TextView contactIconTextView;
    private final TextView contactGroupTextView;

    public ContactViewHolder(View view) {
        super(view);
        this.contactNameTextView = view.findViewById(R.id.text_contact_name);
        this.contactAccountIdTextView = view.findViewById(R.id.text_contact_account_id);
        this.contactIconTextView = view.findViewById(R.id.text_contact_icon);
        this.contactGroupTextView = view.findViewById(R.id.text_contact_group);
    }

    public TextView getContactNameTextView() {
        return contactNameTextView;
    }

    public TextView getContactAccountIdTextView() {
        return contactAccountIdTextView;
    }

    public TextView getContactIconTextView() {
        return contactIconTextView;
    }

    public TextView getContactGroupTextView() {
        return contactGroupTextView;
    }
}
