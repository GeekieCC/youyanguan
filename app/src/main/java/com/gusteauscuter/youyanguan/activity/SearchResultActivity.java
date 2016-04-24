package com.gusteauscuter.youyanguan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.adapter.SearchBookAdapter;
import com.gusteauscuter.youyanguan.commonUrl.IPublicUrl;
import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.data_Class.book.BookSearchEngine;
import com.gusteauscuter.youyanguan.data_Class.book.ResultBook;
import com.gusteauscuter.youyanguan.data_Class.book.SimpleBaseBook;
import com.gusteauscuter.youyanguan.data_Class.bookdatabase.BookCollectionDbHelper;
import com.gusteauscuter.youyanguan.exception.WrongPageException;
import com.gusteauscuter.youyanguan.util.NetworkConnectUtil;
import com.gusteauscuter.youyanguan.util.SoftInputUtil;
import com.gusteauscuter.youyanguan.view.ScrollListView;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity implements IPublicUrl {

    private static final int NUM_OF_BOOKS_PER_SEARCH = 5; // 带可借信息查询时，一次加载的图书数目
    private static final int FIRST_PAGE = 1;
    private static final int FIRST_SEARCH = 1;

    private ImageView mSearchBackground;

    //控件
    private ScrollListView mListView;
    private ProgressBar mProgressBar;

    private SearchBookAdapter mAdapter;
    //第一次搜索时初始化这两个变量
    private int numOfPages = 0;
    private int numOfBooks = 0;
    //带可借信息查询时，一个页面的第几次查询
    private int ithSearch = FIRST_SEARCH;
    private int page;
    
    //// TOD: 2015/10/9 从searchBookFragment传一个整形的常量给searchSN
    //private int searchSN = BookSearchEngine.NORTH_CAMPUS; // 搜索南北两校为0，搜索北校为1，搜索南校

    private TextView mTotalNumber;
    private int currentCount = 0;//当前搜索到的书的数目，用于计算是否所有的图书加载完毕


    private EditText mSearchView;
    private Spinner searchBookTypeSpinner;
    private CheckBox borrowConditionCheckBox;

    private String bookToSearch;
    private String searchBookType="TITLE";
    private boolean checkBorrowCondition = false; //是否检查可借状况
    private boolean reSearch=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book_result);

        mProgressBar=(ProgressBar) findViewById(R.id.progressBarRefreshBookSearched);
        mProgressBar.setVisibility(View.INVISIBLE);
        mTotalNumber=(TextView) findViewById(R.id.totalNumber);
        mListView = (ScrollListView) findViewById(R.id.bookListView);

        searchBookTypeSpinner=(Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.SearchBookType, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        searchBookTypeSpinner.setAdapter(adapter);
        searchBookTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String type = searchBookTypeSpinner.getItemAtPosition(i).toString();
                switch (type) {
                    case "题名":
                        searchBookType = "TITLE";
                        break;
                    case "作者":
                        searchBookType = "AUTHOR";
                        break;
                    case "ISBN":
                        searchBookType = "ISBN";
                        break;
                    case "ISSN":
                        searchBookType = "ISBN.011$a";
                        break;
                    case "出版社":
                        searchBookType = "PUBLISHER";
                        break;
                    case "分类号":
                        searchBookType = "CLASSNO";
                        break;
                    case "主题词":
                        searchBookType = "SUBJECT";
                        break;
                    case "统一刊号":
                        searchBookType = "UNIONNO";
                        break;
                    case "馆藏条码":
                        searchBookType = "BARCODE";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        borrowConditionCheckBox = (CheckBox) findViewById(R.id.borrowConditionCheckBox);

        mSearchView = (EditText) findViewById(R.id.searchBookEditText);
        mSearchView.requestFocus();

        initCustomSearchCondition();
        mSearchView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    reSearch = true;
                    searchBook();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.searchBookButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reSearch = true;
                searchBook();

            }
        });

        mSearchBackground=(ImageView) findViewById(R.id.search_background);
        File file = new File(stringSearchBackgroundName);
        if (file.exists()) {
            Bitmap bitmapHeader= BitmapFactory.decodeFile(stringSearchBackgroundName);
            mSearchBackground.setImageBitmap(bitmapHeader);
        }

        mAdapter = new SearchBookAdapter(this) ;
        mListView.setAdapter(mAdapter);

        mListView.setOnBottomReachedListener(new ScrollListView.OnBottomReachedListener() {
            @Override
            public void onBottomReached() {
                //Toast.makeText(getApplicationContext(),"到底了",Toast.LENGTH_SHORT).show();
                reSearch = false;
                searchBook();
            }
        });

