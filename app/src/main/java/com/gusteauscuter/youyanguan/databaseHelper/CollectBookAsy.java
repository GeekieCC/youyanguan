package com.gusteauscuter.youyanguan.databaseHelper;

/**
 * Created by Z on 2016/5/15 0015.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.domain.BookBase;

/**
 * deal with the action to collect or uncollect a book
 */
public class CollectBookAsy extends AsyncTask<Void, Void, Boolean> {
    public boolean mOperationAdd;// 操作为添加时，为true;操作为删除时，为false
    public BookBase mBookBaseToCollect;
    private Context mContext;



    public CollectBookAsy(Context context,BookBase bookBaseToCollect) {
        this.mBookBaseToCollect = bookBaseToCollect;
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        mOperationAdd = !mBookBaseToCollect.isCollected();
    }

    @Override
    protected Boolean doInBackground(Void... args) {
        //操作成功与否
        BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(mContext);
        if(mOperationAdd)
            return (mDbHelper.addBook(mBookBaseToCollect) !=-1);
        else
            return (mDbHelper.deleteBook(mBookBaseToCollect)!=0);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Toast.makeText(mContext, (mOperationAdd ? "添加" : "删除") + (result ? "成功" : "失败"),
                Toast.LENGTH_SHORT).show();
        if (result) {
            mBookBaseToCollect.setIsCollected(mOperationAdd);
        }
    }

}