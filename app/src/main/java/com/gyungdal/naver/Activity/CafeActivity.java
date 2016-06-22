package com.gyungdal.naver.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.Toast;

import com.gyungdal.naver.Config;
import com.gyungdal.naver.Network.NoticeBoardParsing;
import com.gyungdal.naver.R;

public class CafeActivity extends AppCompatActivity {
    private static final String TAG = CafeActivity.class.getName();
    private EditText eurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_cafe);
        eurl = (EditText)findViewById(R.id.testUrl);
        CookieSyncManager.getInstance().startSync();
        Toast.makeText(getApplicationContext(), Config.CAUTION, Toast.LENGTH_LONG).show();
    }

    public void click(View v){
        switch(v.getId()){
            case R.id.testButton :
                NoticeBoardParsing noticeBoardParsing = new NoticeBoardParsing(getCurrentFocus());
                if(!eurl.getText().toString().isEmpty())
                    noticeBoardParsing.execute(eurl.getText().toString());
                //articleParsing.execute(Config.TEST_URL);
                break;
            default : break;
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
}
