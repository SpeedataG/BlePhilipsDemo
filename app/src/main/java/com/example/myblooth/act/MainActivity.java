package com.example.myblooth.act;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.myblooth.MyApp;
import com.example.myblooth.R;
import com.example.myblooth.base.BaseActivity;
import com.example.myblooth.callback.MesCallBack;
import com.example.myblooth.ui.BluetoothDeviceDialiger;
import com.example.myblooth.utils.MyLogger;

public class MainActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, TextView.OnEditorActionListener {
    private MyLogger logger = MyLogger.jLog();
    private EditText etdHeight;
    private TextView tv;
    private FloatingActionButton fbtn;
    private FloatingActionButton fbtnClear;
    private FloatingActionButton fbtnDisconnect;
    private MyApp app;
    private BluetoothDeviceDialiger dialiger;
    private TextView tvState;
    private SeekBar seekBar;
    private Button btnRun, btnStop, btnTest;
    private RadioButton rb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //01 00 B4 01 01 B6 99
        byte tb[] = {0x01, 0x00, (byte) 0xB4, 0x01, 0x01};
    }

    @Override
    public void onClick(View view) {
        if (view == fbtnClear) {
            tv.setText("");
        } else if (view == fbtnDisconnect) {
            app.disConnect();
            fbtnDisconnect.setVisibility(View.INVISIBLE);
//            tvState.setText("未链接");
        } else if (view == btnRun) {
            btnRun.setTextColor(Color.BLUE);
            btnStop.setTextColor(Color.BLACK);
//            handler.postDelayed(runnable, 500);
            assembleDsatas("01");

        } else if (view == btnStop) {
//            if (!flag) {
//                flag = true;
            btnStop.setTextColor(Color.BLUE);
            btnRun.setTextColor(Color.BLACK);
            assembleDsatas("02");
//            }

            handler.removeCallbacks(runnable);

        } else if (view == btnTest) {

//            assembleDsatas();
        }

    }

    private void assembleDsatas(String state) {
        //例：主机发送（0x01）身高为180cm（0x00 0xB4），部位为head（0x01），运行（0x01），；
        //所以发送的数据为：0x01 0x00 0xB4 0x01  0x01 0xB6 0x99，
        String s = Integer.toHexString(height);
        String cmds = "Please select height and position";
        if (rb != null) {
            if (rb.getText().equals("Head")) {
                cmds = "0100" + s + "01" + state;
            } else if (rb.getText().equals("Stomatch")) {
                cmds = "0100" + s + "02" + state;
            } else if (rb.getText().equals("Butt")) {
                cmds = "0100" + s + "03" + state;
            } else if (rb.getText().equals("Foot")) {
                cmds = "0100" + s + "04" + state;
            }
            app.sendMessageByte(HexString2Bytes(cmds));
        } else {
            app.sendMessage(cmds);
        }
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            assembleDsatas();
            handler.postDelayed(this, 500);

        }
    };
    private int height = 100;

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        height = 100 + i;
        etdHeight.setText(height + "");
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void initView() {
        app = (MyApp) getApplication();
        btnRun = findViewById(R.id.btn_run);
        btnStop = findViewById(R.id.btn_stop);
        btnStop.setTextColor(Color.BLUE);
        btnTest = findViewById(R.id.btn_test);
        seekBar = findViewById(R.id.seek_bar);
        etdHeight = findViewById(R.id.etd_height);
        etdHeight.setOnEditorActionListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        btnStop.setOnClickListener(this);
        btnTest.setOnClickListener(this);
        btnRun.setOnClickListener(this);
        etdHeight.setText(height + "");
        app.setMesCallBack(new MesCallBack() {
            @Override
            public void ReceMsg(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (msg.equals("heart"))
                            return;
                        else
                            tv.append(msg);
                    }
                });
            }

            @Override
            public void isConnect(final boolean isConnect) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        btnSend.setEnabled(isConnect);
                        if (isConnect) {
                            if (dialiger.isShowing())
                                dialiger.dismiss();
                            initBle(true);
                            tvState.setText("蓝牙已链接：" + app.getDevice());
                            fbtnDisconnect.setVisibility(View.VISIBLE);
                        } else {
                            initBle(false);
                            tvState.setText("蓝牙未链接：");
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
        tvState = findViewById(R.id.tv_state);
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
        tv = findViewById(R.id.tv_id);
        initTitle("飞利浦-发送端", false, null, 0);
//        setRightNavigation(R.drawable.icon_back, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                tv.setText("");
//            }
//        });
        RadioGroup radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //获取变更后的选中项的ID
                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                //根据ID获取RadioButton的实例
                rb = MainActivity.this.findViewById(radioButtonId);
                //更新文本内容，以符合选中项
                tv.setText("本次选择是：" + rb.getText() + "\n");
            }
        });

    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE) {
            int s = Integer.parseInt(etdHeight.getText().toString());
            seekBar.setProgress(s - 100);
        }
        return false;
    }
}
