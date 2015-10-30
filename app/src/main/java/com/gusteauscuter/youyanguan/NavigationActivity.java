package com.gusteauscuter.youyanguan;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
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

import android.widget.Toast;
import android.view.MotionEvent;


import com.gusteauscuter.youyanguan.DepActivity.UserInforActivity;
import com.gusteauscuter.youyanguan.content_fragment.homeFragment;
import com.gusteauscuter.youyanguan.content_fragment.loginFragment;
import com.gusteauscuter.youyanguan.content_fragment.CourseFragment;
import com.gusteauscuter.youyanguan.content_fragment.searchBookFragment;
import com.gusteauscuter.youyanguan.content_fragment.settingFragment;
import com.gusteauscuter.youyanguan.data_Class.book.Book;
import com.gusteauscuter.youyanguan.data_Class.HomeItem;
import com.gusteauscuter.youyanguan.data_Class.course.Course;
import com.nineoldandroids.view.ViewHelper;

import com.gusteauscuter.youyanguan.content_fragment.bookFragment;
import com.gusteauscuter.youyanguan.data_Class.userLogin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.update.BmobUpdateAgent;


public class NavigationActivity extends AppCompatActivity  implements View.OnClickListener, View.OnTouchListener,
        GestureDetector.OnGestureListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationViewLeft;
//    private NavigationView mNavigationViewRight;
    private ActionBar mActionBar=null;
    private userLogin mUserLogin;
    private FrameLayout mContentFramelayout;

    private bookFragment mBookFragment;
    private CourseFragment mCourseFragment;
    private homeFragment mHomeFragment;
    private settingFragment mSettingFragment;
    private loginFragment mLoginFragment;
    private searchBookFragment mSearchBookFragment;

    private List<Book> mBookList =new ArrayList<>();
    private List<Course> mCourseList =new ArrayList<>();
    private List<HomeItem> mHomeItemList =new ArrayList<>();

    public Menu mMenu;

    private boolean IsFirstTime =true;

    GestureDetector mGestureDetector;
    private static final int FLING_MIN_DISTANCE = 50;
    private static final int FLING_MIN_VELOCITY = 10;

    String arg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
        initEvents();
        Bmob.initialize(this, "213c7ff4ff5c05bee43e1b5f803ee7cd");
        //BmobUpdateAgent.initAppVersion(this);
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
        mUserLogin = new userLogin(username, password, isLogined);
    }


    public void initView() {

        setContentView(R.layout.activity_navigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.layout_drawer);
        mNavigationViewLeft = (NavigationView) findViewById(R.id.id_nv_menu);
        findViewById(R.id.header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getApplicationContext(), UserInforActivity.class);
                startActivity(intent);

            }
        });
