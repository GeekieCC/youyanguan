package com.gusteauscuter.youyanguan;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;


import com.gusteauscuter.youyanguan.DepActivity.SettingActivity;
import com.gusteauscuter.youyanguan.content_fragment.bookCollectedFragment;
import com.gusteauscuter.youyanguan.content_fragment.loginFragment;
import com.gusteauscuter.youyanguan.content_fragment.bookSearchFragment;
import com.gusteauscuter.youyanguan.data_Class.UserLoginInfo;
import com.gusteauscuter.youyanguan.interfaceYYG.IDirectory_File;
import com.gusteauscuter.youyanguan.util.FileOperation;
import com.gusteauscuter.youyanguan.view.RoundImageView;
import com.nineoldandroids.view.ViewHelper;

import com.gusteauscuter.youyanguan.content_fragment.bookBorrowedFragment;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.update.BmobUpdateAgent;


public class NavigationActivity extends AppCompatActivity  implements IDirectory_File,
        View.OnClickListener, View.OnTouchListener,
        GestureDetector.OnGestureListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationViewLeft;
    private ActionBar mActionBar=null;
    private UserLoginInfo mUserLoginInfo;
    private FrameLayout mContentFramelayout;

    private bookBorrowedFragment mBookBorrowedFragment;
    private bookCollectedFragment mBookCollectedFragment;
    private loginFragment mLoginFragment;
    private bookSearchFragment mBookSearchFragment;

    private RoundImageView mHeaderImage;
    private TextView mTextBackground;
    private ImageView mDrawerBackground;

    public Menu mMenu;

    private boolean IsFirstTime =true;

    GestureDetector mGestureDetector;
    private static final int FLING_MIN_DISTANCE = 50;
    private static final int FLING_MIN_VELOCITY = 10;

    private static int RESULT_LOAD_IMAGE_header = 1;
    private static int RESULT_LOAD_IMAGE_background = 2;

    private int timesOfClickSecretPosition=0;
    String arg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
        initEvents();
        Bmob.initialize(this, "213c7ff4ff5c05bee43e1b5f803ee7cd");
        BmobUpdateAgent.update(this);
    }

    /**
     *
     */
    // TODO updateData such as mBookList,mCourseList,mHomeList
    public void initData(){

        SharedPreferences shareData = getSharedPreferences("data", 0);
        String username = shareData.getString("USERNAME", "");
        String password = shareData.getString("PASSWORD", "");
        boolean isLogined = shareData.getBoolean("ISLOGINED", false);
        mUserLoginInfo = new UserLoginInfo(username, password, isLogined);
    }


    public void initView() {

        setContentView(R.layout.activity_navigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.layout_drawer);
        mNavigationViewLeft = (NavigationView) findViewById(R.id.id_nv_menu);

        mTextBackground=(TextView)findViewById(R.id.id_link);
        mHeaderImage=(RoundImageView)findViewById(R.id.header);
        mDrawerBackground=(ImageView)findViewById(R.id.drawer_background);

        File headFile = new File(stringHeaderName);
        if (headFile.exists()) {
            Bitmap bitmapHeader=BitmapFactory.decodeFile(stringHeaderName);
            mHeaderImage.setImageBitmap(bitmapHeader);
        }

        File backgroundFile = new File(stringBackgroundName);
        if (backgroundFile.exists()) {
            Bitmap bitmapBackground=BitmapFactory.decodeFile(stringBackgroundName);
            mDrawerBackground.setImageBitmap(bitmapBackground);
        }

        mHeaderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "设置头像...", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE_header);
            }
        });

        mTextBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(timesOfClickSecretPosition ==10){
                    timesOfClickSecretPosition =0;
                    Toast.makeText(getApplicationContext(), "设置背景...", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, RESULT_LOAD_IMAGE_background);
                }else{
                    timesOfClickSecretPosition++;
                }
            }
        });

        mContentFramelayout = (FrameLayout) findViewById(R.id.container_frame);

        mContentFramelayout.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        setupDrawerContent(mNavigationViewLeft);
