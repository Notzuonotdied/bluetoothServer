package com.example.wangyu892449346.bluetoothserver.GPS;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.wangyu892449346.bluetoothserver.util.DataUtil;
import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The type Gps activity.
 */
public class GPSActivity extends AppCompatActivity implements OnPermissionCallback {
    private final static String[] MULTI_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    /**
     * 数据操作类
     */
    public DataUtil dataUtil = new DataUtil().getInstance();
    //权限检测类
    private PermissionHelper mPermissionHelper;
    private GPSLocationManager gpsLocationManager;
    private GPSListener gpsListener;
    private double latitude;
    private double longitude;
    private double time;
    private boolean isFirst = true;

    /**
     * Sets gps listener.
     *
     * @param gpsListener the gps listener
     */
    public void setGpsListener(GPSListener gpsListener) {
        this.gpsListener = gpsListener;
    }

    /**
     * Sets gps location manager.
     *
     * @param gpsLocationManager the gps location manager
     */
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

    /**
     * Start gps.
     */
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

    /**
     * 获取当前系统的时间
     *
     * @return 当前时间
     */
    public double getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("ss", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return Double.valueOf(formatter.format(curDate));
    }

    /**
     * 计算两点间（采用经纬度进行粗略计算）的距离--米
     *
     * @param lat1 初始纬度
     * @param lon1 初始经度
     * @param lat2 当前纬度
     * @param lon2 当前经度
     * @return 两点之间的距离
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

    /**
     * String转int.
     *
     * @param string 目标String
     * @return 返回Int
     */
    public int String2Int(String string) {
        return Integer.valueOf(string);
    }

    private class MyListener implements GPSLocationListener {

        @Override
        public void UpdateLocation(Location location) {
            if (location != null) {
                gpsListener.OnLatitudeChange(location.getLatitude());
                gpsListener.OnLongitudeChange(location.getLongitude());
                if (isFirst) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    time = getTime();
                    isFirst = false;
                }
                // 将计算出来的速度回调
                gpsListener.OnSpeedChange(getDistance(latitude, longitude, location.getLatitude(),
                        location.getLongitude()) / (getTime() - time));
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
                    gpsListener.OnStatusInfoChange("GPS开启");
                    break;
                case GPSProviderStatus.GPS_DISABLED:
                    gpsListener.OnStatusInfoChange("GPS关闭");
                    break;
                case GPSProviderStatus.GPS_OUT_OF_SERVICE:
                    gpsListener.OnStatusInfoChange("GPS不可用");
                    break;
                case GPSProviderStatus.GPS_TEMPORARILY_UNAVAILABLE:
                    gpsListener.OnStatusInfoChange("GPS暂时不可用");
                    break;
                case GPSProviderStatus.GPS_AVAILABLE:
                    gpsListener.OnStatusInfoChange("GPS可用啦");
                    break;
            }
        }
    }
}
