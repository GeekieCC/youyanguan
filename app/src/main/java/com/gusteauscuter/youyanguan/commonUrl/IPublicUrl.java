package com.gusteauscuter.youyanguan.commonUrl;

import android.os.Environment;

/**
 * Created by Z on 2015/11/25.
 */
public interface IPublicUrl {

    /**
     * 系统文件路径
     */
    String sdCard= Environment.getExternalStorageDirectory().getPath();
    String savePath = sdCard+"/libSCUT/";

    // 下载包安装路径
    String saveApkFileName = savePath + "wanjuanwu.apk";
    // 界面自定义文件保存路径
    String stringSearchBackgroundName= savePath +"mSearchBackground.jpg";
    String stringBackgroundName= savePath +"mDrawerBackground.jpg";
    String stringHeaderName= savePath +"mHeaderImage.jpg";
    // 分享图片保存路径
    String stringSharedBooksBorrowedName= savePath +"mSharedBooksBorrowed.jpg";
    String stringSharedBookDetailName= savePath +"mSharedBookDetail.jpg";


    /**
     *  服务器根路径
     */
    String ServerUrl = "http://geekie.cc";
    // 安装包及信息url
    String apkDownloadUrl = ServerUrl+"/apk/wanjuanwu.apk";
    String apkVersionUrl = ServerUrl+"/apk/Version.xml";
    // 信息收集url
    String URL_POST_DEVICE_INFO=ServerUrl+"/collectInfo/device.jsp";
    String URL_POST_USER_INFO=ServerUrl+"/collectInfo/user.jsp";

}
