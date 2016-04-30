package com.gusteauscuter.youyanguan.domain;

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
	
	
}
