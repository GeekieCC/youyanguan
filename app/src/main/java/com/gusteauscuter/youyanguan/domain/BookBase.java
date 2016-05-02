package com.gusteauscuter.youyanguan.domain;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BookBase implements Serializable{

	private String title;
	private String author;
	private String publisher;
	private String isbn;
	private String pubdate;
	private String searchNum;
	private String bookId;
	private int locationSummary;
	private boolean isCollected;
	private Bitmap pictureBitmap=null;

	public BookBase(){

	}

	public void copyBookBase(BookBase bookBase){
		setTitle(bookBase.getTitle());
		setAuthor(bookBase.getAuthor());
		setPublisher(bookBase.getPublisher());
		setIsbn(bookBase.getIsbn());
		setPubdate(bookBase.getPubdate());
		setSearchNum(bookBase.getSearchNum());
		setBookId(bookBase.getBookId());
		setLocationSummary(bookBase.getLocationSummary());
		setIsCollected(bookBase.isCollected());
		setPictureBitmap(bookBase.getPictureBitmap());
	}

	public BookBase(String title, String author, String publisher, String isbn, String pubdate,
					String searchNum, String bookId) {
		this.title = title;
		this.author = author;
		this.publisher = publisher;
		this.isbn = isbn;
		this.pubdate = pubdate;
		this.searchNum = searchNum;
		this.bookId = bookId;
		locationSummary = LocationInfo.UNKNOWN;
		isCollected=false;
		pictureBitmap =null;
	}

	public String getTitle() {
		return title;
	}
	public String getAuthor() {
		return author;
	}
	public String getPublisher() {
		return publisher;
	}
	public String getIsbn() {
		return isbn;
	}
	public String getPubdate() {
		return pubdate;
	}
	public String getSearchNum() {
		return searchNum;
	}
	public String getBookId() {
		return bookId;
	}

	public int getLocationSummary() {
		return locationSummary;
	}

	public void setLocationSummary(int locationSummary) {
		this.locationSummary = locationSummary;
	}

	public boolean isCollected() {
		return isCollected;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public void setPubdate(String pubdate) {
		this.pubdate = pubdate;
	}

	public void setSearchNum(String searchNum) {
		this.searchNum = searchNum;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public void setIsCollected(boolean isCollected) {
		this.isCollected = isCollected;
	}

	public Bitmap getPictureBitmap() {
		return pictureBitmap;
	}

	public void setPictureBitmap(Bitmap pictureBitmap) {
		this.pictureBitmap = pictureBitmap;
	}


}
