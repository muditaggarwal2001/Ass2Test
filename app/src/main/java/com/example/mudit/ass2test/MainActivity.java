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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Timestamp;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private ImageView imageView;
    public GoogleApiClient googleApiClient;
    private TextView textView;
    private Timestamp timestamp;
    private DBHelperClass dbHelperClass;
    private MapsActivity mapsActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.activityimage);
        MybroadcastReceiever mybroadcastReceiever = new MybroadcastReceiever();
        LocalBroadcastManager.getInstance(this).registerReceiver(mybroadcastReceiever,new IntentFilter("ACTION"));
        dbHelperClass = new DBHelperClass(getApplicationContext());
        dbHelperClass.open();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
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
        dbHelperClass.close();
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
                    Toast.makeText(getApplicationContext(),"STILL", Toast.LENGTH_LONG).show();
                    break;

                case "WALK": imageView.setImageResource(R.drawable.walking);
                    Toast.makeText(getApplicationContext(),"WALKING", Toast.LENGTH_LONG).show();
                    mapsActivity.checkLocationandAddToMap();
                    sendBroadcast(musicIntent);
                    break;

                case "RUN": imageView.setImageResource(R.drawable.man_running);
                    Toast.makeText(getApplicationContext(),"RUNNING", Toast.LENGTH_LONG).show();
                    mapsActivity.checkLocationandAddToMap();
                    sendBroadcast(musicIntent);
                    break;

                case "IN VEHICLE": imageView.setImageResource(R.drawable.vehicle);
                    Toast.makeText(getApplicationContext(),"IN VEHICLE", Toast.LENGTH_LONG).show();
                    mapsActivity.checkLocationandAddToMap();
                    break;

                default:
                    Toast.makeText(context,"Unknown Activity",Toast.LENGTH_LONG).show();
                    break;
            }
            dbHelperClass.insertData(act,timestamp.toString());
        }
    }
}




