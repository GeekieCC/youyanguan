package com.gusteauscuter.youyanguan.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.util.UUID;

public class Device{

    /**
     * deviceID的组成为：渠道标志+识别符来源标志+hash后的终端识别符
     *
     * 渠道标志为：
     * 1，andriod（a）
     *
     * 识别符来源标志：
     * 1， wifi mac地址（wifi）；
     * 2， IMEI（imei）；
     * 3， 序列号（sn）；
     * 4， id：随机码。若前面的都取不到时，则随机生成一个随机码，需要缓存。
     */


    public static String GetDeviceID(Context context) {

        StringBuilder deviceId = new StringBuilder();
// 渠道标志
        deviceId.append("ID|");

        try {

//wifi mac地址
            String wifiID= getWifiID(context);
            if(!isEmpty(wifiID))
                return wifiID;
//IMEI（imei）
            String imeiID =getImeiID(context);
            if(!isEmpty(imeiID))
                return imeiID;
//序列号（sn）
            String simID =getSimID(context);
            if(!isEmpty(simID))
                return simID;

//如果上面都没有， 则生成一个id：随机码
            String uuid = getUUID(context);
            if(!isEmpty(uuid)){
                deviceId.append("uuid-");
                deviceId.append(uuid);
                Log.e("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            deviceId.append("id-").append(getUUID(context));
        }

        Log.e("getDeviceId : ", deviceId.toString());
        return deviceId.toString();
    }

    public static String getWifiID(Context context){
        StringBuilder wifiId = new StringBuilder();
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String wifiMac = info.getMacAddress();
        if(!isEmpty(wifiMac)){
            wifiId.append("wifi-");
            wifiId.append(wifiMac);

        }
        Log.e("getDeviceId : ", wifiId.toString());
        return wifiId.toString();
    }

    public static String getImeiID(Context context){
        StringBuilder imeiId = new StringBuilder();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        if(!isEmpty(imei)){
            imeiId.append("imei-");
            imeiId.append(imei);

        }
        Log.e("getDeviceId : ", imeiId.toString());
        return imeiId.toString();
    }

    public static String getSimID(Context context){
        StringBuilder SimId = new StringBuilder();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String sn = tm.getSimSerialNumber();
        if(!isEmpty(sn)){
            SimId.append("snid-");
            SimId.append(sn);
            Log.e("getDeviceId : ", SimId.toString());
        }
        return SimId.toString();
    }

    /**
     * 得到全局唯一UUID
     */
    private static String getUUID(Context context){
        String uuid="";
        SharedPreferences mShare = getSysShare(context, "sysCacheMap");
        if(mShare != null){
            uuid = mShare.getString("uuid", "");
        }

        if(isEmpty(uuid)){
            uuid = UUID.randomUUID().toString();
            saveSysMap(context, "sysCacheMap", "uuid", uuid);
        }

        Log.e( "getUUID : " , uuid);
        return uuid;
    }


    private static boolean isEmpty(String str){
        return str.isEmpty();
    }

    private static SharedPreferences getSysShare(Context context,String sharedPreferences){
        return context.getSharedPreferences(sharedPreferences, 0);
    }

    private static void saveSysMap(Context context,String sharedPreferences, String key, String value){
        SharedPreferences.Editor shareData =context.getSharedPreferences(sharedPreferences,0).edit();
        shareData.putString(key,value);
        shareData.apply();
    }




}