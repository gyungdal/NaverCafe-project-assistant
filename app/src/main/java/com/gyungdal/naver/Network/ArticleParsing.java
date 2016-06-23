package com.gyungdal.naver.Network;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by GyungDal on 2016-06-04.
 */

/**
 * Prams
 * 0. URL
 * 1. File path
 */
public class ArticleParsing extends AsyncTask<String, Void, String> {
    private static final String TAG = ArticleParsing.class.getName();
    private static final byte FILE_DUMMY_TEXT_LENGHT = 39;
    private String path, userInfo;
    private Date start, end;
    private View view;
    public ArticleParsing(View view, Date start, Date end){
        this.view = view;
        this.start = start;
        this.end = end;
    }

    @Override
    protected String doInBackground(String... params) {
        String Result = "";
        try {
            Log.i(TAG, "Parsing Start");
            String cookie = CookieManager.getInstance().getCookie("http://m.cafe.naver.com/onlyonedsm.cafe");
            Log.i(TAG, cookie);
            Document doc = Jsoup.connect("http://m.cafe.naver.com/" + params[0])
                    .userAgent(Config.HEADER_UA)
                    .header("ContentType", Config.HEADER_CONTYPE)
                    .header("Cookie", cookie)
                    .get();
            String title = doc.select("h2.tit").get(0).text();
            String date = doc.select("span.date:nth-child(1)").get(0).text();
            Calendar cal = Calendar.getInstance();
            cal.set(getYear(date), getMonth(date), getDay(date), getHour(date), getMin(date));
            Date ArticleDate = cal.getTime();
            Log.i(TAG, title);
            Log.i("Start", start.toString());
            Log.i("Article time", ArticleDate.toString());
            Log.i("End", end.toString());
            if((ArticleDate.after(start) && ArticleDate.before(end))){

                String userGrade = doc.select("span.ellip:nth-child(2)").get(0).text().substring(0, 1);
                Element user = doc.select("h2.tit").get(0);
                userInfo = user.text();
                String userClass = getClass(userInfo);
                String userTeam = getTeam(userInfo);
                if (userGrade.matches("^[0-9]*$") && userClass.matches("^[0-9]*$") && userTeam.matches("^[a-zA-Z]*$"))
                    path = params[1] + userGrade + "학년" + File.separator + userClass + "반" + File.separator
                            + userTeam + File.separator;
                else
                    path = params[1] + "Unknown Name Roll" + File.separator;
                Elements articleText = doc.select("#postContent > div:nth-child(2)");
                articleText = articleText.select("p");
                if (articleText.text().equals("")) {
                    articleText = doc.select("#postContent");
                }
                articleSave(articleText.text());
                Log.i(TAG, articleText.text());
                Log.i(TAG, doc.title());
                Elements fileList = doc.select(".flies_area > ul:nth-child(1)").select("li a");
                for (Element file : fileList) {
                    String fileUrl = file.select("a").attr("class");
                    fileUrl = fileUrl.substring(FILE_DUMMY_TEXT_LENGHT, fileUrl.lastIndexOf(")"));
                    String fileName = file.select("p").text();
                    Log.i(TAG, file.toString());
                    Log.i(TAG, fileName);
                    Log.i(TAG, fileUrl);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        FileDownloader fileDownloader = new FileDownloader();
                        fileDownloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path + fileName, fileUrl);
                    } else {
                        FileDownloader fileDownloader = new FileDownloader();
                        fileDownloader.execute(path + fileName, fileUrl);
                    }
                    Result = Result + "\nFile Name : " + fileName + "\nFile Url : " + fileUrl + "\n";
                }
                //Pattern pat = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");
                //Matcher mat = pat.matcher(Result);
                //Result = mat.replaceAll("");
                Log.i(TAG, Result);
                Snackbar.make(view, "첨부파일 다운로드...", Snackbar.LENGTH_INDEFINITE).show();
            }else {
                Log.i(TAG, "Time!!!");
                Log.i("Article time", ArticleDate.toString());
            }
        }catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
        Log.i(TAG, "Parsing End");
        return Result;
    }
    private void articleSave(String str) throws IOException{
        File file = new File(path);
        if(!file.exists())
            file.mkdirs();
        if(path.contains("Unknown Name Roll"))
            file = new File(path + userInfo + ".txt");
        else
            file = new File(path + "body.txt");

        if(file.isFile()) {
            Log.i(TAG, "Already article text save.");
            return;
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(str.getBytes());
        fileOutputStream.close();
        if(file.length() == 0)
            file.delete();
    }

    @NonNull
    private static String getClass(String s){
        if(!s.contains("_"))
            return "abc";
        Log.i(TAG, "Article Class : " + s.substring(s.indexOf("_")+2,s.indexOf("_")+3));
        return s.substring(s.indexOf("_")+2,s.indexOf("_")+3);
    }


    @NonNull
    private static String getTeam(String s){
        if(!s.contains("("))
            return "abc";
        Log.i(TAG, "Article Team : " + s.substring(s.indexOf("(")-1, s.indexOf("(")));
        return s.substring(s.indexOf("(")-1, s.indexOf("("));
    }

    private static int getYear(String s){
        return Integer.valueOf(s.substring(0, 4));
    }

    private static int getMonth(String s){
        Log.i("Month", s.substring(5, 7));
        return (Integer.valueOf(s.substring(5, 7)));
    }

    private static int getDay(String s){
        return Integer.valueOf(s.substring(8, 10));
    }

    private static int getHour(String s){
        return Integer.valueOf(s.substring(12, 14));
    }

    private static int getMin(String s){
        return Integer.valueOf(s.substring(15, 17));
    }
}
