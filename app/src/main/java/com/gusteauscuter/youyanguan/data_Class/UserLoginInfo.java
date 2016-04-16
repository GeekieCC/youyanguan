package com.gusteauscuter.youyanguan.data_Class;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;

/**用户登陆信息和状态类
 * Created by Ziqian on 2015/9/3.
 */
public class UserLoginInfo implements Serializable {


    private String username;
    private String password;
    private boolean isLogined;

    public UserLoginInfo(){
        this("","");
    }

    public UserLoginInfo(String username, String password){
        this(username, password, false);
    };

    public UserLoginInfo(String username, String password, boolean isLogined){
        this.username=username;
        this.password=password;
        this.isLogined=isLogined;
    };

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public boolean IsLogined(){
        return isLogined;
    }

    public static void SaveData(Context context,String username, String pass){
        SharedPreferences.Editor shareData =context.getSharedPreferences("data",0).edit();
        shareData.putString("USERNAME",username);
        shareData.putString("PASSWORD", pass);
        shareData.putBoolean("ISLOGINED", true);
        shareData.apply();
    }
}

