package com.example.yan.open;

import android.content.Intent;
import android.os.Bundle;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personwindow);
        initOp();
        OpreationAdapter opreationAdapter=new OpreationAdapter(PersonWindow.this,R.layout.opreation_item,opreationList);
        ListView listView=findViewById(R.id.listView);
        listView.setAdapter(opreationAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Opreation opreation=opreationList.get(position);
                if(opreation.getName().equals("人员管理")){
                    Intent intent=new Intent(PersonWindow.this,PersonMange.class);
                    startActivity(intent);
                }
            }
        });
        doorbt=findViewById(R.id.door);
        doorbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PersonWindow.this,DoorControl.class);
                startActivity(intent);
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
    }
}
