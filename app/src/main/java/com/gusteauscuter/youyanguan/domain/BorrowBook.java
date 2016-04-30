package com.gusteauscuter.youyanguan.domain;

public class BorrowBook {
	private String barcode;
	private String title;
	private String author;
	private String volume;
	private String libraryName;
	private String libraryLocation;
	private String borrowDay;
	private String returnDay;
	private int borrowedTime;
	private int maxBorrowTime;
	private boolean isExpired;
	private String renewLink;
	private String detailLink;
    private String bookId;
    
    
	public BorrowBook(String barcode, String title, String author, String volume, String libraryName,
			String libraryLocation, String borrowDay, String returnDay, int borrowedTime, int maxBorrowTime,
			boolean isExpired, String renewLink, String detailLink, String bookId) {
		super();
		this.barcode = barcode;
		this.title = title;
		this.author = author;
		this.volume = volume;
		this.libraryName = libraryName;
		this.libraryLocation = libraryLocation;
		this.borrowDay = borrowDay;
		this.returnDay = returnDay;
		this.borrowedTime = borrowedTime;
		this.maxBorrowTime = maxBorrowTime;
		this.isExpired = isExpired;
		this.renewLink = renewLink;
		this.detailLink = detailLink;
		this.bookId = bookId;
	}
	public String getBarcode() {
		return barcode;
	}
	public String getTitle() {
		return title;
	}
	public String getAuthor() {
		return author;
	}
	public String getVolume() {
		return volume;
	}
	public String getLibraryName() {
		return libraryName;
	}
	public String getLibraryLocation() {
		return libraryLocation;
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
	public String getBookId() {
		return bookId;
	}
	@Override
	public String toString() {
		return "BorrowBook [barcode=" + barcode + ", title=" + title + ", author=" + author + ", volume=" + volume
				+ ", libraryName=" + libraryName + ", libraryLocation=" + libraryLocation + ", borrowDay=" + borrowDay
				+ ", returnDay=" + returnDay + ", borrowedTime=" + borrowedTime + ", maxBorrowTime=" + maxBorrowTime
				+ ", isExpired=" + isExpired + ", renewLink=" + renewLink + ", detailLink=" + detailLink + ", bookId="
				+ bookId + "]";
	}
    
    
}
