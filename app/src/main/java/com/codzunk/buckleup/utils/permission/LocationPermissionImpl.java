package com.codzunk.buckleup.utils.permission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.codzunk.buckleup.R;
import com.codzunk.buckleup.service.tracker.TrackerService;
import com.codzunk.buckleup.utils.PermissionUtils;

import static android.content.Context.LOCATION_SERVICE;
import static com.codzunk.buckleup.constant.Config.PERMISSIONS_REQUEST;

public class LocationPermissionImpl implements LocationPermission {
    private AppCompatActivity activity;
    private View rootView;

    public LocationPermissionImpl(AppCompatActivity activity, View rootView) {
        this.activity = activity;
        this.rootView = rootView;
    }

    @Override
    public void checkLocationPermission() {
        int locationPermission = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int storagePermission = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(activity, PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST);
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(activity, PERMISSIONS_REQUEST,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else {
            checkGpsEnabled();
        }
    }

    @Override
    public void checkGpsEnabled() {
        LocationManager lm = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        if (lm != null) {
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                reportGpsError();
            } else {
                resolveGpsError();
                startLocationService();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            // We request storage perms as well as location perms, but don't care
            // about the storage perms - it's just for debugging.
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        reportPermissionsError();
                    } else {
                        resolvePermissionsError();
                        checkGpsEnabled();
                    }
                }
            }
        }
    }

    @Override
    public void reportPermissionsError() {
        Snackbar snackbar = Snackbar
                .make(rootView, activity.getString(
                        R.string.location_permission_required), Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.enable, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(android.provider.Settings
                                .ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + activity.getPackageName()));
                        activity.startActivity(intent);
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(
                android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    @Override
    public void reportGpsError() {
        Snackbar snackbar = Snackbar
                .make(rootView, activity.getString(R.string
                        .gps_required), Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.enable, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id
                .snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    @Override
    public void resolveGpsError() {

    }

    @Override
    public void resolvePermissionsError() {

    }

    private void startLocationService() {
        // Before we start the service, confirm that we have extra power usage privileges.
        activity.startService(new Intent(activity, TrackerService.class));
    }

    private void stopLocationService() {
        activity.stopService(new Intent(activity, TrackerService.class));
    }
}
