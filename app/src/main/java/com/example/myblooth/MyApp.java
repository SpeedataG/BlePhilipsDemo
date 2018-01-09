package com.example.myblooth;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;

import com.example.myblooth.callback.MesCallBack;
import com.example.myblooth.utils.MyLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
 * <p>
 * 功能描述:TODO
 *
 * @author :EchoXBR in  2017/8/31 上午9:52.
 */

public class MyApp extends Application {
    private MyLogger logger = MyLogger.jLog();
    private MesCallBack mesCallBack;
    private BluetoothSocket transferSocket;

    public void setMesCallBack(MesCallBack mesCallBack) {
        this.mesCallBack = mesCallBack;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 断开链接
     */
    public void disConnect() {
        logger.d("disConnect -1");
        if (transferSocket != null) {
            try {
                transferSocket.close();
                transferSocket = null;
                mesCallBack.isConnect(false);

                logger.d("disConnect close");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean listening = false;

    private long lastTime;//上次心跳时间

    private void listenForMessages(BluetoothSocket socket
    ) {
        listening = true;
        StringBuilder incoming = new StringBuilder();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        try {
            InputStream instream = socket.getInputStream();
            int bytesRead = -1;

            while (listening) {
                bytesRead = instream.read(buffer);
                if (bytesRead != -1) {

                    String result = "";
                    while ((bytesRead == bufferSize)
                            && (buffer[bufferSize - 1] != 0)) {
                        result = result + new String(buffer, 0, bytesRead - 1);
                        bytesRead = instream.read(buffer);
                    }
                    result = result + new String(buffer, 0, bytesRead - 1);
                    incoming.append(result);
                    logger.d("服务器说：" + incoming.toString());
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            // TODO Auto-generated method stub
//                            tv.setText(incoming.toString());
//                        }
//                    });
                    mesCallBack.ReceMsg(incoming.toString());
                    incoming = new StringBuilder();
                    lastTime = SystemClock.currentThreadTimeMillis();
                } else {
                    logger.d("read -1");
                    if (SystemClock.currentThreadTimeMillis() - lastTime > 1000 * 5) {
                        //链接断开
                        disConnect();
                        mesCallBack.isConnect(false);
                    }
                }

            }
            // socket.close();
        } catch (IOException e) {

        } finally {
        }
    }


    public void connectToServerSocket(BluetoothDevice device, UUID uuid) {
        try {
            BluetoothSocket clientSocket = device
                    .createRfcommSocketToServiceRecord(uuid);

            // Block until server connection accepted.
            clientSocket.connect();
            mesCallBack.isConnect(true);
            transferSocket = clientSocket;

            // Start listening for messages.

//            listenForMessages(clientSocket);

            // Add a reference to the socket used to send messages.
        } catch (IOException e) {
            logger.e("Blueooth client I/O Exception", e);
            mesCallBack.shwoErrorMsg("ERROR" + e.getMessage());
        }
    }

    public void sendMessageByte(byte[] bytes) {
        OutputStream outStream;
        try {
            if (transferSocket != null) {
                logger.d("send start");
                outStream = transferSocket.getOutputStream();
                outStream.write(bytes);
                logger.d("send finish");
            }
        } catch (IOException e) {
            logger.e(e.getMessage());
            mesCallBack.isConnect(false);
        }
    }

    public void sendMessage(String message) {
        OutputStream outStream;
        try {
            if (transferSocket != null) {
                logger.d("send start");
                outStream = transferSocket.getOutputStream();

                // Add a stop character.
                byte[] byteArray = (message + " ").getBytes();
                byteArray[byteArray.length - 1] = 0;

                outStream.write(byteArray);
                logger.d("send finish");
            }
//            else {
//                logger.d("send failed because transferSocket=null");
//            }
        } catch (IOException e) {
            logger.e(e.getMessage());
            mesCallBack.isConnect(false);
        }
    }

    public String getDevice() {
        if (transferSocket != null)
            return transferSocket.getRemoteDevice().toString();
        else
            return "";
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (transferSocket != null) {
            try {
                transferSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
