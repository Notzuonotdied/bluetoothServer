package com.example.wangyu892449346.bluetoothserver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wangyu892449346.bluetoothserver.BlueTooth.BluetoothActivity;
import com.example.wangyu892449346.bluetoothserver.BlueTooth.OnChangeText;
import com.example.wangyu892449346.bluetoothserver.GPS.GPSListener;
import com.example.wangyu892449346.bluetoothserver.R;
import com.example.wangyu892449346.bluetoothserver.WIFI.SocketTransceiver;
import com.example.wangyu892449346.bluetoothserver.WIFI.TcpServer;

import java.lang.ref.WeakReference;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends BluetoothActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

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
    private TextView gas;
    private FloatingActionButton fab;
    private LinearLayout tcpClient;
    private android.support.design.widget.TextInputEditText IP;
    private android.support.design.widget.TextInputEditText port;
    private Button client_confirm;
    private LinearLayout tcpServer;
    private android.support.design.widget.TextInputEditText server_port;
    private Button server_confirm;
    private android.support.v7.widget.AppCompatTextView serverIP;
    private MyHandler myHandler;

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
     */
    private OnChangeText onChangeText = new OnChangeText() {
        @Override// 当提示文本有变化的时候回调
        public void changeText(String info) {
            statusLabel.setVisibility(View.VISIBLE);
            statusLabel.setText(info);
        }

        @Override// 当接受并处理完蓝牙发送的数据的时候回调
        public void handleMsg(List<String> list, char what) {
            if (list.size() < 9) {
                putColumnData(list, what);
            } else {
                putTableData(list);
            }
        }
    };
    private TcpServer server;

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
     */
    private void initListener() {
        fab.setOnClickListener(this);
        server_confirm.setOnClickListener(this);
        client_confirm.setOnClickListener(this);
    }

    private void startService() {
        if (server != null) {
            server.stop();
        }
        server = new TcpServer(Integer.valueOf(server_port.getText().toString())) {

            @Override
            public void onConnect(SocketTransceiver client) {
                Message message = Message.obtain();
                message.what = 1;
                message.obj = "Client " + client.getInetAddress().getHostAddress() + " connect.";
                myHandler.sendMessage(message);
            }

            @Override
            public void onConnectFailed() {
                Message message = Message.obtain();
                message.what = 1;
                message.obj = "Client Connect Failed.";
                myHandler.sendMessage(message);
            }

            @Override
            public void onReceive(SocketTransceiver client, String s) {
                Message message = Message.obtain();
                message.what = 2;
                message.obj = s;
                myHandler.sendMessage(message);
                // client.send(s);
                // Log.i("get", s);
            }

            @Override
            public void onDisconnect(SocketTransceiver client) {
                Message message = Message.obtain();
                message.what = 1;
                message.obj = "Client " + client.getInetAddress().getHostAddress() + " disconnect.";
                myHandler.sendMessage(message);
            }

            @Override
            public void onServerStop() {
                Message message = Message.obtain();
                message.what = 1;
                message.obj = "TCP server stop.";
                myHandler.sendMessage(message);
            }
        };
        Message message = Message.obtain();
        message.what = 1;
        message.obj = "TCP server start.";
        myHandler.sendMessage(message);
        server.start();
    }

    private void startClient() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                connect();
                break;
            case R.id.server_confirm:
                if (server_port.isEnabled()) {
                    if (TextUtils.isEmpty(server_port.getText())) {
                        Toast.makeText(MainActivity.this, "请输入端口号", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    startService();
                    server_port.setEnabled(false);
                    server_confirm.setText(getString(R.string.alert));
                } else {
                    server_confirm.setText(getString(R.string.confirm));
                    server_port.setEnabled(true);
                }
                break;
            case R.id.client_confirm:
                Toast.makeText(MainActivity.this, "尚未完成～", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 初始化UI控件
     */
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
        gas = (TextView) findViewById(R.id.gas);

        tcpClient = (LinearLayout) findViewById(R.id.TcpClient);
        IP = (TextInputEditText) findViewById(R.id.ip);
        port = (TextInputEditText) findViewById(R.id.port);
        client_confirm = (Button) findViewById(R.id.client_confirm);

        tcpServer = (LinearLayout) findViewById(R.id.TcpServer);
        serverIP = (AppCompatTextView) findViewById(R.id.server_ip);
        server_port = (TextInputEditText) findViewById(R.id.server_port);
        server_confirm = (Button) findViewById(R.id.server_confirm);

        String temp = getResources().getString(R.string.server_ip) + getHostIP();
        serverIP.setText(temp);

        myHandler = new MyHandler(this);
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
        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_manage:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
            case R.id.nav_send:
                showToast(getString(R.string.about_app));
                break;
            case R.id.connect:
                connect();
                break;
            case R.id.btnQuit:
                disConnect();//中断连接
                break;
            case R.id.gps:
                this.startGPS();
                break;
            case R.id.TcpClient:
                if (tcpClient.getVisibility() == View.GONE) {
                    this.tcpClient.setVisibility(View.VISIBLE);
                } else {
                    this.tcpClient.setVisibility(View.GONE);
                }
                break;
            case R.id.TcpServer:
                if (tcpServer.getVisibility() == View.GONE) {
                    this.tcpServer.setVisibility(View.VISIBLE);
                } else {
                    this.tcpServer.setVisibility(View.GONE);
                }
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void putColumnData(List<String> str, char what) {
        switch (what) {
            case 'A':
                setNavTextStatus(String2Int(str.get(0)), speed11);
                setPitchTextStatus(String2Int(str.get(1)), speed12);
                setYawTextStatus(String2Int(str.get(2)), speed13);
                break;
            case 'B':
                setNavTextStatus(String2Int(str.get(0)), speed21);
                setPitchTextStatus(String2Int(str.get(1)), speed22);
                setYawTextStatus(String2Int(str.get(2)), speed23);
                break;
            case 'C':
                setNavTextStatus(String2Int(str.get(0)), speed31);
                setPitchTextStatus(String2Int(str.get(1)), speed32);
                setYawTextStatus(String2Int(str.get(2)), speed33);
                break;
            case 'S':
                setGasTextStatus(String2Double(str.get(0)), gas);
                break;
        }
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
     * 当gas浓度出现超出临界值的时候颜色改变。
     *
     * @param gas 传入的浓度值
     * @param tv  传入角度值对应的TextView
     */
    private void setGasTextStatus(double gas, TextView tv) {
        if (dataUtil.isOverEdge(gas)) {
            tv.setBackgroundColor(this.getResources().getColor(R.color.colorAccent));
        } else {
            tv.setBackgroundColor(this.getResources().getColor(R.color.white));
        }
        tv.setText(String.valueOf(gas));
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

    /**
     * 获取ip地址
     *
     * @return IP地址
     */
    public String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("FuncTcpServer", "SocketException");
            e.printStackTrace();
        }
        return hostIp;

    }

    private class MyHandler extends android.os.Handler {
        private final WeakReference<MainActivity> mActivity;

        MyHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            switch (msg.what) {
                case 1:
                    Toast.makeText(activity, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    List<String> list = dataUtil.getList(msg.obj.toString());
                    if (null != list && list.size() != 0) {
                        onChangeText.handleMsg(list, msg.obj.toString().trim().charAt(0));
                    }
                    break;
            }
        }
    }
}

