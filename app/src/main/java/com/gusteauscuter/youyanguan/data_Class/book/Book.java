package com.gusteauscuter.youyanguan.data_Class.book;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;

public class Book extends BaseBook implements Serializable {
	// 更新书籍ItemBook代码

	private int rowNumber;
	private String barcode;
	private String description="this is a book";
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

	public String toString() {
		//return name + "(" + author + ")" + "(" + borrowDay + ")" + "(" + returnDay + ")";
		return rowNumber + "||" + barcode + "||" + title + "||" + author + "||"  + volume + "||"  + libraryName + "||"
				+ libraryLocation + "||"  + borrowDay + "||"  + returnDay + "||"  + borrowedTime + "||" 
				+ maxBorrowTime + "||"  + isExpired + "||"  + renewLink + "||" ;
	}

	public Book(Element element) {
		Elements elements = element.getElementsByTag("td");
		rowNumber = Integer.parseInt(elements.get(0).text());
		barcode = elements.get(1).text();		
		String titleAndAuthor = elements.get(2).text();

		detailLink = "http://202.38.232.10/opac" +
				elements.get(2).select("a").first().attr("href").substring(2); //详情页链接
		String find = "bookid=";
		String s1 = detailLink.substring(detailLink.indexOf(find) + find.length());
		bookId = s1.substring(0, s1.indexOf('&'));

		int index1 = titleAndAuthor.indexOf('/');
		title = titleAndAuthor.substring(0, index1);
		author = titleAndAuthor.substring(index1 + 1);
		volume = elements.get(3).text();

		libraryName = elements.get(4).text();
		libraryLocation = elements.get(5).text();
		borrowDay = elements.get(6).text();
		returnDay = elements.get(7).text();
		String borrowedTimeAndMaxBorrowTime = elements.get(8).text();
		int index2 = borrowedTimeAndMaxBorrowTime.indexOf('/');
		borrowedTime = Integer.parseInt(borrowedTimeAndMaxBorrowTime.substring(0, index2));
		maxBorrowTime = Integer.parseInt(borrowedTimeAndMaxBorrowTime.substring(index2 + 1));
		isExpired = elements.get(9).text().equals("是") ? true : false;
		//renewLink = "http://202.38.232.10/opac" + elements.get(10).select("a").first().attr("href").substring(2);
		this.parseRenewLink(elements.get(10));


	}

	private void parseRenewLink(Element renewLinkElement) {
		//  Auto-generated method stub
		if (borrowedTime == maxBorrowTime) {
			renewLink = "";
		} else {
			renewLink = "http://202.38.232.10/opac" + renewLinkElement.select("a").first().attr("href").substring(2);
		}

	}

	//ÐÂÔö
	public String getDetailLink() {
		return detailLink;
	}
	
	public int getRowNumber() {
		return rowNumber;
	}
	
	public String getBarcode() {
		return barcode;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription(){
		description="书名:"+getTitle()+"\n借阅期限:"+getReturnDay()+"\n已续借次数:"+getBorrowedTime()+"/"+getMaxBorrowTime();
		return description;
	}
	

	public String getAuthor() {
		return author;
	}

//	public String getPublisher() {
//		return publisher;
//	}
//
//	public String getIsbn() {
//		return isbn;
//	}
//
//	public String getPubdate() {
//		return pubdate;
//	}

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
	
	public void setBorrowDay(String borrowDay) {
		this.borrowDay = borrowDay;
	}
	
	public String getReturnDay() {
		return returnDay;
	}
	
	public void setReturnDay(String returnDay) {
		this.returnDay = returnDay;
	}
	
	public int getBorrowedTime() {
		return borrowedTime;
	}
	
	public void setBorrowedTime(int borrowedTime) {
		this.borrowedTime = borrowedTime;
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
