package org.flinkcoin.mobile.demo.ui.contacts.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.flinkcoin.mobile.demo.R;
import org.flinkcoin.mobile.demo.data.db.entity.Contact;

import java.util.List;
import java.util.function.Consumer;

public class ContactsAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    private final Consumer<Contact> onClickConsumer;

    private List<ContactListItem> contactsDataSet;

    public ContactsAdapter(Consumer<Contact> onClickConsumer) {
        this.onClickConsumer = onClickConsumer;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.contact_row_item, viewGroup, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder contactViewHolder, int position) {
        ContactListItem contactListItem = contactsDataSet.get(position);
        int type = getItemViewType(position);

        Contact contact = contactListItem.getContact();

        if (type == ContactListItem.TYPE_HEADER) {
            if (contact.isFavorite()) {
                contactViewHolder.getContactGroupTextView().setBackgroundResource(R.drawable.ic_star);
                contactViewHolder.getContactGroupTextView().setText("");
            } else {
                contactViewHolder.getContactGroupTextView().setBackground(null);
                contactViewHolder.getContactGroupTextView().setText(contact.getFirstCharacter());
            }
        } else {
            contactViewHolder.getContactGroupTextView().setText("");
        }

        contactViewHolder.getContactIconTextView().setText(contact.getFirstCharacter());
        contactViewHolder.getContactNameTextView().setText(contact.getFullName());
        contactViewHolder.getContactAccountIdTextView().setText(contact.getAccountId());

        if (onClickConsumer != null) {
            contactViewHolder.itemView.setOnClickListener(v -> onClickConsumer.accept(contact));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return contactsDataSet.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return contactsDataSet == null ? 0 : contactsDataSet.size();
    }

    public void setContacts(List<ContactListItem> contacts) {
        this.contactsDataSet = contacts;
        notifyDataSetChanged();
    }
}
