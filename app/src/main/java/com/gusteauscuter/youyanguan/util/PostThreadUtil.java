package com.gusteauscuter.youyanguan.util;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * post方式提交数据
 */
public class PostThreadUtil extends Thread{
    public static final int CONNECT_TIME_OUT = 6000;
    public static final int READ_TIME_OUT = 6000;

    protected NameValuePair[] mNameValuePairs;
    protected String mUrl;
    protected String mResponse="";
    /**
     * construct method to initial parameters
     * @param url to post data to
     * @param nameValuePairs contains data to post
     */
    public PostThreadUtil(String url, NameValuePair[] nameValuePairs){
        mUrl =url;
        mNameValuePairs =nameValuePairs;
    }

    public String getmResponse() {
        run();
        return mResponse;
    }

    @Override
    public void run() {
        super.run();
        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIME_OUT);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(READ_TIME_OUT);

        PostMethod postMethod = new PostMethod(mUrl);
        postMethod.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=gbk");
        postMethod.setRequestBody(mNameValuePairs);

        try {
            httpClient.executeMethod(postMethod);
            mResponse = postMethod.getResponseBodyAsString();
            //String response = new String(postMethod.getResponseBodyAsString().getBytes("ISO-8859-1"));
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();    //            throw new ConnectTimeoutException();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();    //            throw new SocketTimeoutException();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            postMethod.releaseConnection();
        }
    }
}
