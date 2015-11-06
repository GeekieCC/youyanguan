package com.gusteauscuter.youyanguan.DepActivity;

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
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.ShareActionProvider;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.data_Class.book.Book;
import com.gusteauscuter.youyanguan.data_Class.book.BookDetail;
import com.gusteauscuter.youyanguan.data_Class.book.LocationInformation;
import com.gusteauscuter.youyanguan.data_Class.book.ResultBook;
import com.gusteauscuter.youyanguan.data_Class.bookdatabase.BookCollectionDbHelper;
import com.gusteauscuter.youyanguan.util.ScreenShot;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Objects;

public class BookDetailActivity extends AppCompatActivity {

    private ProgressBar  mProgressBar;
    private Object baseBook;
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
    private boolean isCollected;
    private int position;
    private ScrollView shareView;

    //判断图书类型
    public static final int BOOK = 0;
    public static final int RESULT_BOOK = 1;
    private int baseBookType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {

        setContentView(R.layout.activity_book_detail);

        setSupportActionBar((Toolbar) findViewById(R.id.id_toolbar));
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mProgressBar=(ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        shareView=(ScrollView)findViewById(R.id.scrollViewBookDetail);

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
        baseBook = intent.getSerializableExtra("bookToShowDetail");
        position = intent.getIntExtra("position", 0);

        //TODO
        if (baseBook instanceof ResultBook) {
            isCollected = ((ResultBook) baseBook).isCollected();
            baseBookType = RESULT_BOOK;

        } else if (baseBook instanceof Book) {
            baseBookType = BOOK;
        }

        GetBooksDetailAsy getBooksDetailAsy = new GetBooksDetailAsy();
        getBooksDetailAsy.execute(baseBook);
    }

    private void collectBook(){
        CrudTask crudTask = new CrudTask();
        if (baseBook instanceof Book) {

        }
        if (baseBook instanceof ResultBook) {
            crudTask.execute((ResultBook)baseBook);
        }
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
            if (result) {
                if (operation) {
                    menuCollection.setTitle("取消收藏").setIcon(R.drawable.ic_collect_cancle);
                    Toast.makeText(getApplication(), "添加成功", Toast.LENGTH_SHORT).show();
                } else {
                    menuCollection.setTitle("收藏").setIcon(R.drawable.ic_collect);
                    Toast.makeText(getApplication(), "删除成功", Toast.LENGTH_SHORT).show();
                }

                //返回给上一个activity，
                Intent intent = new Intent();
                intent.putExtra("position", position);
                intent.putExtra("isCollected", operation);
                BookDetailActivity.this.setResult(0, intent);

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

        View v = getWindow().findViewById(Window.ID_ANDROID_CONTENT);///获得根视图
        int top2 = v.getTop();///状态栏标题栏的总高度,所以标题栏的高度为top2-top
        int width = v.getWidth();///视图的宽度,这个宽度好像总是最大的那个
        int height = v.getHeight();////视图的高度，不包括状态栏和标题栏
        String stringFileName="sdcard/_share_Book.png";
        ScreenShot.shoot(stringFileName,shareView);
//        Bitmap share_shot=ScreenShot.takeScreenShot(shareView);

        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
        intent.putExtra(Intent.EXTRA_TEXT, "a wonderful book through YouYanGuan");
//        intent.putExtra("bitmap", share_shot);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(stringFileName)));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getTitle()));

    }
    private class GetBooksDetailAsy extends AsyncTask<Object, Void, BookDetail> {
        private boolean serverOK = true;
        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), R.string.ing_getBookInformation, Toast.LENGTH_SHORT)
                    .show();
            super.onPreExecute();
        }

        @Override
        protected BookDetail doInBackground(Object... baseBook) {
            BookDetail bookDetail = new BookDetail();
            try {
                if (baseBookType == BOOK) {
//                    bookDetail = new BookDetail();
                    bookDetail.getBookDetail((Book) baseBook[0]);

                } else if (baseBookType == RESULT_BOOK) {
//                    bookDetail = new BookDetail();
                    bookDetail.getResultBookDetail((ResultBook) baseBook[0]);
                }
            } catch (SocketTimeoutException e) {
                serverOK = false;
            } catch (Exception e) {
                //serverOK = false;
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

                if (result.isDoubanExisting()) {
                    bookPictureImageView.setImageBitmap(result.getPicture());
                    inflateBottom(result);

                } else {
                    bookPictureImageView.setImageResource(R.drawable.book3); //当网络上没有图片时，自动加载这个图片
                    bottomLinearLayout.removeAllViews();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.server_failed, Toast.LENGTH_SHORT).show();
            }

        }



