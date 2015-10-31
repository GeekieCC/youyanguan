package com.gusteauscuter.youyanguan.data_Class.book;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Strang on 2015/10/1.
 */
public class HttpUtil {

    public static final int CONNECT_TIME_OUT = 6000;
    public static final int READ_TIME_OUT = 6000;

    /**
     * 使用HttpURLConnection,get方法获取网页源代码
     * @param link 网址
     * @return 网页源代码
     */
    public static String getHtml(String link) throws SocketTimeoutException {
        String result = "";
        HttpURLConnection urlConnection = null;
        BufferedReader br = null;
        try {
            URL url = new URL(link);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(CONNECT_TIME_OUT);
            urlConnection.setReadTimeout(READ_TIME_OUT);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                String readLine = "";
                while ((readLine = br.readLine()) != null) {
                    result += readLine;
                }
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            throw new SocketTimeoutException();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    /**
     * 使用HttpURLConnection，post方法获取网页源代码
     * @param baseUrl 网址
     * @param query 查询字符串
     * @return 网页源代码
     */
    public static String getQueryHtml(String baseUrl, String query) throws SocketTimeoutException {
        String resultHtml = "";
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(baseUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setChunkedStreamingMode(0);
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "utf-8");

            writer.write(query);
            writer.flush();
            writer.close();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    resultHtml += line;
                }

            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            throw new SocketTimeoutException();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return resultHtml;
    }

    /**
     * 使用HttpURLConnection,get方法获取网上的图片
     * @param imageLink 图片的URL地址
     * @return 图片
     */
    public static Bitmap getPicture(String imageLink) throws SocketTimeoutException {
        Bitmap picture = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream bis = null;
        try {
            URL url = new URL(imageLink);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(CONNECT_TIME_OUT);
            urlConnection.setReadTimeout(READ_TIME_OUT);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                bis = new BufferedInputStream(urlConnection.getInputStream());
                picture = BitmapFactory.decodeStream(bis);
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            throw new SocketTimeoutException();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return picture;
    }

}
