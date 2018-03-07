package com.example.yan.open;

/**
 * Created by ACM-Yan on 2018/3/2.
 */

public class Opreation {
    private String name;
    private int imageid;
    public int getImageid(){return imageid;}
    public  Opreation(String name,int imageid){
        this.imageid=imageid;
        this.name=name;
    }
    public String getName(){
        return  name;
    }
    public void setName(String name){
        this.name=name;
    }
}
