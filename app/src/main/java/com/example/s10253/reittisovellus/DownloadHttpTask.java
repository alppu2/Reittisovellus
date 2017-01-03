package com.example.s10253.reittisovellus;

/**
 * Created by Eki on 3.1.2017.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadHttpTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        String result = "";

        URL url;

        try {
            url = new URL(params[0]);
            String parameters=params[1];

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");

            // Toimii tämä ao.
            //URLConnection conn = url.openConnection();

            // InputStreamReader:lle voi antaa encoding:ksi UTF-8 jos tulee ongelmia ...
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            String line  = "";
            StringBuffer buffer = new StringBuffer();


            writer.write(parameters);
            writer.flush();
            writer.close();
            os.close();
            conn.connect();

            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            while ( (line = br.readLine()) != null )
            {
                buffer.append(line);
                Log.i("line", line);
            }

            //HttpClient httpclient = new HttpClient();

            //conn.getResponseCode()
            result = buffer.toString();
            Log.i("result", result);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //_tView.setText(e.getMessage());
            //Toast.makeText(getApplicationContext(), "catch", Toast.LENGTH_LONG).show();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        //super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        //super.onPreExecute();
    }
}
