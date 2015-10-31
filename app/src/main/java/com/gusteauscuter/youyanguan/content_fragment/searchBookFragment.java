package com.gusteauscuter.youyanguan.content_fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.DepActivity.SearchResultActivity;
import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.data_Class.book.Book;
import com.gusteauscuter.youyanguan.data_Class.book.BookSearchEngine;
import com.gusteauscuter.youyanguan.data_Class.userLogin;
import com.gusteauscuter.youyanguan.internet.connectivity.NetworkConnectivity;

/**
 * A simple {searchBook Fragment} subclass.
 */
public class searchBookFragment extends Fragment{

    private SearchView searchBookEditText;
    private Spinner searchBookTypeSpinner;
    private CheckBox mSouthCheckBox;
    private CheckBox mNorthCheckBox;

    private String bookToSearch;
    private String searchBookType="TITLE";
    private boolean isAllowedToBorrow;
    private int searchSN;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_book, container, false);

        searchBookTypeSpinner=(Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.SearchBookType, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchBookTypeSpinner.setAdapter(adapter);
        searchBookTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String type = searchBookTypeSpinner.getItemAtPosition(i).toString();
                switch (type) {
                    case "标题":
                        searchBookType = "TITLE";
                        break;
                    case "作者":
                        searchBookType = "AUTHOR";
                        break;
                    case "ISBN":
                        searchBookType = "ISBN";
                        break;
                    case "ISSN":
                        searchBookType = "ISBN.011$a";
                        break;
                    case "出版社":
                        searchBookType = "PUBLISHER";
                        break;
                    case "分类号":
                        searchBookType = "CLASSNO";
                        break;
                    case "主题词":
                        searchBookType = "SUBJECT";
                        break;
                    case "统一刊号":
                        searchBookType = "UNIONNO";
                        break;
                    case "馆藏条码":
                        searchBookType = "BARCODE";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                searchBookType = "TITLE";
            }
        });

        mSouthCheckBox=(CheckBox)view.findViewById(R.id.SouthCheckBox);
        mNorthCheckBox=(CheckBox)view.findViewById(R.id.NorthCheckBox);
        initDataCheckState();

        searchBookEditText = (SearchView) view.findViewById(R.id.searchBookEditText);
//        searchBookEditText.setSubmitButtonEnabled(true);
        searchBookEditText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchBook();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return view;
    }

    public void initDataCheckState(){

        SharedPreferences shareData = getActivity().getSharedPreferences("data", 0);
        Boolean SouthChecked = shareData.getBoolean("South", false);
        Boolean NorthChecked = shareData.getBoolean("North", true);
        mSouthCheckBox.setChecked(SouthChecked);
        mNorthCheckBox.setChecked(NorthChecked);
    }

    public void saveDataCheckState() {
        SharedPreferences.Editor shareData =getActivity().getSharedPreferences("data",0).edit();
        shareData.putBoolean("South",mSouthCheckBox.isChecked());
        shareData.putBoolean("North",mNorthCheckBox.isChecked());
        shareData.putBoolean("ISLOGINED",false);
        shareData.commit();
    }

        private void SearchBook(){

        saveDataCheckState();
        bookToSearch = searchBookEditText.getQuery().toString();
        if(!bookToSearch.equals("")) {

            boolean isConnected = NetworkConnectivity.isConnected(getActivity());
            if (isConnected) {
                //searchBookType;
                isAllowedToBorrow = true;
                if (mNorthCheckBox.isChecked()&&mSouthCheckBox.isChecked()){
                    searchSN= BookSearchEngine.BOTH_CAMPUS;
                }else if(mNorthCheckBox.isChecked()){
                    searchSN=BookSearchEngine.NORTH_CAMPUS;
                }else if(mSouthCheckBox.isChecked()){
                    searchSN= BookSearchEngine.SOUTH_CAMPUS;
                }else{
                    isAllowedToBorrow = false;
                }

                Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bookToSearch", bookToSearch);
                bundle.putSerializable("searchBookType", searchBookType);
                bundle.putSerializable("isAllowedToBorrow", isAllowedToBorrow);
                bundle.putSerializable("searchSN", searchSN);
                intent.putExtras(bundle);
                startActivity(intent);

            } else {
                Toast.makeText(getActivity(), R.string.internet_not_connected, Toast.LENGTH_SHORT).show();
            }
        }else
            Toast.makeText(getActivity(), "请输入搜索内容", Toast.LENGTH_SHORT).show();
    }

}
