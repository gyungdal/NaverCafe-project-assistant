package com.gyungdal.naver.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gyungdal.naver.Config;
import com.gyungdal.naver.Network.NoticeBoardParsing;
import com.gyungdal.naver.R;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

public class CafeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = CafeActivity.class.getName();
    private EditText eurl;
    private Date Start;
    private Date End;
    private ImageButton SetStart;
    private ImageButton SetEnd;
    private TextView StartDate;
    private TextView EndDate;
    protected int select, year, month, day;
    private int i;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_cafe);
        eurl = (EditText)findViewById(R.id.testUrl);
        CookieSyncManager.getInstance().startSync();
        StartDate = (TextView) findViewById(R.id.CafeStartDate);
        EndDate = (TextView) findViewById(R.id.CafeEndDate);
        SetStart = (ImageButton) findViewById(R.id.CafeSetStartDate);
        SetEnd = (ImageButton) findViewById(R.id.CafeSetEndDate);
        Calendar cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        SetStart.setOnClickListener(this);
        SetEnd.setOnClickListener(this);
        Toast.makeText(getApplicationContext(), Config.CAUTION, Toast.LENGTH_LONG).show();
        getPermission();
    }

    @Override
    public void onClick(View v) {
        select = v.getId();
        showDialog(0);
    }

    public void click(View v){
        switch(v.getId()){
            case R.id.testButton :
                if(StartDate.getText().toString().contains("/") &&
                        StartDate.getText().toString().contains("/")) {
                    if (!eurl.getText().toString().isEmpty()){
                        NoticeBoardParsing noticeBoardParsing =
                                new NoticeBoardParsing(getCurrentFocus(), Start, End);
                        noticeBoardParsing.execute(eurl.getText().toString());
                        }
                }else
                    Toast.makeText(getApplicationContext(), "날짜를 설정해주세요", Toast.LENGTH_SHORT).show();
                break;
            default : break;
        }
    }
    private void getPermission(){
        if (ContextCompat.checkSelfPermission(CafeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {

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
    public void onResume(){
        super.onResume();
        CookieSyncManager.getInstance().startSync();
    }

    @Override
    public void onPause(){
        super.onPause();
        CookieSyncManager.getInstance().stopSync();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, Config.MENU_LOGIN, Menu.NONE, "Login");
        menu.add(0, Config.MENU_MAIL, Menu.NONE, "Mail");
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
            case Config.MENU_MAIL :
                Log.i(TAG, "Cafe Activity -> Mail Activity");
                CafeActivity.this.startActivity(new Intent(this, MailActivity.class));
                CafeActivity.this.finish();
                break;
            case Config.MENU_LOGIN :
                Log.i(TAG, "Cafe Activity -> Login Activity");
                CafeActivity.this.startActivity(new Intent(this, LoginActivity.class));
                CafeActivity.this.finish();
                break;
            case Config.MENU_INSERT_CAFE :
                Log.i(TAG, "Cafe Activity -> Insert Cafe Activity");
                CafeActivity.this.startActivity(new Intent(this, InsertCafeActivity.class));
                CafeActivity.this.finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, year, month, day);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            switch(select){
                case R.id.CafeSetStartDate : {
                    Calendar temp = Calendar.getInstance();
                    temp.set(selectedYear, selectedMonth+1, selectedDay, 0, 0, 0);
                    Start = temp.getTime();
                    if(End == null || !End.before(Start)) {
                        new DatePickerDialog(getApplicationContext(), datePickerListener, year, month, day);
                    }
                    StartDate.setText(selectedYear+ " / " + (selectedMonth + 1) + " / "
                            + selectedDay);
                    break;
                }

                case R.id.CafeSetEndDate : {
                    Calendar temp = Calendar.getInstance();
                    temp.set(selectedYear, selectedMonth+1, selectedDay, 23, 59, 59);
                    End = temp.getTime();
                    if(Start == null || !Start.after(End)) {
                        new DatePickerDialog(getApplicationContext(), datePickerListener, year, month, day);
                    }
                    EndDate.setText(selectedYear+ " / " + (selectedMonth + 1) + " / "
                            + selectedDay);
                    break;
                }
            }
        }
    };
}
