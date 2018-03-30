package com.example.yan.open;

/**
 * Created by ACM-Yan on 2018/3/23.
 */

public class Data {
    private static String address="http://192.168.0.122:8080";
    public static String getAddress(){
        return address;
    }
    public static void setAddress( String address1){
        address="http://"+address1;
    }
}
