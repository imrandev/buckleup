package com.codzunk.buckleup.utils.permission;

public interface LocationPermission {
    void checkLocationPermission();
    void checkGpsEnabled();
    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
    void reportPermissionsError();
    void reportGpsError();
    void resolveGpsError();
    void resolvePermissionsError();
}
