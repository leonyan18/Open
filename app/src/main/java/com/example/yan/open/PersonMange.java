package com.example.yan.open;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

            persondata.add(new People("mark","访客",BitmapFactory.decodeResource(getResources(),R.drawable.people1)));
            persondata.add(new People("dalao","常驻",BitmapFactory.decodeResource(getResources(),R.drawable.people2)));
            persondata.add(new People("戴霸天","访客",BitmapFactory.decodeResource(getResources(),R.drawable.people3)));
            persondata.add(new People("Alice","常驻",BitmapFactory.decodeResource(getResources(),R.drawable.people4)));
            adapter=new PeopleAdapter(getActivity(),R.layout.people_item,persondata);
            listView.setAdapter(adapter);
//          initperson();
            materialRefreshLayout=mView.findViewById(R.id.refresh);
            Toolbar toolbar=mView.findViewById(R.id.toolbar);
            toolbar.setTitle("人员管理");
            toolbar.setTitleTextColor(0xffffff);
            adapter=new PeopleAdapter(getActivity(),R.layout.people_item,persondata);
            materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
                @Override
                public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                    persondata=new ArrayList<>();
                    initperson();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter=new PeopleAdapter(getActivity(),R.layout.people_item,persondata);
                            listView.setAdapter(adapter);
                        }
                    });
                }
            });
            button_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getActivity(),PersonInfo.class);
                    intent.putExtra("data","no");
                    startActivityForResult(intent,1);
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent=new Intent(getActivity(),PersonInfo.class);
                    intent.putExtra("data",persondata.get(i).getName());
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
                                .url(Data.getAddress()+"/api/user/"+
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
                                DataSupport.deleteAll(Person.class,"username = ?",persondata.get((int)info.id).getName());
                                initperson();
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
                if(resultCode==-1){
                        initperson();
                        TastyToast.makeText(MyApplication.getContext(), "上传成功", TastyToast.LENGTH_LONG,
                                TastyToast.SUCCESS);
                        break;
                }
            default:break;
        }
    }
    private  void initperson(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client=new OkHttpClient.Builder()
                        .connectTimeout(4, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .build();  //创建OkHttpClient对象。
                Request request=new Request.Builder()
                        .url(Data.getAddress()+"/api/users/1010101")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TastyToast.makeText(MyApplication.getContext(), "连接超时", TastyToast.LENGTH_LONG,
                                        TastyToast.ERROR);
                                materialRefreshLayout.finishRefresh();
                            }
                        });

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
            Log.d("pareseJsonchange: ", jsonData);
            JSONObject json=new JSONObject(jsonData);
            persondata=new ArrayList<>();
            final JSONArray jsonArray=json.getJSONArray("data");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String peoname=jsonArray.getJSONObject(i).getString("username");
                String id=jsonArray.getJSONObject(i).getString("id");
                String date1=jsonArray.getJSONObject(i).getString("endDate");
                String tel=jsonObject.getString("tel");
                String password=jsonObject.getString("password");
                date1=date1.substring(0,10);
                if(DataSupport.where("userid = ?", id).findFirst(Person.class)==null){
                    Person person=new Person();
                    person.setUserid(id);
                    person.setTel(tel);
                    person.setPassword(password);
                    person.setUsername(peoname);
                    person.setEnddate(date1);
                    person.save();
                }
                else{
                    Person person=DataSupport.where("userid = ?", id).findFirst(Person.class);
                    person.setTel(tel);
                    person.setPassword(password);
                    person.setUsername(peoname);
                    person.setEnddate(date1);
                    person.save();
//                    ContentValues values = new ContentValues();
//                    values.put("tel", tel);
//                    values.put("password",password);
//                    values.put("endData",date1);
//                    values.put("username",peoname);
//                    DataSupport.updateAll(Person.class, values, "userid = ?", id);
                }

                SharedPreferencesUtils.saveData(MyApplication.getContext(),peoname,id);
                persondata.add(new People(peoname,"访客",getUrlImage(Data.getAddress()+"/images/"+id+".jpg")));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter=new PeopleAdapter(getActivity(),R.layout.people_item,persondata);
                        listView.setAdapter(adapter);
                    }
                });

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        materialRefreshLayout.finishRefresh();
    }
    public Bitmap getUrlImage(String url) {
        Bitmap img=null;
        try {
            URL picurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection)picurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            img = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img;
    }
}
