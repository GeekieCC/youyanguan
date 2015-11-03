package com.gusteauscuter.youyanguan.DepActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.data_Class.book.BookSearchEngine;
import com.gusteauscuter.youyanguan.data_Class.book.ResultBook;
import com.gusteauscuter.youyanguan.data_Class.bookdatabase.BookCollectionDbHelper;
import com.gusteauscuter.youyanguan.internet.connectivity.NetworkConnectivity;
import com.gusteauscuter.youyanguan.view.ScrollListView;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private static final int NUM_OF_BOOKS_PER_SEARCH = 5; // 带可借信息查询时，一次加载的图书数目
    private static final int FIRST_PAGE = 1;

    private List<ResultBook> mSearchBookList;

    //控件
    private ScrollListView mListView;
    private ProgressBar mProgressBar;

    private SearchBookAdapter mAdapter;
    //第一次搜索时初始化这两个变量
    private int numOfPages = 0;
    private int numOfBooks = 0;
    //带可借信息查询时，一个页面的第几次查询
    private int ithSearch = 1;
    private int page;
    
    //// TOD: 2015/10/9 从searchBookFragment传一个整形的常量给searchSN
    private int searchSN = BookSearchEngine.NORTH_CAMPUS; // 搜索南北两校为0，搜索北校为1，搜索南校

    private TextView mTotalNumber;
    private int currentCount = 0;//当前搜索到的书的数目


    private SearchView searchBookEditText;
    private Spinner searchBookTypeSpinner;
    private CheckBox mSouthCheckBox;
    private CheckBox mNorthCheckBox;

    private String bookToSearch;
    private String searchBookType="TITLE";
    private boolean isAllowedToBorrow;
    private boolean reSearch=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        setSupportActionBar((Toolbar) findViewById(R.id.id_toolbar));
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mProgressBar=(ProgressBar) findViewById(R.id.progressBarRefreshBookSearched);
        mProgressBar.setVisibility(View.INVISIBLE);
        mTotalNumber=(TextView) findViewById(R.id.totalNumber);
        mListView = (ScrollListView) findViewById(R.id.bookListView);

        searchBookTypeSpinner=(Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.SearchBookType, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchBookTypeSpinner.setAdapter(adapter);
        searchBookTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String type = searchBookTypeSpinner.getItemAtPosition(i).toString();
                switch (type) {
                    case "标题":
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

        mSouthCheckBox=(CheckBox)findViewById(R.id.SouthCheckBox);
        mNorthCheckBox=(CheckBox)findViewById(R.id.NorthCheckBox);
        initSearchCondition();

        searchBookEditText = (SearchView) findViewById(R.id.searchBookEditText);
        searchBookEditText.setSubmitButtonEnabled(true);
        searchBookEditText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                reSearch=true;
                searchBook();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mSearchBookList=new ArrayList<>();
        mAdapter = new SearchBookAdapter() ;
        mListView.setAdapter(mAdapter);

        mListView.setOnBottomReachedListener(new ScrollListView.OnBottomReachedListener() {
            @Override
            public void onBottomReached() {
                //Toast.makeText(getApplicationContext(),"到底了",Toast.LENGTH_SHORT).show();
                reSearch=false;
                searchBook();
            }
        });
    }

    public void initSearchCondition(){

        SharedPreferences shareData = getApplication().getSharedPreferences("data", 0);
        Boolean SouthChecked = shareData.getBoolean("South", false);
        Boolean NorthChecked = shareData.getBoolean("North", true);
        mSouthCheckBox.setChecked(SouthChecked);
        mNorthCheckBox.setChecked(NorthChecked);

    }

    public void saveSearchCondition() {
        SharedPreferences.Editor shareData =getApplication().getSharedPreferences("data", 0).edit();
        shareData.putBoolean("South",mSouthCheckBox.isChecked());
        shareData.putBoolean("North",mNorthCheckBox.isChecked());
        shareData.commit();

        //searchBookType;
        isAllowedToBorrow = true;
        if (mNorthCheckBox.isChecked()&&mSouthCheckBox.isChecked()){
            searchSN= BookSearchEngine.BOTH_CAMPUS;
        }else if(mNorthCheckBox.isChecked()){
            searchSN=BookSearchEngine.NORTH_CAMPUS;
        }else if(mSouthCheckBox.isChecked()){
            searchSN= BookSearchEngine.SOUTH_CAMPUS;
        }else{
            isAllowedToBorrow = false;
        }
    }

    private void searchBook() {

        if(reSearch){
            page = FIRST_PAGE;
            mTotalNumber.setText("0");
            mSearchBookList=new ArrayList<>();
            mAdapter.notifyDataSetChanged();
            saveSearchCondition();
            bookToSearch = searchBookEditText.getQuery().toString();
        }

        boolean isConnected = NetworkConnectivity.isConnected(getApplication());
        if(isConnected){
            mListView.setTriggeredOnce(true);
            SearchBookAsyTask searchBookAsyTask=new SearchBookAsyTask();
            searchBookAsyTask.execute(bookToSearch, searchBookType);

        }else{
            mListView.setTriggeredOnce(false);
            Toast.makeText(getApplication(),
                    R.string.internet_not_connected, Toast.LENGTH_SHORT).show();
        }
    }

    private class SearchBookAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public SearchBookAdapter(){
            this.mInflater = LayoutInflater.from(getApplication());
        }

        @Override
        public int getCount() {
            return mSearchBookList.size();
        }

        @Override
        public Object getItem(int position) {
            return mSearchBookList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mSearchBookList.get(position).hashCode();
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup container) {

            final ViewHolder mHolder;
            final ResultBook mResultBook = mSearchBookList.get(position);

            if (convertView == null) {

                convertView=mInflater.inflate(R.layout.card_search_book, container, false);

                mHolder =new ViewHolder();
                mHolder.mBookPicture=(ImageView) convertView.findViewById(R.id.searchBookPicture);
                mHolder.mTitle=((TextView) convertView.findViewById(R.id.searchBook_Title));
                mHolder.mPublisher=((TextView) convertView.findViewById(R.id.searchBook_Publisher));
                mHolder.mPubdate=((TextView) convertView.findViewById(R.id.searchBook_Pubdate));
                mHolder.mBookId=((TextView) convertView.findViewById(R.id.searchBook_BookId));
                mHolder.mAuthor=((TextView) convertView.findViewById(R.id.searchBook_Author));
                mHolder.mButton = (Button) convertView.findViewById(R.id.collect_book);

                convertView.setTag(mHolder);

            } else{
                mHolder=(ViewHolder) convertView.getTag();
            }


            if (isAllowedToBorrow) {
                boolean isBorrowable = mResultBook.isBorrowable();
                if (isBorrowable) {
                    mHolder.mBookPicture.setImageResource(R.drawable.book_sample_blue);
                } else {
                    mHolder.mBookPicture.setImageResource(R.drawable.book_sample_pencil);
                }
            }

            // TO 设置Book对应属性
            String title="【" + (position + 1) + "】"+mResultBook.getTitle();
            String publisher="出版社："+mResultBook.getPublisher();
            String pubdate="出版日期："+mResultBook.getPubdate();
            String bookId="索书号："+mResultBook.getSearchNum();
            String author="作者："+mResultBook.getAuthor();

            mHolder.mTitle.setText(title);
            mHolder.mBookId.setText(bookId);
            mHolder.mAuthor.setText(author);
            mHolder.mPublisher.setText(publisher);
            mHolder.mPubdate.setText(pubdate);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    boolean isConnected = NetworkConnectivity.isConnected(getApplicationContext());
                    if (isConnected) {
                        Intent intent = new Intent(getApplication(), BookDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("bookToShowDetail", mResultBook);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.internet_not_connected, Toast.LENGTH_SHORT)
                                .show();
                    }

                }
            });

            // 对搜索出来的结果显示时，区别已收藏和未收藏图书
            if (!mResultBook.isCollected()) {
                mHolder.mButton.setText("点击收藏");
            } else {
                mHolder.mButton.setText("取消收藏");
            }
            // 收藏和取消收藏的动作监听
            mHolder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getApplicationContext(), "收藏", Toast.LENGTH_SHORT).show();

                    CrudTask crudTask = new CrudTask();
                    crudTask.execute(mResultBook);

                }
            });

