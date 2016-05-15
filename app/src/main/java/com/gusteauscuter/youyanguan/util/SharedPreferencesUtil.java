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
    public static final String COLLECT_ACTION = "collectAction";
    public static final String COLLECT_RESULT = "collectResult";
    public static final String COLLECT_POSITION ="collectPosition";
    protected SharedPreferences mShareData;
    protected SharedPreferences.Editor mShareDataEditor;
    protected Context mContext;

    public SharedPreferencesUtil(Context context){
        mContext=context;
        mShareData = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, 0);
        mShareDataEditor=mContext.getSharedPreferences(SHARED_PREFERENCE_NAME,0).edit();
    }



    public void setCollectAction(boolean action) {
        mShareDataEditor.putBoolean(COLLECT_ACTION,action).apply();
    }

    public void setCollectResult(boolean result) {
        mShareDataEditor.putBoolean(COLLECT_RESULT,result).apply();
    }

    public void setCollectPosition(int position) {
        mShareDataEditor.putInt(COLLECT_POSITION, position).apply();
    }

    public boolean getCollectAction() {
        // 读取之后自动置否
        boolean result = mShareData.getBoolean(COLLECT_ACTION, false);
        setCollectAction(false);
        return result;
    }

    public boolean getCollectResult() {
        return mShareData.getBoolean(COLLECT_RESULT,false);
    }

    public  int getCollectPosition(){
        return mShareData.getInt(COLLECT_POSITION,0);
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
