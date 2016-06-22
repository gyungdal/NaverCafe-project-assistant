package com.gyungdal.naver.Network;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;

import com.gyungdal.naver.Config;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by GyungDal on 2016-06-20.
 */
public class NoticeBoardParsing extends AsyncTask<String, Void, Void> {
    private static final String TAG = NoticeBoardParsing.class.getName();
    private static final String SEARCH_FRONT = "http://m.cafe.naver.com/ArticleSearchList.nhn?search.query=";
    private static final String SEARCH_BACK = "&search.menuid=&search.searchBy=0&search.sortBy=sim&search.clubid=27874272&search.page=";

    private View view;
    public NoticeBoardParsing(View view){
        this.view = view;
    }

    @Override
    protected Void doInBackground(String... params) {
        int i = 0;

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat nowDate = new SimpleDateFormat("yyyyMMddHHmmss");
        String nowString = nowDate.format(date);
        Log.i(TAG, nowString);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "Project Assistant" + File.separator + nowString + File.separator;
        try {
            Log.i(TAG, "Notice Board Parsing Start");
            String cookie = CookieManager.getInstance().getCookie("http://m.cafe.naver.com/onlyonedsm.cafe");
            Log.i(TAG, cookie);
            String search;
            if(params[0].contains("*")) {
                Log.i(TAG, params[0].substring(0, params[0].indexOf("*")));
                search = params[0].substring(0, params[0].indexOf("*"));
            } else {
                Log.i(TAG, params[0].substring(0, params[0].length()));
                search = params[0].substring(0, params[0].length());
            }
                Document doc = Jsoup.connect(SEARCH_FRONT + search
                        + SEARCH_BACK + String.valueOf(i++))
                    .userAgent(Config.HEADER_UA)
                    .header("ContentType", Config.HEADER_CONTYPE)
                    .header("Cookie", cookie)
                    .get();

            while(!doc.toString().contains("에 대한 검색결과가 없습니다.")){
                for(int j = 1 ; j<=20;j++) {
                    Elements articles = doc.select(".lst_section > li:nth-child(" + j + ")");
                    Log.i(TAG, "iterator size : " + articles.size());
                        Log.i(TAG, String.valueOf(articles.size()));
                        Element article = articles.select("a").get(0);
                        Log.i(TAG, article.select("div > h3").get(0).text());

                        if(params[0].contains("*")) {
                            if (!isMatch(article.select("div > h3").get(0).text(), params[0]))
                                continue;
                        }else{
                            if(!isMatch(article.select("div > h3").get(0).text(), (params[0] + "*")))
                                 continue;
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            ArticleParsing articleParsing = new ArticleParsing(view);
                            articleParsing.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                    article.attr("href").toString(), path);
                        } else {
                            ArticleParsing articleParsing = new ArticleParsing(view);
                            articleParsing.execute(article.attr("href").toString(), path);
                        }
                }
                doc = Jsoup.connect(SEARCH_FRONT + search + SEARCH_BACK + String.valueOf(i++))
                        .userAgent(Config.HEADER_UA)
                        .header("ContentType", Config.HEADER_CONTYPE)
                        .header("Cookie", cookie)
                        .get();
                }
            }catch(Exception e){
                Log.e(TAG, e.getMessage());
        }
        Log.i(TAG, "Board Parsing End");
        Snackbar.make(view, "다운로드 시작...", Snackbar.LENGTH_LONG).show();
        return null;
    }

    private static boolean isMatch(String str, String match){
        if(match.contains("*")){
            return isMatch(str, match.substring(0, match.lastIndexOf("*")));
        }else
            return str.contains(match);
    }
}
