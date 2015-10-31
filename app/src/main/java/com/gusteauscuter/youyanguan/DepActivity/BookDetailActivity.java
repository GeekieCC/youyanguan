package com.gusteauscuter.youyanguan.DepActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
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
import com.gusteauscuter.youyanguan.data_Class.book.Book;
import com.gusteauscuter.youyanguan.data_Class.book.BookDetail;
import com.gusteauscuter.youyanguan.data_Class.book.LocationInformation;
import com.gusteauscuter.youyanguan.data_Class.book.ResultBook;

import java.net.SocketTimeoutException;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {

    private ProgressBar  mProgressBar;

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

        Object baseBook = intent.getSerializableExtra("bookToShowDetail");
        GetBooksDetailAsy getBooksDetailAsy = new GetBooksDetailAsy();
        getBooksDetailAsy.execute(baseBook);

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
            BookDetail bookDetail = null;
            try {
                if (baseBook[0] instanceof Book) {
                    bookDetail = new BookDetail();
                    bookDetail.getBookDetail((Book) baseBook[0]);

                } else if (baseBook[0] instanceof ResultBook) {
                    bookDetail = new BookDetail();
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


            for (int i = 0; i < locationInfoLists.size(); i++) {
                LocationInformation locationInfo = locationInfoLists.get(i);
                TableRow tr = new TableRow(getApplicationContext());
                tr.setLayoutParams(new TableRow.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                String tableColor = "#BBDEFB";
                TextView location = createRowTextView(locationInfo.getLocation(),tableColor);
                TextView detailLocation = createRowTextView(locationInfo.getDetailLocation(),tableColor);
                TextView searchNum = createRowTextView(locationInfo.getSearchNum(),tableColor);
                TextView status = createRowTextView(locationInfo.getStatus(),tableColor);

                tr.addView(location);
                tr.addView(detailLocation);
//                tr.addView(searchNum);
                tr.addView(status);

                locationTable.addView(tr);

            }


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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
