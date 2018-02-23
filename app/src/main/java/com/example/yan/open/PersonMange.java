package com.example.yan.open;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yan on 2018/2/1.
 */

public class PersonMange extends AppCompatActivity{
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> persondata=new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_mange);
        FloatingActionButton button_add=findViewById(R.id.add_person);
        listView=findViewById(R.id.personlist);
        persondata.add("father");
        persondata.add("mother");
        adapter=new ArrayAdapter<String>(PersonMange.this,android.R.layout.simple_list_item_1,persondata);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(PersonMange.this,PersonInfo.class);
                intent.putExtra("data",persondata.get(i));
                startActivity(intent);
            }
        });
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PersonMange.this,PersonInfo.class);
                intent.putExtra("data","no");
                startActivityForResult(intent,1);
            }
        });
        listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenuInfo menuInfo) {
                menu.add(0, 1, 0, "删除该条");
            }
        });
    }
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        //info.id得到listview中选择的条目绑定的id
        String id = String.valueOf(info.id);
        switch (item.getItemId()) {
            case 1:
                //System.out.println("删除"+info.id);
                  //删除事件的方法
                Log.d("id", "onContextItemSelected: "+info.id);
                persondata.remove(id);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    persondata.add(data.getStringExtra("newperson"));
                    listView.setAdapter(adapter);
                }
                break;
            default:break;
        }
    }
}
