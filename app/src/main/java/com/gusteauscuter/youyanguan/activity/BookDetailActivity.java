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
import com.gusteauscuter.youyanguan.definedDataClass.Book;
import com.gusteauscuter.youyanguan.definedDataClass.BookDetail;
import com.gusteauscuter.youyanguan.definedDataClass.LocationInformation;
import com.gusteauscuter.youyanguan.definedDataClass.ResultBook;
import com.gusteauscuter.youyanguan.definedDataClass.SimpleBaseBook;
import com.gusteauscuter.youyanguan.databaseHelper.BookCollectionDbHelper;
import com.gusteauscuter.youyanguan.common.PublicURI;
import com.gusteauscuter.youyanguan.util.ACacheUtil;
import com.gusteauscuter.youyanguan.util.ScreenShotUtil;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {

    public static final int PICTURE_RESULT_CODE = 1;
    public static final int COLLECT_RESULT_CODE = 2;

    private ProgressBar  mProgressBar;
    private SimpleBaseBook simpleBaseBook;
    //控件
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
//    private boolean isCollected;
    private int position;
    private LinearLayout shareView;

    private ACacheUtil mCache;
    private SimpleBaseBook bookToCollect;
    private boolean isBookCollected = false;
    //判断图书类型
//    public static final int BOOK = 0;
//    public static final int RESULT_BOOK = 1;
    //private int baseBookType;

    private String mSharedBookDetailFileName = PublicURI.PATH_SHARE_BOOK_DETAIL;
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
        simpleBaseBook = (SimpleBaseBook) intent.getSerializableExtra("bookToShowDetail");
        position = intent.getIntExtra("position", 0);

        //TODO
        mCache = ACacheUtil.get(this);

        GetBooksDetailAsy getBooksDetailAsy = new GetBooksDetailAsy();
        getBooksDetailAsy.execute(simpleBaseBook);
    }

    private void collectBook(){
        CrudTask crudTask = new CrudTask();
        if (simpleBaseBook instanceof Book) {
            if (bookToCollect != null) {
                bookToCollect.setIsCollected(isBookCollected);
                crudTask.execute(bookToCollect);
            }
        } else if (simpleBaseBook instanceof ResultBook) {
            crudTask.execute((ResultBook) simpleBaseBook);
        } else {
            crudTask.execute(simpleBaseBook);
        }
    }

    //TODO 收藏图书的增删改查异步类
    private class CrudTask extends AsyncTask<SimpleBaseBook, Void, Boolean> {
        private boolean operation;// 操作为添加时，为true;操作为删除时，为false
        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(SimpleBaseBook... simpleBaseBooks) {
            //操作成功与否
            boolean result = false;
            BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(getApplicationContext());
            if (!simpleBaseBooks[0].isCollected()) {
                operation = true;
                if (mDbHelper.addBook(simpleBaseBooks[0]) != -1) {
                    simpleBaseBooks[0].setIsCollected(true);
                    result = true;
                }
            } else {
                operation = false;
                if (mDbHelper.deleteBook(simpleBaseBooks[0]) != 0) {
                    simpleBaseBooks[0].setIsCollected(false);
                    result = true;
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (result) {

                //返回给上一个activity，
                Intent intent = new Intent();
                intent.putExtra("position", position);
                intent.putExtra("isCollected", operation);
                BookDetailActivity.this.setResult(COLLECT_RESULT_CODE, intent);

                if (operation) {
                    isBookCollected = true;
                    menuCollection.setTitle("取消收藏").setIcon(R.drawable.ic_action_collect_cancle);
                    Toast.makeText(getApplication(), "添加成功", Toast.LENGTH_SHORT).show();
                } else {
                    isBookCollected = false;
                    Toast.makeText(getApplication(), "删除成功", Toast.LENGTH_SHORT).show();
                    //除了在借的书的详情里，其他的点完取消收藏，就finish掉
                    if (simpleBaseBook instanceof Book) {
                        menuCollection.setTitle("添加收藏").setIcon(R.drawable.ic_action_collect);
                    } else {
                        finish();
                    }
                }

            } else {
                if (operation) {
                    Toast.makeText(getApplication(), "添加失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplication(), "删除失败", Toast.LENGTH_SHORT).show();
                }
            }

            super.onPostExecute(result);
        }
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

    private class GetBooksDetailAsy extends AsyncTask<SimpleBaseBook, Void, BookDetail> {
        private Bitmap bitmap;
        private boolean serverOK = true;
        private int borrowCondition;
        
        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected BookDetail doInBackground(SimpleBaseBook... simpleBaseBooks) {
            BookDetail bookDetail = new BookDetail();
            try {
                bookDetail.getBookDetail(simpleBaseBooks[0]);
                borrowCondition = LocationInformation.checkBorrowCondition(bookDetail.getLocationInfoWithoutStopped());
                simpleBaseBooks[0].setBorrowCondition(borrowCondition);
                ///为数据库的可借状态执行更新
                BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(getApplicationContext());
                mDbHelper.updateTupleBorrowCondition(simpleBaseBooks[0]);
                bitmap = mCache.getAsBitmap(bookDetail.getBookId());
                if (bitmap == null) {
                    bitmap = bookDetail.getPicture();
                    if (bitmap != null) {
                        mCache.put(bookDetail.getBookId(), bitmap);
                    }
                }

            } catch (SocketTimeoutException e) {
                serverOK = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bookDetail;
        }

        @Override
        protected void onPostExecute(BookDetail result) {

            mProgressBar.setVisibility(View.INVISIBLE);

            if (serverOK) {
                inflateTopRight(result);
                inflateTable(result);

                //为将bookToCollect插入数据库封装数据
                if (simpleBaseBook instanceof Book) {
                    bookToCollect = new Book();
                    bookToCollect.setAuthor(result.getAuthor());
                    bookToCollect.setBookId(result.getBookId());
                    bookToCollect.setIsbn(result.getIsbn());
                    bookToCollect.setPubdate(result.getPubdate());
                    bookToCollect.setPublisher(result.getPublisher());
                    bookToCollect.setTitle(result.getTitle());
                    bookToCollect.setSearchNum(result.getSearchNum());
                    bookToCollect.setBorrowCondition(borrowCondition);
                }

                if (bitmap != null) {
                    bookPictureImageView.setImageBitmap(bitmap);
                    inflateBottom(result);
                } else {
                    bookPictureImageView.setImageResource(R.drawable.book_default); //当网络上没有图片时，自动加载这个图片
                    bottomLinearLayout.removeAllViews();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.server_failed, Toast.LENGTH_SHORT).show();
            }

        }

        //将整个详情页分为三大部分，第一部分，图片右侧区域,不包括图片
        private void inflateTopRight(BookDetail bookDetail) {
            titleTextView.setText("【书名】" + bookDetail.getTitle());
            searchNumHeaderTextView.setText("【索书号】" + bookDetail.getSearchNum());
            authorTextView.setText("【作者】" + bookDetail.getAuthor());
            publisherTextView.setText("【出版社】" + bookDetail.getPublisher());
            pubdateTextView.setText("【出版日期】" + bookDetail.getPubdate());
            isbnTextView.setText("【ISBN】" + bookDetail.getIsbn());
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

            List<LocationInformation> locationInfoLists = bookDetail.getLocationInfoWithoutStopped();

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
            for (LocationInformation locationInfo : locationInfoLists) {
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
            if (bookDetail.getAuthorIntro().replaceAll("\\s","").isEmpty()) {
                bottomLinearLayout.removeView(authorIntroTextView);
            } else {
                authorIntroTextView.setText("\n【作者简介】" + bookDetail.getAuthorIntro());
            }

            if (bookDetail.getSummary().replaceAll("\\s","").isEmpty()) {
                bottomLinearLayout.removeView(contentTextView);
            } else {
                contentTextView.setText("\n【内容简介】" + bookDetail.getSummary());
            }

            if (bookDetail.getCatalog().replaceAll("\\s", "").isEmpty()) {
                bottomLinearLayout.removeView(catalogTextView);
            } else {
                catalogTextView.setText("\n【目录】" + bookDetail.getCatalog());
            }

            if (bookDetail.getPages().replaceAll("\\s", "").isEmpty()) {
                bottomLinearLayout.removeView(pagesTextView);
            } else {
                pagesTextView.setText("\n【页数】" + bookDetail.getPages());
            }

            if (bookDetail.getPrice().replaceAll("\\s","").isEmpty()) {
                bottomLinearLayout.removeView(priceTextView);
            } else {
                priceTextView.setText("\n【价格】" + bookDetail.getPrice());
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
        menuCollection = menu.findItem(R.id.action_collection);

        if (simpleBaseBook instanceof Book) {
            CheckBookCollectionTask checkBookCollectionTask = new CheckBookCollectionTask();
            checkBookCollectionTask.execute((Book) simpleBaseBook);
        } else {
            boolean isCollected = simpleBaseBook.isCollected();
            if (isCollected){
                menuCollection.setIcon(R.drawable.ic_action_collect_cancle);
            }else {
                menuCollection.setIcon(R.drawable.ic_action_collect);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_collection:
                collectBook();
                break;
            case R.id.action_share:
                shareBook();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 加载actionbar上的收藏图标的异步类
    private class CheckBookCollectionTask extends AsyncTask<Book, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Book... books) {
            BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(getApplicationContext());
            return mDbHelper.isCollected(books[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean) {
                isBookCollected = true;
                menuCollection.setIcon(R.drawable.ic_action_collect_cancle);
            } else {
                isBookCollected = false;
                menuCollection.setIcon(R.drawable.ic_action_collect);
            }
            super.onPostExecute(aBoolean);
        }

    }
}
