package com.example.wangyu892449346.bluetoothserver.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wangyu892449346.bluetoothserver.GPS.GPSListener;
import com.example.wangyu892449346.bluetoothserver.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends GPSActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    /*
    * 这些是从蓝牙获取的参数
    * */
    //这条是蓝牙串口通用的UUID，不要更改
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //接收到的字符串
    String ReceiveData = "";
    MyHandler handler;
    //定义组件
    private TextView statusLabel;
    private EditText editLatitude;
    private EditText editLongitude;
    private EditText editSpeed;
    //device var
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;
    private ReceiveThread rThread = null;  //数据接收线程

    private TextView speed11;
    private TextView speed12;
    private TextView speed13;
    private TextView speed21;
    private TextView speed22;
    private TextView speed23;
    private TextView speed31;
    private TextView speed32;
    private TextView speed33;

    // --------------------------------------------
    private FloatingActionButton fab;
    private Toast mToast;
    /**
     * Called when the activity is first created.
     */
    private boolean isSupport = true;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private GPSListener gpsListener = new GPSListener() {
        @Override
        public void setLongitudeView(double longitude) {
            editLongitude.setText(String.valueOf(longitude));
        }

        @Override
        public void setLatitudeView(double latitude) {
            editLatitude.setText(String.valueOf(latitude));
        }

        @Override
        public void setSpeed(double speed) {
            editSpeed.setText(String.valueOf(speed));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setGpsListener(gpsListener);
        initView();
        initListener();
        InitBluetooth();
        handler = new MyHandler();
    }

    private void initListener() {
        if (bluetoothAdapter == null) {
            isSupport = false;
            Toast.makeText(MainActivity.this, R.string.no_support, Toast.LENGTH_SHORT).show();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, R.string.loading, Toast.LENGTH_SHORT).show();
//                if (isSupport) {
//                    if (!bluetoothAdapter.isEnabled()) {
//                        bluetoothAdapter.enable();//异步的，不会等待结果，直接返回。
//                    } else {
//                        bluetoothAdapter.startDiscovery();
//                    }
//                }
                if (!TextUtils.isEmpty(statusLabel.getText().toString())) {
                    statusLabel.setText(R.string.loading);
                }
                if (btSocket != null) {
                    statusLabel.setText(getString(R.string.socket_success));
                    return;
                }
                connect();
            }
        });
    }

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

        } else if (id == R.id.connect) {
            connect();// 连接获取数据
        } else if (id == R.id.btnQuit) {
            disConnect();//中断连接
        } else if (id == R.id.gps) {
            this.startGPS();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void InitBluetooth() {
        //得到一个蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            showToast(getString(R.string.no_support));
            finish();
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (rThread != null) {
                btSocket.close();
                btSocket = null;
                rThread.join();
            }
            this.finish();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        //判断蓝牙是否打开
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        mBluetoothAdapter.startDiscovery();
        //创建连接
        new ConnectTask().execute("98:D3:32:10:A3:54");
    }

    private void disConnect() {
        if (btSocket != null) {
            try {
                btSocket.close();
                btSocket = null;
                if (rThread != null) {
                    rThread.join();
                }
                statusLabel.setText(R.string.close);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        } else {
            mToast.setText(text);
        }
        mToast.show();
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

    private void setNavTextStatus(int angle, TextView tv) {
        if (dataUtil.isNavOverStep(angle)) {
            tv.setBackgroundColor(this.getResources().getColor(R.color.colorAccent));
        } else {
            tv.setBackgroundColor(this.getResources().getColor(R.color.white));
        }
        tv.setText(String.valueOf(angle));
    }

    private void setPitchTextStatus(int angle, TextView tv) {
        if (dataUtil.isPitchOverStep(angle)) {
            tv.setBackgroundColor(this.getResources().getColor(R.color.colorAccent));
        } else {
            tv.setBackgroundColor(this.getResources().getColor(R.color.white));
        }
        tv.setText(String.valueOf(angle));
    }

    private void setYawTextStatus(int angle, TextView tv) {
        if (dataUtil.isYawOverStep(angle)) {
            tv.setBackgroundColor(this.getResources().getColor(R.color.colorAccent));
        } else {
            tv.setBackgroundColor(this.getResources().getColor(R.color.white));
        }
        tv.setText(String.valueOf(angle));
    }

    //连接蓝牙设备的异步任务
    private class ConnectTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(params[0]);
            Log.e("Notzuonotdied", getString(R.string.start) + device.toString());
            try {
                btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                btSocket.connect();
                Log.e("error", "ON RESUME: BT connection established, data transfer link open.");
            } catch (IOException e) {
                try {
                    btSocket.close();
                    e.printStackTrace();
                    return getString(R.string.socket_fail);
                } catch (IOException e2) {
                    e.printStackTrace();
                    Log.e("error", "ON RESUME: Unable to close socket during connection failure", e2);
                    return getString(R.string.close_fail);
                }
            }
            //取消搜索
            mBluetoothAdapter.cancelDiscovery();
            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                Log.e("error", "ON RESUME: Output stream creation failed.", e);
                return getString(R.string.steam_fail);
            }
            return getString(R.string.socket_success);
        }

        @Override    //这个方法是在主线程中运行的，所以可以更新界面
        protected void onPostExecute(String result) {
            if (TextUtils.equals(result, getString(R.string.socket_success))) {
                //连接成功则启动监听
                rThread = new ReceiveThread();
                rThread.start();
            }
            statusLabel.setVisibility(View.VISIBLE);
            statusLabel.setText(result);
            super.onPostExecute(result);
        }
    }

    //从蓝牙接收信息的线程
    private class ReceiveThread extends Thread {
        @Override
        public void run() {
            while (btSocket != null) {
                //定义一个存储空间buff
                byte[] buff = new byte[1024];
                try {
                    inStream = btSocket.getInputStream();
                    inStream.read(buff); //读取数据存储在buff数组中
                    processBuffer(buff, 1024);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void processBuffer(byte[] buff, int size) {
//            int length = 0;
//            byte[] newBuff;
//            int start = 0;
//            for (int i = 0; i < size; i++) {
//                if (buff[i] > '\0') {
//                    length++;
//                } else if (buff[i] == '\0') {
//                    newBuff = new byte[length];  //newbuff字节数组，用于存放真正接收到的数据
//                    System.arraycopy(buff, start, newBuff, 0, length);
//                    ReceiveData = new String(newBuff);
//                    Message msg = Message.obtain();
//                    msg.what = 1;
//                    handler.sendMessage(msg);  //发送消息:系统会自动调用handleMessage( )方法来处理消息
//                    start = length;
//                    length = 0;
//                } else {
//                    break;
//                }
//            }
            final List<String> list = dataUtil.getList4Array(new String(buff));
            if (null == list || list.size() == 0) {
                return;
            }
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                int count = 0;

                public void run() {
                    Message msg = Message.obtain();
                    msg.what = 1;
                    ReceiveData = list.get(count++);
                    Log.d("Notzuonotdied", "String: " + list);
                    handler.sendMessage(msg);  //发送消息:系统会自动调用handleMessage( )方法来处理消息
                    timer.cancel();
                }
            }, 55);
        }
    }

    //更新界面的Handler类
    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    Log.i("Data:", ReceiveData);//Data:: x:.+ 0.+ 0.+ 0,y:.+ 0.+ 0.+ 0,z:.+ 0.+ 0
                    if (!TextUtils.isEmpty(ReceiveData) && !"".equals(ReceiveData.trim())) {
//                        if (ReceiveData.indexOf('x') == 0
//                                && ReceiveData.indexOf('}') == ReceiveData.length() - 1) {
                            //ReceiveData = "{" + ReceiveData;
                            Log.i("Notzuonotdied", "开始处理数据了～");
                            List<String> list = dataUtil.getList(ReceiveData);
                            Log.d("Notzuonotdied", "String: " + list);
                            if (null != list && list.size() != 0) {
                                putTableData(list);
                           // }
                        }
                    }
                    break;
            }
        }
    }
}

