package org.flinkcoin.mobile.demo.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.flinkcoin.mobile.demo.databinding.FragmentHomeBinding;
import org.flinkcoin.mobile.demo.ui.transactions.TransactionsViewModel;
import org.flinkcoin.mobile.demo.ui.transactions.adapter.TransactionsAdapter;
import org.flinkcoin.mobile.demo.util.CurrencyUtils;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        HomeViewModel homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        TransactionsViewModel transactionsViewModel = new ViewModelProvider(requireActivity()).get(TransactionsViewModel.class);

        TransactionsAdapter transactionsAdapter = new TransactionsAdapter(transactionData -> {
            transactionsViewModel.setDetailsTransactionData(transactionData);
            Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.actionNavHomeToNavTransactionDetails());
        });
        binding.recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewTransactions.setAdapter(transactionsAdapter);

        binding.swipeRefreshHome.setOnRefreshListener(homeViewModel::requestData);
        binding.swipeRefreshHome.setRefreshing(true);

        homeViewModel.getLastTransaction().observe(getViewLifecycleOwner(), lastTransaction -> {
            binding.textAccountBalance.setText(CurrencyUtils.format(lastTransaction.balance));
            binding.swipeRefreshHome.setRefreshing(false);
        });
        homeViewModel.getTransactions().observe(getViewLifecycleOwner(), transactions -> {
            transactionsAdapter.setItems(transactions);
            binding.recyclerViewTransactions.scheduleLayoutAnimation();
            binding.swipeRefreshHome.setRefreshing(false);
        });
        homeViewModel.getNumberOfPaymentRequests().observe(getViewLifecycleOwner(), numberOfPaymentRequests -> {
            if (numberOfPaymentRequests > 0) {
                binding.textPaymentRequests.setVisibility(View.VISIBLE);
                binding.textPaymentRequestsNumber.setVisibility(View.VISIBLE);
                binding.textPaymentRequestsNumber.setText(String.valueOf(numberOfPaymentRequests));
            } else {
                binding.textPaymentRequests.setVisibility(View.GONE);
                binding.textPaymentRequestsNumber.setVisibility(View.GONE);
            }
        });

        homeViewModel.requestData();

        binding.buttonSend.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(HomeFragmentDirections.actionNavHomeToNavSendSelectContact()));

        binding.buttonRequestPayment.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(HomeFragmentDirections.actionNavHomeToNavPaymentRequestSelectContact()));

        binding.textPaymentRequestsNumber.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(HomeFragmentDirections.actionNavHomeToNavPaymentRequests()));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}