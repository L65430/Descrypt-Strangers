package com.example.administrator.finalprocject.Utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.administrator.finalprocject.R;

/**
 * Created by Administrator on 2016/8/31 0031.
 */
//对话框工具类
public class myDialog {

    Dialog dialog;

    public myDialog(Context context, String dialogText) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_logining, null);
        TextView dialogTextView = (TextView) dialogView.findViewById(R.id.dialog_text);
        dialogTextView.setText(dialogText);
        dialog.setContentView(dialogView);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(R.drawable.mydialog_bg);
        WindowManager.LayoutParams lp = window.getAttributes();
        // 设置透明度为0.3
        lp.alpha = 0.7f;
        window.setAttributes(lp);
    }

    public void show(){
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }

}



