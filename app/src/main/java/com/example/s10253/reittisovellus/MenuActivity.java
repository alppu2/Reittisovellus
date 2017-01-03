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

        String params = "userId="+getIntent().getStringExtra("USER_ID");
        String url = "http://codez.savonia.fi/etp4310_2016/mtb3/getUser.php";
        String name="";
        try {
            Toast.makeText(getApplicationContext(), params, Toast.LENGTH_LONG).show();
            name = new DownloadHttpTask().execute(url,params).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        welcomeText.setText("Tervetuloa: "+name);

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

                String url = "http://codez.savonia.fi/etp4310_2016/mtb3/ChangePassword.php";

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

}
