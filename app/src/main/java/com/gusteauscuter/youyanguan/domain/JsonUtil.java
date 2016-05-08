package com.gusteauscuter.youyanguan.domain;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Z on 2016/5/2 0002.
 */
public class JsonUtil {

    public static List<BookBorrowed> getMyBookBorrowed(JSONObject resultJson){
        List<BookBorrowed> bookBorrowedList = null;
        try {
            JSONArray jsonArray =  resultJson.getJSONArray("bookList");
//            JSONArray jsonArray = new JSONArray(resultJson.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonBookBorrowed = (JSONObject) jsonArray.get(i);
                String  title= jsonBookBorrowed.getString("title");
                String  author= jsonBookBorrowed.getString("author");
                String publisher = "";
                String isbn = "";
                String pubdate = "";
                String searchNum = "";
                String  bookId= jsonBookBorrowed.getString("bookId");
                String  barcode= jsonBookBorrowed.getString("barcode");
                String  borrowDay= jsonBookBorrowed.getString("borrowDay");
                String  returnDay= jsonBookBorrowed.getString("returnDay");
                int  borrowedTime= jsonBookBorrowed.getInt("borrowedTime");
                int  maxBorrowTime= jsonBookBorrowed.getInt("maxBorrowTime");
                boolean  expired = jsonBookBorrowed.getBoolean("expired");
                String  renewLink= jsonBookBorrowed.getString("renewLink");
                String  detailLink= jsonBookBorrowed.getString("detailLink");
//                String  volume= jsonBookBorrowed.getString("volume");
//                String  libraryLocation= jsonBookBorrowed.getString("libraryLocation");
//                String  libraryName = jsonBookBorrowed.getString("libraryName");
                BookBorrowed bookBorrowed = new BookBorrowed(title, author,publisher,isbn,pubdate,
                        searchNum, bookId, barcode,borrowDay,returnDay,borrowedTime,
                        maxBorrowTime, expired, renewLink, detailLink);
                if(bookBorrowedList == null)
                    bookBorrowedList=new ArrayList<>();
                bookBorrowedList.add(bookBorrowed);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return bookBorrowedList;
    }

    public static int getCountOfBooks(JSONObject resultJson){
        int count=0;
        try {
            count=resultJson.getJSONObject("searchInfo").getInt("bookNum");
        }catch (Exception e){
            e.printStackTrace();
        }
        return count;
    }

    public static int getNumOfPages(JSONObject resultJson){
        int count=0;
        try {
            count=resultJson.getJSONObject("searchInfo").getInt("pageNum");
        }catch (Exception e){
            e.printStackTrace();
        }
        return count;
    }


    public static List<BookBase> getBookList(JSONObject resultJson){
        List<BookBase> bookList=null;
        try {
            JSONArray jsonArray = resultJson.getJSONArray("bookList");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonBookBase = (JSONObject) jsonArray.get(i);
                String author = jsonBookBase.getString("author");
                String title = jsonBookBase.getString("title");
                String searchNum = jsonBookBase.getString("searchNum");
                String pubdate = jsonBookBase.getString("pubdate");
                String isbn = jsonBookBase.getString("isbn");
                String bookId = jsonBookBase.getString("bookId");
                String publisher = jsonBookBase.getString("publisher");
                BookBase bookBase=new BookBase(title, author, publisher, isbn, pubdate,searchNum, bookId);
                if(bookList == null)
                    bookList=new ArrayList<>();
                bookList.add(bookBase);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return bookList;
    }


    public static DetailsOfBook getBookDetatl(JSONObject jsonBookDetail){
        DetailsOfBook detailsOfBook =new DetailsOfBook();
        try {
            detailsOfBook.setLocationInfo(getLocationList(jsonBookDetail.getJSONArray("collectInfo")));
            if(jsonBookDetail.getBoolean("doubanExist")) {
                detailsOfBook.setSummary(jsonBookDetail.getString("summary"));
                detailsOfBook.setDoubanExist(jsonBookDetail.getBoolean("doubanExist"));
                detailsOfBook.setPrice(jsonBookDetail.getString("price"));
                detailsOfBook.setPictureUrl(jsonBookDetail.getString("pictureUrl"));
                detailsOfBook.setCatalog(jsonBookDetail.getString("catalog"));
                detailsOfBook.setPages(jsonBookDetail.getString("pages"));
                detailsOfBook.setAuthorIntro(jsonBookDetail.getString("authorIntro"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return detailsOfBook;
    }

    public static List<LocationInfo> getLocationList(@Nullable JSONArray jsonLocationList){
        List<LocationInfo> locationInfoList=new ArrayList<>();
        try {
            for (int i = 0; i < jsonLocationList.length(); i++) {
                JSONObject jsonLocaiton = jsonLocationList.getJSONObject(i);
                LocationInfo locaiton =new LocationInfo(jsonLocaiton.getString("location"),
                        jsonLocaiton.getString("detailLocation"),jsonLocaiton.getString("status"));
                locationInfoList.add(locaiton);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return locationInfoList;
    }
}
