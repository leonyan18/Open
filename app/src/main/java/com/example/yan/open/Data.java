package com.example.yan.open;

/**
 * Created by ACM-Yan on 2018/3/23.
 */

public class Data {
    private static String address="http://120.79.181.75:8080/spring";
    public static String getAddress(){
        return address;
    }
    public static void setAddress( String address1){
        address="http://"+address1;
    }
}
