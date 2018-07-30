package com.example.administrator.finalprocject.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.administrator.finalprocject.R;


public class RegStepOne extends Fragment {

    View view;
    EditText tv_username;
    EditText tv_email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_reg_step_one, container, false);
        tv_username = (EditText) view.findViewById(R.id.regone_et_username);
        tv_email = (EditText) view.findViewById(R.id.regone_et_email);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        System.out.println("RegStepOne执行onAttach"+this.getClass().toString());
        super.onAttach(activity);
    }

    public String getUsername(){

        return tv_username.getText().toString();

    }
    public void resetUsername() {
        tv_username.setText("");
    }
    public String getEmail(){

        return tv_email.getText().toString();

    }
    public void resetEmail() {
        tv_email.setText("");
    }


}
