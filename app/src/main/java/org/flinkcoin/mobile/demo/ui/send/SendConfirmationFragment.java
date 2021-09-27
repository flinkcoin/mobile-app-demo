package org.flinkcoin.mobile.demo.ui.send;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.flinkcoin.mobile.demo.R;
import org.flinkcoin.mobile.demo.databinding.FragmentSendConfirmationBinding;
import org.flinkcoin.mobile.demo.ui.payment.request.PaymentRequestsViewModel;
import org.flinkcoin.mobile.demo.util.CurrencyUtils;

public class SendConfirmationFragment extends Fragment {

    private SendViewModel sendViewModel;
    private FragmentSendConfirmationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSendConfirmationBinding.inflate(inflater, container, false);
        sendViewModel = new ViewModelProvider(requireActivity()).get(SendViewModel.class);
        PaymentRequestsViewModel paymentRequestsViewModel = new ViewModelProvider(requireActivity()).get(PaymentRequestsViewModel.class);

        long amount = sendViewModel.getSendAmount();
        String referenceCode = sendViewModel.getSendReferenceCode();

        binding.transactionDetails.textFromAccount.getEditText().setText(sendViewModel.getAccountIdBase32());
        String contactName = sendViewModel.getSendContactName();

        String to = contactName == null ? sendViewModel.getSendAccountId() : contactName + "\n" + sendViewModel.getSendAccountId();
        binding.transactionDetails.textToAccount.getEditText().setText(to);

        binding.transactionDetails.textAmount.getEditText().setText(CurrencyUtils.format(amount));
        binding.transactionDetails.textReferenceCode.getEditText().setText(getString(R.string.hash_prefix, referenceCode));

        binding.buttonYes.setOnClickListener(view -> {
            sendViewModel.send();
            paymentRequestsViewModel.removePaymentRequest(sendViewModel.getPaymentRequestId());
            Navigation.findNavController(view).navigate(SendConfirmationFragmentDirections.actionNavSendConfirmationToNavHome());
        });

        binding.buttonNo.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(SendConfirmationFragmentDirections.actionNavSendConfirmationToNavHome());
        });

        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}