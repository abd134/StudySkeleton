package com.study.skeleton.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.study.skeleton.R;
import com.study.skeleton.ui.payment.paymentmain;
import com.study.skeleton.ui.settings.SettingsActivity;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button paymentButton = findViewById(R.id.Payment);
        final Button settingsButton = findViewById(R.id.settings);
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), paymentmain.class);
                startActivity(i);
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(i);
            }
        });
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean confirmationSwitch = sharedPref.getBoolean(SettingsActivity.confirmationSwitchKey,false);
//        Toast.makeText(this,  confirmationSwitch.toString(), Toast.LENGTH_SHORT).show();
        sharedPref.registerOnSharedPreferenceChangeListener(SettingsActivity.listener);
    }
}