package com.example.yan.open;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ACM-Yan on 2018/3/15.
 */

public class Bottom_main extends AppCompatActivity{
    private ViewPager pager;
    private List<Fragment> fragments;
    private RadioGroup radioGroup;
    private BottomNavigationBar bottom_navigation_bar;
    private static int time=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_main);
        initView();
        bottom_navigation_bar=findViewById(R.id.bottom_navigation_bar);
        bottom_navigation_bar.setMode(BottomNavigationBar.MODE_SHIFTING)

                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);

//        bottom_navigation_bar //值得一提，模式跟背景的设置都要在添加tab前面，不然不会有效果。
//                .setActiveColor("#ffffff")//选中颜色 图标和文字
//                .setInActiveColor("#8e8e8e")//默认未选择颜色
//                .setBarBackgroundColor("#00796B");//默认背景色

        bottom_navigation_bar
//                .addItem(new BottomNavigationItem(R.drawable.home,"Like").setActiveColorResource(R.color.orange))
                .addItem(new BottomNavigationItem(R.drawable.main_page,"Home").setActiveColorResource(R.color.blue))
                .addItem(new BottomNavigationItem(R.drawable.mange,"Manage").setActiveColorResource(R.color.blue))
                .addItem(new BottomNavigationItem(R.drawable.me,"Me").setActiveColorResource(R.color.blue))
                .setFirstSelectedPosition(0)//设置默认选择的按钮
                .initialise();//所有的设置需在调用该方法前完成
        pager.setCurrentItem(0);

        bottom_navigation_bar //设置lab点击事件
                .setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(int position) {
                        pager.setCurrentItem(position);

                    }

                    @Override
                    public void onTabUnselected(int position) {

                    }

                    @Override
                    public void onTabReselected(int position) {

                    }
                });
    }

    private void initView() {
        pager =findViewById(R.id.packpage_vPager);
        if(pager==null){
            Log.d("bottom", "viewPager should not be null");
        }
        fragments = new ArrayList<Fragment>();
        fragments.add(new DoorControl());
        fragments.add(new PersonMange());
        fragments.add(new PersonWindow());

        pager.setAdapter(new FragmentPagerAdapter(this.getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return fragments.get(arg0);
            }
        });
        // 添加页面切换事件的监听器
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottom_navigation_bar.selectTab(position, false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    @Override
    public void onBackPressed(){
        if(time==1){
            finish();
        }
        time++;
        if(time==1){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TastyToast.makeText(MyApplication.getContext(), "再按一次退出", TastyToast.LENGTH_SHORT,
                                    TastyToast.DEFAULT);
                        }
                    });
                    try {
                        Thread.sleep(2000);
                        time=0;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

    @Override
    public void finish() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
        super.finish();
    }


}

