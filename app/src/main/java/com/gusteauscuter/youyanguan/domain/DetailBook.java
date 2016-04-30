package com.gusteauscuter.youyanguan.domain;

import java.util.List;

public class DetailBook {
	
//	private String title;
	// ��ͼ�������ҳ��ȡ
	private String searchNum;
	private List<CollectInfo> collectInfo;
//	private String author;
	
	//�Ӷ����ȡ
	private String publisher;
	private String pubdate;
	private String authorIntro;
	private String summary;
	private String catalog; // Ŀ¼
	private String pages;
	private String price;
	private String pictureUrl;
	
	private boolean isDoubanExist; // �ж϶���ͼ���Ƿ����

	public DetailBook() {
		
	}
	
	public DetailBook(String searchNum, List<CollectInfo> collectInfo, String publisher, String pubdate,
			String authorIntro, String summary, String catalog, String pages, String price, String pictureUrl) {
		super();
		this.searchNum = searchNum;
		this.collectInfo = collectInfo;
		this.publisher = publisher;
		this.pubdate = pubdate;
		this.authorIntro = authorIntro;
		this.summary = summary;
		this.catalog = catalog;
		this.pages = pages;
		this.price = price;
		this.pictureUrl = pictureUrl;
	}


	public String getSearchNum() {
		return searchNum;
	}


	public String getPublisher() {
		return publisher;
	}

	public String getPubdate() {
		return pubdate;
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

	public List<CollectInfo> getCollectInfo() {
		return collectInfo;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}


	public boolean isDoubanExist() {
		return isDoubanExist;
	}


	public void setDoubanExist(boolean isDoubanExist) {
		this.isDoubanExist = isDoubanExist;
	}


	public void setSearchNum(String searchNum) {
		this.searchNum = searchNum;
	}


	public void setCollectInfo(List<CollectInfo> collectInfo) {
		this.collectInfo = collectInfo;
	}


	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}


	public void setPubdate(String pubdate) {
		this.pubdate = pubdate;
	}


	public void setAuthorIntro(String authorIntro) {
		this.authorIntro = authorIntro;
	}


	public void setSummary(String summary) {
		this.summary = summary;
	}


	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}


	public void setPages(String pages) {
		this.pages = pages;
	}


	public void setPrice(String price) {
		this.price = price;
	}


	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}
	
	
}
