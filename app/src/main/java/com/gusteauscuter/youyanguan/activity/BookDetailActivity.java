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
import android.widget.Toast;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.api.InternetServiceApi;
import com.gusteauscuter.youyanguan.api.InternetServiceApiImpl;
import com.gusteauscuter.youyanguan.databaseHelper.BookCollectionDbHelper;
import com.gusteauscuter.youyanguan.common.PublicURI;
import com.gusteauscuter.youyanguan.domain.BookBase;
import com.gusteauscuter.youyanguan.domain.BookDetail;
import com.gusteauscuter.youyanguan.domain.JsonUtil;
import com.gusteauscuter.youyanguan.domain.LocationInfo;
import com.gusteauscuter.youyanguan.util.ACacheUtil;
import com.gusteauscuter.youyanguan.util.ScreenShotUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {

    public static final int PICTURE_RESULT_CODE = 1;
    public static final int COLLECT_RESULT_CODE = 2;

    private ProgressBar  mProgressBar;

    private ImageView bookPictureImageView;
    private TextView titleTextView;
    private TextView authorTextView;
    private TextView publisherTextView;
    private TextView pubdateTextView;
    private TextView isbnTextView;
    private TextView searchNumHeaderTextView;
    private TableLayout locationTable;

    private LinearLayout bottomLinearLayout;
    private TextView authorIntroTextView;
    private TextView contentTextView;
    private TextView catalogTextView;
    private TextView pagesTextView;
    private TextView priceTextView;
    private MenuItem menuCollection;

    private LinearLayout shareView;
    private String mSharedBookDetailFileName = PublicURI.PATH_SHARE_BOOK_DETAIL;

    private ACacheUtil mCache;
    private BookBase mBookToShowDetail;
    private BookDetail mBookDetail;
    private int mPosition;

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

        bottomLinearLayout = (LinearLayout) findViewById(R.id.bottom);
        authorIntroTextView = (TextView) findViewById(R.id.author_description);
        contentTextView = (TextView) findViewById(R.id.book_description);
        catalogTextView = (TextView) findViewById(R.id.catalog);
        pagesTextView = (TextView) findViewById(R.id.pages);
        priceTextView = (TextView) findViewById(R.id.price);
    }

    private  void initData(){
        Intent intent = this.getIntent();
        mBookToShowDetail = (BookBase) intent.getSerializableExtra("bookToShowDetail");
        mBookDetail=new BookDetail();
        mPosition = intent.getIntExtra("position", 0);

        GetBooksDetailAsy getBooksDetailAsy = new GetBooksDetailAsy();
        getBooksDetailAsy.execute();
    }

    /**
     * 加载actionbar上的收藏图标的异步类加载actionbar上的收藏图标的异步类
     */
    private class CheckBookCollectionTask extends AsyncTask<BookBase, Void, Boolean> {
        @Override
        protected Boolean doInBackground(BookBase... books) {
            if(books[0].isCollected())
                return true;
            BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(getApplicationContext());
            return mDbHelper.isCollected(books[0]);
        }

        @Override
        protected void onPostExecute(Boolean isCollected) {
            mBookToShowDetail.setIsCollected(isCollected);
            menuCollection.setIcon(isCollected?R.drawable.ic_action_collect_cancle:R.drawable.ic_action_collect);
        }
    }

    /**
     * deal with the action to collect or uncollect a book
     */
    private class CollectBookAsy extends AsyncTask<Void, Void, Boolean> {
        private boolean operationAdd;// 操作为添加时，为true;操作为删除时，为false

        public CollectBookAsy() {
            if(mBookToShowDetail.isCollected())
                operationAdd =false;
            else
                operationAdd =true;
        }

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... args) {
            //操作成功与否
            boolean result = false;
            BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(getApplicationContext());
            if(operationAdd && mDbHelper.addBook(mBookToShowDetail) != (operationAdd?-1:0))
                result = true;
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (result) {

                //返回给上一个activity，
                Intent intent = new Intent();
                intent.putExtra("mPosition", mPosition);
                intent.putExtra("isCollected", operationAdd);
                BookDetailActivity.this.setResult(COLLECT_RESULT_CODE, intent);

                if (operationAdd) {
                    mBookToShowDetail.setIsCollected(true);
                    Toast.makeText(getApplication(), "添加成功", Toast.LENGTH_SHORT).show();
                    menuCollection.setTitle("取消收藏").setIcon(R.drawable.ic_action_collect_cancle);
                } else {
                    mBookToShowDetail.setIsCollected(false);
                    Toast.makeText(getApplication(), "删除成功", Toast.LENGTH_SHORT).show();
                    //除了在借的书的详情里，其他的点完取消收藏，就finish掉
                    if (mBookToShowDetail instanceof BookBase) {
                        menuCollection.setTitle("添加收藏").setIcon(R.drawable.ic_action_collect);
                    } else {
                        finish();
                    }
                }

            } else {
                if (operationAdd)
                    Toast.makeText(getApplication(), "添加失败", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplication(), "删除失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetBooksDetailAsy extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... args) {
            try {
                InternetServiceApi internetServiceApi = new InternetServiceApiImpl();
                JSONObject resultJson = internetServiceApi.GetBookDetail(mBookToShowDetail.getBookId());
                mBookDetail= JsonUtil.getBookDetatl(resultJson);
                Bitmap bitmap=null;
//                bitmap = mCache.getAsBitmap(mBookToShowDetail.getBookId());
                if (bitmap == null) {
                    bitmap = mBookToShowDetail.getPictureBitmap();
                    if (bitmap != null) {
                        mCache.put(mBookToShowDetail.getBookId(), bitmap);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgressBar.setVisibility(View.INVISIBLE);

            inflateTopRight(mBookToShowDetail);
            inflateTable(mBookDetail);
            inflateBottom(mBookDetail);

            Bitmap bitmap =mBookToShowDetail.getPictureBitmap();
            if (bitmap != null) {
                bookPictureImageView.setImageBitmap(bitmap);
            } else {
                bookPictureImageView.setImageResource(R.drawable.book_default); //当网络上没有图片时，自动加载这个图片
                bottomLinearLayout.removeAllViews();
            }
        }

        //将整个详情页分为三大部分，第一部分，图片右侧区域,不包括图片
        private void inflateTopRight(BookBase bookBase) {
            titleTextView.setText("【书名】" + bookBase.getTitle());
            searchNumHeaderTextView.setText("【索书号】" + bookBase.getSearchNum());
            authorTextView.setText("【作者】" + bookBase.getAuthor());
            publisherTextView.setText("【出版社】" + bookBase.getPublisher());
            pubdateTextView.setText("【出版日期】" + bookBase.getPubdate());
            isbnTextView.setText("【ISBN】" + bookBase.getIsbn());
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
        private void inflateTable(BookDetail bookDetail) {

            List<LocationInfo> locationInfoLists = bookDetail.getLocationInfo();

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
        private void inflateBottom(BookDetail bookDetail) {
            if(!bookDetail.isDoubanExist())
                return;
            authorIntroTextView.setText("\n【作者简介】" + bookDetail.getAuthorIntro());
            contentTextView.setText("\n【内容简介】" + bookDetail.getSummary());
            catalogTextView.setText("\n【目录】" + bookDetail.getCatalog());
            pagesTextView.setText("\n【页数】" + bookDetail.getPages());
            priceTextView.setText("\n【价格】" + bookDetail.getPrice());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
        menuCollection = menu.findItem(R.id.action_collection);

        CheckBookCollectionTask checkBookCollectionTask = new CheckBookCollectionTask();
        checkBookCollectionTask.execute(mBookToShowDetail);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_collection:
                new CollectBookAsy().execute();
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

}
