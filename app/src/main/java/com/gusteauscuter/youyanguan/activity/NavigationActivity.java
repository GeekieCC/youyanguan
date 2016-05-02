package com.gusteauscuter.youyanguan.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.fragment.bookSearchFragment;
import com.gusteauscuter.youyanguan.fragment.bookBorrowedFragment;
import com.gusteauscuter.youyanguan.fragment.bookCollectedFragment;

import com.gusteauscuter.youyanguan.common.PublicURI;
import com.gusteauscuter.youyanguan.util.FileCopyUtil;
import com.gusteauscuter.youyanguan.util.UpdateManagerUtil;
import com.gusteauscuter.youyanguan.util.SharedPreferencesUtil;
import com.gusteauscuter.youyanguan.view.RoundImageView;

import java.io.File;


public class NavigationActivity extends ZLeftDrawerActivity implements  View.OnClickListener{

    private bookBorrowedFragment mBookBorrowedFragment;
    private bookCollectedFragment mBookCollectedFragment;
    private bookSearchFragment mBookSearchFragment;

    private RoundImageView mHeaderImage;
    private TextView mLinkBackground;
    private ImageView mDrawerBackground;

    private static final int REQUEST_LOAD_IMAGE_header = 1;
    private static final int REQUEST_LOAD_IMAGE_background = 2;
    private static final int REQUEST_LOGIN = 3;

    private static final String mBackgroundFileName = PublicURI.PATH_BG_HOME;
    private static final String mHeaderFileName = PublicURI.PATH_HEADER_IMAGE;

    private int timesOfClickSecretPosition=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        jumpToFragmentWithTitle(mBookSearchFragment, R.string.nav_search_book);
        new UpdateManagerUtil(this).checkUpdateInfo(true);
    }

    public void initView() {

        setContentView(R.layout.activity_navigation_view);
        setSuperDrawerLayout(R.id.drawer_layout, R.id.navigation_view, R.id.content_frame_layout);
        setupActionbar(R.id.id_toolbar, R.drawable.ic_menu);

        mLinkBackground =(TextView)findViewById(R.id.id_link);
        mHeaderImage=(RoundImageView)findViewById(R.id.header);
        mDrawerBackground=(ImageView)findViewById(R.id.drawer_background);
        mLinkBackground.setOnClickListener(this);
        mHeaderImage.setOnClickListener(this);

        File headFile = new File(mHeaderFileName);
        if (headFile.exists()) {
            Bitmap bitmapHeader=BitmapFactory.decodeFile(mHeaderFileName);
            mHeaderImage.setImageBitmap(bitmapHeader);
        }
        File backgroundFile = new File(mBackgroundFileName);
        if (backgroundFile.exists()) {
            Bitmap bitmapBackground=BitmapFactory.decodeFile(mBackgroundFileName);
            mDrawerBackground.setImageBitmap(bitmapBackground);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK)
            return;
        if (requestCode == REQUEST_LOGIN) {
            jumpToBookBorrowedFragment();
            return;
        }
        // 以下为图片设置
        if(data==null)
            return;
        Uri selectedImageUri = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImageUri,
                filePathColumn, null, null, null);
        if(cursor==null)
            return;
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        Bitmap bitmap=BitmapFactory.decodeFile(picturePath);

        if (requestCode == REQUEST_LOAD_IMAGE_header) {
            mHeaderImage.setImageBitmap(bitmap);
            FileCopyUtil.CopySdcardFile(picturePath, mHeaderFileName);
        }
        if (requestCode == REQUEST_LOAD_IMAGE_background) {
            mDrawerBackground.setImageBitmap(bitmap);
            FileCopyUtil.CopySdcardFile(picturePath, mBackgroundFileName);
        }
    }

    public void jumpToBookBorrowedFragment(){
        SharedPreferencesUtil mSharedPreferencesUtil = new SharedPreferencesUtil(this);
        if (mSharedPreferencesUtil.getISLOGINED()) {
            if (mBookBorrowedFragment == null)
                mBookBorrowedFragment = new bookBorrowedFragment();
            jumpToFragmentWithTitle(mBookBorrowedFragment, R.string.nav_book_borrowed);
        } else
            JumpToLoginActivity();
    }

    public void JumpToLoginActivity(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivityForResult(intent,REQUEST_LOGIN);
    }

    private void JumpToBookCollectedFragment() {
        if (mBookCollectedFragment ==null)
            mBookCollectedFragment =new bookCollectedFragment();
        jumpToFragmentWithTitle(mBookCollectedFragment,R.string.nav_collect_book);
    }

    private void JumpToBookSearchFragment(){
        if(mBookSearchFragment ==null)
            mBookSearchFragment =new bookSearchFragment();
        jumpToFragmentWithTitle(mBookSearchFragment,R.string.nav_search_book);
    }

    private void jumpToFragmentWithTitle(Fragment fragment,@StringRes int idTitle){
        closeDrawer();
        if(fragment ==null)
            fragment =new bookSearchFragment();
        setActionbarTitle(idTitle);
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        mTransaction.replace(R.id.content_frame_layout, fragment);
        mTransaction.commit();
    }

    private void JumpToSettingActivity(){
        Intent intent =new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void goToNavigationItem(MenuItem menuItem){

        // 导航栏动作，跳转到子页面
        switch (menuItem.getItemId()) {
            case (R.id.nav_library) :
                jumpToBookBorrowedFragment();
                break;
            case (R.id.nav_collect_book) :
                JumpToBookCollectedFragment();
                break;
            case (R.id.nav_search_book) :
                JumpToBookSearchFragment();
                break;
            case (R.id.nav_setting) :
                JumpToSettingActivity() ;
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.header:
                Toast.makeText(getApplicationContext(), "设置头像...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_LOAD_IMAGE_header);
                break;
            case R.id.drawer_background:
                break;
            case R.id.id_link:
                if(timesOfClickSecretPosition ==10){
                    timesOfClickSecretPosition =0;
                    Toast.makeText(getApplicationContext(), "设置背景...", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent1, REQUEST_LOAD_IMAGE_background);
                }else{
                    timesOfClickSecretPosition++;
                }
                break;
        }
    }

}
