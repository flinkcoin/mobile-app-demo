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

import org.flinkcoin.helper.helpers.Base32Helper;

import org.flinkcoin.mobile.demo.databinding.FragmentPaymentRequestsBinding;
import org.flinkcoin.mobile.demo.ui.payment.request.adapter.PaymentRequestsAdapter;
import org.flinkcoin.mobile.demo.ui.send.SendViewModel;

public class PaymentRequestsFragment extends Fragment {

    private FragmentPaymentRequestsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPaymentRequestsBinding.inflate(inflater, container, false);
        PaymentRequestsViewModel paymentRequestsViewModel = new ViewModelProvider(requireActivity()).get(PaymentRequestsViewModel.class);
        SendViewModel sendViewModel = new ViewModelProvider(requireActivity()).get(SendViewModel.class);

        PaymentRequestsAdapter paymentRequestsAdapter = new PaymentRequestsAdapter(paymentRequest -> {
            sendViewModel.setSendAmount(paymentRequest.getRequest().amount);
            sendViewModel.setSendAccountId(paymentRequest.getAccountId());
            sendViewModel.setSendReferenceCode(new String(Base32Helper.decode(paymentRequest.getRequest().referenceCode)));
            sendViewModel.setSendContactName(paymentRequest.getAccountContact());
            sendViewModel.setPaymentRequestId(paymentRequest.getId());
            Navigation.findNavController(requireView()).navigate(PaymentRequestsFragmentDirections.actionNavPaymentRequestsToNavSendConfirmation());
        });

        binding.recyclerViewPaymentRequests.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewPaymentRequests.setAdapter(paymentRequestsAdapter);

        binding.swipeRefreshPaymentRequests.setOnRefreshListener(() -> paymentRequestsViewModel.requestData());
        binding.swipeRefreshPaymentRequests.setRefreshing(true);

        paymentRequestsViewModel.getPaymentRequests().observe(getViewLifecycleOwner(), paymentRequests -> {
            paymentRequestsAdapter.setPaymentRequests(paymentRequests);
            binding.swipeRefreshPaymentRequests.scheduleLayoutAnimation();
            binding.swipeRefreshPaymentRequests.setRefreshing(false);
        });
        paymentRequestsViewModel.requestData();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}