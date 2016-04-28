package com.gusteauscuter.youyanguan.common;

import android.os.Environment;

/**
 * 全部自定义常量，地址
 */
public class PublicURI {
    /** 系统文件路径     */
    public static final String SD_CARD = Environment.getExternalStorageDirectory().getPath();
    public static final String LOCAL_PATH = SD_CARD +"/libSCUT/";
    // 下载包安装路径
    public static final String PATH_APK = LOCAL_PATH + "wanjuanwu.apk";
    // 界面自定义文件保存路径
    public static final String PATH_BG_SEARCH = LOCAL_PATH +"mSearchBackground.jpg";
    public static final String PATH_BG_HOME = LOCAL_PATH +"mDrawerBackground.jpg";
    public static final String PATH_HEADER_IMAGE = LOCAL_PATH +"mHeaderImage.jpg";
    // 分享图片保存路径
    public static final String PATH_SHARE_MY_BOOKS = LOCAL_PATH +"mSharedBooksBorrowed.jpg";
    public static final String PATH_SHARE_BOOK_DETAIL = LOCAL_PATH +"mSharedBookDetail.jpg";

    /**  服务器根路径     */
    public static final String URL_SERVER_ROOT = "http://geekie.cc";
    // 安装包及信息url
    public static final String URL_APK_DOWNLOAD = URL_SERVER_ROOT +"/apk/wanjuanwu.apk";
    public static final String URL_APK_VERSION = URL_SERVER_ROOT +"/apk/Version.xml";
    // API网络路径
    public static final String URL_API_BASE = "http://geekie.cc/wanjuanwu2/";
    public static final String URL_LOGIN = URL_API_BASE +"LoginServlet";
    public static final String URL_SEARCH_BOOK = URL_API_BASE +"SearchBookServlet";
    public static final String URL_GET_BOOK_STORE_INFOR = URL_API_BASE +"CheckCollectionInfoServlet";
    public static final String URL_GET_BOOK_DETAIL = URL_API_BASE +"BookDetailServlet";
    // 信息收集url
    public static final String URL_POST_DEVICE_INFO= URL_SERVER_ROOT +"/collectInfo/device.jsp";
    public static final String URL_POST_USER_INFO= URL_SERVER_ROOT +"/collectInfo/user.jsp";
}