//        setupDrawerContent(mNavigationViewRight);

        mGestureDetector = new GestureDetector(this);
        FrameLayout touchArea = (FrameLayout) findViewById(R.id.container_frame);
        touchArea.setOnTouchListener(this);
        touchArea.setLongClickable(true);

        JumpToSearchBookFragment();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE_header && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap=BitmapFactory.decodeFile(picturePath);
            mHeaderImage.setImageBitmap(bitmap);

            FileOperation.CopySdcardFile(picturePath, stringHeaderName);
        }

        if (requestCode == RESULT_LOAD_IMAGE_background && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap=BitmapFactory.decodeFile(picturePath);
            mDrawerBackground.setImageBitmap(bitmap);

            FileOperation.CopySdcardFile(picturePath, stringBackgroundName);
        }

    }

    public void JumpToBookFragment(){

        if (mUserLoginInfo.IsLogined()){
            if (mBookBorrowedFragment ==null)
                mBookBorrowedFragment =new bookBorrowedFragment();
            FragmentManager mFragmentManager = getFragmentManager();
            FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
            mTransaction.replace(R.id.container_frame, mBookBorrowedFragment);
            mTransaction.commit();

            if (mMenu!=null) {
                mMenu.findItem(R.id.action_log_out).setVisible(true);
                mMenu.findItem(R.id.action_refresh_book).setVisible(true);
                mMenu.findItem(R.id.action_share).setVisible(true);
            }
        } else{
            JumpToLoginFragment();
        }
    }

    public void JumpToCollectBookFragment(){

        if (mBookCollectedFragment ==null)
            mBookCollectedFragment =new bookCollectedFragment();
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        mTransaction.replace(R.id.container_frame, mBookCollectedFragment);
        mTransaction.commit();
        if (mMenu!=null) {
            mMenu.findItem(R.id.action_log_out).setVisible(false);
            mMenu.findItem(R.id.action_refresh_book).setVisible(false);
            mMenu.findItem(R.id.action_share).setVisible(false);
        }

    }
    private void JumpToLoginFragment(){
        if(mLoginFragment==null)
            mLoginFragment=new loginFragment();
        mActionBar.setTitle(R.string.login_library);
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        mTransaction.replace(R.id.container_frame, mLoginFragment );
        mTransaction.commit();

        if (mMenu!=null) {
            mMenu.findItem(R.id.action_log_out).setVisible(false);
            mMenu.findItem(R.id.action_refresh_book).setVisible(false);
            mMenu.findItem(R.id.action_share).setVisible(false);
        }

    }

    private void JumpToSearchBookFragment(){
        if(mBookSearchFragment ==null)
            mBookSearchFragment =new bookSearchFragment();
        mActionBar.setTitle(R.string.nav_search_book);
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        mTransaction.replace(R.id.container_frame, mBookSearchFragment);
        mTransaction.commit();

        if (mMenu!=null) {
            mMenu.findItem(R.id.action_log_out).setVisible(false);
            mMenu.findItem(R.id.action_refresh_book).setVisible(false);
            mMenu.findItem(R.id.action_share).setVisible(false);
        }

    }

    private void JumpToSettingFragment(){

        Intent intent =new Intent(this, SettingActivity.class);
        startActivity(intent);

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
//                        menuItem.setChecked(false);
                        if(menuItem.getItemId()!=R.id.nav_setting){
                            mDrawerLayout.closeDrawers();
                        }
                        JumpFromNavigation(menuItem);
                        return true;
                    }
                });
    }

    public void JumpFromNavigation(MenuItem menuItem){

        // 导航栏动作，跳转到子页面

        arg=menuItem.toString();

        if(menuItem.getItemId()==R.id.nav_library) {    //图书馆
            JumpToBookFragment();
        }

        if(menuItem.getItemId()==R.id.nav_collect_book) {    //图书馆
            JumpToCollectBookFragment();
        }

        if(menuItem.getItemId()==R.id.nav_search_book) {       //Search
            JumpToSearchBookFragment();
        }

        if(menuItem.getItemId()==R.id.nav_setting) {    //设置
            JumpToSettingFragment();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu=menu;
        /*  I don't know why it doesn't work --
        *  -->   mMenu.setGroupVisible(R.id.groupDefault,false);
        * */
        mMenu.findItem(R.id.action_feedback).setVisible(false);
        mMenu.findItem(R.id.action_open_drawer).setVisible(false);
        mMenu.findItem(R.id.action_log_out).setVisible(false);
        mMenu.findItem(R.id.action_refresh_book).setVisible(false);
        mMenu.findItem(R.id.action_add_course).setVisible(false);
        mMenu.findItem(R.id.action_share).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //菜单栏动作
        if(item.getItemId() == android.R.id.home||item.getItemId() == R.id.action_open_drawer) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        if (item.getItemId()==R.id.action_log_out) {

            SharedPreferences.Editor shareData =getSharedPreferences("data",0).edit();
            shareData.putBoolean("ISLOGINED",false);
            shareData.commit();

            mBookBorrowedFragment =new bookBorrowedFragment();
            mLoginFragment=new loginFragment();
            JumpToLoginFragment();


            Toast.makeText(getApplicationContext(), getString(R.string.re_login), Toast.LENGTH_SHORT).show();
            return true;

        }

        if (item.getItemId()==R.id.action_refresh_book) {
            mBookBorrowedFragment.RefreshData();
            return true;
        }


        if(item.getItemId()==R.id.action_share){
            mBookBorrowedFragment.shareBooksBorrowed();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }


    private boolean isExit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                mDrawerLayout.closeDrawers();
            }else{
                exit();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
    //二次点击退出程序方法
    public void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(),getString(R.string.exit_hint) , Toast.LENGTH_SHORT).show();
            exitHandler.sendEmptyMessageDelayed(0,2000);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            finish();

        }
    }

    final Handler exitHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public void onClick(View v) {
        //?

    }

    public UserLoginInfo getmLogin(){
        return mUserLoginInfo;
    }

    public void setmUserLoginInfo(UserLoginInfo mUserLoginInfo){
        this.mUserLoginInfo = mUserLoginInfo;
    }

    public UserLoginInfo getmUserLoginInfo(){
        return this.mUserLoginInfo;
    }


    public void openDrawer(){
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

/////////////////////////////////////////////////////////////////
    /** 处理侧滑动作
     *
     */
/////////////////////////////////////////////////////////////////
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TOD Auto-generated method stub
        Log.i("touch", "touch");
        return mGestureDetector.onTouchEvent(event);

    }
    @Override
    public boolean onDown(MotionEvent e) {
        // TOD Auto-generated method stub
        return false;
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // TOD Auto-generated method stub
        if (e2.getX()-e1.getX() > FLING_MIN_DISTANCE
                && Math.abs(velocityX) > FLING_MIN_VELOCITY
                && Math.abs(velocityX) >Math.abs(velocityY)) {
            // Fling right
//            Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();
            mDrawerLayout.openDrawer(GravityCompat.START);
            return false;
        }

        return false;
    }
    @Override
    public void onLongPress(MotionEvent e) {
        // TOD Auto-generated method stub
    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TOD Auto-generated method stub
        return false;
    }
    @Override
    public void onShowPress(MotionEvent e) {
        // TOD Auto-generated method stub
    }
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TOD Auto-generated method stub
        return false;
    }


    //处理侧滑动作
    private void initEvents()   {
        mDrawerLayout.setDrawerListener(new DrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = mDrawerLayout.getChildAt(1);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;

                if (drawerView.getTag().equals("LEFT")) {

                    float leftScale = 1 - 0.25f * scale;

                    ViewHelper.setScaleX(mMenu, leftScale);
                    ViewHelper.setScaleY(mMenu, leftScale);
                    ViewHelper.setAlpha(mMenu, 1.f);
//                    ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));

                    ViewHelper.setTranslationX(mContent,
                            mMenu.getMeasuredWidth() * (1 - scale));
                    ViewHelper.setPivotX(mContent, 0);
                    ViewHelper.setPivotY(mContent,
                            mContent.getMeasuredHeight() / 2);
//                    ViewHelper.setAlpha(mContent, 0.5f);
                    mContent.invalidate();
                    ViewHelper.setScaleX(mContent, rightScale);
                    ViewHelper.setScaleY(mContent, rightScale);
                    ViewHelper.setAlpha(mDrawerLayout, 1.f);
                } else {
                    ViewHelper.setTranslationX(mContent,
                            -mMenu.getMeasuredWidth() * slideOffset);
                    ViewHelper.setPivotX(mContent, mContent.getMeasuredWidth());
                    ViewHelper.setPivotY(mContent,
                            mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                    ViewHelper.setScaleX(mContent, rightScale);
                    ViewHelper.setScaleY(mContent, rightScale);
                }

            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }
        });
    }

}
