package com.study.skeleton.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import com.study.skeleton.confirmation.ConfirmationMain;

import com.study.skeleton.R;
import com.study.skeleton.data.model.ServerRequest;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Context mContext = this;
        final Button enableConfirmationButton = findViewById(R.id.enableConfirmation);
        ConfirmationMain confirmationObject = new ConfirmationMain(this);
        enableConfirmationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.i("mobisys","YOLO");
                StringBuilder InitRegistrationBuilder = new StringBuilder();
                InitRegistrationBuilder.append("{\"Request\": ");
                InitRegistrationBuilder.append("\"RegisterConfirmation\"}");
                new ServerRequest(new ServerRequest.AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        Log.i("mobisys",output);
                        String certificateChainString=confirmationObject.initializeRegistration(output);
                        StringBuilder CertificateChainBuilder = new StringBuilder();
                        CertificateChainBuilder.append("{\"Request\": ");
                        CertificateChainBuilder.append("\"RegisterCertificates\",");
                        CertificateChainBuilder.append("\"CertificateChain\": ");
                        CertificateChainBuilder.append("\""+certificateChainString+"\"}");
                        new ServerRequest(new ServerRequest.AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                Log.i("mobisys",output);
                            }
                        },mContext).execute("https://127.0.0.1:8080/",CertificateChainBuilder.toString());
                    }
                },mContext).execute("https://127.0.0.1:8080/",InitRegistrationBuilder.toString());
                enableConfirmationButton.setEnabled(false);
            }
        });


    }

}