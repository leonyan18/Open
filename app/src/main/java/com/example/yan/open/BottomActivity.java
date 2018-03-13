package com.example.yan.open;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import java.util.ArrayList;

/**
 * Created by ACM-Yan on 2018/3/13.
 */

public class BottomActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private BottomNavigationBar bottom_navigation_bar;
    private ArrayList<View> aList;
    private MyPagerAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_lay);
        bottom_navigation_bar=findViewById(R.id.bottom_navigation_bar);
        viewPager=findViewById(R.id.viewPager);
        aList = new ArrayList<View>();
        LayoutInflater li = getLayoutInflater();
        aList.add(li.inflate(R.layout.one,null,false));
        aList.add(li.inflate(R.layout.two,null,false));
        mAdapter = new MyPagerAdapter(aList);
        viewPager.setAdapter(mAdapter);
        bottom_navigation_bar.setMode(BottomNavigationBar.MODE_SHIFTING)

                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);

//        bottom_navigation_bar //值得一提，模式跟背景的设置都要在添加tab前面，不然不会有效果。
//                .setActiveColor("#ffffff")//选中颜色 图标和文字
//                .setInActiveColor("#8e8e8e")//默认未选择颜色
//                .setBarBackgroundColor("#00796B");//默认背景色

        bottom_navigation_bar
                .addItem(new BottomNavigationItem(R.drawable.password,"开门").setActiveColorResource(R.color.colorAccent))
                .addItem(new BottomNavigationItem(R.drawable.mange,"人员管理").setActiveColorResource(R.color.colorPrimary))
                .setFirstSelectedPosition(0)//设置默认选择的按钮
                .initialise();//所有的设置需在调用该方法前完成
        viewPager.setCurrentItem(0);

        bottom_navigation_bar //设置lab点击事件
                .setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(int position) {
                        viewPager.setCurrentItem(position);


                    }

                    @Override
                    public void onTabUnselected(int position) {

                    }

                    @Override
                    public void onTabReselected(int position) {

                    }
                });
    }
}
