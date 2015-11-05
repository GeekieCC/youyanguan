package com.gusteauscuter.youyanguan.content_fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gusteauscuter.youyanguan.DepActivity.SearchBookResultActivity;
import com.gusteauscuter.youyanguan.R;

public class bookSearchFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_book, container, false);
        TextView searchBookEditText = (TextView) view.findViewById(R.id.searchBook);
        searchBookEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchBookResultActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