//        SoftInputUtil.toggleSoftInput(this);

    }


    private void initCustomSearchCondition(){
        SharedPreferences shareData = getApplication().getSharedPreferences("data", 0);
        boolean borrowCondition = shareData.getBoolean("borrowCondition", true);
        borrowConditionCheckBox.setChecked(borrowCondition);
    }

    private void saveCustomSearchCondition() {
        SharedPreferences.Editor shareData =getApplication().getSharedPreferences("data", 0).edit();
        shareData.putBoolean("borrowCondition",borrowConditionCheckBox.isChecked());
        shareData.commit();
    }

    private void searchBook() {

        if(reSearch){
            ithSearch = FIRST_SEARCH;
            page = FIRST_PAGE;
            currentCount = 0;
            mTotalNumber.setText("0");
            mAdapter.clearItems();

            saveCustomSearchCondition();
            checkBorrowCondition=borrowConditionCheckBox.isChecked();

            bookToSearch = mSearchView.getText().toString().replaceAll("\\s", "");
            if(bookToSearch==""){
                Toast.makeText(getApplicationContext(),"请输入搜索内容！",Toast.LENGTH_SHORT).show();
                return;
            }

            SoftInputUtil.hideSoftInput(this, mSearchView); //收起软键盘
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    searchBookHelper();
                }
            }, 200); //收起软键盘需要一定时间

        } else {
            searchBookHelper();
        }
    }

    private void searchBookHelper() {
        boolean isConnected = NetworkConnectUtil.isConnected(getApplication());
        if(isConnected){
            mListView.setTriggeredOnce(true);
            SearchBookAsyTask searchBookAsyTask=new SearchBookAsyTask();
            searchBookAsyTask.execute(bookToSearch, searchBookType);
        }else{
            mListView.setTriggeredOnce(false);
        }
    }


    private class SearchBookAsyTask extends AsyncTask<String, Void, List<ResultBook>> {

        private boolean serverOK = true; //处理服务器异常
        private boolean pageOK = true; //处理缺页异常

        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected List<ResultBook> doInBackground(String... args) {
            List<ResultBook> resultBookLists = null;
            if ((page == FIRST_PAGE && ithSearch == FIRST_SEARCH) || (currentCount != numOfBooks)) {
                try {
                    BookSearchEngine engine = new BookSearchEngine();
                    engine.searchBook(args[0], args[1], page);
                    if (page == FIRST_PAGE && ithSearch == FIRST_SEARCH) {
                        numOfBooks = engine.getNumOfBooks();
                        numOfPages = engine.getNumOfPages();
                    }

                    if (checkBorrowCondition) {
                        int numOfSearchesOnThisPage = engine.getNumOfSearchesOnThisPage(page, NUM_OF_BOOKS_PER_SEARCH);
                        if (page <= numOfPages) {
                            resultBookLists = engine.getBooksOnPageWithBorrowInfo(page, NUM_OF_BOOKS_PER_SEARCH, ithSearch);
                            if (resultBookLists != null) {
                                if (ithSearch >= numOfSearchesOnThisPage) {
                                    ithSearch = FIRST_SEARCH;
                                    page++;
                                } else {
                                    ithSearch++;
                                }
                            }
                        }

                    } else {
                        resultBookLists = engine.getBooksOnPage(page);
                        if (resultBookLists != null && page <= numOfPages) {
                            page++;
                        }
                    }

                    //对于搜索出来的书，检查其是否已经被收藏到数据库
                    BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(getApplicationContext());
                    List<SimpleBaseBook> bookCollections = mDbHelper.getAllBookCollections();
                    for (ResultBook resultBook : resultBookLists) {
                        for (SimpleBaseBook bookCollected : bookCollections) {
                            if (resultBook.getBookId().equals(bookCollected.getBookId())) {
                                resultBook.setIsCollected(true);
                            }
                        }
                    }

                } catch (SocketTimeoutException e) {
                    serverOK = false;
                } catch (WrongPageException e) {
                    pageOK = false;
                } catch (Exception e) {
                    //serverOK = false;
                    e.printStackTrace();
                }
            }
            return resultBookLists;
        }

        @Override
        protected void onPostExecute(List<ResultBook> result) {
            mProgressBar.setVisibility(View.INVISIBLE);

            if (serverOK) {
                if (pageOK) {
                    mTotalNumber.setText(String.valueOf(numOfBooks));
                    if (numOfBooks == 0) {
                        Toast.makeText(getApplication(), "图书未搜索到", Toast.LENGTH_SHORT).show();
                    }else {
                        if (result != null) {
                            mAdapter.addItems(result);
                            mAdapter.notifyDataSetChanged();
                            mListView.setTriggeredOnce(false);
                            currentCount = mListView.getCount();
                        } else if (currentCount >= numOfBooks) {
                            Toast.makeText(getApplication(), "全部图书加载完毕", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplication(), "未知异常", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    page++;
                    ithSearch = FIRST_SEARCH;
                    mListView.setTriggeredOnce(false);
                    Toast.makeText(getApplication(), "缺页异常", Toast.LENGTH_SHORT).show();
                }

            } else {
                mListView.setTriggeredOnce(false);
                Toast.makeText(getApplication(), R.string.server_failed, Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
