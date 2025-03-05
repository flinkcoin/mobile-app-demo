package org.flinkcoin.mobile.demo.ui.nft;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.flinkcoin.mobile.demo.databinding.FragmentNftCreateHashBinding;

import pdqhashing.hasher.PDQHasher;

public class NftCreateHashFragment extends Fragment {

    private NftViewModel nftViewModel;
    private FragmentNftCreateHashBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNftCreateHashBinding.inflate(inflater, container, false);
        nftViewModel = new ViewModelProvider(requireActivity()).get(NftViewModel.class);

        binding.selectedImage.setImageURI(nftViewModel.getSelectedImage());

        binding.buttonConfirm.setOnClickListener(v -> {

            PDQHasher hasher = new PDQHasher();
            //TODO: rewrite hasher


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