package com.example.wangyu892449346.bluetoothserver.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.wangyu892449346.bluetoothserver.R;
import com.example.wangyu892449346.bluetoothserver.util.Constant;
import com.example.wangyu892449346.bluetoothserver.util.SharedPManager;

public class SettingActivity extends AppCompatActivity {

    private android.widget.EditText edit12;
    private android.widget.EditText edit23;
    private android.widget.EditText edit34;
    private android.widget.EditText edit45;
    private android.widget.EditText edit56;
    private android.widget.EditText edit67;
    private android.widget.EditText edit78;
    private android.widget.EditText edit89;
    private android.widget.EditText editPitch;
    private android.widget.EditText edityaw;
    private android.widget.Button btnAlert;
    private android.widget.Button btnConfirm;
    private View.OnClickListener listener = view -> {
        switch (view.getId()) {
            case R.id.alert:
                setFlag(true);
                readData();
                break;
            case R.id.confirm:
                setFlag(false);
                saveData();
                break;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initListener();
        /*读取数据*/
        readData();
    }

    private void initListener() {
        btnAlert.setOnClickListener(listener);
        btnConfirm.setOnClickListener(listener);
    }

    private void initView() {
        this.edityaw = (EditText) findViewById(R.id.edit_yaw);
        this.editPitch = (EditText) findViewById(R.id.edit_roll);
        this.edit89 = (EditText) findViewById(R.id.edit89);
        this.edit78 = (EditText) findViewById(R.id.edit78);
        this.edit67 = (EditText) findViewById(R.id.edit67);
        this.edit56 = (EditText) findViewById(R.id.edit56);
        this.edit45 = (EditText) findViewById(R.id.edit45);
        this.edit34 = (EditText) findViewById(R.id.edit34);
        this.edit23 = (EditText) findViewById(R.id.edit23);
        this.edit12 = (EditText) findViewById(R.id.edit12);
        this.btnAlert = (Button) findViewById(R.id.alert);
        this.btnConfirm = (Button) findViewById(R.id.confirm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void readData() {
        SharedPManager sp = new SharedPManager(SettingActivity.this);
        Constant.edit12Num = sp.getInt(Constant.edit12, Constant.edit12Num);
        Constant.edit23Num = sp.getInt(Constant.edit23, Constant.edit23Num);
        Constant.edit34Num = sp.getInt(Constant.edit34, Constant.edit34Num);
        Constant.edit45Num = sp.getInt(Constant.edit45, Constant.edit45Num);
        Constant.edit56Num = sp.getInt(Constant.edit56, Constant.edit56Num);
        Constant.edit67Num = sp.getInt(Constant.edit67, Constant.edit67Num);
        Constant.editPitchNum = sp.getInt(Constant.editPitch, Constant.editPitchNum);
        Constant.editYawNum = sp.getInt(Constant.editYaw, Constant.editYawNum);

        this.edit12.setText(int2String(Constant.edit12Num));
        this.edit23.setText(int2String(Constant.edit23Num));
        this.edit34.setText(int2String(Constant.edit34Num));
        this.edit45.setText(int2String(Constant.edit45Num));
        this.edit56.setText(int2String(Constant.edit56Num));
        this.edit67.setText(int2String(Constant.edit67Num));
        this.edit78.setText(int2String(Constant.edit78Num));
        this.edit89.setText(int2String(Constant.edit89Num));
        this.edityaw.setText(int2String(Constant.editYawNum));
        this.editPitch.setText(int2String(Constant.editPitchNum));
        sp.commit();
    }

    private void saveData() {
        SharedPManager sp = new SharedPManager(SettingActivity.this);
        Constant.edit12Num = String2Int(edit12.getText().toString());
        Constant.edit23Num = String2Int(edit23.getText().toString());
        Constant.edit34Num = String2Int(edit34.getText().toString());
        Constant.edit45Num = String2Int(edit45.getText().toString());
        Constant.edit56Num = String2Int(edit56.getText().toString());
        Constant.edit67Num = String2Int(edit67.getText().toString());
        Constant.edit78Num = String2Int(edit78.getText().toString());
        Constant.edit89Num = String2Int(edit89.getText().toString());
        Constant.editPitchNum = String2Int(editPitch.getText().toString());
        Constant.editYawNum = String2Int(edityaw.getText().toString());

        sp.putInt(Constant.edit12, Constant.edit12Num);
        sp.putInt(Constant.edit23, Constant.edit23Num);
        sp.putInt(Constant.edit34, Constant.edit34Num);
        sp.putInt(Constant.edit56, Constant.edit56Num);
        sp.putInt(Constant.edit67, Constant.edit67Num);
        sp.putInt(Constant.edit78, Constant.edit78Num);
        sp.putInt(Constant.edit89, Constant.edit89Num);
        sp.putInt(Constant.editPitch, Constant.editPitchNum);
        sp.putInt(Constant.editYaw, Constant.editYawNum);
        sp.commit();
    }

    private void setFlag(boolean flag) {
        this.edit89.setEnabled(flag);
        this.edit78.setEnabled(flag);
        this.edit67.setEnabled(flag);
        this.edit56.setEnabled(flag);
        this.edit45.setEnabled(flag);
        this.edit34.setEnabled(flag);
        this.edit23.setEnabled(flag);
        this.edit12.setEnabled(flag);
        this.editPitch.setEnabled(flag);
        this.edityaw.setEnabled(flag);
    }

    private int String2Int(String str) {
        return Integer.valueOf(str);
    }

    @NonNull
    private String int2String(int num) {
        return String.valueOf(num);
    }
}
