package com.example.myblooth.act;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myblooth.MyApp;
import com.example.myblooth.R;
import com.example.myblooth.base.BaseActivity;
import com.example.myblooth.callback.MesCallBack;
import com.example.myblooth.ui.BluetoothDeviceDialiger;
import com.example.myblooth.utils.MyLogger;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private MyLogger logger = MyLogger.jLog();

    private EditText mEditText;
    private TextView tv;

    FloatingActionButton fbtn;
    FloatingActionButton fbtnClear;
    FloatingActionButton fbtnDisconnect;
    private MyApp app;
    BluetoothDeviceDialiger dialiger;
    private Button btnSend;
    private TextView tvState;
    private CheckBox checkBox;
    private Button btn1, btn2, btn3, btn4, btn5, btn6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (MyApp) getApplication();
        checkBox = findViewById(R.id.ck);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        updateState(false);
        app.setMesCallBack(new MesCallBack() {
            @Override
            public void ReceMsg(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (checkBox.isChecked())
                            tv.append(msg);
                        else {
                            if (msg.equals("heart"))
                                return;
                            else
                                tv.append(msg);

                        }

                    }
                });
            }

            @Override
            public void isConnect(final boolean isConnect) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        btnSend.setEnabled(isConnect);
                        updateState(isConnect);
                        if (isConnect) {
                            if (dialiger.isShowing())
                                dialiger.dismiss();
                            tvState.setText("已链接" + app.getDevice());
                            fbtnDisconnect.setVisibility(View.VISIBLE);
                        } else {
                            tvState.setText("未链接");
                            fbtnDisconnect.setVisibility(View.INVISIBLE);
                        }
//                        if (dialiger.isShowing()) {
//                            dialiger.dismiss();
//                        }
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                    }
                });

            }

            @Override
            public void shwoErrorMsg(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(MainActivity.this).setMessage(msg).show();
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                    }
                });

            }
        });
        mEditText = (EditText) findViewById(R.id.et_id);
        tvState = findViewById(R.id.tv_state);
        btnSend = findViewById(R.id.btn_send);
        btnSend.setEnabled(false);
        fbtn = findViewById(R.id.fab);
        fbtnClear = findViewById(R.id.fab_clear);
        fbtnDisconnect = findViewById(R.id.fab_disconnect);
        fbtnDisconnect.setOnClickListener(this);
        fbtnClear.setOnClickListener(this);

        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialiger = new BluetoothDeviceDialiger(MainActivity.this, app, mProgressDialog);
                dialiger.setTitle("扫描设备");
                dialiger.show();

            }
        });
        tv = (TextView) findViewById(R.id.tv_id);
//        initView();
        initTitle("飞利浦-发送端", false, null, 0);
//        setRightNavigation(R.drawable.icon_back, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                tv.setText("");
//            }
//        });
    }

    private void updateState(boolean state) {
        btn1.setEnabled(state);
        btn2.setEnabled(state);
        btn3.setEnabled(state);
        btn4.setEnabled(state);
    }

    public void click(View view) {
        String str = mEditText.getText().toString();
        if (!TextUtils.isEmpty(str)) {
            app.sendMessage(str);
        } else {
            Toast.makeText(this, "信息不能为空", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void onClick(View view) {
        if (view == fbtnClear) {
            tv.setText("");
        } else if (view == fbtnDisconnect) {
            app.disConnect();
            fbtnDisconnect.setVisibility(View.INVISIBLE);
            tvState.setText("未链接");
        } else if (view == btn1) {
            app.sendMessage("TEST1");

        } else if (view == btn2) {
            app.sendMessage("TEST2");

        } else if (view == btn3) {
            app.sendMessage("TEST3");

        } else if (view == btn4) {
            app.sendMessage("TEST4");
        } else if (view == btn5) {
            app.sendMessage("TEST5");
        } else if (view == btn6) {
            app.sendMessage("TEST6");
        }
    }
}
