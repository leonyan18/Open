package com.example.yan.open;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ACM-Yan on 2018/3/2.
 */

public class OpreationAdapter extends ArrayAdapter<Opreation>{
    private int resourseid;
    public OpreationAdapter(Context context, int textViewResourceid, List<Opreation> objects){
        super(context,textViewResourceid,objects);
        resourseid=textViewResourceid;
    }
    @Override
    public View getView(int postion, View convertView, ViewGroup parent){
        Opreation opreation=getItem(postion);
        View view= LayoutInflater.from(getContext()).inflate(resourseid,parent,false);
        ImageView imageView=view.findViewById(R.id.op_image);
        TextView textView=view.findViewById(R.id.op_name);
        imageView.setImageResource(opreation.getImageid());
        textView.setText(opreation.getName());
        return view;
    }
}
