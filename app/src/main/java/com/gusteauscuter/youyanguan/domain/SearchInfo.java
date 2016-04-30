package com.gusteauscuter.youyanguan.domain;

public class SearchInfo {
	private int pageNum;
	private int bookNum;
	private int bookNumPerPage;
	
	
	public SearchInfo(int pageNum, int bookNum, int bookNumPerPage) {
		super();
		this.pageNum = pageNum;
		this.bookNum = bookNum;
		this.bookNumPerPage = bookNumPerPage;
	}
	
	public int getPageNum() {
		return pageNum;
	}
	public int getBookNum() {
		return bookNum;
	}
	public int getBookNumPerPage() {
		return bookNumPerPage;
	}
	
}
