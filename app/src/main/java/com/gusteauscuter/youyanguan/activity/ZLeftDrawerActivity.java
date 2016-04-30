package com.gusteauscuter.youyanguan.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.R;
import com.nineoldandroids.view.ViewHelper;


public abstract class ZLeftDrawerActivity extends AppCompatActivity implements
        View.OnTouchListener,NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationViewLeft;
    private ActionBar mActionBar;
    private FrameLayout mContentFrameLayout;
    private GestureDetector mGestureDetector;
    private static final int FLING_MIN_DISTANCE = 50;
    private static final int FLING_MIN_VELOCITY = 10;


    /**
     * 设置baseActivity的资源id并绑定监听事件
     * @param idDrawerLayout drawerLayout的id
     * @param idNavigationView 侧栏的id
     * @param idContentFrameLayout 内容区域的ida
     */
    public void setSuperDrawerLayout(@IdRes int idDrawerLayout, @IdRes int idNavigationView, @IdRes int idContentFrameLayout){
        mDrawerLayout = (DrawerLayout) findViewById(idDrawerLayout);
        mNavigationViewLeft = (NavigationView) findViewById(idNavigationView);
        mContentFrameLayout = (FrameLayout) findViewById(idContentFrameLayout);
        setViewListener();
    }

    /**
     * 绑定视图中抽屉栏、侧滑操作的监听事件
     */
    public void setViewListener(){
        mDrawerLayout.setDrawerListener(new MyDrawerListener());
        mGestureDetector = new GestureDetector(new MyOnGestureListener());

        mContentFrameLayout.setLongClickable(true); // 必须使能
        mContentFrameLayout.setOnTouchListener(this);
        mNavigationViewLeft.setNavigationItemSelectedListener(this);
    }

    /**
     * set up actionbar
     * @param toolbarResID 资源文件中的toolbar的id
     * @param iconResId icon文件id
     */
    public void setupActionbar(@IdRes int toolbarResID,@DrawableRes int iconResId){
        Toolbar toolbar = (Toolbar) findViewById(toolbarResID);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        if(mActionBar==null)
            return;
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeAsUpIndicator(iconResId);
    }

    /**
     * 设置actionbar的title属性
     * @param resId title的资源id
     */
    public void setActionbarTitle(@StringRes int resId){
        mActionBar.setTitle(resId);
    }



// 侧栏导航操作========================================================================
    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        goToNavigationItem(menuItem);
        return true;
    }

    public abstract void goToNavigationItem(MenuItem menuItem);



// 侧滑动画============================================================================
    private class MyDrawerListener extends DrawerLayout.SimpleDrawerListener{

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
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);    // 将touch事件关联到gestureDetector
    }

    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (e2.getX()-e1.getX() > FLING_MIN_DISTANCE
                    && Math.abs(velocityX) > FLING_MIN_VELOCITY
                    && Math.abs(velocityX) >Math.abs(velocityY)) {
                openDrawer();
            }
            return false;
        }
    }

    public void openDrawer(){
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    public void closeDrawer(){
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public boolean isDrawerOpen(){
        return mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //菜单栏动作
        if(item.getItemId() == android.R.id.home)
            openDrawer();
        return super.onOptionsItemSelected(item);
    }


// 二次点击退出程序方法====================================================
    private boolean isExit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isDrawerOpen()){
                closeDrawer();
            }else{
                exit();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), getString(R.string.exit_hint), Toast.LENGTH_SHORT).show();
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

}