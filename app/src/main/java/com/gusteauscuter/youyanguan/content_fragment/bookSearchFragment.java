package com.gusteauscuter.youyanguan.content_fragment;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.DepActivity.SearchBookResultActivity;
import com.gusteauscuter.youyanguan.interfaceYYG.IDirectory_File;
import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.util.FileOperation;
import com.gusteauscuter.youyanguan.util.ScreenShot;

import java.io.File;

public class bookSearchFragment extends Fragment implements IDirectory_File{
    private static int RESULT_LOAD_IMAGE_search_background = 3;
    private static int RESULT_OK = -1;
    private int timesOfClickSecretPosition=0;

    private ImageView mSearchBackground;

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

        mSearchBackground=(ImageView) view.findViewById(R.id.search_background);
        File file = new File(stringSearchBackgroundName);
        if (file.exists()) {
            Bitmap bitmapHeader=BitmapFactory.decodeFile(stringSearchBackgroundName);
            mSearchBackground.setImageBitmap(bitmapHeader);
        }
//        if(bitmapHeader!=null){
//            mSearchBackground.setImageBitmap(bitmapHeader);
//        }

        view.findViewById(R.id.secret_book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timesOfClickSecretPosition==10) {
                    timesOfClickSecretPosition = 0;
                    Toast.makeText(getActivity(), "设置搜索背景...", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, RESULT_LOAD_IMAGE_search_background);
                }else{
                    timesOfClickSecretPosition++;
                }

            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE_search_background && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap= BitmapFactory.decodeFile(picturePath);
            mSearchBackground.setImageBitmap(bitmap);

            FileOperation.CopySdcardFile(picturePath, stringSearchBackgroundName);
        }

    }

}
