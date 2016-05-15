package com.gusteauscuter.youyanguan.domain;

public class BookBorrowed  {
	private String bookId;
	private String title;
	private String author;
	private String borrowDay;
	private String returnDay;
	private int borrowedTime;
	private int maxBorrowTime;
	private boolean isExpired;
	private String renewLink;
	private String iconUrl;



	public BookBorrowed ( String title, String author,String bookId,String borrowDay, String returnDay,
				  int borrowedTime,int maxBorrowTime, boolean isExpired, String renewLink){
		this.bookId = bookId;
		this.title = title;
		this.author = author;
		this.borrowDay=borrowDay;
		this.returnDay=returnDay;
		this.borrowedTime=borrowedTime;
		this.maxBorrowTime=maxBorrowTime;
		this.isExpired=isExpired;
		this.renewLink=renewLink;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getBookId() {
		return bookId;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public String getBorrowDay() {
		return borrowDay;
	}

	public String getReturnDay() {
		return returnDay;
	}

	public int getBorrowedTime() {
		return borrowedTime;
	}

	public int getMaxBorrowTime() {
		return maxBorrowTime;
	}

	public boolean isExpired() {
		return isExpired;
	}

	public String getRenewLink() {
		return renewLink;
	}

}
