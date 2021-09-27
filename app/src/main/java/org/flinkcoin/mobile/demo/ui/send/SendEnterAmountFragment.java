package org.flinkcoin.mobile.demo.ui.send;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.flinkcoin.mobile.demo.R;
import org.flinkcoin.mobile.demo.databinding.FragmentSendEnterAmountBinding;

import java.text.DecimalFormat;

public class SendEnterAmountFragment extends Fragment {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    private String valueString;
    private long value;

    private SendViewModel sendViewModel;
    private FragmentSendEnterAmountBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSendEnterAmountBinding.inflate(inflater, container, false);
        sendViewModel = new ViewModelProvider(requireActivity()).get(SendViewModel.class);

        binding.keyboard.button0.setOnClickListener((v) -> addNumber(0));
        binding.keyboard.button1.setOnClickListener((v) -> addNumber(1));
        binding.keyboard.button2.setOnClickListener((v) -> addNumber(2));
        binding.keyboard.button3.setOnClickListener((v) -> addNumber(3));
        binding.keyboard.button4.setOnClickListener((v) -> addNumber(4));
        binding.keyboard.button5.setOnClickListener((v) -> addNumber(5));
        binding.keyboard.button6.setOnClickListener((v) -> addNumber(6));
        binding.keyboard.button7.setOnClickListener((v) -> addNumber(7));
        binding.keyboard.button8.setOnClickListener((v) -> addNumber(8));
        binding.keyboard.button9.setOnClickListener((v) -> addNumber(9));
        binding.keyboard.buttonDel.setOnClickListener((v) -> removeNumber());
        binding.keyboard.buttonOk.setText(getString(R.string.button_ok));
        binding.keyboard.buttonOk.setOnClickListener((v) -> binding.textInputAmount.getEditText().clearFocus());

        binding.buttonContinue.setOnClickListener((v) -> confirmValue());

        binding.keyboard.getRoot().setVisibility(View.INVISIBLE);
        View.OnFocusChangeListener onFocusChangeListenerAmount = (view, hasFocus) -> {
            if (hasFocus && binding.textInputAmount.getEditText().hasFocus()) {
                binding.keyboard.getRoot().setVisibility(View.VISIBLE);
                binding.buttonContinue.setVisibility(View.GONE);
            } else {
                binding.keyboard.getRoot().setVisibility(View.INVISIBLE);
                binding.buttonContinue.setVisibility(View.VISIBLE);
            }
        };
        binding.textInputAmount.getEditText().setInputType(InputType.TYPE_NULL);
        binding.textInputAmount.getEditText().setOnFocusChangeListener(onFocusChangeListenerAmount);

        View.OnFocusChangeListener onFocusChangeListenerReferenceCode = (view, hasFocus) -> {
            if (!hasFocus && !binding.textInputReferenceCode.getEditText().hasFocus()) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                binding.textInputReferenceCode.getEditText().setText(binding.textInputReferenceCode.getEditText().getText().toString().replace("\n", "").trim());
            }
        };
        binding.textInputReferenceCode.getEditText().setOnFocusChangeListener(onFocusChangeListenerReferenceCode);
        binding.textInputReferenceCode.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (-1 != charSequence.toString().indexOf("\n")) {
                    binding.textInputReferenceCode.getEditText().clearFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            binding.textInputAmount.getEditText().clearFocus();
            binding.textInputReferenceCode.getEditText().clearFocus();
            return true;
        });

        valueString = "";

        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Long savedAmount = sendViewModel.getSendAmount();
        if (null != savedAmount) {
            value = savedAmount / 100_000_000L;
            valueString = DECIMAL_FORMAT.format(value / 100.0);
            updateNumber(value);
        }

        String savedReferenceCode = sendViewModel.getSendReferenceCode();
        if (null != savedReferenceCode) {
            binding.textInputReferenceCode.getEditText().setText(savedReferenceCode);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void addNumber(int number) {
        if (valueString.isEmpty() && 0 == number) {
            return;
        }

        if (valueString.length() >= 10) {
            return;
        }

        value = (value * 10) + number;
        valueString = valueString + number;

        updateNumber(value);
    }

    private void removeNumber() {
        if (valueString.isEmpty()) {
            return;
        }

        value = value / 10;
        valueString = valueString.substring(0, valueString.length() - 1);

        updateNumber(value);
    }

    private void updateNumber(long value) {
        binding.textInputAmount.getEditText().setText(value == 0 ? null : DECIMAL_FORMAT.format(value / 100.0));
    }

    private void confirmValue() {
        sendViewModel.setSendAmount(value * 100_000_000L);
        sendViewModel.setSendReferenceCode(binding.textInputReferenceCode.getEditText().getText().toString());
        Navigation.findNavController(getView()).navigate(SendEnterAmountFragmentDirections.actionNavSendEnterAmountToNavSendConfirmation());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}