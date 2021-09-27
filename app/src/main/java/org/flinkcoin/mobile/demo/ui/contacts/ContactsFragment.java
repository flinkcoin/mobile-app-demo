package org.flinkcoin.mobile.demo.ui.contacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.flinkcoin.mobile.demo.databinding.FragmentContactsBinding;
import org.flinkcoin.mobile.demo.ui.contacts.adapter.ContactsAdapter;

public class ContactsFragment extends Fragment {

    private ContactsViewModel contactsViewModel;
    private FragmentContactsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        contactsViewModel = new ViewModelProvider(requireActivity()).get(ContactsViewModel.class);

        binding.fabAddContact.setOnClickListener((v) -> {
            Navigation.findNavController(v).navigate(ContactsFragmentDirections.actionNavContactsToNavAddContact());
        });

        ContactsAdapter contactsAdapter = new ContactsAdapter(contact -> {
            contactsViewModel.setViewContact(contact);
            Navigation.findNavController(requireView()).navigate(ContactsFragmentDirections.actionNavContactsToNavContact());
        });
        binding.recyclerViewContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewContacts.setAdapter(contactsAdapter);

        contactsViewModel.getContacts().observe(getViewLifecycleOwner(), contacts -> {
            contactsAdapter.setContacts(contacts);
        });
        contactsViewModel.loadContacts();

        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}