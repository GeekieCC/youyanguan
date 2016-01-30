package com.gusteauscuter.youyanguan.util;

import android.content.Context;
import android.telephony.TelephonyManager;


public class PhoneInfo {

    private TelephonyManager telephonyManager;
    /**
     * 国际移动用户识别码
     */
    private String IMSI;
    private Context cxt;
    public PhoneInfo(Context context) {
        cxt=context;
        telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * 获取电话号码
     */
    public String getNativePhoneNumber() {
        String NativePhoneNumber=null;
        NativePhoneNumber=telephonyManager.getLine1Number();
        return NativePhoneNumber;
    }

    /**
     * 获取手机服务商信息
     */
    public String getProvidersName() {
        String ProvidersName = "N/A";
        try{
            IMSI = telephonyManager.getSubscriberId();
            // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
            System.out.println(IMSI);
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                ProvidersName = "中国移动";
            } else if (IMSI.startsWith("46001")) {
                ProvidersName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                ProvidersName = "中国电信";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ProvidersName;
    }

    public String  getPhoneInfo(){
        TelephonyManager tm = (TelephonyManager)cxt.getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
        stringBuilder.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
        stringBuilder.append("\nLine1Number = " + tm.getLine1Number());
        stringBuilder.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
        stringBuilder.append("\nNetworkOperator = " + tm.getNetworkOperator());
        stringBuilder.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
        stringBuilder.append("\nNetworkType = " + tm.getNetworkType());
        stringBuilder.append("\nPhoneType = " + tm.getPhoneType());
        stringBuilder.append("\nSimCountryIso = " + tm.getSimCountryIso());
        stringBuilder.append("\nSimOperator = " + tm.getSimOperator());
        stringBuilder.append("\nSimOperatorName = " + tm.getSimOperatorName());
        stringBuilder.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
        stringBuilder.append("\nSimState = " + tm.getSimState());
        stringBuilder.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
        stringBuilder.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
        return  stringBuilder.toString();
    }
}