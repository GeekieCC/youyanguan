package com.gusteauscuter.youyanguan.databaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gusteauscuter.youyanguan.domain.BookBase;
import com.gusteauscuter.youyanguan.databaseHelper.FeedReaderContract.FeedEntry;

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
        String INTEGER_TYPE = " INTEGER";
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
                        FeedEntry.COLUMN_NAME_BOOKID  + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        FeedEntry.COLUMN_NAME_BORROW_CONDITION + INTEGER_TYPE +
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
     * @param simpleBaseBook
     * @return
     */
    public long addBook(BookBase simpleBaseBook) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, simpleBaseBook.getTitle());
        values.put(FeedEntry.COLUMN_NAME_AUTHOR, simpleBaseBook.getAuthor());
        values.put(FeedEntry.COLUMN_NAME_PUBLISHER, simpleBaseBook.getPublisher());
        values.put(FeedEntry.COLUMN_NAME_ISBN, simpleBaseBook.getIsbn());
        values.put(FeedEntry.COLUMN_NAME_PUBDATE, simpleBaseBook.getPubdate());
        values.put(FeedEntry.COLUMN_NAME_SEARCHNUM, simpleBaseBook.getSearchNum());
        values.put(FeedEntry.COLUMN_NAME_BOOKID, simpleBaseBook.getBookId());
        values.put(FeedEntry.COLUMN_NAME_BORROW_CONDITION, simpleBaseBook.getLocationSummary());
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
    public List<BookBase> getAllBookCollections() {
        List<BookBase> bookLists = new ArrayList<BookBase>();
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
                BookBase simpleBaseBook = new BookBase();
                simpleBaseBook.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE)));
                simpleBaseBook.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_AUTHOR)));
                simpleBaseBook.setPublisher(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_PUBLISHER)));
                simpleBaseBook.setIsbn(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_ISBN)));
                simpleBaseBook.setPubdate(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_PUBDATE)));
                simpleBaseBook.setSearchNum(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SEARCHNUM)));
                simpleBaseBook.setBookId(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_BOOKID)));
                simpleBaseBook.setLocationSummary(cursor.getInt(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_BORROW_CONDITION)));
                simpleBaseBook.setIsCollected(true);
                bookLists.add(simpleBaseBook);
            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return bookLists;
    }

    /**
     * 得到收藏的图书数目
     * @return 收藏图书数目
     */
    public int getBookCollectionsCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = getBookCollectionsCountHelper(db);
        db.close();
        return result;
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
     * @param simpleBaseBook
     * @return 若返回非零值，则取消收藏成功；否则失败
     */
    public int deleteBook(BookBase simpleBaseBook) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Define 'where' part of query.
        String selection = FeedEntry.COLUMN_NAME_BOOKID + " LIKE ?";
        // Specify arguments in placeholder order.
        // 每本书的BookId是唯一的
        String[] selectionArgs = { simpleBaseBook.getBookId() };
        // Issue SQL statement.
        //the number of rows affected if a whereClause is passed in, 0 otherwise.
        int num = db.delete(FeedEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
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
        int result = db.delete(FeedEntry.TABLE_NAME, "1", null);
        db.close();
        return result;
    }

    /**
     * 检查一本书是否已被收藏
     * @param simpleBaseBook
     * @return
     */
    public boolean isCollected(BookBase simpleBaseBook) {
        boolean result;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(
                FeedEntry.TABLE_NAME,
                null,
                FeedEntry.COLUMN_NAME_BOOKID + " = ?",
                new String[]{simpleBaseBook.getBookId()},
                null,
                null,
                null);
        if (cursor.getCount() <= 0) {
            result = false;
        } else {
            result = true;
        }
        db.close();
        return result;
    }

    public int updateTupleBorrowCondition(BookBase simpleBaseBook) {
        int result = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_BORROW_CONDITION, simpleBaseBook.getLocationSummary());
        result = db.update(FeedEntry.TABLE_NAME, values,
                           FeedEntry.COLUMN_NAME_BOOKID + " = ?",
                           new String[]{simpleBaseBook.getBookId()});
        db.close();
        return result;
    }
}


