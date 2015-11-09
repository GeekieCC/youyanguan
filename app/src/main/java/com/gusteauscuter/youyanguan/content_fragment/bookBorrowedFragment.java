package com.gusteauscuter.youyanguan.content_fragment;


import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.DepActivity.BookDetailActivity;
import com.gusteauscuter.youyanguan.NavigationActivity;
import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.data_Class.book.Book;
import com.gusteauscuter.youyanguan.data_Class.userLogin;
import com.gusteauscuter.youyanguan.internet.connectivity.NetworkConnectivity;
import com.gusteauscuter.youyanguan.login_Client.LibraryClient;

import org.apache.commons.httpclient.ConnectTimeoutException;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;


public class bookBorrowedFragment extends Fragment {


    private GridView mListView;
    private TextView mEmptyInformation;
    private ProgressBar mProgressBar;

    private BookAdapter mAdapter;
    private List<Book> mBookList=new ArrayList<>();

    private boolean isFirstTime=true;
    private String mUsername ;
    private String mPassword ;
    private boolean mIsLogined ;

    private boolean refreshColor=true;
    private int start=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        mEmptyInformation=(TextView) view.findViewById(R.id.emptyInformation);
        mProgressBar=(ProgressBar) view.findViewById(R.id.progressBarRefresh);
        mListView = (GridView) view.findViewById(R.id.bookListView);

