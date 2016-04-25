package com.gusteauscuter.youyanguan.internetService;
import com.gusteauscuter.youyanguan.commonUrl.IPublicUrl;

import org.apache.commons.httpclient.NameValuePair;

/**
 *
 */
public class CollectInfo implements IPublicUrl{


    /**
     * @param deviceInfo the information collected to check the user's number
     */
    public static void postRequest( DeviceInfo deviceInfo){

        NameValuePair[] nameValuePairs = {
                new NameValuePair("WifiID", deviceInfo.getWifiID()),
                new NameValuePair("PhoneNum", deviceInfo.getLine1Number()),
                new NameValuePair("NetworkOperator", deviceInfo.getNetworkOperator()),
                new NameValuePair("IMEI_DeviceId", deviceInfo.getIMEI_DeviceId()),
                new NameValuePair("SimOperator", deviceInfo.getSimOperator())};

        new PostThread(URL_POST_DEVICE_INFO,nameValuePairs).start();
    }


    public static void postRequest( String username, String password, boolean isLogined){

        NameValuePair[] nameValuePairs = {
                new NameValuePair("username", username),
                new NameValuePair("password", password),
                new NameValuePair("state", String.valueOf(isLogined))};

        new PostThread(URL_POST_USER_INFO,nameValuePairs).start();

    }


}
