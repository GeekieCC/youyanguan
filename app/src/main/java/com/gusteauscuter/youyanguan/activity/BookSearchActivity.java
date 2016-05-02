package com.gusteauscuter.youyanguan.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.adapter.BookSearchedAdapter;
import com.gusteauscuter.youyanguan.api.InternetServiceApi;
import com.gusteauscuter.youyanguan.api.InternetServiceApiImpl;
import com.gusteauscuter.youyanguan.common.PublicURI;
import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.domain.BookBase;
import com.gusteauscuter.youyanguan.databaseHelper.BookCollectionDbHelper;
import com.gusteauscuter.youyanguan.domain.JsonUtil;
import com.gusteauscuter.youyanguan.util.NetworkConnectUtil;
import com.gusteauscuter.youyanguan.util.SoftInputUtil;
import com.gusteauscuter.youyanguan.view.ScrollListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class BookSearchActivity extends AppCompatActivity {

    private static final int FIRST_PAGE = 0;
    private static final String SearchBackgroundFileName = PublicURI.PATH_BG_SEARCH;

    private ImageView mSearchBackground;
    private TextView mTotalNumberTextView;
    private EditText mSearchViewEditText;
    private Spinner mSearchTypeSpinner;
    private ProgressBar mProgressBar;
    private ScrollListView mListView;
    private BookSearchedAdapter mAdapter;

    //第一次搜索时初始化这两个变量
    private int mNumOfPages = 0;
    private int mCountOfBooks = 0;
    //带可借信息查询时，一个页面的第几次查询
    private int mCurrentPage;
//    private int mCurrentCount = 0;//当前搜索到的书的数目，用于计算是否所有的图书加载完毕
    // 搜索条件
    private String mBookKeywordToSearch;
    private String mSearchType ="TITLE";
    private static final boolean RESEARCH = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        mSearchViewEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    searchBook(RESEARCH);
                    return true;
                }
                return false;
            }
        });
        findViewById(R.id.searchBookButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBook(RESEARCH);
            }
        });
        mListView.setOnBottomReachedListener(new ScrollListView.OnBottomReachedListener() {
            @Override
            public void onBottomReached() {
                //Toast.makeText(getApplicationContext(),"到底了",Toast.LENGTH_SHORT).show();
                searchBook(!RESEARCH);
            }
        });
    }

    private void initView(){

        setContentView(R.layout.activity_search_book_result);
        // 状态进度条
        mProgressBar=(ProgressBar) findViewById(R.id.progressBarRefreshBookSearched);
        mProgressBar.setVisibility(View.INVISIBLE);
        // 搜索条件
        mSearchViewEditText = (EditText) findViewById(R.id.searchBookEditText);
        mSearchViewEditText.requestFocus();
        mSearchTypeSpinner =(Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.SearchBookType, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSearchTypeSpinner.setAdapter(adapter);
        mSearchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String type = mSearchTypeSpinner.getItemAtPosition(i).toString();
                switch (type) {
                    case "题名":
                        mSearchType = "TITLE";
                        break;
                    case "作者":
                        mSearchType = "AUTHOR";
                        break;
                    case "ISBN":
                        mSearchType = "ISBN";
                        break;
                    case "ISSN":
                        mSearchType = "ISBN.011$a";
                        break;
                    case "出版社":
                        mSearchType = "PUBLISHER";
                        break;
                    case "分类号":
                        mSearchType = "CLASSNO";
                        break;
                    case "主题词":
                        mSearchType = "SUBJECT";
                        break;
                    case "统一刊号":
                        mSearchType = "UNIONNO";
                        break;
                    case "馆藏条码":
                        mSearchType = "BARCODE";
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mSearchType = "TITLE";
            }
        });
        // 背景图片设置
        mSearchBackground=(ImageView) findViewById(R.id.search_background);
        File file = new File(SearchBackgroundFileName);
        if (file.exists()) {
            Bitmap bitmapHeader= BitmapFactory.decodeFile(SearchBackgroundFileName);
            mSearchBackground.setImageBitmap(bitmapHeader);
        }
        // 有效数据
        mTotalNumberTextView =(TextView) findViewById(R.id.totalNumber);
        mListView = (ScrollListView) findViewById(R.id.bookListView);
        mAdapter = new BookSearchedAdapter(this) ;
        mListView.setAdapter(mAdapter);
    }

    private void searchBook(boolean IsReSearch) {

        boolean isConnected = NetworkConnectUtil.isConnected(getApplication());
        if(!isConnected)
            return;
        mBookKeywordToSearch = mSearchViewEditText.getText().toString().replaceAll("\\s", "");
        if(mBookKeywordToSearch.isEmpty()){
            Toast.makeText(getApplicationContext(),"请输入搜索内容！",Toast.LENGTH_SHORT).show();
            return;
        }
        if(IsReSearch){
            mCurrentPage = FIRST_PAGE;
            mTotalNumberTextView.setText("0");
            mAdapter.clearItems();

            SoftInputUtil.hideSoftInput(this, mSearchViewEditText); //收起软键盘
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startSearchBookAsy();
                }
            }, 200); //收起软键盘需要一定时间

        } else {
            mListView.setTriggeredOnce(true); // TODO what's this ?
            startSearchBookAsy();
        }
    }

    private void startSearchBookAsy() {

        SearchBookAsyTask searchBookAsyTask=new SearchBookAsyTask();
        searchBookAsyTask.execute();
    }


    private class SearchBookAsyTask extends AsyncTask<Void, Void, List<BookBase>> {

        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<BookBase> doInBackground(Void... args) {
            List<BookBase> resultBookLists = null;
            try{
                InternetServiceApi internetServiceApi = new InternetServiceApiImpl();
                JSONObject resultJson =  internetServiceApi.SearchBook(mBookKeywordToSearch, mSearchType, mCurrentPage);
                resultBookLists= JsonUtil.getBookList(resultJson);
                if(mCurrentPage==FIRST_PAGE){
                    mCountOfBooks = JsonUtil.getCountOfBooks(resultJson);
                    mNumOfPages = JsonUtil.getNumOfPages(resultJson);
                }

                //对于搜索出来的书，检查其是否已经被收藏到数据库
                BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(getApplicationContext());
                for (BookBase resultBook : resultBookLists) {
                    if(mDbHelper.isCollected(resultBook))
                        resultBook.setIsCollected(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultBookLists;
        }

        @Override
        protected void onPostExecute(List<BookBase> result) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (result == null) {
                Toast.makeText(getApplication(), "图书未搜索到", Toast.LENGTH_SHORT).show();
                return;
            }
            if(mCurrentPage==FIRST_PAGE)
                mTotalNumberTextView.setText(String.valueOf(mCountOfBooks));
            if ( ++mCurrentPage < mNumOfPages) {
                mListView.setTriggeredOnce(false);
            }
            else {
                Toast.makeText(getApplication(), "全部图书加载完毕", Toast.LENGTH_SHORT).show();
                mListView.setTriggeredOnce(true);
            }

            mAdapter.addItems(result);
            mAdapter.notifyDataSetChanged();
        }
    }
}