//        mNavigationViewRight = (NavigationView) findViewById(R.id.id_nv_menu_right);
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

    private void JumpToHomeFragment() {
        if (mHomeFragment==null)
            mHomeFragment=new homeFragment();
        mActionBar.setTitle(R.string.nav_home);
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        mTransaction.replace(R.id.container_frame, mHomeFragment);
        mTransaction.commit();

        if (mMenu != null) {
//            mMenu.findItem(R.id.action_feedback).setVisible(true);
//            mMenu.findItem(R.id.action_open_drawer).setVisible(true);
            mMenu.findItem(R.id.action_log_out).setVisible(false);
            mMenu.findItem(R.id.action_refresh_book).setVisible(false);
        }

    }

    private void JumpToCourseFragment(){
        if(mCourseFragment==null)
            mCourseFragment=new CourseFragment();
        mActionBar.setTitle(R.string.nav_course);
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        mTransaction.replace(R.id.container_frame, mCourseFragment);
        mTransaction.commit();

        if (mMenu!=null) {
//            mMenu.findItem(R.id.action_feedback).setVisible(true);
//            mMenu.findItem(R.id.action_open_drawer).setVisible(true);
            mMenu.findItem(R.id.action_log_out).setVisible(false);
            mMenu.findItem(R.id.action_refresh_book).setVisible(false);
        }

    }

    public void JumpToBookFragment(){

        if (mUserLogin.IsLogined()){
            if (mBookFragment==null)
                mBookFragment=new bookFragment();
            mActionBar.setTitle(R.string.nav_library);
            FragmentManager mFragmentManager = getFragmentManager();
            FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
            mTransaction.replace(R.id.container_frame, mBookFragment);
            mTransaction.commit();

            if (mMenu!=null) {
//                mMenu.findItem(R.id.action_feedback).setVisible(true);
//                mMenu.findItem(R.id.action_open_drawer).setVisible(true);
                mMenu.findItem(R.id.action_log_out).setVisible(true);
                mMenu.findItem(R.id.action_refresh_book).setVisible(true);
            }
        } else{
            JumpToLoginFragment();
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
//            mMenu.findItem(R.id.action_feedback).setVisible(true);
//            mMenu.findItem(R.id.action_open_drawer).setVisible(true);
            mMenu.findItem(R.id.action_log_out).setVisible(false);
            mMenu.findItem(R.id.action_refresh_book).setVisible(false);
        }

    }

    private void JumpToSearchBookFragment(){
        if(mSearchBookFragment==null)
            mSearchBookFragment=new searchBookFragment();
        mActionBar.setTitle(R.string.nav_search_book);
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        mTransaction.replace(R.id.container_frame, mSearchBookFragment);
        mTransaction.commit();

        if (mMenu!=null) {
//            mMenu.findItem(R.id.action_feedback).setVisible(true);
//            mMenu.findItem(R.id.action_open_drawer).setVisible(true);
            mMenu.findItem(R.id.action_log_out).setVisible(false);
            mMenu.findItem(R.id.action_refresh_book).setVisible(false);
        }

    }

    private void JumpToSettingFragment(){
        if(mSettingFragment==null)
            mSettingFragment=new settingFragment();
        mActionBar.setTitle(R.string.nav_setting);
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        mTransaction.replace(R.id.container_frame, mSettingFragment);
        mTransaction.commit();

        if (mMenu!=null) {
//            mMenu.findItem(R.id.action_feedback).setVisible(false);
//            mMenu.findItem(R.id.action_open_drawer).setVisible(false);
            mMenu.findItem(R.id.action_log_out).setVisible(false);
            mMenu.findItem(R.id.action_refresh_book).setVisible(false);
        }

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        menuItem.setChecked(false);
                        mDrawerLayout.closeDrawers();
                        JumpFromNavigation(menuItem);
                        return true;
                    }
                });
    }

    public void JumpFromNavigation(MenuItem menuItem){

        // 导航栏动作，跳转到子页面

        arg=menuItem.toString();
//        if(menuItem.getItemId()==R.id.nav_home) {     //首页
//            JumpToHomeFragment();
//        }

        if(menuItem.getItemId()==R.id.nav_library) {    //图书馆
            JumpToBookFragment();
        }

//        if(menuItem.getItemId()==R.id.nav_course) {       //日程
//            JumpToCourseFragment();
//        }

        if(menuItem.getItemId()==R.id.nav_search_book) {       //Search
            JumpToSearchBookFragment();
        }

        if(menuItem.getItemId()==R.id.nav_setting) {    //设置
            JumpToSettingFragment();
        }

//        if(menuItem.getItemId()==R.id.nav_course) {       //日程
//            mMenu.findItem(R.id.action_add_course).setVisible(true);
//
//        }else{
//            mMenu.findItem(R.id.action_add_course).setVisible(false);
//        }

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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //菜单栏动作
        if (item.getItemId()==R.id.action_feedback) {
            SendEmailIntent(arg);
            return true;

        }else if(item.getItemId() == android.R.id.home||item.getItemId() == R.id.action_open_drawer) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;

        }else if (item.getItemId()==R.id.action_log_out) {

//            mUserLogin=new userLogin();
            SharedPreferences.Editor shareData =getSharedPreferences("data",0).edit();
            shareData.putBoolean("ISLOGINED",false);
            shareData.commit();

            mBookFragment=new bookFragment();
            mLoginFragment=new loginFragment();
            JumpToLoginFragment();


            Toast.makeText(getApplicationContext(), getString(R.string.re_login), Toast.LENGTH_SHORT).show();
            return true;

        }else if (item.getItemId()==R.id.action_refresh_book) {
            mBookFragment.RefreshData();
            return true;
        }else if(item.getItemId()==R.id.action_add_course){
            mCourseFragment.startAddCourseActivity();
            return true;

        }else
            return super.onOptionsItemSelected(item);
    }


    public void SendEmailIntent(String fromWhere){
        Intent data=new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:gusteauscuter@163.com"));
        data.putExtra(Intent.EXTRA_SUBJECT, "【反馈建议/" + fromWhere + "】");
        data.putExtra(Intent.EXTRA_TEXT, "详细情况：\n");
        startActivity(data);
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
            //System.exit(0);

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

    public userLogin getmLogin(){
        return mUserLogin;
    }

    public void setmUserLogin(userLogin mUserLogin){
        this.mUserLogin=mUserLogin;
    }

    public userLogin getmUserLogin(){
        return this.mUserLogin;
    }

    public void setmCourse(List<Course> mCourseList ){
        this.mCourseList=mCourseList;
        RefreshHomeItemList();
    }

    public List<Course> getmCourse(){
        if(mCourseList==null)
            mCourseList=new ArrayList<>();
        return mCourseList;
    }

    public void setmBookList(List<Book> mBookList ){
        this.mBookList=mBookList;
        RefreshHomeItemList();
    }

    public List<Book> getmBookList(){
        if(mBookList==null)
            mBookList=new ArrayList<>();
        return mBookList;
    }


    public List<HomeItem> getmHomeItemList(){
        return this.mHomeItemList;
    }

    private void RefreshHomeItemList(){
        mHomeItemList=new ArrayList<>();
        int i=0;
        for (i=0;i<mBookList.size();i++){
            HomeItem homeItem =new HomeItem(mBookList.get(i));
            mHomeItemList.add(homeItem);
        }
        for (i=0;i<mCourseList.size();i++){
            HomeItem homeItem =new HomeItem(mCourseList.get(i));
            mHomeItemList.add(homeItem);
        }
        //  TODO 重新排序 mHomeItemList

        Collections.sort(mHomeItemList, new Comparator<HomeItem>() {
            @Override
            public int compare(HomeItem lhs, HomeItem rhs) {
                String lhs_date = lhs.getDate();
                String rhs_date = rhs.getDate();
                if (lhs_date.compareTo(rhs_date) > 0)
                    return 1;
                else if (lhs_date.compareTo(rhs_date) == 0)
                    return 0;
                else
                    return -1;
            }
        });
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
//        if (e1.getX()-e2.getX() > FLING_MIN_DISTANCE
//                && Math.abs(velocityX) > FLING_MIN_VELOCITY
//                && Math.abs(velocityX) >Math.abs(velocityY)) {
//            // Fling right
////            Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();
//            mDrawerLayout.openDrawer(GravityCompat.END);
//            return false;
//        }
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
                View mContent = mDrawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;

                if (drawerView.getTag().equals("LEFT")) {

                    float leftScale = 1 - 0.3f * scale;

                    ViewHelper.setScaleX(mMenu, leftScale);
                    ViewHelper.setScaleY(mMenu, leftScale);
                    ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
                    ViewHelper.setTranslationX(mContent,
                            mMenu.getMeasuredWidth() * (1 - scale));
                    ViewHelper.setPivotX(mContent, 0);
                    ViewHelper.setPivotY(mContent,
                            mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                    ViewHelper.setScaleX(mContent, rightScale);
                    ViewHelper.setScaleY(mContent, rightScale);
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
