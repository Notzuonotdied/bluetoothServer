package com.example.wangyu892449346.bluetoothserver;

import android.util.Log;

import com.example.wangyu892449346.bluetoothserver.util.DataUtil;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String str = "{x:.+ 0.+ 0.+ 0,y:.+ 123.+ 33.- 66,z:.+ 678.- 1223.+ 0}";
        List<String> list = new ArrayList<>();
        System.out.println(new DataUtil().getList(str.replaceAll(" ","")));
//        str = str.replace('{',' ');
//        str = str.replace('}',' ');
//        str = str.replaceAll(" ","");
//        str = str.replaceAll(".", "");
//        String[] strings = str.split(",");
//        list.addAll(Arrays.asList(strings));


    }
}