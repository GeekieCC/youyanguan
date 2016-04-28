package com.gusteauscuter.youyanguan.definedDataClass;

/**
 * Created by Strang on 2015/11/10.
 */
public interface BaseBook {
    int BOTH_NOT = 0;
    int BOTH_YES = 1;
    int NORTH_ONLY = 3;
    int SOUTH_ONLY = 4;
    int UNKNOWN = 5;
    //int BORROWED_ALREADY = 6;

    String getBookId();
    String getTitle();
    String getAuthor();

    String getPublisher();
    String getPubdate();
    String getIsbn();
    String getSearchNum();
    int getBorrowCondition();
    boolean isCollected();

    void setBookId(String bookId);
    void setTitle(String title);
    void setAuthor(String author);
    void setPublisher(String publisher);
    void setPubdate(String pubdate);
    void setIsbn(String isbn);
    void setSearchNum(String searchNum);
    void setBorrowCondition(int borrowCondition);
    void setIsCollected(boolean isCollected);
}
