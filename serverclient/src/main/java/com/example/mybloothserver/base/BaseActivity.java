package com.example.mybloothserver.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mybloothserver.R;
import com.example.mybloothserver.utils.CustomProgressDialog;
import com.example.mybloothserver.utils.MyLogger;
import com.example.mybloothserver.utils.StatusBarUtil;


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
    private CustomProgressDialog mProgressDialog;
    protected ImageView mBarLeft;
    //    public MyApp myApp;
    public MyLogger logger = MyLogger.jLog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.theme));
        StatusBarUtil.setTranslucent(this);
//        myApp= (MyApp) getApplication();
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

    public void initBle(boolean isBle) {
        ImageView StateBle = (ImageView) findViewById(R.id.imag_ble);
        if (isBle) {
            StateBle.setImageDrawable(getResources().getDrawable(R.drawable.ble_true));
        } else {
            StateBle.setImageDrawable(getResources().getDrawable(R.drawable.ble_false));
        }
    }

}