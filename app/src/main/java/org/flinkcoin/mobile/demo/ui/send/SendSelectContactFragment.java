package org.flinkcoin.mobile.demo.ui.send;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.flinkcoin.mobile.demo.databinding.FragmentSendSelectContactBinding;
import org.flinkcoin.mobile.demo.ui.contacts.ContactsViewModel;
import org.flinkcoin.mobile.demo.ui.contacts.adapter.ContactsAdapter;

public class SendSelectContactFragment extends Fragment {

    private SendViewModel sendViewModel;
    private FragmentSendSelectContactBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSendSelectContactBinding.inflate(inflater, container, false);
        ContactsViewModel contactsViewModel = new ViewModelProvider(requireActivity()).get(ContactsViewModel.class);
        sendViewModel = new ViewModelProvider(requireActivity()).get(SendViewModel.class);

        ContactsAdapter contactsAdapter = new ContactsAdapter(contact -> {
            sendViewModel.setSendContactName(contact.getFullName());
            sendViewModel.setSendAccountId(contact.getAccountId());
            Navigation.findNavController(requireView()).navigate(SendSelectContactFragmentDirections.actionNavSendSelectContactToNavSendEnterAmount());
        });
        binding.recyclerViewContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewContacts.setAdapter(contactsAdapter);

        contactsViewModel.getContacts().observe(getViewLifecycleOwner(), contactsAdapter::setContacts);
        contactsViewModel.loadContacts();
        sendViewModel.clearValues();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}