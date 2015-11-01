package com.gusteauscuter.youyanguan.DepActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.data_Class.book.ResultBook;
import com.gusteauscuter.youyanguan.data_Class.bookdatabase.BookCollectionDbHelper;

import java.util.List;

public class BookCollectionActivity extends AppCompatActivity {

    private TextView bookCollectionTextView;
    private Button clearCollectionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_collection);

        bookCollectionTextView = (TextView) findViewById(R.id.book_collection_text);
        clearCollectionButton = (Button) findViewById(R.id.clear_collection_button);
        //TODO 从收藏的图书数据库里取出数据
        GetBookCollectionTask getBookCollectionTask = new GetBookCollectionTask();
        getBookCollectionTask.execute();

        clearCollectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 清空本地收藏图书数据库
                RemoveAllCollectionTask removeAllCollectionTask = new RemoveAllCollectionTask();
                removeAllCollectionTask.execute();
            }
        });

    }

    private class GetBookCollectionTask extends AsyncTask<Void, Void, List<ResultBook>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<ResultBook> doInBackground(Void... params) {
            List<ResultBook> resultBookList = null;
            BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(getApplicationContext());
            resultBookList = mDbHelper.getAllBookCollections();
            return resultBookList;
        }

        @Override
        protected void onPostExecute(List<ResultBook> resultBooks) {
            String collectionText = "";
            for (ResultBook aBook : resultBooks) {
                collectionText += aBook.toString();
            }
            bookCollectionTextView.setText(collectionText);

            super.onPostExecute(resultBooks);
        }
    }

    private class RemoveAllCollectionTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(getApplicationContext());
            return mDbHelper.removeAllCollections();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            int result = integer;
            String toastString = "";
            if (result == -1) {
                toastString += "本地收藏已经清空，无需再次清空！";
            } else {
                toastString += "清空了" + result + "本图书！";
                bookCollectionTextView.setText("");
            }
            Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_SHORT).show();
            super.onPostExecute(integer);
        }
    }

}
