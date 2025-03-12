package org.flinkcoin.mobile.demo.ui.nft;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.flinkcoin.mobile.demo.databinding.FragmentNftsBinding;
import org.flinkcoin.mobile.demo.ui.transactions.adapter.TransactionsAdapter;

import java.util.Objects;

public class NftsFragment extends Fragment {

    private FragmentNftsBinding binding;
    private NftViewModel nftViewModel;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        nftViewModel = new ViewModelProvider(requireActivity()).get(NftViewModel.class);
        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (Objects.nonNull(uri)) {
                        nftViewModel.setSelectedImage(uri);
                        Navigation.findNavController(requireView()).navigate(NftsFragmentDirections.actionNavNftsToNavNftCreateHash());
                    }
                });
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNftsBinding.inflate(inflater, container, false);
        nftViewModel = new ViewModelProvider(requireActivity()).get(NftViewModel.class);
        binding.fabCreateNft.setOnClickListener((v) -> {
            launchPhotoPicker();
        });

        TransactionsAdapter transactionsAdapter = new TransactionsAdapter(transactionData -> {
            //TODO: on click?
        });
        binding.recyclerViewNfts.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewNfts.setAdapter(transactionsAdapter);

        binding.swipeRefreshNfts.setOnRefreshListener(nftViewModel::requestData);
        binding.swipeRefreshNfts.setRefreshing(true);

        nftViewModel.getTransactions().observe(getViewLifecycleOwner(), transactions -> {
            transactionsAdapter.setItems(transactions);
            binding.recyclerViewNfts.scheduleLayoutAnimation();
            binding.swipeRefreshNfts.setRefreshing(false);
        });

        nftViewModel.requestData();

        View view = binding.getRoot();
        return view;
    }

    private void launchPhotoPicker() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pickMedia = null;
    }
}