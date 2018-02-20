package com.example.mudit.ass2test;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    static ImageView imageView;
    public GoogleApiClient googleApiClient;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.activityimage);
        MybroadcastReceiever mybroadcastReceiever = new MybroadcastReceiever();
        LocalBroadcastManager.getInstance(this).registerReceiver(mybroadcastReceiever,new IntentFilter("ACTION"));
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
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"Connection to Google API Failed", Toast.LENGTH_LONG).show();
    }

    public class MybroadcastReceiever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("MainActivity", "Inside onRecieve");
            String act = intent.getStringExtra("activity");
            switch(act){
                case "STILL": imageView.setImageResource(R.drawable.still);
                break;

                case "WALK": imageView.setImageResource(R.drawable.walking);
                    break;

                case "RUN": imageView.setImageResource(R.drawable.man_running);
                    break;

                case "IN VEHICLE": imageView.setImageResource(R.drawable.vehicle);
                    break;
                default:
                    Toast.makeText(context,"Unknown Activity",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}
