package org.flinkcoin.mobile.demo.ui.nft;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.flinkcoin.mobile.demo.R;
import org.flinkcoin.mobile.demo.databinding.FragmentNftsBinding;
import org.flinkcoin.mobile.demo.ui.nft.adapter.NftsAdapter;

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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNftsBinding.inflate(inflater, container, false);
        nftViewModel = new ViewModelProvider(requireActivity()).get(NftViewModel.class);
        binding.fabCreateNft.setOnClickListener((v) -> {
            String accountCode = nftViewModel.getAccountCode();

            if (Objects.isNull(accountCode) || accountCode.isEmpty()) {
                Navigation.findNavController(requireView()).navigate(NftsFragmentDirections.actionNavNftsToNavSetAccountCode());
            } else {
                launchPhotoPicker();
            }
        });

        NftsAdapter nftsAdapter = new NftsAdapter(nftData -> {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Nft code", nftData.getNftCodeBase32());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "NFT code has been copied to clipboard.", Toast.LENGTH_SHORT).show();
        });

        binding.recyclerViewNfts.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewNfts.setAdapter(nftsAdapter);

        binding.swipeRefreshNfts.setOnRefreshListener(nftViewModel::requestData);
        binding.swipeRefreshNfts.setRefreshing(true);

        nftViewModel.getNfts().observe(getViewLifecycleOwner(), nfts -> {
            nftsAdapter.setItems(nfts);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Navigation.findNavController(requireView()).navigate(NftsFragmentDirections.actionNavNftsToNavSetAccountCode());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.nfts_menu, menu);
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