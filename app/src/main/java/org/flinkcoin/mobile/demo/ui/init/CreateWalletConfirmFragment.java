package org.flinkcoin.mobile.demo.ui.init;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.flinkcoin.mobile.demo.LoginActivity;
import org.flinkcoin.mobile.demo.databinding.FragmentCreateWalletConfirmBinding;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class CreateWalletConfirmFragment extends Fragment {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private FragmentCreateWalletConfirmBinding binding;
    private CreateWalletViewModel createWalletViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateWalletConfirmBinding.inflate(inflater, container, false);
        createWalletViewModel = new ViewModelProvider(requireActivity()).get(CreateWalletViewModel.class);

        binding.buttonConfirm.setOnClickListener((v) -> {
            binding.buttonConfirm.setClickable(false);

            compositeDisposable.add(createWalletViewModel.saveWallet().subscribe(success -> {
                if (success) {
                    Intent intent = new Intent(getActivity().getBaseContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }));
        });

        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}