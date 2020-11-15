package com.study.skeleton.ui.payment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.study.skeleton.R;
import com.study.skeleton.confirmation.ConfirmationMain;
import com.study.skeleton.data.model.ServerRequest;
import com.study.skeleton.ui.main.MainMenu;

public class paymentmain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentmain);
        Context mContext = this;
        final Button sendButton = findViewById(R.id.sendbutton);
        final EditText amount = findViewById(R.id.amount);
        final Spinner recipient = findViewById(R.id.selectreceiver);
        ConfirmationMain confirmationObject = new ConfirmationMain(this);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder paymentRequestBuilder = new StringBuilder();
                paymentRequestBuilder.append("{\"Request\": ");
                paymentRequestBuilder.append("\"Payment\", ");
                paymentRequestBuilder.append("\"Recipient\": ");
                paymentRequestBuilder.append("\"" + recipient.getSelectedItem().toString() + "\", ");
                paymentRequestBuilder.append("\"Amount\": ");
                paymentRequestBuilder.append("\"" + amount.getText().toString() + "\"}");
                new ServerRequest(new ServerRequest.AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        Log.i("mobisys",output);
                        String confirmationMessage = confirmationObject.displayConfirmationPrompt(output);
                        StringBuilder confirmationRequestBuilder = new StringBuilder();
                        confirmationRequestBuilder.append("{\"Request\": ");
                        confirmationRequestBuilder.append("\"ConfirmationRequest\", ");
                        confirmationRequestBuilder.append("\"ConfirmationMessage\": ");
                        confirmationRequestBuilder.append("\"" + confirmationMessage + "\"}");
                        new ServerRequest(new ServerRequest.AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                Log.i("mobisys",output);


                            }
                        },mContext).execute("https://127.0.0.1:8080/", confirmationRequestBuilder.toString());
                    }
                },mContext).execute("https://127.0.0.1:8080/", paymentRequestBuilder.toString());
            }
        });
    }
}