package org.flinkcoin.mobile.demo.ui.payment.request;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.flinkcoin.mobile.demo.R;
import org.flinkcoin.mobile.demo.databinding.FragmentPaymentRequestConfirmationBinding;
import org.flinkcoin.mobile.demo.util.CurrencyUtils;

public class PaymentRequestConfirmationFragment extends Fragment {

    private PaymentRequestViewModel paymentRequestViewModel;
    private FragmentPaymentRequestConfirmationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPaymentRequestConfirmationBinding.inflate(inflater, container, false);
        paymentRequestViewModel = new ViewModelProvider(requireActivity()).get(PaymentRequestViewModel.class);

        long amount = paymentRequestViewModel.getPaymentRequestAmount();
        String referenceCode = paymentRequestViewModel.getPaymentRequestReferenceCode();

        binding.transactionDetails.textFromAccount.getEditText().setText(paymentRequestViewModel.getAccountIdBase32());
        String contactName = paymentRequestViewModel.getPaymentRequestContactName();

        binding.transactionDetails.textToAccount.getEditText().setText(contactName + "\n" + paymentRequestViewModel.getPaymentRequestAccountId());
        binding.transactionDetails.textAmount.getEditText().setText(CurrencyUtils.format(amount));
        binding.transactionDetails.textReferenceCode.getEditText().setText(getString(R.string.hash_prefix, referenceCode));

        binding.buttonYes.setOnClickListener(view -> {
            paymentRequestViewModel.paymentRequest();
            Navigation.findNavController(view).navigate(PaymentRequestConfirmationFragmentDirections.actionNavPaymentRequestConfirmationToNavHome());
        });

        binding.buttonNo.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(PaymentRequestConfirmationFragmentDirections.actionNavPaymentRequestConfirmationToNavHome());
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