        initData();
        RefreshView();
        return view;
    }

    private void initData(){

        SharedPreferences shareData = getActivity().getSharedPreferences("data", 0);
        mUsername = shareData.getString("USERNAME", "");
        mPassword = shareData.getString("PASSWORD", "");
        mIsLogined = shareData.getBoolean("ISLOGINED", false);

        if(mBookList==null) {
            mBookList = new ArrayList<>();
        }
        if(mAdapter==null) {
            mAdapter = new BookAdapter();
        }
        mListView.setAdapter(mAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFirstTime) {
                    isFirstTime = false;
                    RefreshData();
                }
            }
        }, 320);

    }

    public void RefreshData(){
        refreshColor=true;
        boolean isConnected = NetworkConnectivity.isConnected(getActivity());

        if(isConnected){
            GetBooksAsy getBooksAsy=new GetBooksAsy();
            getBooksAsy.execute(mUsername,mPassword);
        }else{
            Toast.makeText(getActivity(), R.string.internet_not_connected
                    , Toast.LENGTH_SHORT).show();
        }
    }

    private class BookAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public BookAdapter(){
            this.mInflater = LayoutInflater.from(getActivity());
        }

        @Override
        public int getCount() {
            return mBookList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBookList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mBookList.get(position).hashCode();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {

            final ViewHolder mHolder;
            final Book mBook = mBookList.get(position);

            if (convertView == null) {
                convertView=mInflater.inflate(R.layout.card_book,container, false);
                mHolder =new ViewHolder();
                mHolder.mButtonBorrow=(Button) convertView.findViewById(R.id.button_Borrow);
                mHolder.mBookPicture=(ImageView) convertView.findViewById(R.id.BookPicture);
                mHolder.mName=((TextView) convertView.findViewById(R.id.text_Title));
                mHolder.mBorrowDay=((TextView) convertView.findViewById(R.id.text_BorrowDay));
                mHolder.mReturnDay=(TextView) convertView.findViewById(R.id.text_ReturnDay);
                mHolder.mBorrowedTime=((TextView) convertView.findViewById(R.id.text_BorrowedTime));

                convertView.setTag(mHolder);

            } else{
                mHolder=(ViewHolder) convertView.getTag();
            }

            mHolder.mButtonBorrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBook.getBorrowedTime() < mBook.getMaxBorrowTime()) {
                        boolean isConnected = NetworkConnectivity.isConnected(getActivity());
                        if(isConnected){
                            RenewBookAsy renewBookAsy = new RenewBookAsy(mBook);
                            renewBookAsy.execute(mUsername, mPassword);
                        } else{
                            Toast.makeText(getActivity(), R.string.internet_not_connected, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "已达最大续借次数，请及时归还", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mHolder.mBookPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean isConnected = NetworkConnectivity.isConnected(getActivity());
                    if(isConnected){
                        Intent intent =new Intent(getActivity(), BookDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("bookToShowDetail", mBook);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getActivity(), R.string.internet_not_connected, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // TO 设置Book对应属性
            String name=mBook.getTitle();
            String borrowDay="借阅:"+mBook.getBorrowDay();
            String returnDay="归还:"+mBook.getReturnDay();
            String borrowedTime="续借次数:"+  mBook.getBorrowedTime()+"/"+ mBook.getMaxBorrowTime();

            mHolder.mName.setText( name.toString());
            mHolder.mBorrowDay.setText(borrowDay.toString());
            mHolder.mReturnDay.setText(returnDay.toString());
            mHolder.mBorrowedTime.setText(borrowedTime.toString());

            int[] book_color={
                    getResources().getColor(R.color.book_color_1),
                    getResources().getColor(R.color.book_color_2),
                    getResources().getColor(R.color.book_color_3),
                    getResources().getColor(R.color.book_color_4),
                    getResources().getColor(R.color.book_color_5),
                    getResources().getColor(R.color.book_color_6),
                    getResources().getColor(R.color.book_color_7),
//                    getResources().getColor(R.color.book_color_8),
//                    getResources().getColor(R.color.book_color_9),
//                    getResources().getColor(R.color.book_color_10)
            };

            if(refreshColor){
                refreshColor=false;
                Random ra =new Random();
                start=ra.nextInt(book_color.length);
            }
            int no =(start+position)%book_color.length;
            mHolder.mBookPicture.setBackgroundColor(book_color[no]);

            return convertView;
            }

        public final class ViewHolder{
            public Button mButtonBorrow;
            public ImageView mBookPicture;
            public TextView mName;
            public TextView mBorrowDay;
            public TextView mReturnDay;
            public TextView mBorrowedTime;
        }

    }


    private class GetBooksAsy extends AsyncTask<String, Void, List<Book>> {
        private boolean isLogined;
        private boolean serverOK = true;
        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected List<Book> doInBackground(String... account) {
            List<Book> bookLists = null;
            try {
                LibraryClient libClient = new LibraryClient();
                if (libClient.login(account[0], account[1])) {
                    isLogined = true;
                    bookLists = libClient.getBooks();
                }
            } catch (ConnectTimeoutException | SocketTimeoutException e) {
                serverOK = false;
            } catch (Exception e) {
                e.printStackTrace();
                //serverOK = false;
            }
            return bookLists;
        }

        @Override
        protected void onPostExecute(List<Book> result) {

            mProgressBar.setVisibility(View.INVISIBLE);
            if (serverOK) {
                if (isLogined) {
                    if (result != null) mBookList = result;
                    RefreshView();
                    Toast.makeText(getActivity(), R.string.succeed_to_getBooks, Toast.LENGTH_SHORT)
                            .show();

                } else {
                    Toast.makeText(getActivity(), R.string.failed_to_getBooks, Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(getActivity(), R.string.server_failed, Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }

    private class RenewBookAsy extends AsyncTask<String, Void, List<Book>> {
        private boolean serverOK = true;
        private Book bookToRenew;
        public RenewBookAsy(Book bookToRenew) {
            this.bookToRenew = bookToRenew;
        }

        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected List<Book> doInBackground(String... account) {
            List<Book> bookLists = null;
            try {
                LibraryClient libClient = new LibraryClient();
                if (libClient.login(account[0], account[1])) {
                    if (libClient.renew(bookToRenew)) {
                        bookLists = libClient.getBooks();
                    }
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

            mProgressBar.setVisibility(View.GONE);
            if (serverOK) {
                if (result != null) {
                    mBookList=result;
                    RefreshView();
                    Toast.makeText(getActivity(), "续借成功，自动续期30天" , Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "本书尚未到续借时间", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), R.string.server_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void RefreshView(){
//        SortBookList();
        mAdapter.notifyDataSetChanged();
        ActionBar mActionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        String title=getResources().getString(R.string.nav_book_borrowed);
        mActionBar.setTitle(title+"("+mBookList.size()+")");
        if(!isFirstTime&&mBookList.isEmpty()){
            mEmptyInformation.setVisibility(View.VISIBLE);
        }else{
            mEmptyInformation.setVisibility(View.GONE);
        }

    }

    private void SortBookList(){
        Collections.sort(mBookList, new Comparator<Book>() {
            @Override
            public int compare(Book lhs, Book rhs) {
                String lhs_date = lhs.getReturnDay();
                String rhs_date = rhs.getReturnDay();
                if (lhs_date.compareTo(rhs_date) > 0)
                    return 1;
                else if (lhs_date.compareTo(rhs_date) == 0)
                    return 0;
                else
                    return -1;
            }
        });
    }

}
