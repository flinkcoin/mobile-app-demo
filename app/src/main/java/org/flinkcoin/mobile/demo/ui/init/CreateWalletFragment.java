package org.flinkcoin.mobile.demo.ui.init;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.flinkcoin.mobile.demo.databinding.FragmentCreateWalletBinding;

public class CreateWalletFragment extends Fragment {

    private FragmentCreateWalletBinding binding;
    private CreateWalletViewModel createWalletViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateWalletBinding.inflate(inflater, container, false);
        createWalletViewModel = new ViewModelProvider(requireActivity()).get(CreateWalletViewModel.class);

        createWalletViewModel.getWalletData().observe(getViewLifecycleOwner(), walletData -> {
            if (walletData != null) {

                binding.secretPhrase.textSecretPhraseWord1.setText(walletData.getAllWords()[0]);
                binding.secretPhrase.textSecretPhraseWord2.setText(walletData.getAllWords()[1]);
                binding.secretPhrase.textSecretPhraseWord3.setText(walletData.getAllWords()[2]);
                binding.secretPhrase.textSecretPhraseWord4.setText(walletData.getAllWords()[3]);
                binding.secretPhrase.textSecretPhraseWord5.setText(walletData.getAllWords()[4]);
                binding.secretPhrase.textSecretPhraseWord6.setText(walletData.getAllWords()[5]);
                binding.secretPhrase.textSecretPhraseWord7.setText(walletData.getAllWords()[6]);
                binding.secretPhrase.textSecretPhraseWord8.setText(walletData.getAllWords()[7]);
                binding.secretPhrase.textSecretPhraseWord9.setText(walletData.getAllWords()[8]);
                binding.secretPhrase.textSecretPhraseWord10.setText(walletData.getAllWords()[9]);
                binding.secretPhrase.textSecretPhraseWord11.setText(walletData.getAllWords()[10]);
                binding.secretPhrase.textSecretPhraseWord12.setText(walletData.getAllWords()[11]);
                binding.secretPhrase.textSecretPhraseWord13.setText(walletData.getAllWords()[12]);
                binding.secretPhrase.textSecretPhraseWord14.setText(walletData.getAllWords()[13]);
                binding.secretPhrase.textSecretPhraseWord15.setText(walletData.getAllWords()[14]);
                binding.secretPhrase.textSecretPhraseWord16.setText(walletData.getAllWords()[15]);
                binding.secretPhrase.textSecretPhraseWord17.setText(walletData.getAllWords()[16]);
                binding.secretPhrase.textSecretPhraseWord18.setText(walletData.getAllWords()[17]);

                binding.buttonConfirm.setClickable(true);
            }
        });

        binding.buttonConfirm.setClickable(false);
        binding.buttonConfirm.setOnClickListener((v) -> {
            Navigation.findNavController(v).navigate(CreateWalletFragmentDirections.actionNavInitCreateWalletToNavInitCreateWalletPin());
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