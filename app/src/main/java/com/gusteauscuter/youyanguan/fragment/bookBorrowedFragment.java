package com.gusteauscuter.youyanguan.fragment;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.activity.NavigationActivity;
import com.gusteauscuter.youyanguan.adapter.BookBaseAdapter;
import com.gusteauscuter.youyanguan.api.InternetServiceApi;
import com.gusteauscuter.youyanguan.api.InternetServiceApiImpl;
import com.gusteauscuter.youyanguan.common.PublicURI;
import com.gusteauscuter.youyanguan.domain.BookBorrowed;
import com.gusteauscuter.youyanguan.domain.JsonUtil;
import com.gusteauscuter.youyanguan.util.NetworkConnectUtil;
import com.gusteauscuter.youyanguan.util.CalendarUtil;
import com.gusteauscuter.youyanguan.util.ScreenShotUtil;
import com.gusteauscuter.youyanguan.util.SharedPreferencesUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.List;


public class bookBorrowedFragment extends Fragment {

    private GridView mListView;
    private TextView mEmptyInformation;
    private ProgressBar mProgressBar;
    private View shareView;

    private ActionBar mActionBar ;
    private BookBaseAdapter mAdapter;
    private Context mContext ;
    private SharedPreferencesUtil mSharedPreferencesUtil;
    private boolean isFirstTime=true;

    private static String stringSharedBooksBorrowedName = PublicURI.PATH_SHARE_MY_BOOKS;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_books_borrowed, container, false);
        mEmptyInformation=(TextView) view.findViewById(R.id.emptyInformation);
        mProgressBar=(ProgressBar) view.findViewById(R.id.progressBarRefresh);
        mListView = (GridView) view.findViewById(R.id.bookListView);
        shareView=view.findViewById(R.id.bookListView);
        mActionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        mContext = getActivity();
        mSharedPreferencesUtil = new SharedPreferencesUtil(mContext);
        if(mAdapter==null)
            mAdapter = new BookBaseAdapter(mContext);
        mListView.setAdapter(mAdapter);

        RefreshBook();
        RefreshViewAndCalendar();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshViewAndCalendar();
    }

    public void RefreshBook(){
        if(!isFirstTime||!mAdapter.isEmpty())
            return;
        else
            isFirstTime=false;
        boolean isConnected = NetworkConnectUtil.isConnected(getActivity());
        if(isConnected){
            new GetMyBookAsy().execute();
        }
    }

    private class GetMyBookAsy extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            InternetServiceApi internetService = new InternetServiceApiImpl();
            JSONObject resultJson = internetService.getMyBookBorrowed();
            List<BookBorrowed> bookBorrowedList = JsonUtil.getMyBookBorrowed(resultJson) ;
            if(bookBorrowedList==null)
                return true;
            mAdapter.setItems(bookBorrowedList);
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isnull) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (isnull)
                return;
            RefreshViewAndCalendar();
            Toast.makeText(getActivity(), R.string.succeed_to_getBooks, Toast.LENGTH_SHORT).show();
        }
    }

    private void RefreshViewAndCalendar(){
        String title=getResources().getString(R.string.nav_book_borrowed);
        mActionBar.setTitle(title + "(" + mAdapter.getCount() + ")");
        if( mAdapter.isEmpty())
            mEmptyInformation.setVisibility(View.VISIBLE);
        else
            mEmptyInformation.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();

        new CalendarUtil(getActivity()).new AddCalendarThread(
                (List<BookBorrowed>) mAdapter.getItemList())
                .start();
    }

    public void shareBooksBorrowed(){

        ScreenShotUtil.shoot(stringSharedBooksBorrowedName, shareView);

        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TITLE, "Share");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
        intent.putExtra(Intent.EXTRA_TEXT, "I want to share a wonderful book through YouYanGuan");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(stringSharedBooksBorrowedName)));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.action_share)));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_my_borrowed_books, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case (R.id.action_log_out):
                mSharedPreferencesUtil.setIsLogined(false);
                ((NavigationActivity) getActivity()).JumpToLoginActivity();
                Toast.makeText(getActivity(), getString(R.string.re_login), Toast.LENGTH_SHORT).show();
                break;
            case (R.id.action_share):
                shareBooksBorrowed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
