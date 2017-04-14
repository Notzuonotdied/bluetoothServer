package com.example.wangyu892449346.bluetoothserver.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wangyu892449346.bluetoothserver.GPS.GPSListener;
import com.example.wangyu892449346.bluetoothserver.GPS.GPSLocationListener;
import com.example.wangyu892449346.bluetoothserver.GPS.GPSLocationManager;
import com.example.wangyu892449346.bluetoothserver.GPS.GPSProviderStatus;
import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;

import java.util.Arrays;

public class GPSActivity extends AppCompatActivity implements OnPermissionCallback {
    //权限检测类
    private PermissionHelper mPermissionHelper;
    private final static String[] MULTI_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private GPSLocationManager gpsLocationManager;
    public void setGpsListener(GPSListener gpsListener) {
        this.gpsListener = gpsListener;
    }

    public void setGpsLocationManager(GPSLocationManager gpsLocationManager) {
        this.gpsLocationManager = gpsLocationManager;
    }

    private GPSListener gpsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermissions();
        initData();
        initViews();
        super.onCreate(savedInstanceState);
    }

    private void checkPermissions() {
        mPermissionHelper = PermissionHelper.getInstance(GPSActivity.this);
        mPermissionHelper.request(MULTI_PERMISSIONS);
    }

    private void initData() {
        gpsLocationManager = GPSLocationManager.getInstances(GPSActivity.this);
    }

    private void initViews() {
//        ((Button) findViewById(R.id.btn_gps)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                gpsLocationManager.start(new MyListener());
//            }
//        });
    }

    public void startGPS() {
        gpsLocationManager.start(new MyListener());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPermissionHelper.onActivityForResult(requestCode);
    }

    @Override
    public void onPermissionGranted(@NonNull String[] permissionName) {

    }

    @Override
    public void onPermissionDeclined(@NonNull String[] permissionName) {

    }

    @Override
    public void onPermissionPreGranted(@NonNull String permissionsName) {

    }

    @Override
    public void onPermissionNeedExplanation(@NonNull String permissionName) {

    }

    @Override
    public void onPermissionReallyDeclined(@NonNull String permissionName) {

    }

    @Override
    public void onNoPermissionNeeded() {

    }

    class MyListener implements GPSLocationListener {

        @Override
        public void UpdateLocation(Location location) {
            if (location != null) {
                gpsListener.setLatitudeView(location.getLatitude());
                gpsListener.setLongitudeView(location.getLongitude());
            }
        }

        @Override
        public void UpdateStatus(String provider, int status, Bundle extras) {
            if ("gps" == provider) {
                Toast.makeText(GPSActivity.this, "定位类型：" + provider, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void UpdateGPSProviderStatus(int gpsStatus) {
            switch (gpsStatus) {
                case GPSProviderStatus.GPS_ENABLED:
                    Toast.makeText(GPSActivity.this, "GPS开启", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_DISABLED:
                    Toast.makeText(GPSActivity.this, "GPS关闭", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_OUT_OF_SERVICE:
                    Toast.makeText(GPSActivity.this, "GPS不可用", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_TEMPORARILY_UNAVAILABLE:
                    Toast.makeText(GPSActivity.this, "GPS暂时不可用", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_AVAILABLE:
                    Toast.makeText(GPSActivity.this, "GPS可用啦", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
