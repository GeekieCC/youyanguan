package com.gusteauscuter.youyanguan.content_fragment;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.DepActivity.AddCourseActivity;
import com.gusteauscuter.youyanguan.DepActivity.CourseDetailActivity;
import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.data_Class.course.Course;
import com.gusteauscuter.youyanguan.data_Class.course.CourseDatabase;

import java.util.ArrayList;
import java.util.List;


public class courseFragment extends Fragment {

    private GridLayout MondayRL;

    private GridLayout[] DayRL;
    private List<Course> mCourseList;
    private CourseDatabase courseDatabase;
    private int width;
    public courseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        initData();

        View view =inflater.inflate(R.layout.fragment_course, container, false);

        MondayRL=(GridLayout) view.findViewById(R.id.Day1);
        GridLayout TuesdayRL=(GridLayout) view.findViewById(R.id.Day2);
        GridLayout WednesdayRL=(GridLayout) view.findViewById(R.id.Day3);
        GridLayout ThurdayRL=(GridLayout) view.findViewById(R.id.Day4);
        GridLayout FridayRL=(GridLayout) view.findViewById(R.id.Day5);
        GridLayout SaturdayRL=(GridLayout) view.findViewById(R.id.Day6);
        GridLayout SundayRL=(GridLayout) view.findViewById(R.id.Day7);

        DayRL=new GridLayout[]{MondayRL,TuesdayRL,WednesdayRL,
                ThurdayRL,FridayRL,SaturdayRL,SundayRL};

        ViewTreeObserver vto = MondayRL.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                MondayRL.getViewTreeObserver().removeOnPreDrawListener(this);
                width=MondayRL.getWidth();
                /**
                 * 放在内部才能获取width
                 */
                int color=0;
                for(Course course:mCourseList){

                    if(course!=null){
                        color++;
                        CreatCourseCard(course,color);
                    }
                }
                return true;
            }
        });

        return view;
    }

    /** 创建单个课程信息CourseCard
     *
     * @param course 传入课程信息
     * @param color  Test，设置card颜色，可在内部处理，删掉此参数
     */
    private void CreatCourseCard(Course course, int color){

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.card_course, null);

        ((TextView)view.findViewById(R.id.title)).setText(course.getName());
        ((TextView)view.findViewById(R.id.time)).setText(course.getTimeAndPositions().getTime());
        CardView cardCourse=(CardView) view.findViewById(R.id.CardCourse);

        cardCourse.setCardBackgroundColor(colors[color % 7]);
//        为课程View设置点击的监听器

        cardCourse.setTag(course);
        cardCourse.setOnClickListener(new OnClickCourseListener());

        DayRL[course.getTimeAndPositions().getWeekday()-1].addView(view);

        GridLayout.LayoutParams layoutParams =(GridLayout.LayoutParams) view.getLayoutParams();
        layoutParams.width =(int) (width* 0.33);
        view.setLayoutParams(layoutParams);

    }


    public void startAddCourseActivity(){

        Intent intent =new Intent(getActivity(), AddCourseActivity.class);
        startActivity(intent);
    }

    public void startCourseDetailActivity(Course course){

        Intent intent =new Intent(getActivity(), CourseDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("courseToShow", course);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    /**
     *   deal with dataChange with the "courseDatabase"
     * */
    private void initData(){

        /** @ WangCe
         * TODO  delete code below --> if (mCourseList==null) {...}
         * this is just for test
         * */

        if (mCourseList==null) {
            mCourseList = new ArrayList<>();

            mCourseList.add(new Course());
            mCourseList.add(new Course(5));
            mCourseList.add(new Course(3));
            mCourseList.add(new Course(2));
            mCourseList.add(new Course(4));
            mCourseList.add(new Course(3));
            mCourseList.add(new Course(7));
            mCourseList.add(new Course(6));
            mCourseList.add(new Course(2));
            mCourseList.add(new Course(4));
            mCourseList.add(new Course(3));
            mCourseList.add(new Course(7));
        }

        /** @ WangCe
         * TODO  uncomment code below
         * deal with dataChange with the "courseDatabase"
         * */
//        if(courseDatabase==null) {
//                courseDatabase = new CourseDatabase();
//            }
//        if (mCourseList==null) {
//            mCourseList = courseDatabase.searchCourses(1);
//        }

    }

    class OnClickCourseListener implements View.OnClickListener {

        public void onClick(View v) {
            // TODO course detail
            String title;
            title = (String) ((TextView)v.findViewById(R.id.title)).getText();
            Toast.makeText(getActivity(), "你点击的是:" + title,
                    Toast.LENGTH_SHORT).show();
            Course course=(Course)(v.getTag());
            startCourseDetailActivity(course);
        }
    }

    /**
     * define some colors for background of CardCourse
     */
    private int colors[] = {    //5+7
//            getActivity().getResources().getColor(R.color.course_blue),
//            getActivity().getResources().getColor(R.color.course_green),
//            getActivity().getResources().getColor(R.color.course_red),
//            getActivity().getResources().getColor(R.color.course_purple),
//            getActivity().getResources().getColor(R.color.course_yellow),
//            getActivity().getResources().getColor(R.color.course_orange),
            Color.rgb(0xE6, 0x4C, 0x3C),
            Color.rgb(0x0E, 0xD3, 0xF8),
            Color.rgb(0x00, 0xa1, 0x49),
            Color.rgb(0x5e, 0x5a, 0xb7),
            Color.rgb(0xf9, 0xad, 0x1d),

            Color.rgb(0xdd, 0x11, 0x11),
            Color.rgb(0xf0,0x96,0x09),
            Color.rgb(0x8c,0xbf,0x26),
            Color.rgb(0x00,0xab,0xa9),
            Color.rgb(0x99,0x6c,0x33),
            Color.rgb(0x3b,0x92,0xbc),
            Color.rgb(0xcc,0xcc,0xcc)
    };

}
