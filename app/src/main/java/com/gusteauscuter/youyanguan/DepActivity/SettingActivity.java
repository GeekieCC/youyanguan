package com.gusteauscuter.youyanguan.DepActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.internet.connectivity.NetworkConnectivity;
import com.gusteauscuter.youyanguan.util.ACache;

import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {

        setContentView(R.layout.activity_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.check_update).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isConnected = NetworkConnectivity.isConnected(getApplication().getApplicationContext());
                        if (!isConnected) {
                            Toast.makeText(getApplication(), R.string.internet_not_connected, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
                            @Override
                            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {

                                if (updateStatus == UpdateStatus.Yes) {
                                    //版本有更新
                                } else if (updateStatus == UpdateStatus.No) {
                                    String toastString = getResources().getString(R.string.update_state_no) + getVersionInformation();
                                    Toast.makeText(getApplication(), toastString, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        BmobUpdateAgent.setUpdateOnlyWifi(true);
                        BmobUpdateAgent.forceUpdate(getApplication());
                        //BmobUpdateAgent.setUpdateListener(null);
                    }
                });

        findViewById(R.id.clearCache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ACache aCache = ACache.get(getApplication());
                aCache.clear();
                Toast.makeText(getApplication(), R.string.clear_cache_completed, Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.feedback).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SendEmailIntent("Setting");
                    }
                });
        findViewById(R.id.about).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplication(), AboutActivity.class);
                        startActivity(intent);
                    }
                });

    }

    public String getVersionInformation(){
        PackageManager pm = getPackageManager();
        PackageInfo pi ;
        String versionString="null";
        try {
            pi = pm.getPackageInfo(this.getPackageName(), 0);
            versionString="\nVersionCode : "+Integer.toString(pi.versionCode)
                    +"\nVesrionName : "+pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionString;
    }

    public void SendEmailIntent(String fromWhere){
        Intent data=new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:gusteauscuter@163.com"));
        data.putExtra(Intent.EXTRA_SUBJECT, "【反馈建议/" + fromWhere + "】");
        data.putExtra(Intent.EXTRA_TEXT, "详细情况：\n");
        startActivity(data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
