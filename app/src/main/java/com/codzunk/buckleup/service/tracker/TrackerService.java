package com.codzunk.buckleup.service.tracker;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.codzunk.buckleup.R;
import com.codzunk.buckleup.ui.MainActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.codzunk.buckleup.constant.Config.FASTEST_INTERVAL;
import static com.codzunk.buckleup.constant.Config.FOREGROUND_SERVICE_ID;
import static com.codzunk.buckleup.constant.Config.NOTIFICATION_ID;
import static com.codzunk.buckleup.constant.Config.STATUS_INTENT;
import static com.codzunk.buckleup.constant.Config.UPDATE_INTERVAL;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class TrackerService extends Service implements LocationListener {

    private static final String TAG = TrackerService.class.getSimpleName();
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;

    public TrackerService() {

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //setStatusMessage(getString(R.string.connecting));
        startLocationUpdates();
    }

    @Override
    public void onDestroy() {
        // Set activity title to not tracking.
        setStatusMessage(getString(R.string.not_tracking));
        // Stop the persistent notification.
        mNotificationManager.cancel(NOTIFICATION_ID);
        super.onDestroy();
    }

    /**
     * Pushes a new status to Firebase when location changes.
     */
    @Override
    public void onLocationChanged(Location location) {

        buildNotification();
        Map<String, Object> transportStatus = new HashMap<>();
        transportStatus.put("lat", location.getLatitude());
        transportStatus.put("lng", location.getLongitude());
        transportStatus.put("time", new Date().getTime());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("location").setValue(transportStatus);

        NetworkInfo info = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        boolean connected = info != null && info.isConnectedOrConnecting();
        setStatusMessage(connected ? String.valueOf(location.getLatitude()) : getString(R.string.not_tracking));
    }

    private void buildNotification() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationBuilder = new NotificationCompat.Builder(this, "default")
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.bus_white)
                .setOngoing(true)
                .setContentIntent(resultPendingIntent);
        startForeground(FOREGROUND_SERVICE_ID, mNotificationBuilder.build());
    }

    /**
     * Sets the current status message (connecting/tracking/not tracking).
     */
    private void setStatusMessage(String value) {

        mNotificationBuilder.setContentText(value);
        mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());

        // Also display the status message in the activity.
        Intent intent = new Intent(STATUS_INTENT);
        intent.putExtra("status", value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        Toast.makeText(this, "Update", Toast.LENGTH_SHORT).show();
    }

    protected void startLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(UPDATE_INTERVAL);
        request.setFastestInterval(FASTEST_INTERVAL);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(request);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        try {
            locationClient.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    // do work here
                    onLocationChanged(locationResult.getLastLocation());
                }
            },
            Looper.myLooper());
        } catch (SecurityException ex){
            ex.printStackTrace();
        }
    }

    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        try {
            locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
        } catch (SecurityException e){
            e.printStackTrace();
        }
    }
}
