package com.gusteauscuter.youyanguan.api;


import android.support.annotation.Nullable;

import com.gusteauscuter.youyanguan.common.PublicString;
import com.gusteauscuter.youyanguan.util.PostUtil;

import org.apache.commons.httpclient.NameValuePair;
import org.json.JSONObject;

/**
 * 网络访问的实现类
 */
public class InternetServiceApiImpl implements InternetServiceApi {
    private final static String ACTION_LOG_IN = "login";
    private final static String ACTION_RENEW_BOOK ="renew";
    private final static String ACTION_GET_MY_BOOKS ="borrowed";
    private final static String ACTION_SEARCH_BOOK ="search";
    private final static String ACTION_GET_STORE_INFO ="storeInfo";
    private final static String ACTION_GET_BOOK_DETAIL ="bookDetail";

    private final static String ACTION_TO_COLLECT = "toCollect";
    private final static String ACTION_COLLECTED = "collected";
    private final static String ACTION_USER_INFO = "userInfo";

    private final static String mBaseUrl= PublicString.URL_API_BASE;
    private final static String mLoginUrl= PublicString.URL_LOGIN;
    private final static String mSearchBookUrl= PublicString.URL_SEARCH_BOOK;
    private final static String mGetStoreInforUrl= PublicString.URL_GET_BOOK_STORE_INFOR;
    private final static String mGetBookDetailUrl= PublicString.URL_GET_BOOK_DETAIL;

    @Override
    public boolean Login(String username, String password) {
        try {
            NameValuePair[] values ={
                new NameValuePair("action", ACTION_LOG_IN),
                new NameValuePair("username", username),
                new NameValuePair("passwd", password),
            };
            JSONObject resultJson = PostToServer(mLoginUrl, values);
            assert resultJson != null;
            return resultJson.getBoolean("event");

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean RenewBook(String bookId) {
        boolean result = false;
        try{
            NameValuePair[] values ={
                new NameValuePair("action", ACTION_RENEW_BOOK),
                new NameValuePair("bookId",bookId),
            };
            // TODO to check the return value
            JSONObject resultJson = PostToServer(mLoginUrl, values);
            assert resultJson != null;
            result = resultJson.getBoolean("event");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JSONObject getMyBookBorrowed() {
        NameValuePair[] values ={
            new NameValuePair("action", ACTION_GET_MY_BOOKS),
        };
        return PostToServer(mLoginUrl, values);
    }

    @Override
    public JSONObject SearchBook(String searchContent, String searchCriteria, int page) {
        NameValuePair[] values ={
            new NameValuePair("action", ACTION_SEARCH_BOOK),
            new NameValuePair("searchContent",searchContent),
            new NameValuePair("searchCriteria",searchCriteria),
            new NameValuePair("page",String.valueOf(page)),
        };
        return PostToServer(mSearchBookUrl,values);
    }

    @Override
    public JSONObject GetStoreInfor(String bookId) {
        NameValuePair[] values ={
            new NameValuePair("action", ACTION_GET_STORE_INFO),
            new NameValuePair("bookId",bookId),
        };
        return PostToServer(mGetStoreInforUrl,values);
    }

    @Override
    public JSONObject GetBookDetail(String bookId) {
        NameValuePair[] values ={
            new NameValuePair("action", ACTION_GET_BOOK_DETAIL),
            new NameValuePair("bookId",bookId),
        };
        return PostToServer(mGetBookDetailUrl,values);
    }


    private @Nullable JSONObject PostToServer(String url, NameValuePair[] values){
        try {
            String response = new PostUtil(url,values).getResponse();
            JSONObject responseJson = new JSONObject();
            if(response.equals("true"))
                responseJson.put("event", true);
            else if(response.equals("false"))
                responseJson.put("event",false);
            else {
                if(response.startsWith("["))
                    response="{\"bookList\":"+response+"}";
                responseJson = new JSONObject(response);
            }
            return responseJson;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
