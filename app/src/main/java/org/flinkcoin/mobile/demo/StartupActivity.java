package org.flinkcoin.mobile.demo;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.flinkcoin.mobile.demo.ui.startup.StartupViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        StartupViewModel startupViewModel = new ViewModelProvider(this).get(StartupViewModel.class);

        startupViewModel.getAccountExist().observe(this, accountExist -> {
            Intent intent = new Intent(getBaseContext(), accountExist ? LoginActivity.class : InitWalletActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        startupViewModel.checkAccountExist();
    }

}