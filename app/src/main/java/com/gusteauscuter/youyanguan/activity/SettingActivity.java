package com.gusteauscuter.youyanguan.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.util.NetworkConnectUtil;
import com.gusteauscuter.youyanguan.util.ACache;
import com.gusteauscuter.youyanguan.internetService.server.UpdateManager;

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
                        boolean isConnected = NetworkConnectUtil.isConnected(getApplication().getApplicationContext());
                        if (!isConnected) {
                            Toast.makeText(getApplication(), R.string.internet_not_connected, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new UpdateManager(SettingActivity.this).checkUpdateInfo(false);
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
