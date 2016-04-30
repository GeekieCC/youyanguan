package com.gusteauscuter.youyanguan.api;

import com.gusteauscuter.youyanguan.util.DeviceInfoUtil;

import org.json.JSONObject;

/**
 * Created by Z on 2016/4/22 0022.
 */
public interface InternetServiceApi {
    String ACTION_LOG_IN = "login";
    String ACTION_RENEW_BOOK ="renewBook";
    String ACTION_GET_MY_BOOKS ="getMyBooks";
    String ACTION_SEARCH_BOOK ="searchBook";
    String ACTION_GET_STORE_INFOR ="getStoreInfor";
    String ACTION_GET_BOOK_DETAIL ="getBookDetail";

    boolean Login(String username, String password);

    boolean RenewBook(String bookId);

    JSONObject GetMyBooks();

    JSONObject SearchBook(String searchContent, String searchCriteria, int page);

    JSONObject GetStoreInfor(String bookId);

    JSONObject GetBookDetail(String bookId);

    void sendDeviceInfor( DeviceInfoUtil deviceInfo);

    void sendUserInfor( String username, String password, boolean isLogined);
}
