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

    public boolean isNavOverStep(String content) {
        int num = Integer.valueOf(content);
        if (num > 90) {
            return true;
        } else if (num > 80) {
            return Constant.edit89Num > num / Constant.speed;
        } else if (num > 70) {
            return Constant.edit78Num > num / Constant.speed;
        } else if (num > 60) {
            return Constant.edit56Num > num / Constant.speed;
        } else if (num > 50) {
            return Constant.edit45Num > num / Constant.speed;
        } else if (num > 40) {
            return Constant.edit34Num > num / Constant.speed;
        } else if (num > 30) {
            return Constant.edit23Num > num / Constant.speed;
        } else if (num > 20) {
            return Constant.edit12Num > num / Constant.speed;
        } else if (num > 10) {
            return true;
        }
        return true;
    }

    public List<String> getList(String managers){
        List<String> ls=new ArrayList<String>();
        Pattern pattern = Pattern.compile("(?<=\\()(.+?)(?=\\))");
        Matcher matcher = pattern.matcher(managers);
        while(matcher.find())
            ls.add(matcher.group());
        return ls;
    }
}
