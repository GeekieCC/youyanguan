package com.gusteauscuter.youyanguan.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.adapter.BookBaseAdapter;
import com.gusteauscuter.youyanguan.databaseHelper.BookCollectionDbHelper;
import com.gusteauscuter.youyanguan.domain.BookBase;
import com.gusteauscuter.youyanguan.util.SharedPreferencesUtil;

import java.util.List;

/**
 * A simple {searchBook Fragment} subclass.
 */
public class bookCollectedFragment extends Fragment{

    private ActionBar mActionBar;
    private BookBaseAdapter mAdapter;

    private ListView mListView;
    private ProgressBar mProgressBar;
    private TextView mEmptyInformation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book_collected, container, false);
        mActionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        mEmptyInformation=(TextView) view.findViewById(R.id.emptyInformation);
        mProgressBar=(ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mListView = (ListView) view.findViewById(R.id.bookListView);

        if(mAdapter==null)
            mAdapter = new BookBaseAdapter(getActivity(),false){

                @Override
                protected void onDataChanged() {
                    String title=getResources().getString(R.string.nav_collect_book);
                    int count =mAdapter.getCount();
                    mActionBar.setTitle(title+"("+count+")");
                    mEmptyInformation.setVisibility((count == 0) ? View.VISIBLE : View.GONE);
                }
            };

        mListView.setAdapter(mAdapter);
        new GetBookCollectionTask().execute();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getActivity());
        boolean action = sharedPreferencesUtil.getCollectAction();
        if(action){
            boolean iscollected = sharedPreferencesUtil.getCollectResult();
            int position = sharedPreferencesUtil.getCollectPosition();
            BookBase bookBaseTmp = (BookBase)(mAdapter.getItem(position));
            bookBaseTmp.setIsCollected(iscollected);
            if(!iscollected)
                mAdapter.getItemList().remove(position);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class GetBookCollectionTask extends AsyncTask<Void, Void, List<BookBase>> {

        @Override
        protected List<BookBase> doInBackground(Void... params) {
            BookCollectionDbHelper mDbHelper = new BookCollectionDbHelper(getActivity());
            return mDbHelper.getAllBookCollections();
        }

        @Override
        protected void onPostExecute(List<BookBase> resultBooks) {
            super.onPostExecute(resultBooks);
            mAdapter.setItems(resultBooks);
        }
    }

}
