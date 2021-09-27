package org.flinkcoin.mobile.demo.ui.init;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.flinkcoin.mobile.demo.FlinkApplication;
import org.flinkcoin.mobile.demo.LoginActivity;
import org.flinkcoin.mobile.demo.R;
import org.flinkcoin.mobile.demo.databinding.FragmentCreateWalletPinBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CreateWalletPinFragment extends Fragment {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private FragmentCreateWalletPinBinding binding;
    private CreateWalletViewModel createWalletViewModel;

    private String pin;

    private String firstPin;

    private boolean confirmState;
    private boolean pinMismatch;
    private Map<Integer, ImageView> pinDots;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateWalletPinBinding.inflate(inflater, container, false);
        createWalletViewModel = new ViewModelProvider(requireActivity()).get(CreateWalletViewModel.class);

        pin = "";
        firstPin = "";
        confirmState = false;
        pinDots = new HashMap<>();
        pinDots.put(1, binding.pinInput.imagePinCircle1);
        pinDots.put(2, binding.pinInput.imagePinCircle2);
        pinDots.put(3, binding.pinInput.imagePinCircle3);
        pinDots.put(4, binding.pinInput.imagePinCircle4);
        pinDots.put(5, binding.pinInput.imagePinCircle5);
        pinDots.put(6, binding.pinInput.imagePinCircle6);

        binding.keyboard.button0.setOnClickListener((v) -> addNumber("0"));
        binding.keyboard.button1.setOnClickListener((v) -> addNumber("1"));
        binding.keyboard.button2.setOnClickListener((v) -> addNumber("2"));
        binding.keyboard.button3.setOnClickListener((v) -> addNumber("3"));
        binding.keyboard.button4.setOnClickListener((v) -> addNumber("4"));
        binding.keyboard.button5.setOnClickListener((v) -> addNumber("5"));
        binding.keyboard.button6.setOnClickListener((v) -> addNumber("6"));
        binding.keyboard.button7.setOnClickListener((v) -> addNumber("7"));
        binding.keyboard.button8.setOnClickListener((v) -> addNumber("8"));
        binding.keyboard.button9.setOnClickListener((v) -> addNumber("9"));
        binding.keyboard.buttonDel.setOnClickListener((v) -> removeNumber());

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

    private void removeNumber() {
        binding.textEnterPin.setText(confirmState ? getResources().getString(R.string.confirm_pin) : getResources().getString(R.string.enter_pin));

        if (pinMismatch) {
            unFillAllCircles();
            pinMismatch = false;
        }

        pin = pin.length() > 0 ? pin.substring(0, pin.length() - 1) : pin;
        unFillCircle(pin.length());
    }

    private void addNumber(String number) {
        binding.textEnterPin.setText(confirmState ? getResources().getString(R.string.confirm_pin) : getResources().getString(R.string.enter_pin));

        if (pinMismatch) {
            unFillAllCircles();
            pinMismatch = false;
        }

        pin = pin + number;
        fillCircle(pin.length());

        if (FlinkApplication.PIN_LENGTH == pin.length()) {
            disableKeyboard();
            checkPin();
        }

    }

    private void checkPin() {
        if (confirmState) {

            Disposable d = Observable.timer(100, TimeUnit.MILLISECONDS).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(a -> {

                        if (pin.equals(firstPin)) {
                            createWalletViewModel.setPin(pin);

                            disableKeyboard();
                            compositeDisposable.add(createWalletViewModel.saveWallet().subscribe(success -> {
                                if (success) {
                                    Intent intent = new Intent(getActivity().getBaseContext(), LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                                enableKeyboard();
                            }, throwable -> enableKeyboard()));

                        } else {
                            confirmState = false;
                            pin = "";
                            firstPin = "";
                            pinMismatch = true;

                            binding.textEnterPin.setText(getResources().getString(R.string.pin_does_not_match));
                            enableKeyboard();
                        }
                    });
            compositeDisposable.add(d);
        } else {

            Disposable d = Observable.timer(100, TimeUnit.MILLISECONDS).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(a -> {
                        firstPin = pin;
                        pin = "";
                        confirmState = true;

                        binding.textEnterPin.setText(getResources().getString(R.string.confirm_pin));
                        unFillAllCircles();
                        enableKeyboard();
                    });
            compositeDisposable.add(d);
        }
    }

    private void enableKeyboard() {
        setKeyboardClickable(true);
    }

    private void disableKeyboard() {
        setKeyboardClickable(false);
    }

    private void setKeyboardClickable(boolean clickable) {
        binding.keyboard.button0.setClickable(clickable);
        binding.keyboard.button1.setClickable(clickable);
        binding.keyboard.button2.setClickable(clickable);
        binding.keyboard.button3.setClickable(clickable);
        binding.keyboard.button4.setClickable(clickable);
        binding.keyboard.button5.setClickable(clickable);
        binding.keyboard.button6.setClickable(clickable);
        binding.keyboard.button7.setClickable(clickable);
        binding.keyboard.button8.setClickable(clickable);
        binding.keyboard.button9.setClickable(clickable);
        binding.keyboard.buttonDel.setClickable(clickable);
        binding.keyboard.buttonOk.setClickable(clickable);
    }

    private void fillCircle(int pinLength) {
        ImageView imageView = pinDots.get(pinLength);
        if (imageView != null) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.pin_circle_full, getActivity().getTheme()));
        }
    }

    private void unFillCircle(int pinLength) {
        ImageView imageView = pinDots.get(pinLength + 1);
        if (imageView != null) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.pin_circle_empty, getActivity().getTheme()));
        }
    }

    private void unFillAllCircles() {
        pinDots.entrySet().forEach(imageView -> imageView.getValue().setImageDrawable(getResources().getDrawable(R.drawable.pin_circle_empty, getActivity().getTheme())));
    }

}