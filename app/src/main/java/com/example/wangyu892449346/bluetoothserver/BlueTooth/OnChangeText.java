package com.example.wangyu892449346.bluetoothserver.BlueTooth;

import java.util.List;

/**
 * Created by wangyu892449346 on 4/17/17.
 * BlueTooth接口
 */

public interface OnChangeText {
    void changeText(String info);
    void handleMsg(List<String> list, char what);
}
