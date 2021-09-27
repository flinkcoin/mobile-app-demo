package org.flinkcoin.mobile.demo.ui.transactions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;

import org.flinkcoin.mobile.demo.data.model.TransactionData;
import org.flinkcoin.mobile.demo.databinding.FragmentTransactionDetailsBinding;
import org.flinkcoin.mobile.demo.ui.contacts.ContactsViewModel;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionDetailsFragment extends Fragment {

    private static final DateTimeFormatter TRANSACTION_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private FragmentTransactionDetailsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTransactionDetailsBinding.inflate(inflater, container, false);
        TransactionsViewModel transactionsViewModel = new ViewModelProvider(requireActivity()).get(TransactionsViewModel.class);
        ContactsViewModel contactsViewModel = new ViewModelProvider(requireActivity()).get(ContactsViewModel.class);

        TransactionData transactionData = transactionsViewModel.getDetailsTransactionData();

        binding.transactionDetails.textTimestamp.setVisibility(View.VISIBLE);
        binding.transactionDetails.textTimestamp.getEditText().setText(ZonedDateTime.ofInstant(Instant.ofEpochMilli(transactionData.getWalletBlock().timestamp),
                ZoneId.systemDefault()).format(TRANSACTION_TIMESTAMP_FORMATTER));

        String from;
        String to;
        String accountContact;

        switch (transactionData.getType()) {
            case RECEIVE:
                accountContact = transactionData.getAccountContact();
                if (null == accountContact) {
                    binding.transactionDetails.textFromAccount.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
                } else {
                    binding.transactionDetails.textFromAccount.setEndIconMode(TextInputLayout.END_ICON_NONE);
                }
                from = accountContact != null ? accountContact + "\n" + transactionData.getAccountId() : transactionData.getAccountId();

                to = transactionsViewModel.getAccountIdBase32();
                break;
            case SEND:
            default:
                from = transactionsViewModel.getAccountIdBase32();

                accountContact = transactionData.getAccountContact();
                if (null == accountContact) {
                    binding.transactionDetails.textToAccount.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
                } else {
                    binding.transactionDetails.textToAccount.setEndIconMode(TextInputLayout.END_ICON_NONE);
                }
                to = accountContact != null ? accountContact + "\n" + transactionData.getAccountId() : transactionData.getAccountId();

                break;
        }

        binding.transactionDetails.textFromAccount.getEditText().setText(from);
        binding.transactionDetails.textToAccount.getEditText().setText(to);

        binding.transactionDetails.textAmount.getEditText().setText(transactionData.getAmount());
        binding.transactionDetails.textReferenceCode.getEditText().setText(transactionData.getReferenceCode() == null || transactionData.getReferenceCode().isEmpty() ? "-" : transactionData.getReferenceCode());
        binding.transactionDetails.textBlockHash.setVisibility(View.VISIBLE);
        binding.transactionDetails.textBlockHash.getEditText().setText(transactionData.getWalletBlock().hash);

        binding.transactionDetails.textFromAccount.setEndIconOnClickListener(v -> {
            contactsViewModel.setContactAccountId(transactionData.getAccountId());
            Navigation.findNavController(requireView()).navigate(TransactionDetailsFragmentDirections.actionNavTransactionDetailsToNavAddContact());
        });

        binding.transactionDetails.textToAccount.setEndIconOnClickListener(v -> {
            contactsViewModel.setContactAccountId(transactionData.getAccountId());
            Navigation.findNavController(requireView()).navigate(TransactionDetailsFragmentDirections.actionNavTransactionDetailsToNavAddContact());
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}