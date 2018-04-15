package com.example.yan.open;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yan.open.other.SharedPreferencesUtils;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            initperson();
            mView = inflater.inflate(R.layout.personwindow, container, false);
            initOp();
            final OpreationAdapter opreationAdapter=new OpreationAdapter(getActivity(),R.layout.opreation_item,opreationList);
            ListView listView=mView.findViewById(R.id.listView);
            personimg=mView.findViewById(R.id.perimg);
            textView=mView.findViewById(R.id.myname);
//            Person person=DataSupport.where("username = ?", textView.getText().toString()).findFirst(Person.class);
//            personimg.setImageBitmap(getUrlImage(Data.getAddress()+"/images/"+person.getUserid()+".jpg"));
//            Log.d("123", "onCreateView: "+getUrlImage(Data.getAddress()+"/images/"+person.getUserid()+".jpg"));
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
                    intent.putExtra("me",true);
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
    private  void initperson(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client=new OkHttpClient.Builder()
                        .connectTimeout(4, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .build();  //创建OkHttpClient对象。
                Request request=new Request.Builder()
                        .url(Data.getAddress()+"/api/user/1010101")
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
                final String peoname=json.getString("username");
                String id=json.getString("id");
                String date1=json.getString("endDate");
                String tel=json.getString("tel");
                String password=json.getString("password");
                date1=date1.substring(0,10);
                if(DataSupport.where("userid = ?", id).findFirst(Person.class)==null){
                    Person person=new Person();
                    person.setUserid(id);
                    person.setTel(tel);
                    person.setPassword(password);
                    person.setUsername(peoname);
                    person.setEnddate(date1);
                    person.save();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(peoname);
                        }
                    });
                }
                else{
                    Person person=DataSupport.where("userid = ?", id).findFirst(Person.class);
                    person.setTel(tel);
                    person.setPassword(password);
                    person.setUsername(peoname);
                    person.setEnddate(date1);
                    person.save();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(peoname);
                        }
                    });
//                    ContentValues values = new ContentValues();
//                    values.put("tel", tel);
//                    values.put("password",password);
//                    values.put("endData",date1);
//                    values.put("username",peoname);
//                    DataSupport.updateAll(Person.class, values, "userid = ?", id);
                }
                SharedPreferencesUtils.saveData(MyApplication.getContext(),peoname,id);
                File appDir = new File(Environment.getExternalStorageDirectory(), "FaceOpen");
                if (!appDir.exists()) {
                  appDir.mkdir();
                }
                File outputimage = new File(appDir,id+".jpg");
                Log.d("outputimage", "pareseJsonchange: "+outputimage.getPath());
                if(outputimage.exists()){
                    outputimage.delete();
                }
                getUrlImage(Data.getAddress()+"/images/"+id+".jpg",outputimage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public Bitmap getUrlImage(String url,File file) {
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
            compressImage(img,file);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img;
    }
    private Bitmap compressImage(Bitmap image, File outputimage) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > 100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                if(options>10)
                    options -= 10;//每次都减少10
                else
                    options--;
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            }
            //压缩好后写入文件中
            FileOutputStream fos = new FileOutputStream(outputimage);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
