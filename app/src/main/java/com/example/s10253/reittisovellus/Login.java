package com.example.s10253.reittisovellus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class Login extends AppCompatActivity {

    String data;
    private EditText user;
    private EditText rpass;
    private EditText pass;
    private EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final CheckBox chkReg = (CheckBox) findViewById(R.id.chkRegister);
        user = (EditText) findViewById(R.id.txtUser);
        rpass = (EditText) findViewById(R.id.txtRPass);
        name = (EditText) findViewById(R.id.txtName);
        pass = (EditText) findViewById(R.id.txtPass);
        final Button btnlogin = (Button) findViewById(R.id.btnLogin);

        chkReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (chkReg.isChecked()) {
                    rpass.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    btnlogin.setText("Register");
                } else {
                    rpass.setVisibility(View.GONE);
                    name.setVisibility(View.GONE);
                    btnlogin.setText("Login");
                }
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "";

                if (chkReg.isChecked()) {
                    if (rpass.getText().toString().equals(pass.getText().toString())) {
                        url = "http://codez.savonia.fi/etp4310_2016/mtb3/register.php?email=" + user.getText().toString() + "&password=" + pass.getText().toString() + "&name=" + name.getText().toString();
                    } else {
                        Toast.makeText(getApplicationContext(), "Salasanat eivät täsmää!", Toast.LENGTH_SHORT).show();
                    }
                } else if (!chkReg.isChecked()) {
                    url = "http://codez.savonia.fi/etp4310_2016/mtb3/login.php?email=" + user.getText().toString() + "&password=" + pass.getText().toString();
                }



                try {
                    data = new DownloadHttpTask().execute(url).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if (data.equals("OK") || data.equals(("REGISTER OK"))) {
                    startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                }
            }
        });
    }

    public class DownloadHttpTask extends AsyncTask<String, Void, String>
    {
        private ProgressDialog dialog = new ProgressDialog(Login.this);

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
