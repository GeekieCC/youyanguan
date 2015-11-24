package com.gusteauscuter.youyanguan.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Z on 2015/11/24.
 */
public class SaveAsImage extends AsyncTask<Object, Void, Void> {


    @Override
    protected Void doInBackground(Object... args) {

        try {
            Bitmap bitmap=(Bitmap) args[0] ;
            String strFileName=(String)args[1] ;
            File file =new File(strFileName);
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(strFileName,false);
            boolean bollean = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}