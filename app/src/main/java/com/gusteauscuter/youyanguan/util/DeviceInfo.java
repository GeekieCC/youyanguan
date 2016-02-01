package com.gusteauscuter.youyanguan.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

public class DeviceInfo {

    /**
     *
     * IMEI_DeviceId            设备标志符
     * DeviceSoftwareVersion    软件版本
     * Line1Number              电话号码
     * NetworkCountryIso        国家标志
     * NetworkOperator          网络编号
     * NetworkOperatorName      网络运营商
     * NetworkType              网络类型
     * PhoneType                手机类型
     * SimCountryIso            SIM卡国家标志
     * SimOperator              SIM卡网络编号
     * SimOperatorName          SIM卡网络运营商
     * SimSerialNumber          SIM卡SN
     * SimState                 SIM卡状态
     * IMSI_SubscriberId
     * VoiceMailNumber
     * ProvidersName
     * WifiID
     *
     */

    private String IMEI_DeviceId;
    private String DeviceSoftwareVersion;
    private String Line1Number ;
    private String NetworkCountryIso ;
    private String NetworkOperator ;
    private String NetworkOperatorName ;
    private String NetworkType ;
    private String PhoneType ;
    private String SimCountryIso ;
    private String SimOperator ;
    private String SimOperatorName ;
    private String SimSerialNumber ;
    private String SimState ;
    private String IMSI_SubscriberId;
    private String VoiceMailNumber ;
    private String WifiID;

    public DeviceInfo(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        IMEI_DeviceId = telephonyManager.getDeviceId();
        DeviceSoftwareVersion = telephonyManager.getDeviceSoftwareVersion();
        Line1Number = telephonyManager.getLine1Number();
        NetworkCountryIso = telephonyManager.getNetworkCountryIso();
        NetworkOperator = telephonyManager.getNetworkOperator();
        NetworkOperatorName = telephonyManager.getNetworkOperatorName();
        NetworkType = String.valueOf(telephonyManager.getNetworkType());
        PhoneType = String.valueOf(telephonyManager.getPhoneType());;
        SimCountryIso = telephonyManager.getSimCountryIso();
        SimOperator = telephonyManager.getSimOperator();
        SimOperatorName = telephonyManager.getSimOperatorName();
        SimSerialNumber = telephonyManager.getSimSerialNumber();
        SimState = String.valueOf(telephonyManager.getSimState());
        IMSI_SubscriberId = telephonyManager.getSubscriberId();
        VoiceMailNumber = telephonyManager.getVoiceMailNumber();

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        WifiID = info.getMacAddress();
    }



    /**
     * 获取手机服务商信息
     */
    public String getProvidersName() {
        String ProvidersName = "N/A";
        // IMSI_SubscriberId号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        System.out.println(IMSI_SubscriberId);
        if (IMSI_SubscriberId.startsWith("46000") || IMSI_SubscriberId.startsWith("46002")) {
            ProvidersName = "中国移动";
        } else if (IMSI_SubscriberId.startsWith("46001")) {
            ProvidersName = "中国联通";
        } else if (IMSI_SubscriberId.startsWith("46003")) {
            ProvidersName = "中国电信";
        }
        return ProvidersName;
    }

    public String getIMEI_DeviceId() {
        return IMEI_DeviceId;
    }

    public String getDeviceSoftwareVersion() {
        return DeviceSoftwareVersion;
    }

    public String getLine1Number() {
        return Line1Number;
    }

    public String getNetworkCountryIso() {
        return NetworkCountryIso;
    }

    public String getNetworkOperator() {
        return NetworkOperator;
    }

    public String getNetworkOperatorName() {
        return NetworkOperatorName;
    }

    public String getNetworkType() {
        return NetworkType;
    }

    public String getPhoneType() {
        return PhoneType;
    }

    public String getSimCountryIso() {
        return SimCountryIso;
    }

    public String getSimOperatorName() {
        return SimOperatorName;
    }

    public String getSimSerialNumber() {
        return SimSerialNumber;
    }

    public String getSimState() {
        return SimState;
    }

    public String getVoiceMailNumber() {
        return VoiceMailNumber;
    }

    public String getWifiID() {
        return WifiID;
    }

    public String getSimOperator() {
        return SimOperator;
    }
}