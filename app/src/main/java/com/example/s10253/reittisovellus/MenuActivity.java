package com.example.s10253.reittisovellus;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MenuActivity extends AppCompatActivity {

    String data;
    TextView welcomeText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        welcomeText =(TextView) findViewById(R.id.welcome);
        final Button startTraining = (Button) findViewById(R.id.StartTraining);
        final Button changePass = (Button) findViewById(R.id.ChangePass);
        final Button logout = (Button) findViewById(R.id.Logout);

        welcomeText.setText("Tervetuloa: ");

        startTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });

        changePass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ShowAlert();

                String url = "";

                try {
                    data = new DownloadHttpTask().execute(url).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }

    public void ShowAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Salasanan vaihto");
        final ActionBar.LayoutParams lparams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final EditText vanha = new EditText(this);
        final EditText uusi1 = new EditText(this);
        final EditText uusi2 = new EditText(this);

        vanha.setLayoutParams(lparams);
        vanha.setHint("Vanha salasana");
        vanha.setTransformationMethod(new PasswordTransformationMethod());
        uusi1.setLayoutParams(lparams);
        uusi1.setHint("Uusi salasana");
        uusi1.setTransformationMethod(new PasswordTransformationMethod());
        uusi2.setLayoutParams(lparams);
        uusi2.setHint("Uusi salasana2");
        uusi2.setTransformationMethod(new PasswordTransformationMethod());

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(vanha);
        layout.addView(uusi1);
        layout.addView(uusi2);

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (uusi1.getText().toString().equals(uusi2.getText().toString())) {

                    Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Salasanat eivät täsmää", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Peruuta", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public class DownloadHttpTask extends AsyncTask<String, Void, String>
    {
        private ProgressDialog dialog = new ProgressDialog(MenuActivity.this);

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String result = "";

            URL url;

            try {
                url = new URL(params[0]);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                // Toimii tämä ao.
                //URLConnection conn = url.openConnection();

                // InputStreamReader:lle voi antaa encoding:ksi UTF-8 jos tulee ongelmia ...
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line  = "";
                StringBuffer buffer = new StringBuffer();

                while ( (line = rd.readLine()) != null )
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
            dialog.dismiss();

            // LOGIN MESSAGET
            if (result.toString().equals("OK")) {
                Toast.makeText(getApplicationContext(), "Kirjautuminen onnistui", Toast.LENGTH_LONG).show();
            } else if(result.equals("PASSWORD ERROR")) {
                Toast.makeText(getApplicationContext(), "Salasanasi tai tunnuksesi on väärin!", Toast.LENGTH_LONG).show();
            }

            // REGISTER MESSAGET
            else if(result.equals("EMAIL ERROR")) {
                Toast.makeText(getApplicationContext(), "Käyttäjätunnus on rekisteröity jo", Toast.LENGTH_LONG).show();
            }
            else if(result.equals("REGISTER OK")) {
                Toast.makeText(getApplicationContext(), "Rekisteröinti onnistui", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            //super.onPreExecute();

            dialog.setMessage("Odota haetaan dataa...");
            dialog.setTitle("Odota");
            dialog.show();
        }
    }
}
