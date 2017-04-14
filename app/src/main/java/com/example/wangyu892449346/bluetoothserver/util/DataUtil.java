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

        } else if (num > 80) {

        } else if (num > 70) {

        } else if (num > 60) {

        } else if (num > 50) {

        } else if (num > 40) {

        } else if (num > 30) {

        } else if (num > 20) {

        } else if (num > 10) {

        }
        return false;
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
