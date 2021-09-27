package org.flinkcoin.mobile.demo.ui.transactions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.flinkcoin.mobile.demo.databinding.FragmentTransactionsBinding;
import org.flinkcoin.mobile.demo.ui.transactions.adapter.TransactionsAdapter;

public class TransactionsFragment extends Fragment {

    private TransactionsViewModel transactionsViewModel;

    private FragmentTransactionsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTransactionsBinding.inflate(inflater, container, false);
        transactionsViewModel = new ViewModelProvider(requireActivity()).get(TransactionsViewModel.class);

        TransactionsAdapter transactionsAdapter = new TransactionsAdapter(transactionData -> {
            transactionsViewModel.setDetailsTransactionData(transactionData);
            Navigation.findNavController(requireView()).navigate(TransactionsFragmentDirections.actionNavTransactionsToNavTransactionDetails());
        });

        binding.recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewTransactions.setAdapter(transactionsAdapter);

        binding.swipeRefreshTransactions.setOnRefreshListener(() -> transactionsViewModel.requestTransactions());
        binding.swipeRefreshTransactions.setRefreshing(true);

        transactionsViewModel.getTransactions().observe(getViewLifecycleOwner(), transactions -> {
            transactionsAdapter.setItems(transactions);
            binding.recyclerViewTransactions.scheduleLayoutAnimation();
            binding.swipeRefreshTransactions.setRefreshing(false);
        });
        transactionsViewModel.requestTransactions();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}