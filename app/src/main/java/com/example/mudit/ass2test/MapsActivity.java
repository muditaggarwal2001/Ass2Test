package com.example.mudit.ass2test;

import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

   private static GoogleApiClient googleApiClient;
   private static final int LOCATION_REQUEST_CODE = 101;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_maps);

//Instantiating the GoogleApiClient
       googleApiClient = new GoogleApiClient.Builder(this)
               .addApi(LocationServices.API)
               .addConnectionCallbacks(this)
               .addOnConnectionFailedListener(this)
               .build();

   }

   public void onStart() {
       super.onStart();
// Initiating the connection
       googleApiClient.connect();
   }

   public void onStop() {
       super.onStop();
// Disconnecting the connection
       googleApiClient.disconnect();

   }

   public void onMapReady(GoogleMap googleMap) {
       GoogleMap mMap = googleMap;

       checkLocationandAddToMap();
   }

   //Callback invoked once the GoogleApiClient is connected successfully
   @Override
   public void onConnected(Bundle bundle) {
      if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED  && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED)

       {

           ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
           return;
       }

//Fetching the last known location using the FusedLocationProviderApi
       Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
   }

   @Override
   public void onConnectionSuspended(int i) {

   }

   //Callback invoked if the GoogleApiClient connection fails
   @Override
   public void onConnectionFailed(ConnectionResult connectionResult) {

   }
   @Override
   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
       switch (requestCode) {
           case LOCATION_REQUEST_CODE:
               if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
//Permission Granted
               } else
                   Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
               break;
       }
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
       GoogleMap mMap = null;
       mMap.addMarker(markerOptions);

      // BitmapDescriptor icon= BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.logo));
         //  markerOptions.icon(icon);
          // markerOptions.icon(BitmapDescriptor);//
       mMap.setTrafficEnabled(true);
       mMap.animateCamera(CameraUpdateFactory.zoomIn());
       mMap.animateCamera(CameraUpdateFactory.zoomOut());


       }
}
