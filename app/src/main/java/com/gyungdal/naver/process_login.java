package com.gyungdal.naver;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.gyungdal.naver.Activity.CafeActivity;
import com.gyungdal.naver.Activity.LoginActivity;

import java.net.CookieStore;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by GyungDal on 2016-06-03.
 */
public class process_login extends AppCompatActivity {
    private static final String TAG = process_login.class.getName();
    private static final String NAVER_LOGIN = "https://nid.naver.com/nidlogin.login";
    private static final int ERROR_MAX = 5;

    private WebView web;

    private int stack;
    private String ID;
    private String PASS;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.process_login);
        web = (WebView)findViewById(R.id.loginView);
        CookieSyncManager.createInstance(getApplicationContext());
        CookieSyncManager.getInstance().startSync();
        CookieManager.getInstance().setAcceptCookie(true);
        ID = Config.TEST_ID;
        PASS = Config.TEST_PASS;
        stack = 0;
        web.setVisibility(View.INVISIBLE);
        naverLogin();
    }

    private void naverLogin(){
        dialog = ProgressDialog.show(process_login.this, "Wait...", "로그인 중...", true);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setSaveFormData(true);
        web.getSettings().setSupportMultipleWindows(true);
        web.getSettings().setAppCacheEnabled(true);
        web.getSettings().setDatabaseEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            web.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        Map<String, String> extraHeaders = new HashMap<String, String>();
        extraHeaders.put("Referer", Config.HEADER_REFERERURI);
        extraHeaders.put("ContentType", Config.HEADER_CONTYPE);
        extraHeaders.put("User-Agent", Config.HEADER_UA);
        web.setWebViewClient(new ViewClient());
        web.loadUrl(NAVER_LOGIN, extraHeaders);
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

    protected class ViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            view.setClickable(false);
            Log.d("Browse", url);
            if(url.contains("nidlogin.login")){
                Log.i(TAG, "stack : " + ++stack);
                if(stack >= ERROR_MAX){
                    dialog.dismiss();
                    CookieSyncManager.getInstance().stopSync();
                    web.setWebViewClient(null);
                    Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                    process_login.this.startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    process_login.this.finish();
                }
            }
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!url.contains("cafe")) {
                CookieSyncManager.getInstance().sync();
                view.loadUrl(Config.CAFE_URL);
            } else if(url.equals("http://m.cafe.naver.com/onlyonedsm.cafe")) {
                CookieSyncManager.getInstance().sync();
                dialog.dismiss();
                process_login.this.startActivity(new Intent(getApplicationContext(), CafeActivity.class));
                process_login.this.finish();
            } else {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.e(TAG, "ERROR CODE : " + errorCode);
            Log.e(TAG, description);
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onPageFinished(WebView v, String url){
            String Script = "var id = document.getElementById(\"id\");id.value = \"" + ID + "\";" +
                    "var pw = document.getElementById(\"pw\");pw.value = \"" + PASS + "\";" +
                    "document.getElementById(\"login_chk\").click();" +
                    "var button = document.getElementsByClassName(\"int_jogin\");" +
                    "for (var i=0;i<button.length; i++) {\n" +
                    "    button[i].click();\n" +
                    "};";
            v.evaluateJavascript(Script, null);

        }
    }


}
