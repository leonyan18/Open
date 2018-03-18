package com.example.yan.open;

/**
 * Created by ACM-Yan on 2018/3/15.
 */

public class People {
    private String name;

    private String slabel;
    private int image;

    public  People(String name,String slabel,int image){
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
    public  int getImage(){ return image;}
}
