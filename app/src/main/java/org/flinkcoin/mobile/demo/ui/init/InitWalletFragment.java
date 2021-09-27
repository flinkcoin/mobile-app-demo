package org.flinkcoin.mobile.demo.ui.init;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.flinkcoin.mobile.demo.databinding.FragmentInitWalletBinding;

public class InitWalletFragment extends Fragment {

    private FragmentInitWalletBinding binding;
    private CreateWalletViewModel createWalletViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInitWalletBinding.inflate(inflater, container, false);
        createWalletViewModel = new ViewModelProvider(requireActivity()).get(CreateWalletViewModel.class);

        binding.buttonCreateWallet.setOnClickListener(v -> {
            createWalletViewModel.generateWalletData();
            Navigation.findNavController(v).navigate(InitWalletFragmentDirections.actionNavInitHomeToNavInitCreateWallet());
        });

        binding.buttonImportWallet.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(InitWalletFragmentDirections.actionNavInitHomeToNavInitImportWallet()));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}