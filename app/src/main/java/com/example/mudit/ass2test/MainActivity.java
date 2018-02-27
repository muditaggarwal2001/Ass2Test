package com.example.mudit.ass2test;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Timestamp;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private ImageView imageView;
    public GoogleApiClient googleApiClient;
    private TextView textView;
    private Timestamp timestamp;
    private DBHelperClass dbHelperClass;
    private static final int LOCATION_REQUEST_CODE = 101;
    GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.activityimage);
        MybroadcastReceiever mybroadcastReceiever = new MybroadcastReceiever();
        LocalBroadcastManager.getInstance(this).registerReceiver(mybroadcastReceiever,new IntentFilter("ACTION"));
        dbHelperClass = new DBHelperClass(getApplicationContext());
        dbHelperClass.open();
        SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(getApplicationContext(),ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.getClient(getApplicationContext()).requestActivityUpdates(3000,pendingIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        textView = (TextView) findViewById(R.id.Ctime);
        timestamp = new Timestamp(System.currentTimeMillis());
        textView.setText(timestamp.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"Connection to Google API Failed", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //dbHelperClass.close();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkLocationandAddToMap();
    }

    public void checkLocationandAddToMap() {
//Checking if the user has granted the permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//Requesting the Location permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

//Fetching the last known location using the Fus
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

//MarkerOptions are used to create a new Marker.You can specify location, title etc with MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("You are Here");

//Adding the created the marker on the map
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        mMap.setTrafficEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomOut());


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
//Permission Granted
                    checkLocationandAddToMap();
                } else
                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public class MybroadcastReceiever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            Cursor cursor = dbHelperClass.getdata();
            if(cursor.moveToFirst())
            {
                timestamp = Timestamp.valueOf(cursor.getString(2));
                Timestamp temp = new Timestamp(System.currentTimeMillis());
                long x = temp.getTime()-timestamp.getTime();
                timestamp = temp;
                Toast.makeText(getApplicationContext(),cursor.getString(1)+" Ended after "+x/60000+" minutes and "+x/1000+" Seconds", Toast.LENGTH_LONG).show();
                dbHelperClass.truncate();
            }
            Log.e("MainActivity", "Inside onRecieve");
            String act = intent.getStringExtra("activity");
            Intent musicIntent = new Intent("com.android.music.musicservicecommand");
            musicIntent.putExtra("command","play");

            switch(act){
                case "STILL": imageView.setImageResource(R.drawable.still);
                    checkLocationandAddToMap();
                Toast.makeText(getApplicationContext(),"STILL", Toast.LENGTH_LONG).show();
                    break;

                case "WALK": imageView.setImageResource(R.drawable.walking);
                    Toast.makeText(getApplicationContext(),"WALKING", Toast.LENGTH_LONG).show();
                    checkLocationandAddToMap();
                    sendBroadcast(musicIntent);
                    break;

                case "RUN": imageView.setImageResource(R.drawable.man_running);
                    Toast.makeText(getApplicationContext(),"RUNNING", Toast.LENGTH_LONG).show();
                    checkLocationandAddToMap();
                    sendBroadcast(musicIntent);
                    break;

                case "IN VEHICLE": imageView.setImageResource(R.drawable.vehicle);
                    Toast.makeText(getApplicationContext(),"IN VEHICLE", Toast.LENGTH_LONG).show();
                    checkLocationandAddToMap();
                    break;

                default:

                    Toast.makeText(context,"Unknown Activity",Toast.LENGTH_LONG).show();
                    break;
            }
            dbHelperClass.insertData(act,timestamp.toString());
        }
    }
}




