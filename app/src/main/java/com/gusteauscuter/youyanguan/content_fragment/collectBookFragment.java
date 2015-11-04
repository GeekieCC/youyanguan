package com.gusteauscuter.youyanguan.content_fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.gusteauscuter.youyanguan.data_Class.bookdatabase.BookCollectionDbHelper;
import com.gusteauscuter.youyanguan.internet.connectivity.NetworkConnectivity;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {searchBook Fragment} subclass.
 */
public class collectBookFragment extends Fragment{


    private List<ResultBook> mBookList=new ArrayList<>();
    private GridView mListView;
    private LayoutInflater mLayoutInflater;
    private ProgressBar mProgressBar;

    private CollectSearchBookAdapter mAdapter;

    private TextView mTotalNumber;
    private TextView mEmptyInformation;
    private boolean IsCollectionCancled=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book_collection, container, false);
        mLayoutInflater=inflater;

        mEmptyInformation=(TextView) view.findViewById(R.id.emptyInformation);
        mEmptyInformation.setVisibility(View.GONE);

        mProgressBar=(ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mTotalNumber=(TextView) view.findViewById(R.id.totalNumber);
        mListView = (GridView) view.findViewById(R.id.bookListView);

        mAdapter = new CollectSearchBookAdapter() ;
        mListView.setAdapter(mAdapter);
        mTotalNumber.setText(String.valueOf(mBookList.size()));


        refreshBook();

        return view;
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
            final ResultBook mResultBook = mBookList.get(position);

            if (convertView == null) {

                convertView=mInflater.inflate(R.layout.card_search_book, container, false);

                mHolder =new ViewHolder();
                mHolder.mBookPicture=(ImageView) convertView.findViewById(R.id.searchBookPicture);
                mHolder.mTitle=((TextView) convertView.findViewById(R.id.searchBook_Title));
                mHolder.mPublisher=((TextView) convertView.findViewById(R.id.searchBook_Publisher));
                mHolder.mPubdate=((TextView) convertView.findViewById(R.id.searchBook_Pubdate));
                mHolder.mBookId=((TextView) convertView.findViewById(R.id.searchBook_BookId));
                mHolder.mAuthor=((TextView) convertView.findViewById(R.id.searchBook_Author));
                mHolder.mButton = (Button) convertView.findViewById(R.id.collect_book);

                convertView.setTag(mHolder);

            } else{
                mHolder=(ViewHolder) convertView.getTag();
            }


            final boolean isBorrowable = mResultBook.isBorrowable();
            if (isBorrowable) {
                mHolder.mBookPicture.setImageResource(R.drawable.book_sample_blue);
            } else {
                mHolder.mBookPicture.setImageResource(R.drawable.book_sample_pencil);
            }


            // TO 设置Book对应属性
            String title="【" + (position + 1) + "】"+mResultBook.getTitle();
            String publisher="出版社："+mResultBook.getPublisher();
            String pubdate="出版日期："+mResultBook.getPubdate();
            String bookId="索书号："+mResultBook.getSearchNum();
            String author="作者："+mResultBook.getAuthor();

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
                        bundle.putSerializable("bookToShowDetail", mResultBook);
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
            if (!mResultBook.isCollected()) {
                mHolder.mButton.setText("点击收藏");
            } else {
                mHolder.mButton.setText("取消收藏");
            }
            // 收藏和取消收藏的动作监听
            mHolder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getApplicationContext(), "收藏", Toast.LENGTH_SHORT).show();

                    CrudTask crudTask = new CrudTask();
                    crudTask.execute(mResultBook);

                }
            });

            return convertView;
        }

        public final class ViewHolder{
            public ImageView mBookPicture;
            public TextView mTitle;
            public TextView mPublisher;
            public TextView mPubdate;
            public TextView mBookId;
            public TextView mAuthor;
            public Button mButton;
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
            BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(getActivity());
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

            mBookList=resultBooks;
            mAdapter.notifyDataSetChanged();
            mTotalNumber.setText(String.valueOf(mBookList.size()));

            super.onPostExecute(resultBooks);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 0 && resultCode == 0 && data != null) {
//            int position = data.getIntExtra("position", 0);
//            boolean operation = data.getBooleanExtra("isCollected", false);
//            ResultBook resultBook = (ResultBook) mAdapter.getItem(position);
//            resultBook.setIsCollected(operation);
//            mAdapter.notifyDataSetChanged();
//        }
        refreshBook();

    }


}
