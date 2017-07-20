package com.example.wangyu892449346.bluetoothserver.GPS;

/**
 * Created by wangyu892449346 on 4/14/17.
 * 接口
 */
public interface GPSListener {
    /**
     * 经度发生变化的时候回调
     *
     * @param longitude 经度
     */
    void OnLongitudeChange(double longitude);

    /**
     * 纬度发生变化的时候回调
     *
     * @param latitude 纬度
     */
    void OnLatitudeChange(double latitude);

    /**
     * 当速度发生变化的时候回调
     *
     * @param speed 速度
     */
    void OnSpeedChange(double speed);

    /**
     * 当GPS发生变化的时候回调
     *
     * @param info 提示信息
     */
    void OnStatusInfoChange(String info);
}
