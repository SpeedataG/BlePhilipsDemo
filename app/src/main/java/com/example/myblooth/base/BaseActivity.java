package com.example.myblooth.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myblooth.ui.CustomProgressDialog;
import com.example.myblooth.R;
import com.example.myblooth.utils.MyLogger;
import com.example.myblooth.utils.StatusBarUtil;


/**
 * ----------Dragon be here!----------/
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃神兽保佑
 * 　　　　┃　　　┃代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━神兽出没━━━━━━
 *
 * @author Reginer on  2016/10/9 9:15.
 *         Description:Activity基类
 */
public class BaseActivity extends Activity {

    protected TextView mBarTitle;
    public CustomProgressDialog mProgressDialog;
    protected ImageView mBarLeft;
    //    public MyApp myApp;
    public MyLogger logger = MyLogger.jLog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.theme));
        StatusBarUtil.setTranslucent(this);
        mProgressDialog = new CustomProgressDialog(this, R.style.loading_dialog, "");
    }


    /**
     * 这是导航
     *
     * @param left  左侧图标
     * @param title 标题
     */
    protected void setNavigation(int left, int title) {
        mBarTitle = (TextView) findViewById(R.id.tv_bar_title);
        if (title != 0) {
            mBarTitle.setText(title);
        }
    }

    public void initBle(boolean isBle) {
        ImageView StateBle = (ImageView) findViewById(R.id.imag_ble);
        if (isBle) {
            StateBle.setImageDrawable(getResources().getDrawable(R.drawable.ble_true));
        } else {
            StateBle.setImageDrawable(getResources().getDrawable(R.drawable.ble_false));
        }
    }

    /**
     * 初始化标题
     */
    public void initTitle(String title, boolean leftVis, View.OnClickListener lis, int resid) {
        TextView tvTitle = (TextView) findViewById(R.id.tv_bar_title);
        tvTitle.setText(title);
        ImageView imgExit = (ImageView) findViewById(R.id.iv_left);


        if (leftVis) {
            imgExit.setVisibility(View.VISIBLE);
            imgExit.setImageDrawable(getResources().getDrawable(R.drawable.icon_back));
        } else
            imgExit.setVisibility(View.INVISIBLE);
        imgExit.setOnClickListener(lis);

    }

    /**
     * 这是导航
     *
     * @param left  左侧图标
     * @param title 标题
     */
    protected void setNavigation(int left, String title) {
        mBarTitle = (TextView) findViewById(R.id.tv_bar_title);
        mBarLeft = (ImageView) findViewById(R.id.iv_left);
        if (!TextUtils.isEmpty(title)) {
            mBarTitle.setText(title);
        }
        mBarLeft.setVisibility(left == 0 ? View.GONE : View.VISIBLE);
    }


    protected void setRightNavigation(int right, View.OnClickListener lis) {
        mBarTitle = (TextView) findViewById(R.id.tv_bar_title);
        mBarLeft = (ImageView) findViewById(R.id.iv_right);

        mBarLeft.setVisibility(right == 0 ? View.GONE : View.VISIBLE);
        mBarLeft.setOnClickListener(lis);
    }


    /**
     * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
     *
     * @param src0 byte
     * @param src1 byte
     * @return byte
     */
    public  byte uniteBytes(byte src0, byte src1) {
        try {
            byte _b0 = Byte.decode("0x" + new String(new byte[]{src0}))
                    .byteValue();
            _b0 = (byte) (_b0 << 4);
            byte _b1 = Byte.decode("0x" + new String(new byte[]{src1}))
                    .byteValue();
            byte ret = (byte) (_b0 ^ _b1);
            return ret;
        } catch (Exception e) {
            // TODO: handle exception
            return 0;
        }

    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
     * 0xD9}
     *
     * @param temp String
     * @return byte[]
     */
    public  byte[] HexString2Bytes(String temp) {
//		src.replace("\\s","");
        String src = temp.replace(" ", "");
        System.out.println(" src= " + src);
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < src.length() / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }
}