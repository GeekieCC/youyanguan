package com.gusteauscuter.youyanguan.login_Client;


import com.gusteauscuter.youyanguan.data_Class.book.Book;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class LibraryClient {

	public static final int CONNECT_TIME_OUT = 6000;
	public static final int READ_TIME_OUT = 6000;

	private static final String MAIN_PAGE_URL = "http://202.38.232.10/opac/servlet/opac.go?cmdACT=mylibrary.login";
	private static final String LOAN_PAGE_URL = "http://202.38.232.10/opac/servlet/opac.go?cmdACT=loan.list";

	private HttpClient httpClient;
	private String bookHtml;
	private boolean isLogined = false;

	private List<Book> bookListsAfterRenew = null;
	private boolean isRenewed = false;

	//登录图书馆
	public boolean login(String username, String pass) throws ConnectTimeoutException, SocketTimeoutException {
		NameValuePair[] nameValuePairs = {
				new NameValuePair("userid", username),
				new NameValuePair("passwd", pass) };
		PostMethod postMethod = new PostMethod(MAIN_PAGE_URL);
		postMethod.setRequestBody(nameValuePairs);
		httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIME_OUT);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(READ_TIME_OUT);
		try {
			httpClient.executeMethod(postMethod);
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			throw new ConnectTimeoutException();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			throw new SocketTimeoutException();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			postMethod.releaseConnection();
		}
		this.getBookHtml();
		isLogined = (bookHtml.contains("个人资料")) ? true : false;
		return isLogined;
	}

	//续借图书
	public boolean renew(Book bookToRenew) {
		
		if (isLogined && (bookToRenew.getBorrowedTime() < bookToRenew.getMaxBorrowTime())) {
			int borrowedTime = bookToRenew.getBorrowedTime();
			int rowNumber = bookToRenew.getRowNumber();
			String renewLink = bookToRenew.getRenewLink();
			GetMethod renewGetMethod = new GetMethod(renewLink);
			try {
				httpClient.executeMethod(renewGetMethod);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				renewGetMethod.releaseConnection();
			}
			this.getBookHtml();
			//isRenewed为false,调用getBooks方法，将重新从bookHtml中解析，返回给bookListsAfterRenew
			bookListsAfterRenew = this.getBooks();
			if (borrowedTime == bookListsAfterRenew.get(rowNumber-1).getBorrowedTime()) {
				isRenewed = false;
			} else {
				isRenewed = true;
			}
		}
		return isRenewed;
	}

	//刷新bookHtml实例变量
	private void getBookHtml() {
		GetMethod getMethod = new GetMethod(LOAN_PAGE_URL);
		BufferedReader br = null;
		try {
			bookHtml = "";
			String readLine = "";
			int responseCode = httpClient.executeMethod(getMethod);
			if (responseCode == HttpStatus.SC_OK) {
				br = new BufferedReader(new InputStreamReader(getMethod.getResponseBodyAsStream(), "utf-8"));
				while ((readLine = br.readLine()) != null) {
					bookHtml += readLine;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			getMethod.releaseConnection();
		}

	}

	//从bookHtml实例变量中解析到借阅图书信息
	public List<Book> getBooks() {
		//如果续借成功，可以直接将bookListAfterRenew返回
		if (isRenewed) {
			return bookListsAfterRenew;
		} else {
			List<Book> bookLists = null;
			if (isLogined) {
				bookLists = new ArrayList<Book>();
				Document doc = Jsoup.parse(bookHtml);
				Elements elements = doc.getElementsByTag("tr");
				elements.remove(0);
				for (Element element : elements) {
					Book book = new Book(element);
					bookLists.add(book);
				}
			}
			return bookLists;
		}
	}
}





















