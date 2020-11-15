package com.study.skeleton.data.model;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.HttpCookie;
import java.net.URL;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;

import android.content.Context;
import android.util.Log;

import com.study.skeleton.R;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class ServerRequest extends AsyncTask <String, Integer, String> {
    private Context mContext;
    //private HttpCookie cookie = new HttpCookie("name","");
    public interface AsyncResponse {
        void processFinish(String output)   ;
    }

    public AsyncResponse delegate=null;
//    private Context mContext;
    public ServerRequest(AsyncResponse delegate, Context context){
        this.delegate=delegate;
        mContext=context;
    }

    private void writeStream(OutputStream out, String output){
//        Log.i("mobisys","WOHOO");
        //HashMap<String, String> HM=new HashMap<String, String>(){

        //    private  static final long serialVersionUID = 1L;

        //    {

        //        put("Content-Length",)

        //    }

        //}

        try {

            out.write(output.getBytes("ISO-8859-1"));
            out.flush();
        } catch(IOException e){
            Log.i("mobisys",e.toString());
        }

    }

    private String readStream(InputStream in){
        String finalInput="";
        //Log.i("mobisys","WOHOO2");
        try{
            BufferedReader br= new BufferedReader(new InputStreamReader(in));
            StringBuilder input = new StringBuilder();
            for (String line; (line=br.readLine()) !=null;){
                input.append(line).append('\n');
            }
            //Log.i("mobisys", Integer.toString(in.available()));
            //Log.i("mobisys",input.toString());
            finalInput= input.toString();
        } catch(IOException e){
            Log.i("mobisys",e.toString());
        }
        //Log.i("mobisys",finalInput);
        return  finalInput;
    }

    @Override
    protected String doInBackground(String... urls) {
        String result= null;
        if (!isCancelled() && urls != null && urls.length > 0) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);
                String resultString = webRequest(url, urls[1]);
                if (resultString != null) {
                    result = resultString;
                } else {
                    throw new IOException("No response received.");
                }
            } catch(Exception e) {
                result = e.toString();
            }
        }
        return result;
    }

    private String webRequest(URL url, String output) throws IOException {
        String response = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = mContext.getResources().openRawResource(R.raw.servercert);
            Certificate ca;
            ca = cf.generateCertificate(caInput);
//            Log.i("mobisys","ca=" + ((X509Certificate) ca).getSubjectDN());
            caInput.close();

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null,null);
            keyStore.setCertificateEntry("ca",ca);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null,tmf.getTrustManagers(),null);

            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
//            Log.i("mobisys", CookieHandler.getDefault().get(url.toURI(), urlConnection.getRequestProperties()).getOrDefault("Cookie", Collections.singletonList("NA")).get(0));
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setDoOutput(true);
            urlConnection.setFixedLengthStreamingMode(output.getBytes("ISO-8859-1").length);
            urlConnection.setRequestProperty("Content-Type","application/json; utf-8");
            urlConnection.setRequestProperty("Cookie",CookieHandler.getDefault().get(url.toURI(), urlConnection.getRequestProperties()).getOrDefault("Cookie", Collections.singletonList("NA")).get(0));


            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            writeStream(out,output);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            response=readStream(in);

            //cookie.setValue(urlConnection.getHeaderField("Set-Cookie").split("=")[1]);
            CookieHandler.getDefault().put(url.toURI(),urlConnection.getHeaderFields());

            urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}
