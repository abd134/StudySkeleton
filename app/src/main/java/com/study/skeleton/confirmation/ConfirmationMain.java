package com.study.skeleton.confirmation;

import android.content.Context;
import android.util.Pair;

public class ConfirmationMain {
    Context mContext;

     public ConfirmationMain(Context context){
         mContext=context;
     }

     public String initializeRegistration(String initVars){
         //TODO: use initVars that are produced by initializeRegistration on the server
         // to generate required keypair and return the certificateChain as a single string which will be sent to the server
         return "";

     }

     public String displayConfirmationPrompt(String promptDetails){
         //TODO: use promptDetails to generate a confirmationPrompt that the user will confirm.
         // promptDetails is the string returned by the initialize transaction function on the server.
         // return the dataThatWasConfirmed and the signature as one single concatenated string
         // that will be used by verifyConfirmationMessage on the server
         // (Hint: When converting byte arrays to string use the charset ISO-8859-1)

         return "";
     }

}
