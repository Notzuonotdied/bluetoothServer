package com.example.wangyu892449346.bluetoothserver.GPS;

/**
 * Created by wangyu892449346 on 4/14/17.
 * 常量
 */
class GPSProviderStatus {
    /**
     * 用户手动开启GPS
     * */
    static final int GPS_ENABLED = 0;
    /**
     * 用户手动关闭GPS
     * */
    static final int GPS_DISABLED = 1;
    /**
     * 服务已停止，并且在短时间内不会改变
     * */
    static final int GPS_OUT_OF_SERVICE = 2;
    /**
     * 服务暂时停止，并且在短时间内会恢复
     * */
    static final int GPS_TEMPORARILY_UNAVAILABLE = 3;
    /**
     * 服务正常有效
     * */
    static final int GPS_AVAILABLE = 4;
}
