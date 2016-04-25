package com.gusteauscuter.youyanguan.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.activity.BookDetailActivity;
import com.gusteauscuter.youyanguan.data_Class.book.Book;
import com.gusteauscuter.youyanguan.internetService.LibraryLoginClient;
import com.gusteauscuter.youyanguan.util.ACacheUtil;
import com.gusteauscuter.youyanguan.util.BitmapUtil;
import com.gusteauscuter.youyanguan.util.CalendarUtil;
import com.gusteauscuter.youyanguan.util.NetworkConnectUtil;
import com.gusteauscuter.youyanguan.util.ShareDataUtil;
import com.gusteauscuter.youyanguan.view.XImageView;

import org.apache.commons.httpclient.ConnectTimeoutException;

import java.net.SocketTimeoutException;
import java.util.List;


public class CollectedBookAdapter extends ZBaseAdapter {

    private static final int HAS_PICTURE = 5;
    private static final int HAS_NO_PICTURE = 10;

    public CollectedBookAdapter(Context context){
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup container) {

        final ViewHolder mHolder;
        if (convertView == null) {
            convertView=mLayoutInflater.inflate(R.layout.card_book,container, false);
            mHolder =new ViewHolder();
            mHolder.mButtonBorrow=(TextView) convertView.findViewById(R.id.button_Borrow);
            mHolder.mBookPicture=(XImageView) convertView.findViewById(R.id.BookPicture);
            mHolder.mName=((TextView) convertView.findViewById(R.id.text_Title));
            mHolder.mBorrowDay=((TextView) convertView.findViewById(R.id.text_BorrowDay));
            mHolder.mReturnDay=(TextView) convertView.findViewById(R.id.text_ReturnDay);
            mHolder.mBorrowedTime=((TextView) convertView.findViewById(R.id.text_BorrowedTime));
            convertView.setTag(mHolder);
        } else{
            mHolder=(ViewHolder) convertView.getTag();
        }

        final Book mBook = (Book) mItemList.get(position);
        mHolder.mButtonBorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBook.getBorrowedTime() < mBook.getMaxBorrowTime()) {
                    boolean isConnected = NetworkConnectUtil.isConnected(mContext);
                    if(isConnected){
                        RenewBookAsy renewBookAsy = new RenewBookAsy(mBook);
                        ShareDataUtil shareDataUtil=new ShareDataUtil(mContext);
                        renewBookAsy.execute(shareDataUtil.getUSERNAME(), shareDataUtil.getPASSWORD());
                    } else{
                        Toast.makeText(mContext, R.string.internet_not_connected, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "已达最大续借次数，请及时归还", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mHolder.mBookPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isConnected = NetworkConnectUtil.isConnected(mContext);
                if(isConnected){
                    Intent intent =new Intent(mContext, BookDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bookToShowDetail", mBook);
                    bundle.putInt("position", position);
                    intent.putExtras(bundle);
                    //int requestCode = (mBook.getPicture() != null) ? HAS_PICTURE : HAS_NO_PICTURE;
                    mContext.startActivity(intent);
                }else{
                    Toast.makeText(mContext, R.string.internet_not_connected, Toast.LENGTH_SHORT).show();
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
                mContext.getResources().getColor(R.color.book_color_1),
                mContext.getResources().getColor(R.color.book_color_2),
                mContext.getResources().getColor(R.color.book_color_3),
                mContext.getResources().getColor(R.color.book_color_4),
                mContext.getResources().getColor(R.color.book_color_5),
                mContext.getResources().getColor(R.color.book_color_6),
                mContext.getResources().getColor(R.color.book_color_7),
        };

        int no =(position)%book_color.length;
        //设置图片
        ACacheUtil mCache = ACacheUtil.get(mContext);
        byte[] bitmap2Bytes = mCache.getAsBitmap2Bytes(mBook.getBookId());
        if (bitmap2Bytes != null) {
            mBook.setPicture(bitmap2Bytes);
        }
        if (mBook.getPicture() != null) {
            mHolder.mBookPicture.setImageBitmap(BitmapUtil.getBitmap(mBook.getPicture()));
            mHolder.mBookPicture.setImageAlpha(255);
            mHolder.mBookPicture.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            mHolder.mName.setVisibility(View.GONE);
        }else {
            mHolder.mBookPicture.setBackgroundColor(book_color[no]);
            mHolder.mBookPicture.setImageAlpha(0);
            mHolder.mName.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public final class ViewHolder{
        public TextView mButtonBorrow;
        public XImageView mBookPicture;
        public TextView mName;
        public TextView mBorrowDay;
        public TextView mReturnDay;
        public TextView mBorrowedTime;
    }


    private class RenewBookAsy extends AsyncTask<String, Void, List<Book>> {
        private boolean serverOK = true;
        private Book bookToRenew;
        public RenewBookAsy(Book bookToRenew) {
            this.bookToRenew = bookToRenew;
        }

        @Override
        protected List<Book> doInBackground(String... account) {
            List<Book> bookLists = null;
            try {
                LibraryLoginClient libClient = new LibraryLoginClient();
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
            if (serverOK) {
                if (result != null) {
                    setItems(result);
                    new CalendarUtil(mContext).new AddCalendarThread(
                            (List<Book>) getItemList())
                            .start();
                    Toast.makeText(mContext, "续借成功" , Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "本书尚未到续借时间", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, R.string.server_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
