package com.gusteauscuter.youyanguan.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ziqian on 2016/4/25.
 */
public class SharedPreferencesUtil {
    public static final String SHARED_PREFERENCE_NAME="data";
    public static final String USERNAME="USERNAME";
    public static final String PASSWORD="PASSWORD";
    public static final String ISLOGINED="ISLOGINED";
    protected SharedPreferences mShareData;
    protected SharedPreferences.Editor mShareDataEditor;
    protected Context mContext;

    public SharedPreferencesUtil(Context context){
        mContext=context;
        mShareData = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, 0);
        mShareDataEditor=mContext.getSharedPreferences(SHARED_PREFERENCE_NAME,0).edit();
    }

    public String getUSERNAME() {
        return mShareData.getString(USERNAME,"");
    }

    public String getPASSWORD() {
        return mShareData.getString(PASSWORD,"");
    }

    public boolean getISLOGINED() {
        return mShareData.getBoolean(ISLOGINED, false);
    }

    public void setUsername(String username) {
        mShareDataEditor.putString(USERNAME,username).apply();
    }

    public void setPassword(String password) {
        mShareDataEditor.putString(PASSWORD,password).apply();
    }

    public void setIsLogined(boolean isLogined) {
        mShareDataEditor.putBoolean(ISLOGINED, isLogined).apply();
    }

    public void saveUserLoginData(String username, String password){
        setUsername(username);
        setPassword(password);
    }

    public void saveUserLoginData(String username, String password, boolean isLogined){
        saveUserLoginData(username,password);
        setIsLogined(isLogined);
    }

}
