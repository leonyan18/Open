package com.example.yan.open;

import android.app.Application;
import android.content.Context;

/**
 * Created by ACM-Yan on 2018/3/4.
 */

public class MyApplication extends Application{
    private static Context context;
    @Override
    public void onCreate(){
        super.onCreate();
        context=getApplicationContext();
    }
    public static Context getContext(){
        return  context;
    }
}
