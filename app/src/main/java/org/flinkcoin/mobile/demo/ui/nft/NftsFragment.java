package org.flinkcoin.mobile.demo.ui.nft;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.flinkcoin.mobile.demo.databinding.FragmentNftsBinding;

public class NftsFragment extends Fragment {

    private FragmentNftsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNftsBinding.inflate(inflater, container, false);

        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}