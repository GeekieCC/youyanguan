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
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.adapter.CollectedBookAdapter;
import com.gusteauscuter.youyanguan.data_Class.book.Book;
import com.gusteauscuter.youyanguan.commonUrl.IPublicUrl;
import com.gusteauscuter.youyanguan.util.NetworkConnectUtil;
import com.gusteauscuter.youyanguan.internetService.LibraryLoginClient;
import com.gusteauscuter.youyanguan.util.CalendarUtil;
import com.gusteauscuter.youyanguan.util.ScreenShotUtil;
import com.gusteauscuter.youyanguan.util.ShareDataUtil;

import org.apache.commons.httpclient.ConnectTimeoutException;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.List;


public class bookBorrowedFragment extends Fragment implements IPublicUrl {

    private GridView mListView;
    private TextView mEmptyInformation;
    private ProgressBar mProgressBar;
    private View shareView;

    private ActionBar mActionBar ;
    private CollectedBookAdapter mAdapter;
    private Context mContext ;
    private ShareDataUtil mShareDataUtil;
    private boolean isFirstTime=true;

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
        mShareDataUtil = new ShareDataUtil(mContext);
        if(mAdapter==null)
            mAdapter = new CollectedBookAdapter(mContext);
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
            GetBooksAsy getBooksAsy=new GetBooksAsy(mShareDataUtil.getUSERNAME(), mShareDataUtil.getPASSWORD());
            getBooksAsy.execute();
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
                (List<Book>) mAdapter.getItemList())
                .start();
    }

    private class GetBooksAsy extends AsyncTask<Void, Void, List<Book>> {
        private String mUsername;
        private String mPssword;
        private boolean isLogined;
        private boolean serverOK = true;

        public GetBooksAsy(String username,String password){
            mUsername=username;
            mPssword=password;
        }

        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected List<Book> doInBackground(Void... params) {
            List<Book> bookLists = null;
            try {
                LibraryLoginClient libClient = new LibraryLoginClient();
                if (libClient.login(mUsername,mPssword)) {
                    isLogined = true;
                    bookLists = libClient.getBooks();
                }
            } catch (ConnectTimeoutException | SocketTimeoutException e) {
                serverOK = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bookLists;
        }

        @Override
        protected void onPostExecute(List<Book> result) {

            mProgressBar.setVisibility(View.INVISIBLE);
            if (serverOK) {
                if (isLogined) {
                    if (result != null) {
                        mAdapter.setItems(result);
                        RefreshViewAndCalendar();
                    }
                    Toast.makeText(getActivity(), R.string.succeed_to_getBooks, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getActivity(), R.string.failed_to_getBooks, Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getActivity(), R.string.server_failed, Toast.LENGTH_SHORT).show();
        }
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

}
