package com.gusteauscuter.youyanguan.util;

/**
 * Created by Z on 2016/2/25 0025.
 */

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.common.PublicURI;
import com.gusteauscuter.youyanguan.R;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateManagerUtil {

    private Context mContext;
    private Dialog downloadDialog;
    private String mLocalVersion;
    private String mServerVersion;
    private String mNewFeatures;

    //提示语
    private static final String updateMsg = "update: v";
    private static final String noUpdateInfor = "已是最新版本！";

    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;

    private static final String mApkDownloadUrl = PublicURI.URL_APK_DOWNLOAD;
    private static final String mApkVersionUrl = PublicURI.URL_APK_VERSION;
    private static final String mApkSavePath = PublicURI.LOCAL_PATH;
    private static final String mApkSaveFileName = PublicURI.PATH_APK;

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;
    private int progress;
    private boolean interceptFlag = false;

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:
                    installApk();
                    break;
                default:
                    break;
            }
        };
    };

    public UpdateManagerUtil(Context context) {
        this.mContext = context;
    }


    /**
     * 外部接口让主Activity调用
     * @param isHome  是否是从setting中进行更新的，如果是则强制显示信息提示，
     *                      否则即为打开app时的更新，若无更新则此时不应该提示状态
     */
    public void checkUpdateInfo(boolean isHome) {
        if(!NetworkConnectUtil.isConnected(mContext))
            return;
        CheckUpdateAsy checkUpdateAsy =new CheckUpdateAsy(isHome);
        checkUpdateAsy.execute();
    }

    private class CheckUpdateAsy extends AsyncTask<Void, Void, Integer>{

        private String TAG = "checkUpdateAsy";
        private int HAS_UPDATE = 0;
        private int NO_UPDATE = 1;
        private int NO_ALERT = 2;
        private boolean isHome =true;

        public CheckUpdateAsy(boolean isForceUpdate){
            this.isHome =isForceUpdate;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Log.i(TAG, "---check update---");

            try{
                getLocalVersion();
                getServerVersion();

            }catch (Exception e){
                e.printStackTrace();
            }
            if(!mLocalVersion.equals(mServerVersion))
                return HAS_UPDATE;
            if(isHome)
                return NO_ALERT;
            return NO_UPDATE;
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.i(TAG, "---check update postExecute---");
            super.onPostExecute(result);

            if(result==HAS_UPDATE) {
                showNoticeDialog();
            }
            else if(result==NO_UPDATE)
                Toast.makeText(mContext,noUpdateInfor,Toast.LENGTH_SHORT).show();
        }

    }

    private void showNoticeDialog(){
        Builder builder = new Builder(mContext);
        builder.setTitle(updateMsg + mServerVersion);
        builder.setMessage(mNewFeatures);
        builder.setPositiveButton("下载", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        });
        builder.setNegativeButton("忽略", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog noticeDialog;
        noticeDialog = builder.create();
        noticeDialog.show();
    }

    private void showDownloadDialog(){
        Builder builder = new Builder(mContext);
        builder.setTitle(updateMsg + mServerVersion);

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress_update, null);
        mProgress = (ProgressBar)v.findViewById(R.id.progressUpdate);

        builder.setView(v);
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        downloadDialog = builder.create();
        downloadDialog.show();

        downloadApk();
    }

    private Runnable downloadApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(mApkDownloadUrl);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                File file = new File(mApkSavePath);
                if(!file.exists()){
                    file.mkdir();
                }
                File ApkFile = new File(mApkSaveFileName);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do{
                    int numread = is.read(buf);
                    count += numread;
                    progress =(int)(((float)count / length) * 100);
                    //更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if(numread <= 0){
                        //下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf,0,numread);
                }while(!interceptFlag);//点击取消就停止下载.

                fos.close();
                is.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };


    private void getLocalVersion() throws Exception {
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(mContext.getPackageName(),0);
        mLocalVersion= packInfo.versionName;
    }

    public void getServerVersion() throws Exception {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(mApkVersionUrl).openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode()== HttpURLConnection.HTTP_OK){
                InputStream inputStream = conn.getInputStream();// 从服务器获得一个输入流
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(inputStream, "utf-8");
                int type = parser.getEventType();
                while (type != XmlPullParser.END_DOCUMENT) {
                    switch (type) {
                        case XmlPullParser.START_TAG:
                            if ("version".equals(parser.getName())) {
                                mServerVersion = parser.nextText();
                                break;
                            }
                            if ("description".equals(parser.getName())) {
                                mNewFeatures = parser.nextText();
                                break;
                            }
                    }
                    type = parser.next();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void downloadApk(){
        new Thread(downloadApkRunnable).start();
    }

    private void installApk(){
        File apkfile = new File(mApkSaveFileName);
        if (!apkfile.exists())
            return;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
        downloadDialog.dismiss();
    }
}