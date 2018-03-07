package com.example.yan.open;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yan on 2018/1/30.
 */

public class PersonWindow extends AppCompatActivity {
    private List<Opreation> opreationList=new ArrayList<>();
    private ImageButton doorbt;
    private Button ch_user,exitbt;
    private AlertDialog dialog;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personwindow);
        initOp();
        final OpreationAdapter opreationAdapter=new OpreationAdapter(PersonWindow.this,R.layout.opreation_item,opreationList);
        ListView listView=findViewById(R.id.listView);
        listView.setAdapter(opreationAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Opreation opreation=opreationList.get(position);
                if(opreation.getName().equals("人员管理")){
                    Intent intent=new Intent(PersonWindow.this,PersonMange.class);
                    startActivity(intent);
                }
                if(opreation.getName().equals("开启指纹")||opreation.getName().equals("关闭指纹")){
                    if (!SharedPreferencesUtils.getData(MyApplication.getContext(), "openFinger", false)){
                        dialog = new AlertDialog.Builder(PersonWindow.this)
                                .setTitle("提示")
                                .setMessage("\n   是否开启指纹开门")
                                .setIcon(R.drawable.finger_red)
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
                        dialog = new AlertDialog.Builder(PersonWindow.this)
                                .setTitle("提示")
                                .setMessage("\n   是否关闭指纹开门")
                                .setIcon(R.drawable.finger_red)
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
        doorbt=findViewById(R.id.door);
        doorbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PersonWindow.this,DoorControl.class);
                startActivity(intent);
                finish();
            }
        });
        ch_user=findViewById(R.id.chuser);ch_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PersonWindow.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        exitbt=findViewById(R.id.exitbt);
        exitbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initOp(){
        Opreation history=new Opreation("历史记录",R.drawable.history);
        opreationList.add(history);
        Opreation mange=new Opreation("人员管理",R.drawable.mange);
        opreationList.add(mange);
        Opreation change=new Opreation("修改密码",R.drawable.change);
        opreationList.add(change);
        Opreation pass=new Opreation("开启指纹",R.drawable.finger);
        opreationList.add(pass);
    }
}
