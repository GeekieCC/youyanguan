package com.gusteauscuter.youyanguan.content_fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.data_Class.HomeItem;
import com.gusteauscuter.youyanguan.NavigationActivity;

import java.util.ArrayList;
import java.util.List;


public class homeFragment extends Fragment {

    private GridView mListView;
    private LayoutInflater mLayoutInflater;
    private TextView emptyInformation;
    private HomeItemAdapter mAdapter;

    private List<HomeItem> mHomeItemList=new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_list, container, false);
        mLayoutInflater=inflater;

        mHomeItemList=new ArrayList<>();

        mListView = (GridView) view.findViewById(R.id.homeListView);
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

        mAdapter = new HomeItemAdapter() ;
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        emptyInformation =(TextView) view.findViewById(R.id.emptyInformation);

        initDataFromActivity();

        return view;
    }


    /* @ WangCe
* TODO  deal with dataChange with the "NavigationActivity"
* such as UserLogin, BookList if they are already existed
* so don't have to get data again
* */
    private void initDataFromActivity(){

        mHomeItemList=((NavigationActivity)getActivity()).getmHomeItemList();
        if (mHomeItemList.size()==0)
            emptyInformation.setText("暂无日程，请添加");
//            mHomeItemList.add(new HomeItem());
        mAdapter.notifyDataSetChanged();

    }


    private class HomeItemAdapter extends BaseAdapter {
        //HomeItem mHomeItem = new HomeItem();
        @Override
        public int getCount() {
            return mHomeItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return mHomeItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mHomeItemList.get(position).hashCode();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {

            ViewHolder mHolder=null;

            if (convertView == null) {
                convertView=mLayoutInflater.inflate(R.layout.card_home,container, false);
                mHolder =new ViewHolder();

                mHolder.mTextViewColor=(ImageView) convertView.findViewById(R.id.homePicture);
                mHolder.mTitle=((TextView) convertView.findViewById(R.id.home_Title));
                mHolder.mDescription=((TextView) convertView.findViewById(R.id.home_Description));

                convertView.setTag(mHolder);

            } else{
                mHolder=(ViewHolder) convertView.getTag();
            }

            mHolder.mTextViewColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(getActivity(), mHomeItemList.get(position).getDescription(),
                            Toast.LENGTH_SHORT).show();

                }
            });

            // TODO 设置HomeItem对应属性

            String title=mHomeItemList.get(position).getDate();
            String description="【还书】\n《"+mHomeItemList.get(position).getTitle()+"》";

            mHolder.mTitle.setText(title);
            mHolder.mDescription.setText(description);

            return convertView;
            }


        public final class ViewHolder{
            public ImageView mTextViewColor;
            public TextView mTitle;
            public TextView mDescription;

        }

    }


}
