package com.gusteauscuter.youyanguan.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.api.InternetServiceApi;
import com.gusteauscuter.youyanguan.api.InternetServiceApiImpl;
import com.gusteauscuter.youyanguan.api.JsonUtil;
import com.gusteauscuter.youyanguan.common.PublicString;
import com.gusteauscuter.youyanguan.databaseHelper.BookCollectionDbHelper;
import com.gusteauscuter.youyanguan.databaseHelper.CollectBookAsy;
import com.gusteauscuter.youyanguan.domain.BookDetail;
import com.gusteauscuter.youyanguan.domain.LocationInfo;
import com.gusteauscuter.youyanguan.util.ACacheUtil;
import com.gusteauscuter.youyanguan.util.HttpUtil;
import com.gusteauscuter.youyanguan.util.ScreenShotUtil;
import com.gusteauscuter.youyanguan.util.SharedPreferencesUtil;

import org.json.JSONObject;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {

    private ProgressBar  mProgressBar;

    private ImageView bookPictureImageView;
    private TextView titleTextView;
    private TextView authorTextView;
    private TextView publisherTextView;
    private TextView pubdateTextView;
    private TextView isbnTextView;
    private TextView searchNumHeaderTextView;
    private TableLayout locationTable;

    private TextView bookDetailTextView;
    private MenuItem menuCollection;

    private LinearLayout shareView;
    private String mSharedBookDetailFileName = PublicString.PATH_SHARE_BOOK_DETAIL;

    private int mPosition;
    private boolean isFromBase;
    private BookDetail mBookDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {

        setContentView(R.layout.activity_book_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        mProgressBar=(ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        shareView=(LinearLayout) findViewById(R.id.share_layoutView);

        bookPictureImageView = (ImageView) findViewById(R.id.BookPicture);
        titleTextView = (TextView) findViewById(R.id.title);
        authorTextView = (TextView) findViewById(R.id.author);
        publisherTextView = (TextView) findViewById(R.id.publisher);
        pubdateTextView = (TextView) findViewById(R.id.pubdate);
        isbnTextView = (TextView) findViewById(R.id.isbn);
        searchNumHeaderTextView = (TextView) findViewById(R.id.searchNumHeader);

        locationTable = (TableLayout) findViewById(R.id.table);

        bookDetailTextView = (TextView) findViewById(R.id.bookDetailTextView);
    }

    private  void initData(){
        mBookDetail = new BookDetail();
        Intent intent = this.getIntent();
        mPosition = intent.getIntExtra(PublicString.BUNDLE_Position, 0);
        isFromBase = intent.getBooleanExtra(PublicString.BUNDLE_IsFromBase, false);
        mBookDetail.setBookId(intent.getStringExtra(PublicString.BUNDLE_BookId));
        mBookDetail.setTitle(intent.getStringExtra(PublicString.BUNDLE_Title));
        if(isFromBase){
            mBookDetail.setAuthor(intent.getStringExtra(PublicString.BUNDLE_Author));
            mBookDetail.setPubdate(intent.getStringExtra(PublicString.BUNDLE_Pubdate));
            mBookDetail.setPublisher(intent.getStringExtra(PublicString.BUNDLE_Publisher));
            mBookDetail.setSearchNum(intent.getStringExtra(PublicString.BUNDLE_SearchNum));
            mBookDetail.setIsbn(intent.getStringExtra(PublicString.BUNDLE_Isbn));
        }
        mBookDetail.setIsCollected(intent.getBooleanExtra(PublicString.BUNDLE_IsCollected,false));
        new GetBooksDetailAsy().execute(mBookDetail.getBookId());
    }

    /**
     * 加载actionbar上的收藏图标的异步类加载actionbar上的收藏图标的异步类
     */
    private class CheckBookCollectionTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... args) {
            BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(getApplicationContext());
            return mDbHelper.isCollected(mBookDetail.getBookId());
        }

        @Override
        protected void onPostExecute(Boolean isCollected) {
            mBookDetail.setIsCollected(isCollected);
            menuCollection.setIcon(isCollected ? R.drawable.ic_action_collect_cancle : R.drawable.ic_action_collect);
        }
    }

    private class mCollectBookAsy extends CollectBookAsy {

        public mCollectBookAsy(){
            super(getApplicationContext(),mBookDetail);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mProgressBar.setVisibility(View.INVISIBLE);
            boolean isCollected = mBookDetail.isCollected();
            menuCollection.setIcon(isCollected ? R.drawable.ic_action_collect_cancle : R.drawable.ic_action_collect);
            menuCollection.setTitle(isCollected ? "取消收藏" : "收藏");
            sentResult(isCollected);
            if(!isCollected) {
                BookDetailActivity.this.finish();
            }
        }
    }


    private class GetBooksDetailAsy extends AsyncTask<String, Void, BookDetail> {

        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected BookDetail doInBackground(String... args) {
            try {
                String bookId = args[0];
                InternetServiceApi internetServiceApi = new InternetServiceApiImpl();
                JSONObject resultJson = internetServiceApi.GetBookDetail(bookId);
                BookDetail bookDetail = JsonUtil.getBookDetail(resultJson);
                return bookDetail;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(BookDetail result) {
            mProgressBar.setVisibility(View.INVISIBLE);
            // add result property(part of whole) to mBookdetail
            addPropertyFromResult(result);
            inflateView();
        }
    }

    private void addPropertyFromResult(BookDetail resultDetail){
        // 必然有的信息
        mBookDetail.setLocationInfo(resultDetail.getLocationInfo());
        mBookDetail.setIsDoubanExist(resultDetail.isDoubanExist());
        mBookDetail.setPictureUrl(resultDetail.getPictureUrl());
        // 如果豆瓣有则获取的信息
        if(mBookDetail.isDoubanExist()){
            mBookDetail.setAuthorIntro(resultDetail.getAuthorIntro());
            mBookDetail.setPages(resultDetail.getPages());
            mBookDetail.setCatalog(resultDetail.getCatalog());
            mBookDetail.setPrice(resultDetail.getPrice());
            mBookDetail.setSummary(resultDetail.getSummary());
        }
        // 传进来的信息如果没有，尝试从服务器获取的信息中获得
        if(mBookDetail.getPublisher().isEmpty())
            mBookDetail.setPublisher(resultDetail.getPublisher());
        if (mBookDetail.getPubdate().isEmpty())
            mBookDetail.setPubdate(resultDetail.getPubdate());
        if(mBookDetail.getPublisher().isEmpty())
            mBookDetail.setPublisher(resultDetail.getPublisher());
        if (mBookDetail.getPubdate().isEmpty())
            mBookDetail.setPubdate(resultDetail.getPubdate());
    }

    private void inflateView(){
        inflateTopRight();
        inflateTable();
        inflateBottom();
        new GetBookIconAsy(bookPictureImageView,mBookDetail.getPictureUrl()).execute();
    }

    private class GetBookIconAsy extends AsyncTask<Void, Void ,Bitmap>{
        private ImageView imageView;
        private String url;

        public GetBookIconAsy(ImageView imageView, String url) {
            this.imageView = imageView;
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            if(url==null)
                return null;
            ACacheUtil aCacheUtil = ACacheUtil.get(getApplicationContext());
            String bookId = mBookDetail.getBookId();
            Bitmap bitmap = aCacheUtil.getAsBitmap(bookId);
            if(bitmap!=null)
                return bitmap;
            try {
                bitmap = HttpUtil.getPicture(url);
                if(bitmap!=null) {
                    aCacheUtil.put(bookId,bitmap);
                    return bitmap;
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap!=null)
                imageView.setImageBitmap(bitmap);
            else
                imageView.setImageResource(R.drawable.book_default);
        }

    }


    //将整个详情页分为三大部分，第一部分，图片右侧区域,不包括图片
    private void inflateTopRight() {
        titleTextView.setText("【书名】" + mBookDetail.getTitle());
        searchNumHeaderTextView.setText("【索书号】" + mBookDetail.getSearchNum());
        authorTextView.setText("【作者】" + mBookDetail.getAuthor());
        publisherTextView.setText("【出版社】" + mBookDetail.getPublisher());
        pubdateTextView.setText("【出版日期】" + mBookDetail.getPubdate());
        if(mBookDetail.getIsbn().isEmpty())
            isbnTextView.setVisibility(View.GONE);
        else 
            isbnTextView.setText("【ISBN】" + mBookDetail.getIsbn());
    }

    private TextView createRowTextView(String content, String bgColor) {
        TextView textView = new TextView(getApplicationContext());

        textView.setText(content);
        //文字与边框间距离
        textView.setPadding(13, 13, 13, 13);
        //定义背景颜色，蓝色
        textView.setBackgroundColor(Color.parseColor(bgColor));
        //定义字体颜色,黑色
        int textColor = Color.parseColor("#000000");
        textView.setTextColor(textColor);
        //定义透明度
        textView.setAlpha((float) 0.87);
        //居中显示
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        return textView;
    }

    //将整个详情页分为三大部分，第二部分，馆藏信息
    private void inflateTable() {

        List<LocationInfo> locationInfoLists = mBookDetail.getLocationInfo();

        String headerColor = "#2196F3"; // 表头颜色
        String tableColor = "#BBDEFB"; // 表格颜色

        TextView locationHeader = createRowTextView("馆址", headerColor);
        TextView detailLocationHeader = createRowTextView("馆藏地", headerColor);
        TextView statusHeader = createRowTextView("状态", headerColor);

        TableRow headerRow = new TableRow(getApplicationContext());
        headerRow.addView(detailLocationHeader);
        headerRow.addView(locationHeader);
        headerRow.addView(statusHeader);

        locationTable.addView(headerRow);
        for (LocationInfo locationInfo : locationInfoLists) {
            TableRow tr = new TableRow(getApplicationContext());
            tr.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            TextView location = createRowTextView(locationInfo.getLocation(),tableColor);
            TextView detailLocation = createRowTextView(locationInfo.getDetailLocation(),tableColor);
            TextView status = createRowTextView(locationInfo.getStatus(),tableColor);

            tr.addView(location);
            tr.addView(detailLocation);
            tr.addView(status);

            locationTable.addView(tr);
        }

        if(locationInfoLists.isEmpty()){
            TextView emptyInfor = createRowTextView("本书已全部暂停外借",tableColor);
            locationTable.addView(emptyInfor);
        }
    }

    //将整个详情页分为三大部分，第三部分，从豆瓣获取的详细信息
    private void inflateBottom() {
        if(!mBookDetail.isDoubanExist())
            return;
        String authorIntro = mBookDetail.getAuthorIntro();
//        if(!authorIntro.isEmpty())
            bookDetailTextView.append("\n【作者简介】" + authorIntro);
        String summary = mBookDetail.getSummary();
//        if(!summary.isEmpty())
            bookDetailTextView.append("\n【内容简介】" + summary);
        String catalog = mBookDetail.getCatalog();
//        if(!catalog.isEmpty())
            bookDetailTextView.append("\n【目录】" + catalog);
        String pages = mBookDetail.getPages();
//        if(!pages.isEmpty())
            bookDetailTextView.append("\n【页数】" + pages);
        String price = mBookDetail.getPrice();
//        if(!price.isEmpty())
            bookDetailTextView.append("\n【价格】" + price);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
        menuCollection = menu.findItem(R.id.action_collection);
        if(mBookDetail.isCollected())
            menuCollection.setIcon(R.drawable.ic_action_collect_cancle);
        else
            new CheckBookCollectionTask().execute();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                BookDetailActivity.this.finish();
                break;
            case R.id.action_collection:
                new mCollectBookAsy().execute();
                break;
            case R.id.action_share:
                shareBook();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareBook(){
        ScreenShotUtil.shoot(mSharedBookDetailFileName, shareView);
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TITLE, "Share");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
        intent.putExtra(Intent.EXTRA_TEXT, "I want to share a wonderful book through YouYanGuan");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(mSharedBookDetailFileName)));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "Share"));
    }


    private void sentResult(boolean isCollected) {
        // 如果是从搜索或是收藏中传递进来的，则将信息传回
        if(isFromBase) {
            SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getApplicationContext());
            sharedPreferencesUtil.setCollectAction(true);
            sharedPreferencesUtil.setCollectPosition(mPosition);
            sharedPreferencesUtil.setCollectResult(isCollected);
        }
    }
}
