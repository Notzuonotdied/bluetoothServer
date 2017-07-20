package com.example.wangyu892449346.bluetoothserver.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangyu892449346 on 4/14/17.
 * 数据操作
 */
public class DataUtil {

    private volatile DataUtil instance;

    public DataUtil() {
    }

    /**
     * 单例模式
     * */
    public DataUtil getInstance() {
        if (instance == null) {
            synchronized (DataUtil.class) {
                if (instance == null) {
                    instance = new DataUtil();
                }
            }
        }
        return instance;
    }

    public boolean isPitchOverStep(int angle) {
        return Constant.editPitchNum < Math.abs(angle);
    }

    public boolean isYawOverStep(int angle) {
        return Constant.editYawNum < Math.abs(angle);
    }

    public boolean isOverEdge(double gas) {
        return gas > 0.3 || gas < 0.1;
    }

    public boolean isNavOverStep(int angle) {
        angle = Math.abs(angle);
        int speed = Math.abs(Constant.speed);
        if (speed == 0) {
            return false;
        }
        if (angle > 90) {
            return true;
        } else if (angle > 80) {
            return Constant.edit89Num < angle / speed;
        } else if (angle > 70) {
            return Constant.edit78Num < angle / speed;
        } else if (angle > 60) {
            return Constant.edit56Num < angle / speed;
        } else if (angle > 50) {
            return Constant.edit45Num < angle / speed;
        } else if (angle > 40) {
            return Constant.edit34Num < angle / speed;
        } else if (angle > 30) {
            return Constant.edit23Num < angle / speed;
        } else if (angle > 20) {
            return Constant.edit12Num < angle / speed;
        } else if (angle > 10) {
            return false;
        }
        return false;
    }

    /**
     * 将所有出现的数字匹配出来
     * */
    public List<String> getList(String managers) {
        List<String> ls = new ArrayList<>();
//        Pattern pattern = Pattern.compile("\\{x:(.*?),y:(.*?),z:(.*?)\\}");
        Pattern pattern = Pattern.compile("-*\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(managers);
        while (matcher.find())
            ls.add(matcher.group());
        return ls;
    }

    /**
     * 将完整的一个帧匹配出来
     * */
    public List<String> getList4Array(String managers) {
        List<String> ls = new ArrayList<>();
        // Pattern pattern = Pattern.compile("\\{x:(.*?),y:(.*?),z:(.*?)\\}");
        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(managers);
        while (matcher.find())
            ls.add(matcher.group());
        return ls;
    }
}
