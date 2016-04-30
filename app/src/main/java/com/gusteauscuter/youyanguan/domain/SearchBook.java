package com.gusteauscuter.youyanguan.domain;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchBook {
	
	private String title;
	private String author;
	private String publisher;
	private String isbn;
	private String pubdate;
	private String searchNum;
	private String type;
	private String bookId;
	private int collectInfo = CollectInfo.UNKNOWN;
	
	public SearchBook(String title, String author, String publisher, String isbn, String pubdate,
			String searchNum, String type, String bookId) {
		super();
		this.title = title;
		this.author = author;
		this.publisher = publisher;
		this.isbn = isbn;
		this.pubdate = pubdate;
		this.searchNum = searchNum;
		this.type = type;
		this.bookId = bookId;
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
	public String getType() {
		return type;
	}
	public String getBookId() {
		return bookId;
	}

	public int getCollectInfo() {
		return collectInfo;
	}

	public void setCollectInfo(int collectInfo) {
		this.collectInfo = collectInfo;
	}
	
	public static List<SearchBook> getBook(JSONArray jsonArray){
		List<SearchBook> bookList=new ArrayList<>();
		try {
			for(int i=0;i<jsonArray.length();i++){
				JSONObject jsonBook = (JSONObject) jsonArray.get(i);
				String author = jsonBook.getString("author");
				String title = jsonBook.getString("title");
				String searchNum = jsonBook.getString("searchNum");
				String pubdate = jsonBook.getString("pubdate");
				String isbn = jsonBook.getString("isbn");
				String bookId = jsonBook.getString("bookId");
				String type = jsonBook.getString("type");
				String publisher = jsonBook.getString("publisher");
				bookList.add(new SearchBook(title, author, publisher, isbn, pubdate,searchNum, type, bookId));
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return bookList;
	}
}
