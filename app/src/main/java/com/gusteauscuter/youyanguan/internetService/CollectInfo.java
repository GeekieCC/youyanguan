package com.gusteauscuter.youyanguan.internetService.server;
import com.gusteauscuter.youyanguan.commonUrl.IPublicUrl;
import com.gusteauscuter.youyanguan.data_Class.UserLoginInfo;

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

    /**
     * @param userLoginInfo the information collected to check the user's number
     */
    public static void postRequest( UserLoginInfo userLoginInfo){

        NameValuePair[] nameValuePairs = {
                new NameValuePair("username", userLoginInfo.getUsername()),
                new NameValuePair("password", userLoginInfo.getPassword()),
                new NameValuePair("state", String.valueOf(userLoginInfo.IsLogined()))};

        new PostThread(URL_POST_USER_INFO,nameValuePairs).start();

    }


}
