package com.example.yan.open;

import android.graphics.Bitmap;

/**
 * Created by ACM-Yan on 2018/3/15.
 */

public class People {
    private String name;

    private String slabel;
    private Bitmap image;

    public  People(String name,String slabel,Bitmap image){
        this.name=name;
        this.slabel=slabel;
        this.image=image;
    }

    public String getName() {
        return name;
    }
    public String getSlabel() {
        return slabel;
    }
    public  Bitmap getImage(){ return image;}
}
