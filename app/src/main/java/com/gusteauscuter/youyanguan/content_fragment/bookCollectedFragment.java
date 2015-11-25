package com.gusteauscuter.youyanguan.content_fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.data_Class.book.ResultBook;
import com.gusteauscuter.youyanguan.data_Class.book.SimpleBaseBook;
import com.gusteauscuter.youyanguan.data_Class.bookdatabase.BookCollectionDbHelper;
import com.gusteauscuter.youyanguan.internet.connectivity.NetworkConnectivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {searchBook Fragment} subclass.
 */
public class bookCollectedFragment extends Fragment{


    private List<SimpleBaseBook> mBookList=new ArrayList<>();
    private GridView mListView;
    private ProgressBar mProgressBar;

    private CollectSearchBookAdapter mAdapter;

    private TextView mEmptyInformation;
    private boolean IsCollectionCancled=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book_collection, container, false);
        mEmptyInformation=(TextView) view.findViewById(R.id.emptyInformation);
        mProgressBar=(ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mListView = (GridView) view.findViewById(R.id.bookListView);

        initData();

        return view;
    }

    private void initData(){
        if(mBookList==null) {
            mBookList = new ArrayList<>();
        }
        if(mAdapter==null) {
            mAdapter = new CollectSearchBookAdapter();
        }
        mListView.setAdapter(mAdapter);

        refreshBook();
    }

    private void refreshBook(){
        //TODO 从收藏的图书数据库里取出数据
        GetBookCollectionTask getBookCollectionTask = new GetBookCollectionTask();
        getBookCollectionTask.execute();
    }

    private class CollectSearchBookAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public CollectSearchBookAdapter(){
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
        public View getView(final int position, View convertView, final ViewGroup container) {

            final ViewHolder mHolder;
            final SimpleBaseBook mBook = mBookList.get(position);

            if (convertView == null) {

                convertView=mInflater.inflate(R.layout.card_search_book, container, false);

                mHolder =new ViewHolder();
                mHolder.mSearchBookState =(ImageView) convertView.findViewById(R.id.searchBookState);
                mHolder.mTitle=((TextView) convertView.findViewById(R.id.searchBook_Title));
                mHolder.mPublisher=((TextView) convertView.findViewById(R.id.searchBook_Publisher));
                mHolder.mPubdate=((TextView) convertView.findViewById(R.id.searchBook_Pubdate));
                mHolder.mBookId=((TextView) convertView.findViewById(R.id.searchBook_BookId));
                mHolder.mAuthor=((TextView) convertView.findViewById(R.id.searchBook_Author));
                mHolder.mButton = (TextView) convertView.findViewById(R.id.collect_book);

                convertView.setTag(mHolder);

            } else{
                mHolder=(ViewHolder) convertView.getTag();
            }


            int borrowCondition = mBook.getBorrowCondition();

            if (borrowCondition == ResultBook.BOTH_YES) { //两校区都可借
                mHolder.mSearchBookState.setImageResource(R.drawable.ic_s_n);
                mHolder.mSearchBookState.setColorFilter(getResources().getColor(R.color.primaryColor));
            } else if (borrowCondition == ResultBook.BOTH_NOT) { //两校区都不可借
                mHolder.mSearchBookState.setImageResource(R.drawable.ic_not_s_n);
                mHolder.mSearchBookState.setColorFilter(getResources().getColor(R.color.gray));
            } else if (borrowCondition == ResultBook.NORTH_ONLY) { // 只有北校区可借
                mHolder.mSearchBookState.setImageResource(R.drawable.ic_north);
                mHolder.mSearchBookState.setColorFilter(getResources().getColor(R.color.primaryColor));
            } else if (borrowCondition == ResultBook.SOUTH_ONLY) { // 只有南校区可借
                mHolder.mSearchBookState.setImageResource(R.drawable.ic_south);
                mHolder.mSearchBookState.setColorFilter(getResources().getColor(R.color.primaryColor));
            } else if (borrowCondition == ResultBook.UNKNOWN) { // 不知道是否可借
                mHolder.mSearchBookState.setImageResource(R.drawable.ic_not_known);
                mHolder.mSearchBookState.setColorFilter(getResources().getColor(R.color.gray));
            }


            // TO 设置Book对应属性
            String title="【" + (position + 1) + "】"+mBook.getTitle();
            String publisher="出版社："+mBook.getPublisher();
            String pubdate="出版日期："+mBook.getPubdate();
            String bookId="索书号："+mBook.getSearchNum();
            String author="作者："+mBook.getAuthor();

            mHolder.mTitle.setText(title);
            mHolder.mBookId.setText(bookId);
            mHolder.mAuthor.setText(author);
            mHolder.mPublisher.setText(publisher);
            mHolder.mPubdate.setText(pubdate);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    boolean isConnected = NetworkConnectivity.isConnected(getActivity());
                    if (isConnected) {
                        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                        Bundle bundle = new Bundle();
//                        ResultBook resultBook = (ResultBook) mBook;
                        bundle.putSerializable("bookToShowDetail", mBook);
                        bundle.putInt("position", position);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 0);
                    } else {
                        Toast.makeText(getActivity(), R.string.internet_not_connected, Toast.LENGTH_SHORT)
                                .show();
                    }

                }
            });

            // 对搜索出来的结果显示时，区别已收藏和未收藏图书
            if (!mBook.isCollected()) {
                mHolder.mButton.setText("收藏");
                mHolder.mButton.setTextColor(getResources().getColor(R.color.white));
                mHolder.mButton.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            } else {
                mHolder.mButton.setText("取消");
                mHolder.mButton.setTextColor(getResources().getColor(R.color.primaryColor));
                mHolder.mButton.setBackgroundColor(getResources().getColor(R.color.gray_light));
            }
            // 收藏和取消收藏的动作监听
            mHolder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getApplicationContext(), "收藏", Toast.LENGTH_SHORT).show();

                    CrudTask crudTask = new CrudTask();
                    crudTask.execute(mBook);

                }
            });

            return convertView;
        }

        public final class ViewHolder{
            public ImageView mSearchBookState;
            public TextView mTitle;
            public TextView mPublisher;
            public TextView mPubdate;
            public TextView mBookId;
            public TextView mAuthor;
            public TextView mButton;
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
            BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(getActivity());
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
            if (operation) {
                if (result) {
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "添加成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "添加失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (result) {
                    IsCollectionCancled=true;
                    Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
                }
            }

            if(IsCollectionCancled){
                refreshBook();
                IsCollectionCancled=false;
            }
            super.onPostExecute(result);
        }


    }


    private class GetBookCollectionTask extends AsyncTask<Void, Void, List<SimpleBaseBook>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<SimpleBaseBook> doInBackground(Void... params) {
            BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(getActivity());
            return mDbHelper.getAllBookCollections();
        }

        @Override
        protected void onPostExecute(List<SimpleBaseBook> resultBooks) {
            mBookList = resultBooks;
            refreshView();
            super.onPostExecute(resultBooks);
        }
    }

    private void refreshView(){
        mAdapter.notifyDataSetChanged();
        ActionBar mActionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        String title=getResources().getString(R.string.nav_collect_book);
        mActionBar.setTitle(title+"("+mBookList.size()+")");
        if(mBookList.isEmpty()){
            mEmptyInformation.setVisibility(View.VISIBLE);
        }else{
            mEmptyInformation.setVisibility(View.GONE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        refreshBook();
    }

}
