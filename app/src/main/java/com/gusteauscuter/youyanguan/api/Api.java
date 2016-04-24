package com.gusteauscuter.youyanguan.api;

import org.json.JSONObject;

/**
 * Created by Z on 2016/4/22 0022.
 */
public interface Api {
    String LOG_IN = "login";
    String RENEW_BOOK="renewBook";
    String GET_MY_BOOKS="getMyBooks";
    String SEARCH_BOOK="searchBook";
    String GET_STORE_INFOR="getStoreInfor";
    String GET_BOOK_DETAIL="getBookDetail";

    boolean Login(String username, String password);

    boolean RenewBook(String bookId);

    JSONObject GetMyBooks();

    JSONObject SearchBook(String searchContent, String searchCriteria, String page);

    JSONObject GetStoreInfor(String bookId);

    JSONObject GetBookDetail(String bookId);
}
