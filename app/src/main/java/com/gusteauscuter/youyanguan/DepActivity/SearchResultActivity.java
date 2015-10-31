package com.gusteauscuter.youyanguan.DepActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.data_Class.book.BookSearchEngine;
import com.gusteauscuter.youyanguan.data_Class.book.ResultBook;
import com.gusteauscuter.youyanguan.internet.connectivity.NetworkConnectivity;
import com.gusteauscuter.youyanguan.view.ScrollListView;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private static final int NUM_OF_BOOKS_PER_SEARCH = 5; // 带可借信息查询时，一次加载的图书数目
    private static final int FIRST_PAGE = 1;

    private List<ResultBook> mSearchBookList;
    //传入参数
    private String bookToSearch;
    private String searchBookType;
    private boolean isAllowedToBorrow;
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
    private int searchSN = BookSearchEngine.NORTH_CAMPUS; // 搜索南北两校为0，搜索北校为1，搜索南校为2

    private TextView mTotalNumber;

//    private boolean isConnected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initData();
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_search_result);

        setSupportActionBar((Toolbar) findViewById(R.id.id_toolbar));
        final ActionBar ab = getSupportActionBar();
//        ab.setHomeAsUpIndicator(R.drawable.ic_back);
        ab.setDisplayHomeAsUpEnabled(true);


        mProgressBar=(ProgressBar) findViewById(R.id.progressBarRefreshBookSearched);
        mProgressBar.setVisibility(View.INVISIBLE);
        mTotalNumber=(TextView) findViewById(R.id.totalNumber);
        mListView = (ScrollListView) findViewById(R.id.bookListView);

        initData();
        mListView.setOnBottomReachedListener(new ScrollListView.OnBottomReachedListener() {
            @Override
            public void onBottomReached() {
                //Toast.makeText(getApplicationContext(),"到底了",Toast.LENGTH_SHORT).show();
                SearchBook();

            }
        });
    }

    private  void initData(){
        Intent intent = this.getIntent();
        bookToSearch=(String)intent.getSerializableExtra("bookToSearch");
        searchBookType=(String)intent.getSerializableExtra("searchBookType");
        isAllowedToBorrow=(boolean) intent.getSerializableExtra("isAllowedToBorrow");
        searchSN=(int) intent.getSerializableExtra("searchSN");
        //// TOD: 2015/10/9 在这里从intent中获取一个整型参数，赋值给searchSN

        mSearchBookList=new ArrayList<>();
        mAdapter = new SearchBookAdapter() ;
        mListView.setAdapter(mAdapter);

        page = FIRST_PAGE;

        SearchBook();
    }

    public void SearchBook(){

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
        public View getView(final int position, View convertView, ViewGroup container) {

            ViewHolder mHolder=null;

            if (convertView == null) {

                convertView=mInflater.inflate(R.layout.card_search_book, container, false);

                mHolder =new ViewHolder();
                mHolder.mBookPicture=(ImageView) convertView.findViewById(R.id.searchBookPicture);
                mHolder.mTitle=((TextView) convertView.findViewById(R.id.searchBook_Title));
                mHolder.mPublisher=((TextView) convertView.findViewById(R.id.searchBook_Publisher));
                mHolder.mPubdate=((TextView) convertView.findViewById(R.id.searchBook_Pubdate));
                mHolder.mBookId=((TextView) convertView.findViewById(R.id.searchBook_BookId));
                mHolder.mAuthor=((TextView) convertView.findViewById(R.id.searchBook_Author));

                convertView.setTag(mHolder);

            } else{
                mHolder=(ViewHolder) convertView.getTag();
            }


            if (isAllowedToBorrow) {
                boolean isBorrowable = mSearchBookList.get(position).isBorrowable();
                if (isBorrowable) {
                    mHolder.mBookPicture.setImageResource(R.drawable.book_sample_blue);
                } else {
                    mHolder.mBookPicture.setImageResource(R.drawable.book_sample_pencil);
                }
            }

            // TO 设置Book对应属性
            String title="【" + (position + 1) + "】"+mSearchBookList.get(position).getTitle();
            String publisher="出版社："+mSearchBookList.get(position).getPublisher();
            String pubdate="出版日期："+mSearchBookList.get(position).getPubdate();
            String bookId="索书号："+mSearchBookList.get(position).getSearchNum();
            String author="作者："+mSearchBookList.get(position).getAuthor();

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
                        bundle.putSerializable("bookToShowDetail", mSearchBookList.get(position));
//                        bundle.putSerializable("type","bookSearched");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.internet_not_connected, Toast.LENGTH_SHORT)
                                .show();
                    }

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
                    if (page <= numOfPages) page++;
                }
            } catch (SocketTimeoutException e) {
                serverOK = false;
            } catch (Exception e) {
                //serverOK = false;
                e.printStackTrace();
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

                    } else if (page >= numOfPages) {
                        Toast.makeText(getApplication(), "全部图书加载完毕", Toast.LENGTH_SHORT).show();
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


}
