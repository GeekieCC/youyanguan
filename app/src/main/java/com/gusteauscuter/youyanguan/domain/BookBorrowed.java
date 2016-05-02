package com.gusteauscuter.youyanguan.domain;

public class BookBorrowed extends BookBase {

	private String barcode;
	private String borrowDay;
	private String returnDay;
	private int borrowedTime;
	private int maxBorrowTime;
	private boolean isExpired;
	private String renewLink;
	private String detailLink;

	public BookBorrowed ( String title, String author, String publisher, String isbn, String pubdate,
				  String searchNum, String bookId, String barcode,String borrowDay, String returnDay,
				  int borrowedTime,int maxBorrowTime, boolean isExpired, String renewLink, String detailLink){
		super(title, author, publisher, isbn, pubdate,searchNum, bookId);
		this.barcode=barcode;
		this.borrowDay=borrowDay;
		this.returnDay=returnDay;
		this.borrowedTime=borrowedTime;
		this.maxBorrowTime=maxBorrowTime;
		this.isExpired=isExpired;
		this.renewLink=renewLink;
		this.detailLink=detailLink;
	}

	public String getBarcode() {
		return barcode;
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

	public String getDetailLink() {
		return detailLink;
	}
}
