package com.gyungdal.naver.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by GyungDal on 2016-06-22.
 */
public class InsertCafeActivity extends AppCompatActivity {
    private static final String TAG = InsertCafeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_insert_cafe);
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
        menu.add(0, Config.MENU_CAFE, Menu.NONE, "Cafe");
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
                Log.i(TAG, "Insert Cafe Activity -> Mail Activity");
                InsertCafeActivity.this.startActivity(new Intent(this, MailActivity.class));
                InsertCafeActivity.this.finish();
                break;
            case Config.MENU_LOGIN :
                Log.i(TAG, "Insert Cafe Activity -> Login Activity");
                InsertCafeActivity.this.startActivity(new Intent(this, LoginActivity.class));
                InsertCafeActivity.this.finish();
                break;
            case Config.MENU_CAFE :
                Log.i(TAG, "Insert Cafe Activity -> Cafe Activity");
                InsertCafeActivity.this.startActivity(new Intent(this, CafeActivity.class));
                InsertCafeActivity.this.finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
