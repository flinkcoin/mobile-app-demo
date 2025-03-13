package org.flinkcoin.mobile.demo.ui.nft;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.flinkcoin.mobile.demo.R;
import org.flinkcoin.mobile.demo.databinding.FragmentNftSetAccountCodeBinding;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NftSetAccountCodeFragment extends Fragment {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private FragmentNftSetAccountCodeBinding binding;
    private NftViewModel nftViewModel;
    private Menu appBarMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNftSetAccountCodeBinding.inflate(inflater, container, false);
        nftViewModel = new ViewModelProvider(requireActivity()).get(NftViewModel.class);

        View.OnFocusChangeListener onFocusChangeListener = (view, hasFocus) -> {
            if (!hasFocus && !binding.accountCode.textInputAccountCode.getEditText().hasFocus()) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } else {
                clearErrors();
            }
        };

        binding.accountCode.textInputAccountCode.getEditText().setOnFocusChangeListener(onFocusChangeListener);

        String accountCode = nftViewModel.getAccountCode();
        if (accountCode != null) {
            binding.accountCode.textInputAccountCode.getEditText().setText(accountCode);
        }

        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        this.appBarMenu = menu;
        inflater.inflate(R.menu.set_account_code_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                disableButtons();
                if (checkInput()) {
                    Disposable disposable = nftViewModel.updateAccountCode(binding.accountCode.textInputAccountCode.getEditText().getText().toString())
                            .subscribeOn(Schedulers.io()).
                            observeOn(AndroidSchedulers.mainThread()).
                            subscribe(() -> Navigation.findNavController(getView()).navigate(NftSetAccountCodeFragmentDirections.actionNavSetAccountCodeToNavNfts()),
                                    throwable -> {
                                        Navigation.findNavController(getView()).navigate(NftSetAccountCodeFragmentDirections.actionNavSetAccountCodeToNavNfts());
                                        //TODO handle error
                                    });
                    compositeDisposable.add(disposable);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    private boolean checkInput() {
        binding.accountCode.textInputAccountCode.getEditText().clearFocus();

        if (binding.accountCode.textInputAccountCode.getEditText().getText().toString().isEmpty()) {
            binding.accountCode.textInputAccountCode.setError(getString(R.string.x_is_required, binding.accountCode.textInputAccountCode.getEditText().getHint()));
            return false;
        }

        return true;
    }

    private void clearErrors() {
        enableButtons();
        binding.accountCode.textInputAccountCode.setError(null);
    }

    private void disableButtons() {
        this.appBarMenu.findItem(R.id.action_save).setEnabled(false);
    }

    private void enableButtons() {
        this.appBarMenu.findItem(R.id.action_save).setEnabled(true);
    }


}
