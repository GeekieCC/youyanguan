package com.gusteauscuter.youyanguan.domain;

public class CollectBook {
	private String title;
	private String author;
	private String publisher;
	private String pubdate;
	private String searchNum;
	private String bookId;
	private int collectInfo = CollectInfo.UNKNOWN;

	
	public CollectBook(String title, String author, String publisher, String pubdate, String searchNum, String bookId) {
		super();
		this.title = title;
		this.author = author;
		this.publisher = publisher;
		this.pubdate = pubdate;
		this.searchNum = searchNum;
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
	public String getPubdate() {
		return pubdate;
	}
	public String getSearchNum() {
		return searchNum;
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

}
