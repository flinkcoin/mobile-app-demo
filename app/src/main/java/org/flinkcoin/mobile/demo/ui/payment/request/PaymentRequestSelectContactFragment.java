package org.flinkcoin.mobile.demo.ui.payment.request;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.flinkcoin.mobile.demo.databinding.FragmentPaymentRequestSelectContactBinding;
import org.flinkcoin.mobile.demo.ui.contacts.ContactsViewModel;
import org.flinkcoin.mobile.demo.ui.contacts.adapter.ContactsAdapter;

public class PaymentRequestSelectContactFragment extends Fragment {

    private PaymentRequestViewModel paymentRequestViewModel;
    private FragmentPaymentRequestSelectContactBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPaymentRequestSelectContactBinding.inflate(inflater, container, false);
        ContactsViewModel contactsViewModel = new ViewModelProvider(requireActivity()).get(ContactsViewModel.class);
        paymentRequestViewModel = new ViewModelProvider(requireActivity()).get(PaymentRequestViewModel.class);

        ContactsAdapter contactsAdapter = new ContactsAdapter(contact -> {
            paymentRequestViewModel.setPaymentRequestContactName(contact.getFullName());
            paymentRequestViewModel.setPaymentRequestAccountId(contact.getAccountId());
            Navigation.findNavController(requireView()).navigate(PaymentRequestSelectContactFragmentDirections.actionNavPaymentRequestSelectContactToNavPaymentRequestEnterAmount());
        });
        binding.recyclerViewContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewContacts.setAdapter(contactsAdapter);

        contactsViewModel.getContacts().observe(getViewLifecycleOwner(), contactsAdapter::setContacts);
        contactsViewModel.loadContacts();
        paymentRequestViewModel.clearValues();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}