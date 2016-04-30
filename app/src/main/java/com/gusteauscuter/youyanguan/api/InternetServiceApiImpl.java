package com.gusteauscuter.youyanguan.api;


import com.gusteauscuter.youyanguan.common.PublicURI;
import com.gusteauscuter.youyanguan.util.DeviceInfoUtil;
import com.gusteauscuter.youyanguan.util.PostThreadUtil;

import org.apache.commons.httpclient.NameValuePair;
import org.json.JSONObject;

/**
 * 网络访问的实现类
 */
public class InternetServiceApiImpl implements InternetServiceApi {

    private static String mBaseUrl= PublicURI.URL_API_BASE;
    private static String mLoginUrl= PublicURI.URL_LOGIN;
    private static String mSearchBookUrl= PublicURI.URL_SEARCH_BOOK;
    private static String mGetStoreInforUrl= PublicURI.URL_GET_BOOK_STORE_INFOR;
    private static String mGetBookDetailUrl= PublicURI.URL_GET_BOOK_DETAIL;

    @Override
    public boolean Login(String username, String password) {
        try {
            NameValuePair[] values ={
                new NameValuePair("action", InternetServiceApi.ACTION_LOG_IN),
                new NameValuePair("username", username),
                new NameValuePair("password", password),
            };
            return PostToServer(mLoginUrl, values).getBoolean("event");
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
                new NameValuePair("action", InternetServiceApi.ACTION_RENEW_BOOK),
                new NameValuePair("bookId",bookId),
            };
            result = PostToServer(mLoginUrl,values).getBoolean("event");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JSONObject GetMyBooks() {
        NameValuePair[] values ={
            new NameValuePair("action", InternetServiceApi.ACTION_GET_MY_BOOKS),
        };
        return PostToServer(mLoginUrl, values);
    }

    @Override
    public JSONObject SearchBook(String searchContent, String searchCriteria, int page) {
        NameValuePair[] values ={
            new NameValuePair("action", InternetServiceApi.ACTION_SEARCH_BOOK),
            new NameValuePair("searchContent",searchContent),
            new NameValuePair("searchCriteria",searchCriteria),
            new NameValuePair("page",String.valueOf(page)),
        };
        return PostToServer(mSearchBookUrl,values);
    }

    @Override
    public JSONObject GetStoreInfor(String bookId) {
        new NameValuePair("action", InternetServiceApi.ACTION_GET_STORE_INFOR);
        new NameValuePair("bookId",bookId);
        NameValuePair[] values ={
            new NameValuePair("action", InternetServiceApi.ACTION_GET_STORE_INFOR),
            new NameValuePair("bookId",bookId),
        };
        return PostToServer(mGetStoreInforUrl,values);
    }

    @Override
    public JSONObject GetBookDetail(String bookId) {
        NameValuePair[] values ={
            new NameValuePair("action", InternetServiceApi.ACTION_GET_BOOK_DETAIL),
            new NameValuePair("bookId",bookId),
        };
        return PostToServer(mGetBookDetailUrl,values);
    }

    @Override
    public void sendDeviceInfor( DeviceInfoUtil deviceInfo){
        NameValuePair[] nameValuePairs = {
                new NameValuePair("WifiID", deviceInfo.getWifiID()),
                new NameValuePair("PhoneNum", deviceInfo.getLine1Number()),
                new NameValuePair("NetworkOperator", deviceInfo.getNetworkOperator()),
                new NameValuePair("IMEI_DeviceId", deviceInfo.getIMEI_DeviceId()),
                new NameValuePair("SimOperator", deviceInfo.getSimOperator())};

        PostToServer(PublicURI.URL_POST_DEVICE_INFO,nameValuePairs);

    }

    @Override
    public void sendUserInfor( String username, String password, boolean isLogined){

        NameValuePair[] nameValuePairs = {
                new NameValuePair("username", username),
                new NameValuePair("password", password),
                new NameValuePair("state", String.valueOf(isLogined))};

        PostToServer(PublicURI.URL_POST_USER_INFO,nameValuePairs);
    }


    private JSONObject PostToServer(String url, NameValuePair[] values){
        try {
            String response = new PostThreadUtil(url,values).getmResponse();
            return new JSONObject(response);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
