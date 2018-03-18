package com.example.yan.open;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by ACM-Yan on 2018/3/15.
 */

public class PeopleAdapter extends ArrayAdapter<People> {
    private int resourceId;

    public PeopleAdapter(Context context, int textViewResourceId,List<People> objects) {
        super(context, textViewResourceId, objects);
        resourceId=textViewResourceId;
    }
    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        People people=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView name=view.findViewById(R.id.people_name);
        TextView slabel=view.findViewById(R.id.label);
        ImageView imageView=view.findViewById(R.id.imgpeo);
        name.setText(people.getName());
        imageView.setImageResource(people.getImage());
        slabel.setText(people.getSlabel());
        return view;
    }
}
