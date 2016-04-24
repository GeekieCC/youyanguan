package com.gusteauscuter.youyanguan.api;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 网络访问的实现类
 */
public class ApiImpl implements Api {

    private static String mBaseUrl= "http://geekie.cc/wanjuanwu2/";
    private static String mLoginUrl= mBaseUrl+"LoginServlet";
    private static String mSearchBookUrl= mBaseUrl+"SearchBookServlet";
    private static String mGetStoreInforUrl= mBaseUrl+"CheckCollectionInfoServlet";
    private static String mGetBookDetailUrl= mBaseUrl+"BookDetailServlet";

    @Override
    public boolean Login(String username, String password) {
        try {
            Map<String, String> values = new HashMap<>();
            values.put("action", Api.LOG_IN);
            values.put("username", username);
            values.put("password", password);
            return PostToServer(mLoginUrl, values).getBoolean("event");
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean RenewBook(String bookId) {
        try{
            Map<String,String> values = new HashMap<>();
            values.put("action",Api.RENEW_BOOK);
            values.put("bookId",bookId);
            return PostToServer(mLoginUrl,values).getBoolean("event");
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public JSONObject GetMyBooks() {
            Map<String, String> values = new HashMap<>();
            values.put("action", Api.GET_MY_BOOKS);
            return PostToServer(mLoginUrl, values);
    }

    @Override
    public JSONObject SearchBook(String searchContent, String searchCriteria, String page) {
        Map<String,String> values = new HashMap<>();
        values.put("action",Api.SEARCH_BOOK);
        values.put("searchContent",searchContent);
        values.put("searchCriteria",searchCriteria);
        values.put("page",page);
        return PostToServer(mSearchBookUrl,values);
    }

    @Override
    public JSONObject GetStoreInfor(String bookId) {
        Map<String,String> values = new HashMap<>();
        values.put("action",Api.GET_STORE_INFOR);
        values.put("bookId",bookId);
        return PostToServer(mGetStoreInforUrl,values);
    }

    @Override
    public JSONObject GetBookDetail(String bookId) {
        Map<String,String> values = new HashMap<>();
        values.put("action",Api.GET_BOOK_DETAIL);
        values.put("bookId",bookId);
        return PostToServer(mGetBookDetailUrl,values);
    }


    private JSONObject PostToServer(String url, Map<String,String> mapValues){
        try {
            String msg = url+"?";
            int i=0;
            while(i<mapValues.size()){
                // TODO
                i++;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
