package com.gusteauscuter.youyanguan.definedDataClass;

import java.io.Serializable;

/**
 * Created by Strang on 2015/11/12.
 */
public class SimpleBaseBook implements BaseBook, Serializable {
    private String publisher;
    private String pubdate;
    private String isbn;
    private String searchNum;
    private int borrowCondition;
    private boolean isCollected;
    private String author;
    private String title;
    private String bookId;
    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public int getBorrowCondition() {
        return borrowCondition;
    }

    @Override
    public void setBorrowCondition(int borrowCondition) {
        this.borrowCondition = borrowCondition;
    }

    @Override
    public String getIsbn() {
        return isbn;
    }

    @Override
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Override
    public boolean isCollected() {
        return isCollected;
    }

    @Override
    public void setIsCollected(boolean isCollected) {
        this.isCollected = isCollected;
    }

    @Override
    public String getPubdate() {
        return pubdate;
    }

    @Override
    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    @Override
    public String getPublisher() {
        return publisher;
    }

    @Override
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public String getSearchNum() {
        return searchNum;
    }

    @Override
    public void setSearchNum(String searchNum) {
        this.searchNum = searchNum;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getBookId() {
        return bookId;
    }

    @Override
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
