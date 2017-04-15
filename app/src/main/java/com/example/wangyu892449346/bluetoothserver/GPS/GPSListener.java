package com.example.wangyu892449346.bluetoothserver.GPS;

/**
 * Created by wangyu892449346 on 4/14/17.
 */

public interface GPSListener {
    void setLongitudeView(double longitude);
    void setLatitudeView(double latitude);
    void setSpeed(double speed);
}
