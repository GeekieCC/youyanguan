package com.gusteauscuter.youyanguan.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.activity.BookDetailActivity;
import com.gusteauscuter.youyanguan.definedDataClass.ResultBook;
import com.gusteauscuter.youyanguan.databaseHelper.BookCollectionDbHelper;
import com.gusteauscuter.youyanguan.util.NetworkConnectUtil;

public class SearchBookAdapter extends ZBaseAdapter {

    private static int COLOR_DEFAULT= Color.parseColor("#295086");
    private static int COLOR_VIEWED=Color.parseColor("#938490");
    private static boolean isViewed = false ;
    int colorWhite = mContext.getResources().getColor(R.color.white);
    int colorPrimary =mContext.getResources().getColor(R.color.primaryColor);
    int colorGrayLight =mContext.getResources().getColor(R.color.gray_light);
    int colorGray =mContext.getResources().getColor(R.color.gray);


    public SearchBookAdapter(Context context){
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup container) {
        // 组件-变量匹配
        ViewHolder mHolder;
        if (convertView == null) {
            convertView= mLayoutInflater.inflate(R.layout.card_search_book, container, false);
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
        // 获取对象
        final ResultBook mBook = (ResultBook) mItemList.get(position);


        // 根据馆藏条件，设置视图
        int borrowCondition = mBook.getBorrowCondition();
        if (borrowCondition == ResultBook.BOTH_YES) { //两校区都可借
            mHolder.mSearchBookState.setImageResource(R.drawable.ic_s_n);
            mHolder.mSearchBookState.setColorFilter(colorPrimary);
        } else if (borrowCondition == ResultBook.BOTH_NOT) { //两校区都不可借
            mHolder.mSearchBookState.setImageResource(R.drawable.ic_not_s_n);
            mHolder.mSearchBookState.setColorFilter(colorGray);
        } else if (borrowCondition == ResultBook.NORTH_ONLY) { // 只有北校区可借
            mHolder.mSearchBookState.setImageResource(R.drawable.ic_north);
            mHolder.mSearchBookState.setColorFilter(colorPrimary);
        } else if (borrowCondition == ResultBook.SOUTH_ONLY) { // 只有南校区可借
            mHolder.mSearchBookState.setImageResource(R.drawable.ic_south);
            mHolder.mSearchBookState.setColorFilter(colorPrimary);
        } else if (borrowCondition == ResultBook.UNKNOWN) { // 不知道是否可借
            mHolder.mSearchBookState.setImageResource(R.drawable.ic_not_known);
            mHolder.mSearchBookState.setColorFilter(colorGray);
        }
        // 设置Book对应属性
        String title="【" + (position + 1) + "】"+mBook.getTitle();
        String publisher="出版社："+mBook.getPublisher();
        String pubdate="出版日期："+mBook.getPubdate();
        String bookId="索书号："+mBook.getSearchNum();
        String author="作者："+mBook.getAuthor();
        mHolder.mTitle.setText(title);
        mHolder.mTitle.setTextColor(isViewed?COLOR_VIEWED:COLOR_DEFAULT);
        mHolder.mBookId.setText(bookId);
        mHolder.mAuthor.setText(author);
        mHolder.mPublisher.setText(publisher);
        mHolder.mPubdate.setText(pubdate);

        // 对搜索出来的结果显示时，区别已收藏和未收藏图书
        boolean collected =mBook.isCollected();
        mHolder.mButton.setText(collected ? "取消" : "收藏");
        mHolder.mButton.setTextColor(collected ? colorPrimary : colorWhite);
        mHolder.mButton.setBackgroundColor(collected? colorGrayLight : colorPrimary);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isConnected = NetworkConnectUtil.isConnected(mContext);
                if (isConnected) {
                    Intent intent = new Intent(mContext, BookDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bookToShowDetail", mBook);
                    bundle.putInt("position", position);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            }
        });


        // 收藏和取消收藏的动作监听
        mHolder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CrudTask().execute(mBook);
            }
        });

        return convertView;
    }

    private class CrudTask extends AsyncTask<ResultBook, Void, Boolean> {
        private boolean operation;// 操作为添加时，为true;操作为删除时，为false

        @Override
        protected Boolean doInBackground(ResultBook... resultBooks) {
            //操作成功与否
            boolean result = false;
            BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(mContext);
            if (!resultBooks[0].isCollected()) {
                operation = true;
                if (mDbHelper.addBook(resultBooks[0]) != -1) {
                    resultBooks[0].setIsCollected(true);
                    result = true;
                }
            } else {
                operation = false;
                if (mDbHelper.deleteBook(resultBooks[0]) != 0) {
                    resultBooks[0].setIsCollected(false);
                    result = true;
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (operation) {
                if (result) {
                    notifyDataSetChanged();
                    Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (result) {
                    notifyDataSetChanged();
                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
            super.onPostExecute(result);
        }

    }

    static class ViewHolder{
        public ImageView mSearchBookState;
        public TextView mTitle;
        public TextView mPublisher;
        public TextView mPubdate;
        public TextView mBookId;
        public TextView mAuthor;
        public TextView mButton;
    }
}