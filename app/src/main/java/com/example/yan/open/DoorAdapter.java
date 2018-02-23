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
 * Created by yan on 2018/1/31.
 */

public class DoorAdapter extends ArrayAdapter<Door> {
    private  int resourceId;
    public DoorAdapter(Context context, int textViewResourceId, List<Door>odjects){
        super(context,textViewResourceId,odjects);
        resourceId=textViewResourceId;
    }
    public View getView(int postion, View convertView, ViewGroup parent){
        Door door=getItem(postion);
        View view;
        if (convertView==null)
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        else
            view=convertView;
        ImageView doorImage=view.findViewById(R.id.door_image);
        TextView textView=view.findViewById(R.id.door_name);
        textView.setText(door.getName());
        return view;
    }
}
