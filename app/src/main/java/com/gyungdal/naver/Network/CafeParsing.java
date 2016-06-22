package com.gyungdal.naver.Network;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;

import com.gyungdal.naver.Config;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by GyungDal on 2016-06-22.
 */
public class CafeParsing extends AsyncTask<String, Void, HashMap<String, String>> {
    private static final String TAG = CafeParsing.class.getName();

    @Override
    protected HashMap<String, String> doInBackground(String... params) {
        int max, i = 0;
        HashMap<String, String> hashMap = new HashMap<>();
        try {
            Log.i(TAG, "Parsing Start");
            String cookie = CookieManager.getInstance().getCookie("http://m.cafe.naver.com/onlyonedsm.cafe");
            Log.i(TAG, cookie);
            Document doc = Jsoup.connect("http://m.cafe.naver.com/SectionCafeSearch.nhn?page="
                    + String.valueOf(i++) + "&sortBy=0&query=" + params[0])
                    .userAgent(Config.HEADER_UA)
                    .header("ContentType", Config.HEADER_CONTYPE)
                    .header("Cookie", cookie)
                    .get();
            Log.i(TAG, "http://m.cafe.naver.com/SectionCafeSearch.nhn?page="
                    + String.valueOf(i) + "&sortBy=0&query=" + params[0]);
            max = Integer.valueOf(doc.select("span.cnt:nth-child(2)").get(0).text()) / 5;
            if(max > 20)
                max = 20;

            while (max > i) {
                for (int j = 1; j <= 5; j++) {
                    Elements articles = doc.select("#sectionCafeSearchList > li:nth-child(" + j + ") > a:nth-child(1)");
                    String cafeName = articles.select("a").get(0).select("strong").get(0).text();
                    String cafeUrl = articles.select("a").get(0).attr("href");
                    Log.i(TAG, cafeName);
                    Log.i(TAG, cafeUrl);
                    hashMap.put(cafeName, cafeUrl);
                }
                doc = Jsoup.connect("http://m.cafe.naver.com/SectionCafeSearch.nhn?page="
                        + String.valueOf(i++) + "&sortBy=0&query=" + params[0])
                        .userAgent(Config.HEADER_UA)
                        .header("ContentType", Config.HEADER_CONTYPE)
                        .header("Cookie", cookie)
                        .get();
            }
        }catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
        return hashMap;
    }
}
