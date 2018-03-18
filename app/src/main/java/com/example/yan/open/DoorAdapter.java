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
    public DoorAdapter(Context context, int textViewResourceId, List<Door>odjects){
        super(context,textViewResourceId,odjects);
    }
    @Override
    public int getCount() {
        // don't display last item. It is used as hint.
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }

}
