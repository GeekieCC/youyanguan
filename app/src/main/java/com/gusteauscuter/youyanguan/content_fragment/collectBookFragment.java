package com.gusteauscuter.youyanguan.content_fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.DepActivity.BookCollectionActivity;
import com.gusteauscuter.youyanguan.DepActivity.SearchResultActivity;
import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.data_Class.book.BookSearchEngine;
import com.gusteauscuter.youyanguan.data_Class.book.ResultBook;
import com.gusteauscuter.youyanguan.data_Class.bookdatabase.BookCollectionDbHelper;
import com.gusteauscuter.youyanguan.internet.connectivity.NetworkConnectivity;

import java.util.List;

/**
 * A simple {searchBook Fragment} subclass.
 */
public class collectBookFragment extends Fragment{

    private TextView bookCollectionTextView;
    private Button clearCollectionButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_book_collection, container, false);

        bookCollectionTextView = (TextView) view.findViewById(R.id.book_collection_text);
        clearCollectionButton = (Button) view.findViewById(R.id.clear_collection_button);
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
        return view;
    }

    private class GetBookCollectionTask extends AsyncTask<Void, Void, List<ResultBook>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<ResultBook> doInBackground(Void... params) {
            List<ResultBook> resultBookList = null;
            BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(getActivity());
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
            BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(getActivity());
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
            Toast.makeText(getActivity(), toastString, Toast.LENGTH_SHORT).show();
            super.onPostExecute(integer);
        }
    }

}