        //将整个详情页分为三大部分，第一部分，图片右侧区域,不包括图片
        private void inflateTopRight(BookDetail bookDetail) {
            titleTextView.setText("【书名】" + bookDetail.getTitle());
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

            List<LocationInformation> locationInfoLists = bookDetail.getLocationInfo();

            if(locationInfoLists.size()!=0) {
                searchNumHeaderTextView.setText("【索书号】" + locationInfoLists.get(0).getSearchNum());
            }

            String headerColor = "#2196F3";
            TextView locationHeader = createRowTextView("馆址", headerColor);
            TextView detailLocationHeader = createRowTextView("馆藏地", headerColor);
//            TextView searchNumHeader = createRowTextView("索书号", headerColor);
            TextView statusHeader = createRowTextView("状态", headerColor);

            TableRow headerRow = new TableRow(getApplicationContext());
            headerRow.addView(detailLocationHeader);
            headerRow.addView(locationHeader);
//            headerRow.addView(searchNumHeader);
            headerRow.addView(statusHeader);

            locationTable.addView(headerRow);
            boolean isNoBookEnanbled=true;
            boolean isChecking=false;


            for (int i = 0; i < locationInfoLists.size(); i++) {
                LocationInformation locationInfo = locationInfoLists.get(i);

                Boolean conditon1=!locationInfo.getLocation().contains("停");
                Boolean conditon2=!locationInfo.getDetailLocation().contains("停");
//                Boolean conditon3=!locationInfo.getStatus().contains("验收");

                if(conditon1&&conditon2){
//                    if(conditon3){
                        isNoBookEnanbled=false;
                        TableRow tr = new TableRow(getApplicationContext());
                        tr.setLayoutParams(new TableRow.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                        String tableColor = "#BBDEFB";
                        TextView location = createRowTextView(locationInfo.getLocation(),tableColor);
                        TextView detailLocation = createRowTextView(OptimLocation(locationInfo.getDetailLocation()),tableColor);
                        //                TextView searchNum = createRowTextView(locationInfo.getSearchNum(),tableColor);
                        TextView status = createRowTextView(locationInfo.getStatus(),tableColor);

                        tr.addView(location);
                        tr.addView(detailLocation);
                        //                tr.addView(searchNum);
                        tr.addView(status);

                        locationTable.addView(tr);
//                    }else{
//                        isChecking=true;
//                    }

                }
            }

            if(isNoBookEnanbled){
                TextView emptyInfor = createRowTextView("本书已全部暂停外借","#BBDEFB");
                locationTable.addView(emptyInfor);
            }
        }

        // 优化图书馆藏位置的显示
        private String OptimLocation(String location){
            String op1="(";
            String op2="（";
            String add="\n";
            if(location.contains(op1)){
                int index=location.indexOf(op1);
                int end=location.length()-1;
                return location.substring(0,index-1)+add+location.substring(index,end);
            }else if(location.contains(op2)){
                int index=location.indexOf(op2);
                int end=location.length();
                return location.substring(0,index)+add+location.substring(index,end);
            }else
                return location;
        }


        //将整个详情页分为三大部分，第三部分，从豆瓣获取的详细信息
        private void inflateBottom(BookDetail bookDetail) {
            if (bookDetail.getAuthorIntro().replaceAll("\\s","").isEmpty()) {
                bottomLinearLayout.removeView(authorIntroTextView);
//                authorIntroTextView.setVisibility(View.INVISIBLE);
            } else {
                authorIntroTextView.setText("\n【作者简介】" + bookDetail.getAuthorIntro());
            }

            if (bookDetail.getSummary().replaceAll("\\s","").isEmpty()) {
                bottomLinearLayout.removeView(contentTextView);
//                contentTextView.setVisibility(View.INVISIBLE);
            } else {
                contentTextView.setText("\n【内容简介】" + bookDetail.getSummary());
            }

            if (bookDetail.getCatalog().replaceAll("\\s", "").isEmpty()) {
                bottomLinearLayout.removeView(catalogTextView);
//                catalogTextView.setVisibility(View.INVISIBLE);
            } else {
                catalogTextView.setText("\n【目录】" + bookDetail.getCatalog());
            }

            if (bookDetail.getPages().replaceAll("\\s", "").isEmpty()) {
                bottomLinearLayout.removeView(pagesTextView);
//                pagesTextView.setVisibility(View.INVISIBLE);
            } else {
                pagesTextView.setText("\n【页数】" + bookDetail.getPages());
            }

            if (bookDetail.getPrice().replaceAll("\\s","").isEmpty()) {
                bottomLinearLayout.removeView(priceTextView);
//                priceTextView.setVisibility(View.INVISIBLE);
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

        if (baseBookType == BOOK) {
            menuCollection.setVisible(false);
        } else if (baseBookType == RESULT_BOOK) {
            if(isCollected){
                menuCollection.setTitle("取消收藏").setIcon(R.drawable.ic_collect_cancle);
            }else {
                menuCollection.setTitle("收藏").setIcon(R.drawable.ic_collect);
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

}
