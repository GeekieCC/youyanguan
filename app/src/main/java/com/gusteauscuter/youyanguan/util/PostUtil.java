package com.gusteauscuter.youyanguan.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
/**
 * post方式提交数据
 */
public class PostUtil {
    public static final int CONNECT_TIME_OUT = 60000;
    public static final int READ_TIME_OUT = 60000;

    protected NameValuePair[] mNameValuePairs;
    protected String mUrl;
    protected String mResponse="";

    // single instance
    static HttpClient mHttpClient;
    public static HttpClient getHttpClient(){
        if(mHttpClient==null) {
            mHttpClient = new HttpClient();
            mHttpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIME_OUT);
            mHttpClient.getHttpConnectionManager().getParams().setSoTimeout(READ_TIME_OUT);
        }
        return mHttpClient;
    }
    /**
     * construct method to initial parameters
     * @param url to post data to
     * @param nameValuePairs contains data to post
     */
    public PostUtil(String url, NameValuePair[] nameValuePairs){
        mUrl =url;
        mNameValuePairs =nameValuePairs;
    }

    public String getResponse() {
        run();
        return mResponse;
    }

    public void run() {
        HttpClient httpClient = getHttpClient();

        PostMethod postMethod = new PostMethod(mUrl);
        postMethod.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        postMethod.setRequestBody(mNameValuePairs);

        try {
            httpClient.executeMethod(postMethod);
            mResponse = postMethod.getResponseBodyAsString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            postMethod.releaseConnection();
        }
    }
}
