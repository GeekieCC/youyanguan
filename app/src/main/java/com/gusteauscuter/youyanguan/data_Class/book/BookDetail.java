package com.gusteauscuter.youyanguan.data_Class.book;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;


public class BookDetail {

	private static final String DOUBAN_BASE_URL = "https://api.douban.com/v2/book/isbn/:";
	private static final String DETAIL_BASE_URL = "http://202.38.232.10/opac/servlet/opac.go";
	private Bitmap picture;
	private String title="";
	private String author = "";
	private String authorIntro="";
	private String summary="";
	private String catalog="";
	private String pages="";
	private String price="";
	private String publisher="";
	private String pubdate="";

	private boolean isDoubanExisting;

	//馆藏信息
	private List<LocationInformation> locationLists;
	private String isbn = "";

	public BookDetail() {

	}

	//新方法1
	public void getBookDetail(Book book) throws SocketTimeoutException {
		title=book.getTitle();
		author = book.getAuthor();

		String detailLink = book.getDetailLink();
		String detailHtml = getHtml(detailLink);
		Document detailDoc = Jsoup.parse(detailHtml);
		isbn = getISBN(detailDoc);
		getLocationInfo(detailDoc);
		if (!isbn.isEmpty()) {
			String doubanUrl = DOUBAN_BASE_URL + isbn;
			JSONObject doubanJson = getDetailJson(doubanUrl);
			if (doubanJson != null) {
				isDoubanExisting = true;
				initIVar(doubanJson);
			} else {
				isDoubanExisting = false;
			}
		}

	}

	//新方法2
	public void getResultBookDetail(ResultBook resultBook) throws SocketTimeoutException {
		title=resultBook.getTitle();
		author = resultBook.getAuthor();
		publisher=resultBook.getPublisher();
		pubdate=resultBook.getPubdate();

		String bookId = resultBook.getBookId();
		String detailLink = buildDetailLink(bookId);
		String detailHtml = getHtml(detailLink);
		Document detailDoc = Jsoup.parse(detailHtml);
		isbn = resultBook.getIsbn();
		//处理detialHtml
		getLocationInfo(detailDoc);
		if (!isbn.isEmpty()) {
			String doubanUrl = DOUBAN_BASE_URL + isbn;
			JSONObject doubanJson = getDetailJson(doubanUrl);
			if (doubanJson != null) {
				isDoubanExisting = true;
				initIVar(doubanJson);
			} else {
				isDoubanExisting = false;
			}
		}
	}


	public String toString() {
		String locationStr = "";
		for (LocationInformation locationInfo : locationLists) {
			locationStr += locationInfo.toString()+"\n\n";
		}


//		if(authorLists.size()==0)
//			authorLists.add("暂无此类信息");
		if(authorIntro.isEmpty())
			authorIntro="暂无此类信息";
		if(summary.isEmpty())
			summary="暂无此类信息";
		if(catalog.isEmpty())
			catalog="暂无此类信息";
		else
			catalog="\n\n"+catalog;
		if(pages.isEmpty())
			pages="暂无此类信息";
		if(price.isEmpty())
			price="暂无此类信息";
		if(publisher.isEmpty())
			publisher="暂无此类信息";
		if(pubdate.isEmpty())
			pubdate="暂无此类信息";


		String toString = null;
		toString = "\n【标题】" +title + "\n\n【作者】" //+ authorLists
				+"\n\n【馆藏地点】\n\n"+ locationStr
				+ "\n\n【概述】" + summary+ "\n\n【目录】" + catalog
				+ "\n\n【页数】" + pages + "\n\n【价格】" + price
				+ "\n\n【出版社】" + publisher+ "\n\n【出版日期】" + pubdate;

		return toString;
	}

	private String buildDetailLink(String bookId) {
		//http://202.38.232.10/opac/servlet/opac.go?cmdACT=query.bookdetail&bookid=1124335&marcformat=&libcode=&source=
		String detailLink = DETAIL_BASE_URL + "?cmdACT=query.bookdetail&bookid=" + bookId + "&marcformat=&libcode=&source=";

		return detailLink;
	}

