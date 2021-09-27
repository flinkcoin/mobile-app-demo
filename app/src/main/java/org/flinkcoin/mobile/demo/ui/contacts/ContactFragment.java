package org.flinkcoin.mobile.demo.ui.contacts;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.flinkcoin.mobile.demo.R;
import org.flinkcoin.mobile.demo.data.db.entity.Contact;
import org.flinkcoin.mobile.demo.databinding.FragmentContactBinding;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ContactFragment extends Fragment {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private FragmentContactBinding binding;
    private ContactsViewModel contactsViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContactBinding.inflate(inflater, container, false);
        contactsViewModel = new ViewModelProvider(requireActivity()).get(ContactsViewModel.class);

        Contact contact = contactsViewModel.getViewContact();

        binding.contact.textInputAccountId.getEditText().setText(contact.getAccountId());
        binding.contact.textInputFirstName.getEditText().setText(contact.getFirstName());
        binding.contact.textInputLastName.getEditText().setText(contact.getLastName() == null || contact.getLastName().isEmpty() ? "-" : contact.getLastName());


        binding.contact.textInputAccountId.setFocusable(false);
        binding.contact.textInputAccountId.setClickable(false);
        binding.contact.textInputAccountId.getEditText().setClickable(false);
        binding.contact.textInputAccountId.getEditText().setFocusable(false);
        binding.contact.textInputFirstName.setFocusable(false);
        binding.contact.textInputFirstName.setClickable(false);
        binding.contact.textInputFirstName.getEditText().setClickable(false);
        binding.contact.textInputFirstName.getEditText().setFocusable(false);
        binding.contact.textInputLastName.setFocusable(false);
        binding.contact.textInputLastName.setClickable(false);
        binding.contact.textInputLastName.getEditText().setClickable(false);
        binding.contact.textInputLastName.getEditText().setFocusable(false);

        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.contact_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to delete this contact?")
                        .setPositiveButton(R.string.yes, (dialog, id) -> {
                            compositeDisposable.add(contactsViewModel.deleteContact(contactsViewModel.getViewContact()).subscribeOn(Schedulers.io()).
                                    observeOn(AndroidSchedulers.mainThread()).
                                    subscribe(() -> {
                                        Navigation.findNavController(requireView()).navigate(ContactFragmentDirections.actionNavContactToNavContacts());
                                    }));
                        })
                        .setNegativeButton(R.string.no, (dialog, id) -> {

                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

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
}