package com.gusteauscuter.youyanguan.content_fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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


public class bookFragment extends Fragment {

    private List<Book> mBookList=new ArrayList<>();
    private GridView mListView;
    private LayoutInflater mLayoutInflater;

    private BookAdapter mAdapter;
    private ProgressBar mProgressBar;

    private userLogin mUserLogin=new userLogin();
    private boolean isFirstTime=true;

    private TextView mTotalNumber;
    private TextView mEmptyInformation;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        initDataFromActivity();

        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        mLayoutInflater=inflater;

        mEmptyInformation=(TextView) view.findViewById(R.id.emptyInformation);
        mEmptyInformation.setVisibility(View.GONE);

        mProgressBar=(ProgressBar) view.findViewById(R.id.progressBarRefreshBookBorrowed);
        mProgressBar.setVisibility(View.INVISIBLE);

        mTotalNumber=(TextView) view.findViewById(R.id.totalNumber);
        mListView = (GridView) view.findViewById(R.id.bookListView);
        // 处理侧滑事件
        mListView.setOnTouchListener(new View.OnTouchListener() {
            float x ,Ux ,y, Uy;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = event.getX();
                        y = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        Ux = event.getX();
                        Uy = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        boolean c1=(Ux-x-50)>0;
                        boolean c2=Math.abs(Ux - x)>Math.abs(Uy - y);
                        if(c1&&c2){
                            ((NavigationActivity)getActivity()).openDrawer();
                            return true;
                        }
                }
                return false;
            }
        });
        mAdapter = new BookAdapter() ;
        mListView.setAdapter(mAdapter);
        mTotalNumber.setText(String.valueOf(mBookList.size()));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFirstTime) {
                    isFirstTime = false;
                    RefreshData();
                }
            }
        }, 320);
        return view;
    }

    /* @ WangCe
    * TO  deal with dataChange with the "NavigationActivity"
    * such as UserLogin, BookList if they are already existed
    * so don't have to get data again
    * */
    private void initDataFromActivity(){
        mUserLogin=((NavigationActivity)getActivity()).getmLogin();
    }


    public void RefreshData(){
        boolean isConnected = NetworkConnectivity.isConnected(getActivity());
        if(isConnected){
            GetBooksAsy getBooksAsy=new GetBooksAsy();
            getBooksAsy.execute(mUserLogin.getUsername(),mUserLogin.getPassword());
        }else{
            Toast.makeText(getActivity(), R.string.internet_not_connected
                    , Toast.LENGTH_SHORT).show();
        }
    }

    private class BookAdapter extends BaseAdapter {
        //Book mBook = new Book();
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

            ViewHolder mHolder=null;

            if (convertView == null) {
                convertView=mLayoutInflater.inflate(R.layout.card_book,container, false);
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
                    if (mBookList.get(position).getBorrowedTime() < mBookList.get(position).getMaxBorrowTime()) {
                        boolean isConnected = NetworkConnectivity.isConnected(getActivity());
                        if(isConnected){
                            RenewBookAsy renewBookAsy = new RenewBookAsy(mBookList.get(position));
                            renewBookAsy.execute(mUserLogin.getUsername(), mUserLogin.getPassword());
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
                        bundle.putSerializable("bookToShowDetail", mBookList.get(position));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getActivity(), R.string.internet_not_connected, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // TO 设置Book对应属性
            String name=mBookList.get(position).getTitle();
            String borrowDay="借阅:"+mBookList.get(position).getBorrowDay();
            String returnDay="归还:"+mBookList.get(position).getReturnDay();
            String borrowedTime="续借次数:"+  mBookList.get(position).getBorrowedTime()+"/"+ mBookList.get(position).getMaxBorrowTime();

            mHolder.mName.setText( name.toString());
            mHolder.mBorrowDay.setText(borrowDay.toString());
            mHolder.mReturnDay.setText(returnDay.toString());
            mHolder.mBorrowedTime.setText(borrowedTime.toString());
            int[] book={R.drawable.book1,R.drawable.book2,R.drawable.book3,
                    R.drawable.book4,R.drawable.book5};

            int no =position%book.length;

//            Random ra =new Random();
//            int no =ra.nextInt(6);

            mHolder.mBookPicture.setImageResource(book[no]);
            mHolder.mButtonBorrow.setBackgroundResource(book[no]);
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
                    if (result == null) {
                        mTotalNumber.setText("0");
                        mEmptyInformation.setVisibility(View.VISIBLE);
                    } else {
                        mBookList = result;
                        SortBookList();
                        mTotalNumber.setText(String.valueOf(mBookList.size()));
                        ((NavigationActivity) getActivity()).setmBookList(mBookList);
                        mAdapter.notifyDataSetChanged();
                    }

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

            mProgressBar.setVisibility(View.INVISIBLE);
            if (serverOK) {
                if (result != null) {
                    mBookList=result;
                    SortBookList();
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "续借成功，自动续期30天" , Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "本书尚未到续借时间", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), R.string.server_failed, Toast.LENGTH_SHORT).show();
            }
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
