package com.gusteauscuter.youyanguan.data_Class.bookdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gusteauscuter.youyanguan.data_Class.book.ResultBook;
import com.gusteauscuter.youyanguan.data_Class.bookdatabase.FeedReaderContract.FeedEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Strang on 2015/10/31.
 */
public class BookCollectionDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BookCollection.db";

    public BookCollectionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        String TEXT_TYPE = " TEXT";
        String NOT_NULL = " NOT NULL";
        String COMMA_SEP = ",";
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                        FeedEntry._ID + " INTEGER PRIMARY KEY," +
                        FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        FeedEntry.COLUMN_NAME_AUTHOR + TEXT_TYPE + COMMA_SEP +
                        FeedEntry.COLUMN_NAME_PUBLISHER + TEXT_TYPE + COMMA_SEP +
                        FeedEntry.COLUMN_NAME_ISBN + TEXT_TYPE + COMMA_SEP +
                        FeedEntry.COLUMN_NAME_PUBDATE + TEXT_TYPE + COMMA_SEP +
                        FeedEntry.COLUMN_NAME_SEARCHNUM + TEXT_TYPE + COMMA_SEP +
                        FeedEntry.COLUMN_NAME_BOOKID  + TEXT_TYPE + NOT_NULL +
                        " )";

        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }



    //CRUD

    /**
     * 将某本书收藏到本地数据库
     * @param resultBook
     * @return
     */
    public long addResultBook(ResultBook resultBook) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, resultBook.getTitle());
        values.put(FeedEntry.COLUMN_NAME_AUTHOR, resultBook.getAuthor());
        values.put(FeedEntry.COLUMN_NAME_PUBLISHER, resultBook.getPublisher());
        values.put(FeedEntry.COLUMN_NAME_ISBN, resultBook.getIsbn());
        values.put(FeedEntry.COLUMN_NAME_PUBDATE, resultBook.getPubdate());
        values.put(FeedEntry.COLUMN_NAME_SEARCHNUM, resultBook.getSearchNum());
        values.put(FeedEntry.COLUMN_NAME_BOOKID, resultBook.getBookId());

        // Insert the new row, returning the primary key value of the new row
        //the row ID of the newly inserted row, or -1 if an error occurred
        long newRowId = db.insert(FeedEntry.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    /**
     * 得到收藏的所有图书
     * @return 收藏的所有图书
     */
    public List<ResultBook> getAllBookCollections() {
        List<ResultBook> bookLists = new ArrayList<ResultBook>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(
                            FeedEntry.TABLE_NAME,                   // The table to query
                            null,                               // The columns to return
                            null,                                // The columns for the WHERE clause
                            null,                            // The values for the WHERE clause
                            null,                                     // don't group the rows
                            null,                                     // don't filter by row groups
                            null                                 // The sort order
        );

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ResultBook resultBook = new ResultBook();
                resultBook.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE)));
                resultBook.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_AUTHOR)));
                resultBook.setPublisher(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_PUBLISHER)));
                resultBook.setIsbn(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_ISBN)));
                resultBook.setPubdate(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_PUBDATE)));
                resultBook.setSearchNum(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SEARCHNUM)));
                resultBook.setBookId(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_BOOKID)));

                bookLists.add(resultBook);
            } while (cursor.moveToNext());
        }
        // return contact list
        return bookLists;
    }

    /**
     * 得到收藏的图书数目
     * @return 收藏图书数目
     */
    public int getBookCollectionsCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        return getBookCollectionsCountHelper(db);
    }

    private int getBookCollectionsCountHelper(SQLiteDatabase db) {
        Cursor cursor = db.query(
                FeedEntry.TABLE_NAME,                   // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        return cursor.getCount();
    }

    /**
     * 取消收藏谋一本书
     * @param resultBook
     * @return 若返回非零值，则取消收藏成功；否则失败
     */
    public int deleteResultBook(ResultBook resultBook) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Define 'where' part of query.
        String selection = FeedEntry.COLUMN_NAME_BOOKID + " LIKE ?";
        // Specify arguments in placeholder order.
        // 每本书的BookId是唯一的
        String[] selectionArgs = { resultBook.getBookId() };
        // Issue SQL statement.
        //the number of rows affected if a whereClause is passed in, 0 otherwise.
        int num = db.delete(FeedEntry.TABLE_NAME, selection, selectionArgs);

        return num;
    }

    /**
     * 清空所收藏的图书
     * @return 如果本来就没有收藏，则返回-1；否则返回清空图书的数目
     */
    public int removeAllCollections() {
        SQLiteDatabase db = this.getWritableDatabase();
        int numOfBooks = getBookCollectionsCountHelper(db);
        if (numOfBooks == 0) return -1; //当前收藏图书为零，无需清空
        return db.delete(FeedEntry.TABLE_NAME, "1", null);
    }

//    /**
//     * 检查一本书是否已被收藏
//     * @param resultBook
//     * @return
//     */
//    public boolean isCollected(ResultBook resultBook) {
//        boolean result;
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.query(
//                                FeedEntry.TABLE_NAME,
//                                null,
//                                FeedEntry.COLUMN_NAME_BOOKID + " = ?",
//                                new String[]{resultBook.getBookId()},
//                                null,
//                                null,
//                                null);
//        if (cursor.getCount() <= 0) {
//            result = false;
//        } else {
//            result = true;
//        }
//        return result;
//    }

}


