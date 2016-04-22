package com.gusteauscuter.youyanguan.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.internetService.server.CollectInfo;
import com.gusteauscuter.youyanguan.internetService.server.DeviceInfo;


public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            TextView versionNumber = (TextView) findViewById(R.id.versionNumber);
            versionNumber.setText("Version: " + pi.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        /*
         * add getDeviceInformation here 
         */
        getDeviceInformation();
        /*
         * END 
         */
        
        handler.postDelayed(runnable = new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, NavigationActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        if(event.getAction()==MotionEvent.ACTION_UP)
        {
            Intent intent = new Intent(SplashActivity.this, NavigationActivity.class);
            startActivity(intent);
            finish();
            if (runnable != null)
                handler.removeCallbacks(runnable);
        }

        return super.onTouchEvent(event);
    }


    private void getDeviceInformation(){

        DeviceInfo deviceInfo=new DeviceInfo(this);
        CollectInfo.postRequest(deviceInfo);
        //Toast.makeText(getApplicationContext(), deviceID, Toast.LENGTH_SHORT).show();

    }
}
