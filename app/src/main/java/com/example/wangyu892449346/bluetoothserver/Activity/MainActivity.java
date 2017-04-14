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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wangyu892449346.bluetoothserver.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /*
    * 这些是从蓝牙获取的参数
    * */
    //这条是蓝牙串口通用的UUID，不要更改
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //定义组件
    TextView statusLabel;
    //接收到的字符串
    String ReceiveData = "";
    MyHandler handler;
    //device var
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;
    private ReceiveThread rThread = null;  //数据接收线程

    // --------------------------------------------
    private FloatingActionButton fab;
    private Toast mToast;
    /**
     * Called when the activity is first created.
     */
    private boolean isSupport = true;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();

        InitBluetooth();
        handler = new MyHandler();
    }

    private void initListener() {
        if (bluetoothAdapter == null) {
            isSupport = false;
            Toast.makeText(MainActivity.this, "您的设备不支持蓝牙～", Toast.LENGTH_SHORT).show();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "正在连接中，请稍后～", Toast.LENGTH_SHORT).show();
//                if (isSupport) {
//                    if (!bluetoothAdapter.isEnabled()) {
//                        bluetoothAdapter.enable();//异步的，不会等待结果，直接返回。
//                    } else {
//                        bluetoothAdapter.startDiscovery();
//                    }
//                }
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void InitBluetooth() {
        //得到一个蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            showToast("你的手机不支持蓝牙");
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
                statusLabel.setText("当前连接已断开");
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

    //连接蓝牙设备的异步任务
    private class ConnectTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(params[0]);
            Log.e("Notzuonotdied", "开始查找设备" + device.toString());
            try {
                btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                btSocket.connect();
                Log.e("error", "ON RESUME: BT connection established, data transfer link open.");
            } catch (IOException e) {
                try {
                    btSocket.close();
                    e.printStackTrace();
                    return "Socket 创建失败";
                } catch (IOException e2) {
                    e.printStackTrace();
                    Log.e("error", "ON RESUME: Unable to close socket during connection failure", e2);
                    return "Socket 关闭失败";
                }
            }
            //取消搜索
            mBluetoothAdapter.cancelDiscovery();
            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                Log.e("error", "ON RESUME: Output stream creation failed.", e);
                return "Socket 流创建失败";
            }
            return "蓝牙连接正常,Socket 创建成功";
        }

        @Override    //这个方法是在主线程中运行的，所以可以更新界面
        protected void onPostExecute(String result) {
            //连接成功则启动监听
            rThread = new ReceiveThread();
            rThread.start();
            statusLabel.setText(result);
            super.onPostExecute(result);
        }
    }

    //发送数据到蓝牙设备的异步任务
    private class SendInfoTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            statusLabel.setText(result);
        }

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            if (btSocket == null) {
                return "还没有创建连接";
            }
            if (arg0[0].length() > 0)//不是空白串
            {
                byte[] msgBuffer = arg0[0].getBytes();
                try {
                    //  将msgBuffer中的数据写到outStream对象中
                    outStream.write(msgBuffer);
                } catch (IOException e) {
                    Log.e("error", "ON RESUME: Exception during write.", e);
                    return "发送失败";
                }
            }
            return "发送成功";
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
                    System.out.println("waitting for instream");
                    inStream.read(buff); //读取数据存储在buff数组中
                    processBuffer(buff, 1024);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void processBuffer(byte[] buff, int size) {
            int length = 0;
            byte[] newbuff;
            int start = 0;
            for (int i = 0; i < size; i++) {
                if (buff[i] > '\0') {
                    length++;
                } else if (buff[i] == '\0') {
                    newbuff = new byte[length];  //newbuff字节数组，用于存放真正接收到的数据
                    System.arraycopy(buff, start, newbuff, 0, length);
                    ReceiveData = new String(newbuff);
                    Log.e("Data", ReceiveData);
                    Message msg = Message.obtain();
                    msg.what = 1;
                    handler.sendMessage(msg);  //发送消息:系统会自动调用handleMessage( )方法来处理消息
                    start = length + 1;
                    length = 0;
                } else {
                    break;
                }
            }
        }
    }

    //更新界面的Handler类
    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    //etReceived.setText(ReceiveData);
                    Log.i("Data:", ReceiveData/* + ",长度：" + String.valueOf(ReceiveData.length())*/);
                    break;
            }
        }
    }
}
