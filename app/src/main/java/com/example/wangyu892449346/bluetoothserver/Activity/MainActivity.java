package com.example.wangyu892449346.bluetoothserver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wangyu892449346.bluetoothserver.BlueTooth.BluetoothActivity;
import com.example.wangyu892449346.bluetoothserver.BlueTooth.OnChangeText;
import com.example.wangyu892449346.bluetoothserver.GPS.GPSListener;
import com.example.wangyu892449346.bluetoothserver.R;

import java.util.List;

public class MainActivity extends BluetoothActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //定义组件
    private TextView statusLabel;
    private EditText editLatitude;
    private EditText editLongitude;
    private EditText editSpeed;
    private TextView speed11;
    private TextView speed12;
    private TextView speed13;
    private TextView speed21;
    private TextView speed22;
    private TextView speed23;
    private TextView speed31;
    private TextView speed32;
    private TextView speed33;
    private FloatingActionButton fab;
    private GPSListener gpsListener = new GPSListener() {
        @Override// 当获取到经度的时候回调
        public void OnLongitudeChange(double longitude) {
            editLongitude.setText(String.valueOf(longitude));
        }

        @Override// 当获取到纬度的时候回调
        public void OnLatitudeChange(double latitude) {
            editLatitude.setText(String.valueOf(latitude));
        }

        @Override// 当计算完一秒内速度的值的时候回调
        public void OnSpeedChange(double speed) {
            editSpeed.setText(String.valueOf(speed));
        }

        @Override
        public void OnStatusInfoChange(String info) {
            statusLabel.setText(info);
        }
    };

    /**
     * 设置消息回调时候的文本回调
     * */
    private OnChangeText onChangeText = new OnChangeText() {
        @Override// 当提示文本有变化的时候回调
        public void changeText(String info) {
            statusLabel.setVisibility(View.VISIBLE);
            statusLabel.setText(info);
        }

        @Override// 当接受并处理完蓝牙发送的数据的时候回调
        public void handleMsg(List<String> list) {
            putTableData(list);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setGpsListener(gpsListener);// 设置回调接口
        this.setOnChangeText(onChangeText);// 设置回调接口
        initView();
        initListener();
    }

    /**
     * 初始化响应事件
     * */
    private void initListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });
    }

    /**
     * 初始化UI控件
     * */
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        statusLabel = (TextView) findViewById(R.id.statusLabel);
        // 表格下属性值
        editLatitude = (EditText) findViewById(R.id.latitude);
        editLongitude = ((EditText) findViewById(R.id.longitude));
        editSpeed = (EditText) findViewById(R.id.speed);
        // 表格单元
        speed11 = (TextView) findViewById(R.id.speed11);
        speed12 = (TextView) findViewById(R.id.speed12);
        speed13 = (TextView) findViewById(R.id.speed13);
        speed21 = (TextView) findViewById(R.id.speed21);
        speed22 = (TextView) findViewById(R.id.speed22);
        speed23 = (TextView) findViewById(R.id.speed23);
        speed31 = (TextView) findViewById(R.id.speed31);
        speed32 = (TextView) findViewById(R.id.speed32);
        speed33 = (TextView) findViewById(R.id.speed33);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_manage) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        } else if (id == R.id.nav_send) {
            showToast(getString(R.string.about_app));
        } else if (id == R.id.connect) {
            connect();
        } else if (id == R.id.btnQuit) {
            disConnect();//中断连接
        } else if (id == R.id.gps) {
            this.startGPS();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void putTableData(List<String> str) {
        setNavTextStatus(String2Int(str.get(0)), speed11);
        setNavTextStatus(String2Int(str.get(1)), speed21);
        setNavTextStatus(String2Int(str.get(2)), speed31);

        setPitchTextStatus(String2Int(str.get(3)), speed12);
        setPitchTextStatus(String2Int(str.get(4)), speed22);
        setPitchTextStatus(String2Int(str.get(5)), speed32);

        setYawTextStatus(String2Int(str.get(6)), speed13);
        setYawTextStatus(String2Int(str.get(7)), speed23);
        setYawTextStatus(String2Int(str.get(8)), speed33);
    }

    /**
     * 当Nav角度出现超出临界值的时候颜色改变。
     *
     * @param angle 传入的角度值
     * @param tv    传入角度值对应的TextView
     */
    private void setNavTextStatus(int angle, TextView tv) {
        if (dataUtil.isNavOverStep(angle)) {
            tv.setBackgroundColor(this.getResources().getColor(R.color.colorAccent));
        } else {
            tv.setBackgroundColor(this.getResources().getColor(R.color.white));
        }
        tv.setText(String.valueOf(angle));
    }

    /**
     * 当Pitch角度出现超出临界值的时候颜色改变。
     *
     * @param angle 传入的角度值
     * @param tv    传入角度值对应的TextView
     */
    private void setPitchTextStatus(int angle, TextView tv) {
        if (dataUtil.isPitchOverStep(angle)) {
            tv.setBackgroundColor(this.getResources().getColor(R.color.colorAccent));
        } else {
            tv.setBackgroundColor(this.getResources().getColor(R.color.white));
        }
        tv.setText(String.valueOf(angle));
    }

    /**
     * 当Yaw角度出现超出临界值的时候颜色改变。
     *
     * @param angle 传入的角度值
     * @param tv    传入角度值对应的TextView
     */
    private void setYawTextStatus(int angle, TextView tv) {
        if (dataUtil.isYawOverStep(angle)) {
            tv.setBackgroundColor(this.getResources().getColor(R.color.colorAccent));
        } else {
            tv.setBackgroundColor(this.getResources().getColor(R.color.white));
        }
        tv.setText(String.valueOf(angle));
    }
}

