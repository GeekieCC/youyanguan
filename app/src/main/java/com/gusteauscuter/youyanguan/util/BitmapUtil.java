package com.gusteauscuter.youyanguan.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;

/**
 * Created by Strang on 2015/11/11.
 */
public class BitmapUtil {

    //该方法的方法参数为一个Bitmap,最后返回的就是字节数组
    public static byte[] getBytes(Bitmap bitmap){
        if (bitmap == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);//采用PNG格式，无损压缩
        return baos.toByteArray();//创建分配字节数组
    }
    //该方法的方法参数为一个字节数组，返回的就是Bitmap
    public static Bitmap getBitmap(byte[] data){
        if (data.length == 0) return null;
        return BitmapFactory.decodeByteArray(data, 0, data.length);//从字节数组解码位图
    }

    public  static Bitmap getBitmap(Context context,Uri selectedImageUri){
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(selectedImageUri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return BitmapFactory.decodeFile(picturePath);
    }

}
