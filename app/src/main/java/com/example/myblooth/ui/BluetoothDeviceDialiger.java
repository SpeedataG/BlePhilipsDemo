package com.example.myblooth.ui;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myblooth.MyApp;
import com.example.myblooth.R;
import com.example.myblooth.act.MainActivity;
import com.example.myblooth.utils.MyLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * //                            _ooOoo_
 * //                           o8888888o
 * //                           88" . "88
 * //                           (| -_- |)
 * //                            O\ = /O
 * //                        ____/`---'\____
 * //                      .   ' \\| |// `.
 * //                       / \\||| : |||// \
 * //                     / _||||| -:- |||||- \
 * //                       | | \\\ - /// | |
 * //                     | \_| ''\---/'' | |
 * //                      \ .-\__ `-` ___/-. /
 * //                   ___`. .' /--.--\ `. . __
 * //                ."" '< `.___\_<|>_/___.' >'"".
 * //               | | : `- \`.;`\ _ /`;.`/ - ` : | |
 * //                 \ \ `-. \_ __\ /__ _/ .-` / /
 * //         ======`-.____`-.___\_____/___.-`____.-'======
 * //                            `=---='
 * //
 * //         .............................................
 * //                  佛祖镇楼                  BUG辟易
 *
 * @author :EchoXBR in  2017/8/31 上午9:40.
 *         功能描述:显示蓝牙设备烈点 点击进行链接
 */

public class BluetoothDeviceDialiger extends Dialog implements AdapterView.OnItemClickListener {

    private Context mContext;
    private MyLogger logger = MyLogger.jLog();

    private List<String> nameList = new ArrayList<String>();
    private List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();

    private BluetoothAdapter bAtapter;


    MyApp app;
    CustomProgressDialog mProgressDialog;


    public BluetoothDeviceDialiger(@NonNull Context context, MyApp app, CustomProgressDialog mProgressDialog) {
        super(context);
        mContext = context;
        initView();
        this.app = app;
        this.mProgressDialog = mProgressDialog;
    }

    private FloatingActionButton ftbRefash;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ble);

        mListView = (ListView) findViewById(R.id.list_id);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(baseAdapter);
        bAtapter.enable();

    }

    BaseAdapter baseAdapter = new BaseAdapter() {

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            arg1 = LayoutInflater.from(mContext).inflate(
                    R.layout.item, arg2, false);
            TextView tv = (TextView) arg1.findViewById(R.id.item_text);
            tv.setText(nameList.get(arg0));
            return arg1;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public Object getItem(int arg0) {
            return nameList.get(arg0);
        }

        @Override
        public int getCount() {
            return nameList.size();
        }
    };

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final BluetoothDevice device = deviceList.get(i);
        new Thread() {
            @Override
            public void run() {
                bAtapter.cancelDiscovery();
                app.connectToServerSocket(device,
                        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

            }
        }.start();
        mProgressDialog.setMessage("正在链接");
        mProgressDialog.show();
    }

    //得到已绑定的设备
    private void addBoundDevice(BluetoothAdapter bAdapter) {
        Set<BluetoothDevice> set = bAdapter.getBondedDevices();
        logger.d("set:" + set.size());
        for (BluetoothDevice device : set) {
            nameList.add(device.getName());
            deviceList.add(device);
        }
        baseAdapter.notifyDataSetChanged();
    }

    BroadcastReceiver discoveryRsult = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String stringExtra = arg1.getStringExtra(BluetoothDevice.EXTRA_NAME);
//            for (int i = 0; i < nameList.size(); i++) {
//                if (nameList.get(i).equals(stringExtra)) {
//                    nameList.remove(i);
//                    logger.d("remove:" + stringExtra);
//                }
//            }
            nameList.add(stringExtra);
            deviceList.add((BluetoothDevice) arg1
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
            logger.d("nameList:" + nameList.size());
            baseAdapter.notifyDataSetChanged();
        }
    };

    private void initView() {
        bAtapter = BluetoothAdapter.getDefaultAdapter();

        startDiscovery();
        addBoundDevice(bAtapter);
    }

    private void startDiscovery() {
        mContext.registerReceiver(discoveryRsult, new IntentFilter(
                BluetoothDevice.ACTION_FOUND));
        deviceList.clear();
        nameList.clear();
        bAtapter.startDiscovery();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mContext.unregisterReceiver(discoveryRsult);
    }
}
