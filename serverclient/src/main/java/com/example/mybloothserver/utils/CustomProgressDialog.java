package com.example.mybloothserver.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.example.mybloothserver.R;


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
 * @author Reginer on  2016/9/22 14:10.
 *         Description:加载框
 */
public class CustomProgressDialog extends Dialog {

    private TextView mMessageView;

    public CustomProgressDialog(Context context) {
        this(context, null);
    }

    private CustomProgressDialog(Context context, String strMessage) {
        this(context, R.style.loading_dialog, strMessage);
    }

    private CustomProgressDialog(Context context, int theme, String strMessage) {
        super(context, theme);
        this.setContentView(R.layout.view_custom_progress_dialog);
        if (this.getWindow() != null)
            this.getWindow().getAttributes().gravity = Gravity.CENTER;
        mMessageView = (TextView) this.findViewById(R.id.tipTextView);
        if (mMessageView != null) {
            mMessageView.setText(strMessage);
        }
    }

    public void setMessage(String message) {
        mMessageView.setText(message);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (!hasFocus) {
            dismiss();
        }
    }
}
