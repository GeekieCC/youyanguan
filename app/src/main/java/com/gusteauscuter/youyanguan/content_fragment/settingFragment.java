package com.gusteauscuter.youyanguan.content_fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.DepActivity.AboutActivity;
import com.gusteauscuter.youyanguan.NavigationActivity;
import com.gusteauscuter.youyanguan.R;

import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;


public class settingFragment extends Fragment {


    public settingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        view.findViewById(R.id.check_update).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
                            @Override
                            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {

                                if (updateStatus == UpdateStatus.Yes) {
                                    //版本有更新
                                } else if (updateStatus == UpdateStatus.No) {
                                    Toast.makeText(getActivity(), R.string.update_state_no, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        BmobUpdateAgent.forceUpdate(getActivity());

                    }
                });
        view.findViewById(R.id.feedback).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((NavigationActivity)getActivity()).SendEmailIntent("Setting");
                    }
                });
        view.findViewById(R.id.about).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent =new Intent(getActivity(), AboutActivity.class);
                        startActivity(intent);
                    }
                });

        return view;
    }


}
