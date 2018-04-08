package com.example.yan.open;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yan.open.other.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yan on 2018/1/30.
 */

public class PersonWindow extends Fragment {
    private List<Opreation> opreationList=new ArrayList<>();
    private ImageView personimg;
    private Button ch_user,exitbt;
    private AlertDialog dialog;
    private View mView;
    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.personwindow, container, false);
            initOp();
            final OpreationAdapter opreationAdapter=new OpreationAdapter(getActivity(),R.layout.opreation_item,opreationList);
            ListView listView=mView.findViewById(R.id.listView);
            personimg=mView.findViewById(R.id.perimg);
            textView=mView.findViewById(R.id.myname);
            listView.setAdapter(opreationAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Opreation opreation=opreationList.get(position);
                    if(opreation.getName().equals("开启指纹")||opreation.getName().equals("关闭指纹")){
                        if (!SharedPreferencesUtils.getData(MyApplication.getContext(), "openFinger", false)){
                            dialog = new AlertDialog.Builder(getActivity())
                                    .setTitle("提示")
                                    .setMessage("\n   是否开启指纹开门")
                                    .setIcon(R.drawable.finger1)
                                    .setCancelable(false)
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            SharedPreferencesUtils.saveData(MyApplication.getContext(),"openFinger",false);
                                        }
                                    })
                                    .setPositiveButton("确认设置", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SharedPreferencesUtils.saveData(MyApplication.getContext(),"openFinger",true);
                                            opreation.setName("关闭指纹");
                                            opreationAdapter.notifyDataSetChanged();
                                        }
                                    })
                                    .create();
                            dialog.show();
                        }
                        else{
                            dialog = new AlertDialog.Builder(getActivity())
                                    .setTitle("提示")
                                    .setMessage("\n   是否关闭指纹开门")
                                    .setIcon(R.drawable.finger1)
                                    .setCancelable(false)
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            SharedPreferencesUtils.saveData(MyApplication.getContext(),"openFinger",true);
                                        }
                                    })
                                    .setPositiveButton("确认设置", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SharedPreferencesUtils.saveData(MyApplication.getContext(),"openFinger",false);
                                            opreation.setName("开启指纹");
                                            opreationAdapter.notifyDataSetChanged();
                                        }
                                    })
                                    .create();
                            dialog.show();
                        }
                    }
                }
            });
            personimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getActivity(),PersonInfo.class);
                    intent.putExtra("data",textView.getText().toString());
                    startActivityForResult(intent,1);
                }
            });
            ch_user=mView.findViewById(R.id.chuser);
            ch_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getActivity(),MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
            exitbt=mView.findViewById(R.id.exitbt);
            exitbt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getActivity().finish();
                }
            });
        }
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) mView.getParent()).removeView(mView);
    }
    private void initOp(){
        Opreation history=new Opreation("历史记录",R.drawable.history);
        opreationList.add(history);
        Opreation change=new Opreation("修改密码",R.drawable.change);
        opreationList.add(change);
        if(SharedPreferencesUtils.getData(MyApplication.getContext(), "openFinger", false)){
            Opreation pass=new Opreation("开启指纹",R.drawable.finger1);
            opreationList.add(pass);
        }
        else{
            Opreation pass=new Opreation("关闭指纹",R.drawable.finger1);
            opreationList.add(pass);
        }
        Opreation back=new Opreation("用户反馈",R.drawable.back);
        opreationList.add(back);
    }
}
