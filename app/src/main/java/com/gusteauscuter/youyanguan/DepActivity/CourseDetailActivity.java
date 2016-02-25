package com.gusteauscuter.youyanguan.DepActivity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.data_Class.course.Course;

import java.util.ArrayList;

public class CourseDetailActivity extends AppCompatActivity {

    private Course courseToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        initData();
        initView();

    }

    private  void initData(){
        Intent intent = this.getIntent();
        courseToShow=(Course)intent.getSerializableExtra("courseToShow");

    }

    private void  initView(){
        ((TextView)findViewById(R.id.hello_textView)).setText(courseToShow.getName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
