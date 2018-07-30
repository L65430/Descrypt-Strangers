package com.example.administrator.finalprocject.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.administrator.finalprocject.Info.ClientManger;
import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.StaticValues.CSKeys;

import java.util.Calendar;

public class BirthEditActivity extends Activity {

    ImageButton ib_back;
    Button btn_submit;
    EditText et_birth;
    String birth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_birth_edit);
        /**
         * 返回
         */
        ib_back = (ImageButton) findViewById(R.id.birth_ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        /**
         * 获取生日
         */
        et_birth = (EditText) findViewById(R.id.birth_et_birthday);
        et_birth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showDatePickerDialog();
            }
        });
        /***
         * 提交数据库
         */
        btn_submit = (Button) findViewById(R.id.birth_btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                birth = et_birth.getText().toString();
                if (!birth.equals("")) {
                    MainActivity.myBinder.ResetUserInfo(
                            CSKeys.RESET_BIRTHDAY, birth);
                    ClientManger.clientBirthday = birth;
                    PersonalInfoActivity.refreshClientInfo(
                            CSKeys.RESET_BIRTHDAY, birth);
                    finish();
                } else {
                    Toast.makeText(BirthEditActivity.this, "请填写生日", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    /**
     * 日期选择框
     *
     * @author Administrator
     *
     */
    private class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Log.d("OnDateSet", "select year:" + year + ";month:" + month
                    + ";day:" + day);
            et_birth.setText(year + "-" + (month + 1) + "-" + day);
        }
    }

    /**
     * 创建日期选择框
     */
    public void showDatePickerDialog() {
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.show(getFragmentManager(), "datePicker");
    }

}
