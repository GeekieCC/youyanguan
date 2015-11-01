package com.gusteauscuter.youyanguan.data_Class.bookdatabase;

import android.provider.BaseColumns;

/**
 * Created by Strang on 2015/10/31.
 */
public final class FeedReaderContract {

    public FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "BOOK_COLLECTION_DATABASE";
        public static final String COLUMN_NAME_TITLE = "TITLE";
        public static final String COLUMN_NAME_AUTHOR = "AUTHOR";
        public static final String COLUMN_NAME_PUBLISHER = "PUBLISHER";
        public static final String COLUMN_NAME_ISBN = "ISBN";
        public static final String COLUMN_NAME_PUBDATE = "PUBDATE";
        public static final String COLUMN_NAME_SEARCHNUM = "SEARCHNUM";
        public static final String COLUMN_NAME_BOOKID = "BOOKID";

    }
}