	private void getLocationInfo(Document doc) {
		locationLists = new ArrayList<LocationInformation>();
		Elements tableElements = doc.getElementsByTag("tbody");
		Elements trElements = tableElements.last().getElementsByTag("tr");
		trElements.remove(0);
		for (Element tr : trElements) {
			LocationInformation locationInfo = new LocationInformation(tr);
			locationLists.add(locationInfo);
		}
	}

	private void initIVar(JSONObject doubanJson) throws SocketTimeoutException {
		try {
			picture = getPictureHelper(doubanJson);

			if (title.isEmpty()) {
				title = doubanJson.getString("title");
			}

			if (author.isEmpty()) {
				List<String> authorLists = getAuthorHelper(doubanJson);
				for (String anAuthor : authorLists) {
					author += anAuthor + " ";
				}
			}
			if (publisher.isEmpty()) {
				publisher = doubanJson.getString("publisher");
			}

			if (pubdate.isEmpty()) {
				pubdate = doubanJson.getString("pubdate");
			}

			authorIntro = doubanJson.getString("author_intro");
			summary = doubanJson.getString("summary");
			catalog = doubanJson.getString("catalog");
			pages = doubanJson.getString("pages");
			price = doubanJson.getString("price");


		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private Bitmap getPictureHelper(JSONObject doubanJson) throws SocketTimeoutException {
		String imageLink = "";
		try {
			JSONObject imageJson = doubanJson.getJSONObject("images");
			imageLink = imageJson.getString("large");// 获取大分辨率的图片
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return HttpUtil.getPicture(imageLink);
	}


	private JSONObject getDetailJson(String doubanUrl) throws SocketTimeoutException {
		JSONObject doubanJson = null;
		String doubanHtml = getHtml(doubanUrl);
		try {
			doubanJson = new JSONObject(doubanHtml);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return doubanJson;
	}


	private String getISBN(Document doc) {
		//Document doc = Jsoup.parse(detailHtml);
		String isbn = "";
		Elements elements = doc.getElementsContainingOwnText("价格:CNY");
		if (elements.size() > 0) {
			String targetString = elements.get(0).text();
			int index = targetString.indexOf("价格");
			isbn = targetString.substring(0, index).replaceAll("\\W","");
		}
		return isbn;
	}

	private String getHtml(String link) throws SocketTimeoutException {
		return  HttpUtil.getHtml(link);
	}


	private List<String> getAuthorHelper(JSONObject doubanJson) {
		List<String> authorLists = new ArrayList<String>();
		try {
			JSONArray authorJsonArray = doubanJson.getJSONArray("author");
			for (int i = 0; i < authorJsonArray.length(); i++) {
				authorLists.add(authorJsonArray.get(i).toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return authorLists;
	}

	public boolean isDoubanExisting() {
		return isDoubanExisting;
	}

	public Bitmap getPicture() {
		return picture;
	}


	public String getTitle() {
		return title;
	}


	public String getAuthorIntro() {
		return authorIntro;
	}


	public String getSummary() {
		return summary;
	}


	public String getCatalog() {
		return catalog;
	}


	public String getPages() {
		return pages;
	}


	public String getPrice() {
		return price;
	}


	public String getAuthor() {
		if (author.isEmpty()) {
			return "暂无";
		} else {
			return author;
		}
	}


	public String getPublisher() {
		if (publisher.isEmpty()) {
			return "暂无";
		} else {
			return publisher;
		}

	}


	public String getPubdate() {
		if (pubdate.isEmpty()) {
			return "暂无";
		} else {
			return pubdate;
		}

	}

	public List<LocationInformation> getLocationInfo() {
		return locationLists;
	}

	public String getIsbn() {
		if (isbn.isEmpty()) {
			return "暂无";
		} else {
			return isbn;
		}

	}

}
