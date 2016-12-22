package com.example.s10253.reittisovellus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.*;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.R.attr.data;

public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LocationSource.OnLocationChangedListener, LocationListener {

    private GoogleMap mMap;
    MapView mMapView;
    private View rootView;
    LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    Button start;
    boolean trainingStarted = false;
    int rdn; //route dot number kertoo monesko reittipiste kyseessä
    ArrayList<Double> routeDotLattitude = new ArrayList<>();
    ArrayList<Double> routeDotLongitude = new ArrayList<>();

    public static final String TAG = MapsActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 10;
    PolylineOptions poption = new PolylineOptions();
    private String routeName="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(routeName.equals(""))
            ShowAlert();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        //mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        start = (Button) findViewById(R.id.start);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(trainingStarted==false)
                    StartTraining();
                else
                    StopTraining();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
        mGoogleApiClient.connect();
        Toast.makeText(getApplicationContext(), "Tulit takaisin", Toast.LENGTH_LONG);
    }

     @Override
     public void onMapReady(GoogleMap googleMap) {
         mMap = googleMap;
     }

    @Override
    public void onConnected(@Nullable Bundle bundle) {;
        //Toast.makeText(getApplicationContext(), "Yhdistetty!", Toast.LENGTH_SHORT).show();

        checkPermission();
        Location location = LocationServices.FusedLocationApi.getLastLocation (mGoogleApiClient);
        if(location != null)
            SetLocation(location);

    }
    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(getApplicationContext(), "Pause", Toast.LENGTH_SHORT).show();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        SetLocation(location);
        Toast.makeText(getApplicationContext(), "Sijainti vaihtunut", Toast.LENGTH_SHORT).show();
        poption.add(new LatLng(location.getLatitude(), location.getLongitude())).width(15).color(Color.BLACK).geodesic(true);
        mMap.addPolyline(poption);

        routeDotLattitude.add(location.getLatitude());
        routeDotLongitude.add(location.getLongitude());


    }
    public void SetLocation(Location location) {
        if (location == null) {
            Toast.makeText(getApplicationContext(), "Ei sijaintia...", Toast.LENGTH_SHORT).show();
        }
        else {
            if(mMap!=null) {
                //mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Olet tässä!"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),16));
                //Toast.makeText(getApplicationContext(), "Sijainti löyty!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void checkPermission(){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1600);
        }
    }
    public void StartTraining() {
        trainingStarted = true;
        Toast.makeText(getApplicationContext(), "Harjoittelu aloitettu!", Toast.LENGTH_SHORT).show();

        start.setText("Lopeta harjoittelu");

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        checkPermission();
        Location location = LocationServices.FusedLocationApi.getLastLocation (mGoogleApiClient);
        routeDotLattitude.add(location.getLatitude());
        routeDotLongitude.add(location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Aloitus piste"));

    }
    public void StopTraining() {

        start.setText("Aloita harjoittelu");
        trainingStarted = false;
        Toast.makeText(getApplicationContext(),"Harjoitus lopetettu!", Toast.LENGTH_LONG).show();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        checkPermission();
        Location location = LocationServices.FusedLocationApi.getLastLocation (mGoogleApiClient);
        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Lopetus piste"));


       new InsertDataAsync().execute(routeDotLattitude, routeDotLongitude);


    }
    public void ShowAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reitin nimi:");

        final EditText input = new EditText(this);

        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               routeName = input.getText().toString();
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
        private ProgressDialog dialog = new ProgressDialog(MapsActivity.this);

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

                //while ( (line = rd.readLine()) != null )
                //{
                //    buffer.append(line);
                //    Log.i("line", line);
                //}
//
                ////HttpClient httpclient = new HttpClient();
//
                ////conn.getResponseCode()
                //result = buffer.toString();
                //Log.i("result", result);
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

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            //super.onPreExecute();

           dialog.setMessage("Odota haetaan dataaa ...");
           dialog.setTitle("Odota");
           dialog.show();
        }
    }
    public class InsertDataAsync extends AsyncTask<ArrayList, Integer, Void>{


        @Override
        protected Void doInBackground(ArrayList... values) {
            ArrayList rpLongitude = values[0], rpLatitude=values[1];
            for(int i=0; i<rpLatitude.size(); i++ ){
                String data="";


                try {
                    URL url=new URL("http://codez.savonia.fi/etp4310_2016/mtb3/AddRoutepoints.php?routePointLatitude="+rpLatitude.get(i)+"&routePointLongitude="+rpLongitude.get(i)+"&routeId=1");
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String line  = "";
                    StringBuffer buffer = new StringBuffer();

                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    //_tView.setText(e.getMessage());
                    //Toast.makeText(getApplicationContext(), "catch", Toast.LENGTH_LONG).show();
                }
                //Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();

                String result = "";

            }
            return null;
        }
        protected void onPostExecute(Void asd) {
            // TODO Auto-generated method stub
            //super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "Siirretty", Toast.LENGTH_SHORT).show();

        }

    }
}

