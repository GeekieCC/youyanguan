package com.gusteauscuter.youyanguan.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.activity.BookDetailActivity;
import com.gusteauscuter.youyanguan.common.PublicString;
import com.gusteauscuter.youyanguan.databaseHelper.CollectBookAsy;
import com.gusteauscuter.youyanguan.domain.BookBase;
import com.gusteauscuter.youyanguan.domain.LocationInfo;
import com.gusteauscuter.youyanguan.util.NetworkConnectUtil;

public abstract class BookBaseAdapter extends ZBaseAdapter {

    private boolean isSearched;// 标识是搜索（true）还是收藏（false）
    private static int COLOR_DEFAULT= Color.parseColor("#295086");
    private static int COLOR_VIEWED=Color.parseColor("#938490");
    private static boolean isViewed = false ;
    int colorWhite = mContext.getResources().getColor(R.color.white);
    int colorPrimary =mContext.getResources().getColor(R.color.primaryColor);
    int colorGrayLight =mContext.getResources().getColor(R.color.gray_light);
    int colorGray =mContext.getResources().getColor(R.color.gray);

    public BookBaseAdapter(Context context, boolean isSearched){
        super(context);
        this.isSearched = isSearched;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup container) {
        // 组件-变量匹配
        ViewHolder mHolder;
        if (convertView == null) {
            convertView= mLayoutInflater.inflate(R.layout.card_search_book, container, false);
            mHolder =new ViewHolder();
            mHolder.mSearchBookState = (ImageView) convertView.findViewById(R.id.searchBookState);

            if(!isSearched) {
                // 收藏显示位置
                mHolder.mSearchBookState.setVisibility(View.GONE);
            }
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
        final BookBase mBook = (BookBase) mItemList.get(position);

        if(isSearched) {
            // 根据馆藏条件，设置视图
            int borrowCondition = mBook.getLocationSummary();
            if (borrowCondition == LocationInfo.BOTH_YES) { //两校区都可借
                mHolder.mSearchBookState.setImageResource(R.drawable.ic_s_n);
                mHolder.mSearchBookState.setColorFilter(colorPrimary);
            } else if (borrowCondition == LocationInfo.BOTH_NOT) { //两校区都不可借
                mHolder.mSearchBookState.setImageResource(R.drawable.ic_not_s_n);
                mHolder.mSearchBookState.setColorFilter(colorGray);
            } else if (borrowCondition == LocationInfo.NORTH_ONLY) { // 只有北校区可借
                mHolder.mSearchBookState.setImageResource(R.drawable.ic_north);
                mHolder.mSearchBookState.setColorFilter(colorPrimary);
            } else if (borrowCondition == LocationInfo.SOUTH_ONLY) { // 只有南校区可借
                mHolder.mSearchBookState.setImageResource(R.drawable.ic_south);
                mHolder.mSearchBookState.setColorFilter(colorPrimary);
            } else if (borrowCondition == LocationInfo.UNKNOWN) { // 不知道是否可借
                mHolder.mSearchBookState.setImageResource(R.drawable.ic_not_known);
                mHolder.mSearchBookState.setColorFilter(colorGray);
            }
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
                    bundle.putString(PublicString.BUNDLE_BookId, mBook.getBookId());
                    bundle.putString(PublicString.BUNDLE_Title, mBook.getTitle());
                    bundle.putInt(PublicString.BUNDLE_Position, position);
                    bundle.putBoolean(PublicString.BUNDLE_IsFromBase, true);
                    bundle.putString(PublicString.BUNDLE_Author, mBook.getAuthor());
                    bundle.putString(PublicString.BUNDLE_Isbn,mBook.getIsbn());
                    bundle.putString(PublicString.BUNDLE_Publisher,mBook.getPublisher());
                    bundle.putString(PublicString.BUNDLE_Pubdate,mBook.getPubdate());
                    bundle.putString(PublicString.BUNDLE_SearchNum,mBook.getSearchNum());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            }
        });
        // 收藏和取消收藏的动作监听
        mHolder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new mCollectBookAsy(mContext,mBook).execute();
            }
        });
        return convertView;
    }

    private class mCollectBookAsy extends CollectBookAsy {

        public mCollectBookAsy(Context context , BookBase bookBase){
            super(context, bookBase);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                // 如果是取消操作，而且是来自于收藏，则移除对应项
                if(!mOperationAdd&&!isSearched)
                    mItemList.remove(mBookBaseToCollect);
                notifyDataSetChanged();

            }
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        onDataChanged();
    }

    protected abstract void onDataChanged() ;

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