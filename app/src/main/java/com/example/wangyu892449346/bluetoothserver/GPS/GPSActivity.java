package com.example.wangyu892449346.bluetoothserver.GPS;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.wangyu892449346.bluetoothserver.util.DataUtil;
import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GPSActivity extends AppCompatActivity implements OnPermissionCallback {
    private final static String[] MULTI_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    public DataUtil dataUtil = new DataUtil().getInstance();
    //权限检测类
    private PermissionHelper mPermissionHelper;
    private GPSLocationManager gpsLocationManager;
    private GPSListener gpsListener;
    private double latitude;
    private double longitude;
    private double time;
    private boolean isFirst = true;

    public void setGpsListener(GPSListener gpsListener) {
        this.gpsListener = gpsListener;
    }

    public void setGpsLocationManager(GPSLocationManager gpsLocationManager) {
        this.gpsLocationManager = gpsLocationManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermissions();
        initData();
        super.onCreate(savedInstanceState);
    }

    private void checkPermissions() {
        mPermissionHelper = PermissionHelper.getInstance(GPSActivity.this);
        mPermissionHelper.request(MULTI_PERMISSIONS);
    }

    private void initData() {
        gpsLocationManager = GPSLocationManager.getInstances(GPSActivity.this);
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

    public double getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("ss", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return Double.valueOf(formatter.format(curDate));
    }

    /**
     * 计算两点间的距离--米
     */
    public double getDistance(double lat1, double lon1,
                              double lat2, double lon2) {
        float[] results = new float[1];
        try {
            Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results[0];
    }

    public int String2Int(String string) {
        return Integer.valueOf(string);
    }

    private class MyListener implements GPSLocationListener {

        @Override
        public void UpdateLocation(Location location) {
            if (location != null) {
                gpsListener.setLatitudeView(location.getLatitude());
                gpsListener.setLongitudeView(location.getLongitude());
                if (isFirst) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    time = getTime();
                    isFirst = false;
                }
                gpsListener.setSpeed(getDistance(latitude, longitude, location.getLatitude(),
                        location.getLongitude()) / (getTime() - time));
                Log.i("速度", "time = " + time);
                time = getTime();
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }

        @Override
        public void UpdateStatus(String provider, int status, Bundle extras) {
            if (TextUtils.equals("gps", provider)) {
                //Toast.makeText(GPSActivity.this, "定位类型：" + provider, Toast.LENGTH_SHORT).show();
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
                    //Toast.makeText(GPSActivity.this, "GPS可用啦", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
