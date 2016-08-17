package com.gyungdal.naver.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gyungdal.naver.Config;
import com.gyungdal.naver.Network.MailClient;
import com.gyungdal.naver.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by GyungDal on 2016-06-18.
 */
public class MailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MailActivity.class.getName();
    private Handler mHandler;
    private ProgressDialog dialog;
    protected static final int MAIL_FAIL = 1;
    protected static final int MAIL_SUCCESS = 0;

    private EditText input;
    private Date Start;
    private Date End;
    private ImageButton SetStart;
    private ImageButton SetEnd;
    private Calendar cal;
    private int year, month, day, hour, min;
    private TextView StartDate;
    private TextView EndDate;
    protected int select;
    private int i;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_mail);


        SetStart = (ImageButton) findViewById(R.id.SetStartDate);
        SetEnd = (ImageButton) findViewById(R.id.SetEndDate);
        input = (EditText)findViewById(R.id.Input) ;
        StartDate = (TextView) findViewById(R.id.StartDate);
        EndDate = (TextView) findViewById(R.id.EndDate);
        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        SetStart.setOnClickListener(this);
        SetEnd.setOnClickListener(this);

        Toast.makeText(getApplicationContext(), Config.CAUTION, Toast.LENGTH_LONG).show();
        getPermission();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                switch (msg.what) {
                    case MAIL_SUCCESS:
                        Toast.makeText(MailActivity.this, "성공적으로 메일 메시지를 읽었습니다.", Toast.LENGTH_SHORT).show();
//                    adapter.notifyDataSetChanged();
                        break;
                    case MAIL_FAIL :
                        Toast.makeText(MailActivity.this, "메일 메시지 읽기에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }
    @Override
    public void onClick(View v) {
        select = v.getId();
        switch(select){
            case R.id.MailButton :
                if(StartDate.getText().toString().contains("/") &&
                        StartDate.getText().toString().contains("/")){
                    if(!input.getText().toString().isEmpty()) {
                        mThread m = new mThread(MailActivity.this);
                        m.start();
                    }
                }
                else
                    Toast.makeText(getApplicationContext(), "날짜를 지정해주세요", Toast.LENGTH_SHORT).show();
                break;
            default:
                showDialog(0);
                break;
        }
    }


    private class mThread extends Thread {
        public mThread(Context c) {
            dialog = new ProgressDialog(c);
            dialog.setTitle("Wait...");
            dialog.setMessage("메일을 읽고 있습니다...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        public void run() {
            try {
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat nowDate = new SimpleDateFormat("yyyyMMddHHmmss");
                String nowString = nowDate.format(date);
                Log.i(TAG, nowString);
                MailClient mail = new MailClient();

                mail.setAccountDetails(Config.TEST_ID, Config.TEST_PASS, Config.NAVER_IMAP);
                Message mes = new Message();
                mes.obj = mes.what = mail.readMail(Start, End, input.getText().toString(), nowString);

                mHandler.sendMessage(mes);
            } catch (Exception e) {
                Log.e("IMAP Protocol", e.getMessage(), e);
                Message mes = new Message();
                mes.what = MAIL_FAIL;
                mes.obj = new String("메일을 읽는데 실패하였습니다.");
                mHandler.sendMessage(mes);
            }
        }
    }

    private void getPermission(){
        if (ContextCompat.checkSelfPermission(MailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {

            //권한이 없을 경우
            //최초 권한 요청인지, 혹은 사용자에 의한 재요청인지 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ) { //사용자가임의로 권한을취소 시킨 경우
                //권한 재요청
                Toast.makeText(getApplication(), "파일 권한이 없으면 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, i++);
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {  //최초로 권한을 요청하는 경우(첫실행)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, i++);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, Config.MENU_LOGIN, Menu.NONE, "Login");
        menu.add(0, Config.MENU_CAFE, Menu.NONE, "Cafe");
        menu.add(0, Config.MENU_INSERT_CAFE, Menu.NONE, "Add Cafe");
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case Config.MENU_CAFE :
                Log.i(TAG, "Mail Activity -> Cafe Activity");
                MailActivity.this.startActivity(new Intent(this, CafeActivity.class));
                MailActivity.this.finish();
                break;
            case Config.MENU_LOGIN :
                Log.i(TAG, "Mail Activity -> Login Activity");
                MailActivity.this.startActivity(new Intent(this, LoginActivity.class));
                MailActivity.this.finish();
                break;
            case Config.MENU_INSERT_CAFE :
                Log.i(TAG, "Mail Activity -> Insert Cafe Activity");
                MailActivity.this.startActivity(new Intent(this, InsertCafeActivity.class));
                MailActivity.this.finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // TODO Auto-generated method stub
            switch(select){
                case R.id.SetStartDate :
                    Start.setHours(hourOfDay);
                    Start.setMinutes(minute);
                    break;
                case R.id.SetEndDate :
                    End.setHours(hourOfDay);
                    End.setMinutes(minute);
                    break;
            }
            String msg = String.format("%d / %d / %d", year, hourOfDay, minute);
            Snackbar.make(getCurrentFocus(), msg, Snackbar.LENGTH_INDEFINITE).show();
        }
    };
    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, year, month, day);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            switch(select){
                case R.id.SetStartDate : {
                    Calendar temp = Calendar.getInstance();
                    temp.set(selectedYear, selectedMonth, selectedDay, min, 0, 0);
                    Start = temp.getTime();
                    if(End == null || !End.before(Start)) {
                        new DatePickerDialog(getApplicationContext(), datePickerListener, year, month, day);
                        new TimePickerDialog(getApplicationContext(), timeSetListener, hour, min, true);
                    }
                    StartDate.setText(selectedYear+ " / " + (selectedMonth) + " / "
                            + selectedDay);
                    break;
                }

                case R.id.SetEndDate : {
                    Calendar temp = Calendar.getInstance();
                    temp.set(selectedYear, selectedMonth, selectedDay, 23, 59, 59);
                    End = temp.getTime();
                    if(Start == null || !Start.after(End)) {
                        new DatePickerDialog(getApplicationContext(), datePickerListener, year, month, day);
                        new TimePickerDialog(getApplicationContext(), timeSetListener, hour, min, true);
                    }
                    EndDate.setText(selectedYear+ " / " + (selectedMonth) + " / "
                            + selectedDay);
                    break;
                }
            }
        }
    };

}
