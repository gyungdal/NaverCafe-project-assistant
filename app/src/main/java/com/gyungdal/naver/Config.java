package com.gyungdal.naver;

/**
 * Created by GyungDal on 2016-06-04.
 */
public class Config {
    public static String TEST_ID = "";
    public static String TEST_PASS = "";
    public static final String HEADER_CONTYPE = "header_ConType = \"application/x-www-form-urlencoded\";";
    public static final String HEADER_UA = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0";
    public static final String HEADER_REFERERURI = "http://static.nid.naver.com/login.nhn?svc=wme&amp;url=http%3A%2F%2Fwww.naver.com&amp;t=20120425";
    public static final String CAFE_URL = "http://cafe.naver.com/onlyonedsm";
    public static final String NAVER_IMAP = "imap.naver.com";
    public static final int MENU_LOGIN = 0;
    public static final int MENU_MAIL = 1;
    public static final int MENU_CAFE = 2;
    public static final int MENU_INSERT_CAFE = 3;
    public static final String CAUTION = "변수가 들어갈 자리에는 *을 넣어주세요\n" +
            "예시 : 자바*8*주차";
}
