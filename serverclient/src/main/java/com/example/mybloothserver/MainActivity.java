package com.example.mybloothserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybloothserver.base.BaseActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private EditText mEditText;
    private StringBuffer sb;
    private TextView tv;
    private StringBuilder incoming = new StringBuilder();
    HeardThread heardThread;
    private TextView tvState;
    //    private Button btnClear;
    private Button btn1, btn2, btn3, btn4, btn5, btn6;
    FloatingActionButton fbtnClear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        fbtnClear = findViewById(R.id.fab_clear);
        fbtnClear.setOnClickListener(this);
        mEditText = (EditText) findViewById(R.id.et_id);
        tvState = findViewById(R.id.tv_state);
        sb = new StringBuffer();
        tv = (TextView) findViewById(R.id.tv_id);
        checkBox = findViewById(R.id.ck);
//        btnClear = findViewById(R.id.btn_clear);
//        btnClear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                tv.setText("");
//            }
//        });
        startServerSocket(BluetoothAdapter.getDefaultAdapter());
        initTitle("飞利浦-接收端", false, null, 0);
        heardThread = new HeardThread();
        heardThread.start();
    }

    public void click(View view) {

        if (transferSocket != null) {
            String str = mEditText.getText().toString();
            sendMessage(transferSocket, "收到主设备返回数据：" + str + "\n");
        }

    }

    private void updateState(final boolean state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (state) {
                    initBle(true);
                    tvState.setText("蓝牙已链接" + transferSocket.getRemoteDevice());
                } else {
                    tvState.setText("蓝牙未链接");
                    initBle(false);
//                    disConnect();
                }
            }
        });

    }

    private BluetoothSocket transferSocket;
    BluetoothServerSocket btserver;

    private UUID startServerSocket(BluetoothAdapter bluetooth) {
        //a60f35f0-b93a-11de-8a39-08002009c666
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        String name = "bluetoothserver";

        try {
            btserver = bluetooth
                    .listenUsingRfcommWithServiceRecord(name, uuid);

            Thread acceptThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        // Block until client connection established.
                        logger.d("accepting");
                        final BluetoothSocket serverSocket = btserver.accept();
                        logger.d("accept ok");
                        transferSocket = serverSocket;
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "连接成功",
                                        Toast.LENGTH_LONG).show();
                                updateState(true);
                            }
                        });
                        // Start listening for messages.

                        listenForMessages(serverSocket);
                        // Add a reference to the socket used to send messages.

                    } catch (IOException e) {
                        Log.e("BLUETOOTH", "Server connection IO Exception", e);
                    }
                }
            });
            acceptThread.start();
        } catch (IOException e) {
            Log.e("BLUETOOTH", "Socket listener IO Exception", e);
        }
        return uuid;
    }

    /**
     * Listing 16-8: Sending and receiving strings using Bluetooth Sockets
     */
    private void sendMessage(BluetoothSocket socket, String message) {
        OutputStream outStream;
        try {
            outStream = socket.getOutputStream();

            // Add a stop character.
            byte[] byteArray = (message + " ").getBytes();
            byteArray[byteArray.length - 1] = 0;

            outStream.write(byteArray);
        } catch (IOException e) {

        }
    }

    private boolean listening = false;
    private CheckBox checkBox;


    private void listenForMessages(BluetoothSocket socket
    ) {
        listening = true;

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        try {
            InputStream instream = socket.getInputStream();
            int bytesRead = -1;

            while (listening) {
                bytesRead = instream.read(buffer);
                if (bytesRead != -1) {
                    incoming = new StringBuilder();
                    String result = "";
                    while ((bytesRead == bufferSize)
                            && (buffer[bufferSize - 1] != 0)) {
                        result = result + new String(buffer, 0, bytesRead - 1);
                        bytesRead = instream.read(buffer);
                    }
                    result = result + new String(buffer, 0, bytesRead - 1);
                    incoming.append(result);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            String msg = incoming.toString();
//                            tv.setText(msg);
                            if (checkBox.isChecked())
                                tv.append(msg);
                            else {
                                if (msg.equals("heart"))
                                    return;
                                else
                                    tv.append("接收数据：" + msg + "\n");

                            }
                        }
                    });
                }
            }

        } catch (IOException e) {
            logger.e("IOException" + e.getMessage());
            updateState(false);
            disConnect();


        } finally {
        }
    }

    private BluetoothAdapter bAtapter;

    private void disConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (transferSocket != null) {
//                        transferSocket.
                        transferSocket.close();

                        transferSocket = null;
                    }
                    if (btserver != null)
                        btserver.close();
                    bAtapter = BluetoothAdapter.getDefaultAdapter();
                    bAtapter.disable();
                    Thread.sleep(1000);
                    bAtapter.enable();
                    Thread.sleep(1000 * 3);
                    startServerSocket(BluetoothAdapter.getDefaultAdapter());

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (transferSocket != null) {
                transferSocket.close();
                transferSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class HeardThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                if (transferSocket != null)
                    sendMessage(transferSocket, "heart");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btn1) {
            sendMessage(transferSocket, "收到主设备返回数据：" + "ServerTEST1\n");

        } else if (view == btn2) {
            sendMessage(transferSocket, "收到主设备返回数据：" + "ServerTEST2\n");


        } else if (view == btn3) {
            sendMessage(transferSocket, "收到主设备返回数据：" + "ServerTEST3\n");


        } else if (view == btn4) {
            sendMessage(transferSocket, "收到主设备返回数据：" + "ServerTEST4\n");

        } else if (view == btn5) {
            sendMessage(transferSocket, "收到主设备返回数据：" + "ServerTEST5\n");

        } else if (view == btn6) {
            sendMessage(transferSocket, "收到主设备返回数据：" + "ServerTEST6\n");

        } else if (view == fbtnClear) {
            tv.setText("");
        }
    }

}
