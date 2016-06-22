package com.gyungdal.naver.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gyungdal.naver.Config;
import com.gyungdal.naver.R;
import com.gyungdal.naver.process_login;
/**
 * Created by GyungDal on 2016-06-18.
 */
public class LoginActivity extends AppCompatActivity{
    private static final String TAG = LoginActivity.class.getName();
    private EditText eid;
    private EditText epass;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);
        eid = (EditText) findViewById(R.id.id);
        epass = (EditText) findViewById(R.id.pass);

    }

    public void Click(View v){
        switch(v.getId()){
            case R.id.login :
                if(eid.getText().toString().isEmpty() ||
                    epass.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "ID와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    break;
                }
                Config.TEST_ID = eid.getText().toString();
                Config.TEST_PASS = epass.getText().toString();
                LoginActivity.this.startActivity(new Intent(this, process_login.class));
                LoginActivity.this.finish();
                break;
        }
    }
}
