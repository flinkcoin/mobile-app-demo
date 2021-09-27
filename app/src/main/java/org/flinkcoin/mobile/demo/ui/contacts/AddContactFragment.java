package org.flinkcoin.mobile.demo.ui.contacts;

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
import org.flinkcoin.mobile.demo.databinding.FragmentAddContactBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddContactFragment extends Fragment {

    private static final Pattern ACCOUNT_ID_BASE_32_PATTERN = Pattern.compile("^[a-zA-Z2-7]{26}$");

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private FragmentAddContactBinding binding;
    private ContactsViewModel contactsViewModel;
    private Menu appBarMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddContactBinding.inflate(inflater, container, false);
        contactsViewModel = new ViewModelProvider(requireActivity()).get(ContactsViewModel.class);

        View.OnFocusChangeListener onFocusChangeListener = (view, hasFocus) -> {
            if (!hasFocus && !binding.contact.textInputAccountId.getEditText().hasFocus() &&
                    !binding.contact.textInputFirstName.hasFocus() &&
                    !binding.contact.textInputLastName.hasFocus()) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } else {
                clearErrors();
            }
        };

        binding.contact.textInputAccountId.getEditText().setOnFocusChangeListener(onFocusChangeListener);
        binding.contact.textInputFirstName.getEditText().setOnFocusChangeListener(onFocusChangeListener);
        binding.contact.textInputLastName.getEditText().setOnFocusChangeListener(onFocusChangeListener);

        String contactAccountId = contactsViewModel.getContactAccountId();
        if (contactAccountId != null) {
            binding.contact.textInputAccountId.getEditText().setText(contactAccountId);
        }
        contactsViewModel.setContactAccountId(null);

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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        appBarMenu = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        this.appBarMenu = menu;
        inflater.inflate(R.menu.add_contact_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                disableButtons();

                if (checkInput()) {
                    Disposable disposable = contactsViewModel.saveContact(binding.contact.textInputAccountId.getEditText().getText().toString(),
                            binding.contact.textInputFirstName.getEditText().getText().toString(),
                            binding.contact.textInputLastName.getEditText().getText().toString())
                            .subscribeOn(Schedulers.io()).
                                    observeOn(AndroidSchedulers.mainThread()).
                                    subscribe(() -> Navigation.findNavController(getView()).navigate(AddContactFragmentDirections.actionNavAddContactToNavContacts()),
                                            throwable -> {
                                                Navigation.findNavController(getView()).navigate(AddContactFragmentDirections.actionNavAddContactToNavContacts());
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
        binding.contact.textInputAccountId.getEditText().clearFocus();
        binding.contact.textInputFirstName.getEditText().clearFocus();
        binding.contact.textInputLastName.getEditText().clearFocus();

        if (binding.contact.textInputAccountId.getEditText().getText().toString().isEmpty()) {
            binding.contact.textInputAccountId.setError(getString(R.string.x_is_required, binding.contact.textInputAccountId.getEditText().getHint()));
            return false;
        }

        Matcher matcher = ACCOUNT_ID_BASE_32_PATTERN.matcher(binding.contact.textInputAccountId.getEditText().getText().toString());
        if (!matcher.matches()) {
            binding.contact.textInputAccountId.setError(getString(R.string.account_id_format_does_not_match));
            return false;
        }

        if (binding.contact.textInputFirstName.getEditText().getText().toString().isEmpty()) {
            binding.contact.textInputFirstName.setError(getString(R.string.x_is_required, binding.contact.textInputFirstName.getEditText().getHint()));
            return false;
        }

        return true;
    }

    private void clearErrors() {
        enableButtons();

        binding.contact.textInputAccountId.setError(null);
        binding.contact.textInputFirstName.setError(null);
    }

    private void disableButtons() {
        this.appBarMenu.findItem(R.id.action_save).setEnabled(false);
    }

    private void enableButtons() {
        this.appBarMenu.findItem(R.id.action_save).setEnabled(true);
    }
}