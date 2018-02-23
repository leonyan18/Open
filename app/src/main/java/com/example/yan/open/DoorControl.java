package com.example.yan.open;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yan on 2018/1/29.
 */

public class DoorControl extends AppCompatActivity {
    private ImageButton personal;
    private List<Door> doorList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showdoor);
        initDoor();
        DoorAdapter doorAdapter=new DoorAdapter(DoorControl.this,R.layout.door_item,doorList);
        ListView listView=findViewById(R.id.doorlist);
        listView.setAdapter(doorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Door door=doorList.get(i);
                Toast.makeText(DoorControl.this,door.getName(),Toast.LENGTH_SHORT).show();
            }
        });
        personal=findViewById(R.id.user);
        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(DoorControl.this,PersonWindow.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void initDoor(){
        Door door1=new Door("12#611");
        doorList.add(door1);
        Door door2=new Door("12#612");
        doorList.add(door2);
    }
}
