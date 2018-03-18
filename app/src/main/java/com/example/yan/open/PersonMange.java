package com.example.yan.open;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;
//import com.gc.materialdesign.views.ButtonFloat;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.yan.open.other.SharedPreferencesUtils;
import com.gc.materialdesign.views.ButtonFloat;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



/**
 * Created by yan on 2018/2/1.
 */

public class PersonMange extends Fragment {
    private ListView listView;
    private ArrayAdapter<People> adapter;
    private List<People> persondata=new ArrayList<>();
    private View mView;
    private ButtonFloat button_add;
    private MaterialRefreshLayout materialRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.person_mange, container, false);
            button_add=mView.findViewById(R.id.add_person);
            listView=mView.findViewById(R.id.personlist);

            persondata.add(new People("颜颜颜颜","访客",R.drawable.people1));
            persondata.add(new People("颜颜","常驻",R.drawable.people2));
            persondata.add(new People("颜","访客",R.drawable.people3));
            persondata.add(new People("颜颜颜","常驻",R.drawable.people4));
//            initperson();
            materialRefreshLayout=mView.findViewById(R.id.refresh);
            materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
                @Override
                public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {

                }
            });
            Toolbar toolbar=mView.findViewById(R.id.toolbar);
            toolbar.setTitle("人员管理");
            toolbar.setTitleTextColor(0xffffff);
            adapter=new PeopleAdapter(getActivity(),R.layout.people_item,persondata);
            listView.setAdapter(adapter);
            materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
                @Override
                public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                    persondata.add(new People("颜颜","常驻",R.drawable.people2));
                    listView.setAdapter(adapter);
                    final MaterialRefreshLayout temp=materialRefreshLayout;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            temp.finishRefresh();
                        }
                    }).start();
                }
            });
            button_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getActivity(),PersonInfo.class);
                    intent.putExtra("data","no");
                    startActivity(intent);
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent=new Intent(getActivity(),PersonInfo.class);
                    intent.putExtra("data",persondata.get(i).getName());
                    startActivity(intent);
                }
            });
            listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
                public void onCreateContextMenu(ContextMenu menu, View v,
                                                ContextMenuInfo menuInfo) {
                    menu.add(0, 1, 0, "删除该条");
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
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            default:
                Log.d("id121", "onContextItemSelected: "+info.id);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client=new OkHttpClient.Builder()
                                .connectTimeout(4, TimeUnit.SECONDS)
                                .readTimeout(20, TimeUnit.SECONDS)
                                .build();
                        Request request=new Request.Builder()
                                .url("http://192.168.0.122:8080/api/user/"+
                                        SharedPreferencesUtils.getData
                                                (MyApplication.getContext(),persondata.get((int)info.id).getName(),""))
                                .delete()
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TastyToast.makeText(MyApplication.getContext(), "连接超时", TastyToast.LENGTH_LONG,
                                                TastyToast.ERROR);
                                    }
                                });

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                DataSupport.deleteAll(Person.class,"name = ?",persondata.get((int)info.id).getName());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        persondata.remove((int )info.id);
                                        listView.setAdapter(adapter);
                                    }
                                });


                            }
                        });
                    }
                }).start();
                return true;
        }
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case 1:
                if(data!=null&&data.getBooleanExtra("or",false)){
                    persondata.add(new People(data.getStringExtra("newperson"),"访客",R.drawable.people1));
                    listView.setAdapter(adapter);
                    TastyToast.makeText(MyApplication.getContext(), "上传成功", TastyToast.LENGTH_LONG,
                            TastyToast.SUCCESS);
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
                        .url("http://192.168.0.122:8080/api/users/0")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        pareseJsonchange(response.body().string());
                    }
                });
            }
        }).start();
    }
    private void pareseJsonchange(String jsonData){
        try {
            JSONObject json=new JSONObject(jsonData);
            final JSONArray jsonArray=json.getJSONArray("data");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                final String peoname=jsonArray.getJSONObject(i).getString("username");
                final String id=jsonArray.getJSONObject(i).getString("id");
                String date1=jsonArray.getJSONObject(i).getString("endDate");
                date1=date1.substring(0,10);
                Person person=new Person();
                person.setUserid(id);
                person.setTel(jsonObject.getString("tel"));
                person.setUsername(peoname);
                person.setEnddate(date1);
                person.save();
                SharedPreferencesUtils.saveData(MyApplication.getContext(),peoname,id);
                persondata.add(new People(peoname,"访客",R.drawable.people1));
                listView.setAdapter(adapter);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
