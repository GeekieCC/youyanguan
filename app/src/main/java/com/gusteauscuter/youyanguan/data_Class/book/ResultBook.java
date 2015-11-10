package com.gusteauscuter.youyanguan.data_Class.book;

import android.graphics.Color;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class ResultBook implements BaseBook, Serializable {
	
	private static final String DETAIL_BASE_URL = "http://202.38.232.10/opac/servlet/opac.go";

    public static int BORTH_NOT = 0;
    public static int BORTH_YES = 1;
	public static int NORTH_ONLY = 3;
    public static int SOUTH_ONLY = 4;
	public static int UNKNOWN = 5;

	private int rowNumber;
	private String title;
	private String author;
	private String publisher;
	private String isbn;
	private String pubdate;
	private String searchNum;
	private String type;
	private String bookId;
	private int borrowCondition = UNKNOWN;
	private boolean isCollected = false;

    private int color = Color.parseColor("#FFFFFF");

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getBorrowCondition() {
		return borrowCondition;
	}
	
	public ResultBook() {
		
	}
	
	
	public void getResultBook(Element element) {
		getResultBookHelper(element);
	}
	
	
//	public void getResultBookWithBorrowInfo(Element element) throws SocketTimeoutException {
//		getResultBookHelper(element);
//		isBorrowable = isBorrowableHelper();
//	}

	public void getResultBookWithBorrowInfo(Element element) throws SocketTimeoutException {
		getResultBookHelper(element);
		borrowCondition = checkBorrowCondition();
	}


	private int checkBorrowCondition() throws SocketTimeoutException {
        boolean south = false;
        boolean north = false;
		String detailLink = buildDetailLink(bookId);
		String detailHtml = getHtml(detailLink);
		Document detailDoc = Jsoup.parse(detailHtml);
		List<LocationInformation> locationLists = getLocationInfo(detailDoc);
		for (LocationInformation locInfo : locationLists) {
			String location = locInfo.getLocation();
			String detailLocation = locInfo.getDetailLocation();
			String status = locInfo.getStatus();
			if (!location.contains("停") && !detailLocation.contains("停") && status.contains("在馆")) {
                if ((location.contains("北") || detailLocation.contains("北"))) {
					north = true;
				} else if ((location.contains("南") || detailLocation.contains("南"))) {
					south = true;
				}
			}
		}
        if (!south && !north) return BORTH_NOT;
        if (south && north) return BORTH_YES;
        if (south && !north) return SOUTH_ONLY;
        return NORTH_ONLY;
	}
	
//	private boolean isBorrowableHelper() throws SocketTimeoutException {
//
//		String detailLink = buildDetailLink(bookId);
//		String detailHtml = getHtml(detailLink);
//		Document detailDoc = Jsoup.parse(detailHtml);
//		List<LocationInformation> locationLists = getLocationInfo(detailDoc);
//		for (LocationInformation locInfo : locationLists) {
//			if (!locInfo.getLocation().contains("停") && !locInfo.getDetailLocation().contains("停") && locInfo.getStatus().contains("在馆")) {
//				return true;
//			}
//		}
//
//		return false;
//	}
	
	private String buildDetailLink(String bookId) {
		//http://202.38.232.10/opac/servlet/opac.go?cmdACT=query.bookdetail&bookid=1124335&marcformat=&libcode=&source=
		String detailLink = DETAIL_BASE_URL + "?cmdACT=query.bookdetail&bookid=" + bookId + "&marcformat=&libcode=&source=";
		
		return detailLink;
	}
	
	private List<LocationInformation> getLocationInfo(Document doc) {
		ArrayList<LocationInformation> locationLists = new ArrayList<LocationInformation>();
		Elements tableElements = doc.getElementsByTag("tbody");
		Elements trElements = tableElements.last().getElementsByTag("tr");
		trElements.remove(0);
		for (Element tr : trElements) {
			LocationInformation locationInfo = new LocationInformation(tr);
			locationLists.add(locationInfo);
		}
		return locationLists;
	}
	
	private String getHtml(String link) throws SocketTimeoutException {
		return HttpUtil.getHtml(link);
	}
	


	private void getResultBookHelper(Element element) {
		Elements elements = element.getElementsByTag("td");
		rowNumber = Integer.parseInt(elements.get(0).text());
		title = elements.get(1).text();
		bookId = elements.get(1).select("a").first().attr("href").replaceAll("\\D", "");
		author = elements.get(2).text();
		publisher = elements.get(3).text();
		isbn = elements.get(4).text().replaceAll("\\W","");
		pubdate = elements.get(5).text();
		searchNum = elements.get(6).text();
		type = elements.get(7).text();
	}
	
	
	
	
	public String toString() {
		return rowNumber + "||" + title + "||" + author + "||"
	             + publisher + "||" + isbn + "||" + pubdate 
	             + "||" + searchNum + "||" + type + "||" + bookId + "||" + borrowCondition + isCollected + "\n";
	}
	
	
	public String getBookId() {
		return bookId;
	}
	
	public int getRowNumber() {
		return rowNumber;
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

	public void setTitle(String title) {
		this.title = title;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
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

	public boolean isCollected() {
		return isCollected;
	}

	public void setBorrowCondition(int borrowCondition) {
		this.borrowCondition = borrowCondition;
	}

	public void setIsCollected(boolean isCollected) {
		this.isCollected = isCollected;
	}
}
