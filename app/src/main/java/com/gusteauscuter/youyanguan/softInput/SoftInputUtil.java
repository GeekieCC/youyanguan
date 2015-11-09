package com.gusteauscuter.youyanguan.softInput;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Strang on 2015/11/8.
 */
public class SoftInputUtil {

    /**
     * 隐藏软键盘
     * @param activity
     * @param view
     * @return
     */

    public static boolean hideSoftInput(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }


}
