package com.example.yan.open;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonFloat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



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
        ButtonFloat button_add=findViewById(R.id.add_person);
        listView=findViewById(R.id.personlist);
        persondata.add("father");
        initperson();
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("人员管理");
        toolbar.setTitleTextColor(0xffffff);
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
        switch (item.getItemId()) {
            default:
                Log.d("id121", "onContextItemSelected: "+info.id);
                persondata.remove((int )info.id);
                listView.setAdapter(adapter);
                return true;
        }
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case 1:
                if(data!=null&&data.getBooleanExtra("or",false)){
                    persondata.add(data.getStringExtra("newperson"));
                    listView.setAdapter(adapter);
                    Toast.makeText(PersonMange.this,"上传成功",Toast.LENGTH_SHORT).show();
                }
                break;
            default:break;
        }
    }
    private  void initperson(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client=new OkHttpClient();
                Request request=new Request.Builder()
                        .url("http://192.168.2.187:8080/docs/person.json")
                        .build();
            }
        }).start();
    }
}
