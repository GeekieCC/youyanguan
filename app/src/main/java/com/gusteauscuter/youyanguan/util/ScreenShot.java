package com.gusteauscuter.youyanguan.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ScreenShot {

    /**
     * 截取scrollview的屏幕
     * @param listView
     * @return
     */
    public static Bitmap getBitmapByView(ListView listView) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取scrollview实际高度
        for (int i = 0; i < listView.getChildCount(); i++) {
            h += listView.getChildAt(i).getHeight();
//            listView.getChildAt(i).setBackgroundColor(
//                    Color.parseColor("#ffffff"));
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(listView.getWidth(), h,
                Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        listView.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmapByView(GridView gridView) {
        int h = 0;
        Bitmap bitmap = null;
        int num=gridView.getNumColumns();
        // 获取scrollview实际高度
        for (int i = 0; i < gridView.getChildCount(); i++) {
            if(i%num==0) {
                h += gridView.getChildAt(i).getHeight();
            }
            gridView.getChildAt(i).setBackgroundColor(
                    Color.parseColor("#ffffff"));
        }
        bitmap = Bitmap.createBitmap(gridView.getWidth(), h,
                Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        gridView.setBackgroundColor(Color.parseColor("#ffffff"));
        gridView.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmapByView(View view) {
        Bitmap bitmap = null;
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        view.setBackgroundColor(Color.parseColor("#ffffff"));
        view.draw(canvas);
        return bitmap;
    }

    /**
     * 压缩图片
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        int options = 100;
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > 100) {
            // 重置baos
            baos.reset();
            // 这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.PNG, options, baos);
            // 每次都减少10
            options -= 10;
        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }


    // 获取指定Activity的截屏，保存到png文件
    public static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

        int statusBarHeight = frame.top+175;
        Log.i("TAG", "" + statusBarHeight);
        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    // 保存到sdcard

    /**
     * 保存到sdcard
     * @param b the bitmap to save by system time
     * @return
     */
    public static String savePic(Bitmap b) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss",
                Locale.US);
        File outfile = new File("/sdcard/image");
        // 如果文件不存在，则创建一个新文件
        if (!outfile.isDirectory()) {
            try {
                outfile.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String fname = outfile + "/" + sdf.format(new Date()) + ".png";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fname);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fname;
    }

    /**
     * 保存到sdcard
     * @param bitmap the bitmap to save by system time
     * @param strFileName file name to save the picture
     */

    public static void saveAsImg(Bitmap bitmap ,String strFileName) {

        SaveAsImage saveAsImgAsy=new SaveAsImage();
        saveAsImgAsy.execute(bitmap,strFileName);
    }



    // 程序入口

    public static void shoot(String strFileName,View view) {
        ScreenShot.saveAsImg(ScreenShot.getBitmapByView(view), strFileName);
    }

    public static void shoot(String strFileName,ScrollView scrollView) {
        ScreenShot.saveAsImg(ScreenShot.getBitmapByView(scrollView), strFileName);
    }

    public static void shoot(String strFileName,GridView gridView) {
        ScreenShot.saveAsImg(ScreenShot.getBitmapByView(gridView), strFileName);
    }

    // 程序入口
    public static void shoot(String strFileName,Activity activity) {
        ScreenShot.saveAsImg(ScreenShot.takeScreenShot(activity), strFileName);
    }
}