//            mHolder.mBookPicture.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Intent intent = new Intent(getApplication(), BookDetailActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("bookToShowDetail", mSearchBookList.get(position));
//                    bundle.putSerializable("type","bookSearched");
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                }
//            });

            return convertView;
        }

        public final class ViewHolder{
            public ImageView mBookPicture;
            public TextView mTitle;
            public TextView mPublisher;
            public TextView mPubdate;
            public TextView mBookId;
            public TextView mAuthor;
            public Button mButton;
        }
    }

    private class SearchBookAsyTask extends AsyncTask<String, Void, List<ResultBook>> {

        private boolean serverOK = true; //处理服务器异常

        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected List<ResultBook> doInBackground(String... args) {
            List<ResultBook> resultBookLists = null;
            if ((page == 1 && ithSearch == 1) || (currentCount != numOfBooks)) {
                try {
                    BookSearchEngine engine = new BookSearchEngine();
                    engine.searchBook(args[0], args[1], page);
                    if (page == 1) {
                        numOfBooks = engine.getNumOfBooks();
                        numOfPages = engine.getNumOfPages();
                    }

                    if (isAllowedToBorrow) {
                        int numOfSearchesOnThisPage = engine.getNumOfSearchesOnThisPage(page, NUM_OF_BOOKS_PER_SEARCH);
                        if (page <= numOfPages) {
                            resultBookLists = engine.getBooksOnPageWithBorrowInfo(page, NUM_OF_BOOKS_PER_SEARCH, ithSearch, searchSN);
                            if (resultBookLists != null) {
                                if (ithSearch >= numOfSearchesOnThisPage) {
                                    ithSearch = 1;
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
                    List<ResultBook> bookCollections = mDbHelper.getAllBookCollections();
                    for (ResultBook resultBook : resultBookLists) {
                        for (ResultBook bookCollected : bookCollections) {
                            if (resultBook.getBookId().equals(bookCollected.getBookId())) {
                                resultBook.setIsCollected(true);
                            }
                        }
                    }

                } catch (SocketTimeoutException e) {
                    serverOK = false;
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
                mTotalNumber.setText(String.valueOf(numOfBooks));
                if (numOfBooks == 0) {
                    Toast.makeText(getApplication(), "图书未搜索到", Toast.LENGTH_SHORT).show();
                }else {
                    if (result != null) {
                        mSearchBookList.addAll(result);
                        mAdapter.notifyDataSetChanged();
                        mListView.setTriggeredOnce(false);
                        currentCount = mListView.getCount();
                    } else if (currentCount >= numOfBooks) {
                        Toast.makeText(getApplication(), "全部图书加载完毕", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplication(), R.string.server_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
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

    //TODO 收藏图书的增删改查异步类
    private class CrudTask extends AsyncTask<ResultBook, Void, Boolean> {
        private boolean operation;// 操作为添加时，为true;操作为删除时，为false
        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(ResultBook... resultBooks) {
            //操作成功与否
            boolean result = false;
            BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(getApplicationContext());
            if (!resultBooks[0].isCollected()) {
                operation = true;
                if (mDbHelper.addResultBook(resultBooks[0]) != -1) {
                    resultBooks[0].setIsCollected(true);
                    result = true;
                }
            } else {
                operation = false;
                if (mDbHelper.deleteResultBook(resultBooks[0]) != 0) {
                    resultBooks[0].setIsCollected(false);
                    result = true;
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (operation) {
                if (result) {
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplication(), "添加成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplication(), "添加失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (result) {
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplication(), "删除成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplication(), "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
            super.onPostExecute(result);
        }


    }
}
