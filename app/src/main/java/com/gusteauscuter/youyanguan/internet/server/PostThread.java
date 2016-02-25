package com.gusteauscuter.youyanguan.internet.server;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Created by Z on 2016/1/30 0030.
 */
public class PostThread extends Thread{

    NameValuePair[] nameValuePairs;
    private String url;

    public PostThread(String url,NameValuePair[] nameValuePairs){
        this.url=url;
        this.nameValuePairs=nameValuePairs;
    }

    @Override
    public void run() {
        super.run();
        HttpClient httpClient = new HttpClient();

        PostMethod postMethod = new PostMethod(url);
        postMethod.setRequestBody(nameValuePairs);
//        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(NetworkConnectivity.CONNECT_TIME_OUT);
//        httpClient.getHttpConnectionManager().getParams().setSoTimeout(NetworkConnectivity.READ_TIME_OUT);
        try {
            httpClient.executeMethod(postMethod);
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
//            throw new ConnectTimeoutException();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
//            throw new SocketTimeoutException();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            postMethod.releaseConnection();
        }
    }



}
