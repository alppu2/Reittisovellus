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

    //Moi alppu!
    //Ja Huisko
    //tämähän on oikein näppärä

    //Moi aLEKSI!!!!!!!!
    
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
                String params = "";
                if (chkReg.isChecked()) {
                    if (rpass.getText().toString().equals(pass.getText().toString())) {
                        params="email=" + user.getText().toString() + "&password=" + pass.getText().toString() + "&name=" + name.getText().toString();
                        url = "http://codez.savonia.fi/etp4310_2016/mtb3/register.php";
                    } else {
                        Toast.makeText(getApplicationContext(), "Salasanat eivät täsmää!", Toast.LENGTH_SHORT).show();
                    }
                } else if (!chkReg.isChecked()) {
                    params = "email=" + user.getText().toString() + "&password=" + pass.getText().toString();
                    url = "http://codez.savonia.fi/etp4310_2016/mtb3/login.php";
                }

                try {
                    data = new DownloadHttpTask().execute(url, params).get();

                    if (data.toString().equals("OK")) {
                        Toast.makeText(getApplicationContext(), "Kirjautuminen onnistui", Toast.LENGTH_LONG).show();
                    } else if(data.equals("PASSWORD ERROR")) {
                        Toast.makeText(getApplicationContext(), "Salasanasi tai tunnuksesi on väärin!", Toast.LENGTH_LONG).show();
                    }

                    // REGISTER MESSAGET
                    else if(data.equals("EMAIL ERROR")) {
                        Toast.makeText(getApplicationContext(), "Käyttäjätunnus on rekisteröity jo", Toast.LENGTH_LONG).show();
                    }
                    else if(data.equals("REGISTER OK")) {
                        Toast.makeText(getApplicationContext(), "Rekisteröinti onnistui", Toast.LENGTH_LONG).show();
                    }
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
    
}
