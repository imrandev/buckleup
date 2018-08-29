package com.codzunk.buckleup.ui;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.codzunk.buckleup.R;
import com.codzunk.buckleup.advert.AdService;
import com.codzunk.buckleup.constant.Config;
import com.codzunk.buckleup.utils.permission.LocationPermissionImpl;
import com.codzunk.buckleup.service.tracker.TrackerService;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;

import static com.codzunk.buckleup.constant.Config.STATUS_INTENT;

public class MainActivity extends AppCompatActivity {

    private LocationPermissionImpl locationPermission;
    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View rootView = findViewById(R.id.activity_main);
        AdService adService = new AdService(this);

        // Rewarded ads
//        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
//        adService.initRewardedAdsAds(mRewardedVideoAd);

        if (isServiceRunning(TrackerService.class)) {
            // If service already running, simply update UI.
            setTrackingStatus(getString(R.string.tracking));
        } else {
            // Inputs have previously been stored, start validation.
            locationPermission = new LocationPermissionImpl(this, rootView);
            locationPermission.checkLocationPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void setTrackingStatus(String status) {
        ((TextView) findViewById(R.id.status_title)).setText(status);
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(STATUS_INTENT));
        super.onResume();
        mRewardedVideoAd.resume(this);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
        mRewardedVideoAd.pause(this);
    }

    /**
     * Receives status messages from the tracking service.
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setTrackingStatus(intent.getStringExtra("status"));
        }
    };
}
