package com.example.yan.open;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yan on 2018/2/1.
 */

public class PersonInfo extends AppCompatActivity {
    public static final int take_photo=1;
    private Uri imageuri;
    private  EditText text;
    private ImageView imageView;
    private String name=null;
    private Intent intent;
    public void setName(String name){
        this.name=name;
    }
    public void upload(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client =client = new OkHttpClient.Builder()
                        .connectTimeout(4, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .build();  //创建OkHttpClient对象。
                File appDir = new File(Environment.getExternalStorageDirectory(), "FaceOpen");
                File file = new File(appDir,name+".jpg");
                MultipartBody body = new MultipartBody.Builder("AaB03x")
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", null, new MultipartBody.Builder("BbC04y")
                                .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\""+name+".jpg\""),
                                        RequestBody.create(MediaType.parse("image/jpg"),file))
                                .build())
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.2.187:8080/my/UploadFileServlet")
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if(e.getCause().equals(SocketTimeoutException.class))
                        {
                            Toast.makeText(PersonInfo.this, "上传失败", Toast.LENGTH_SHORT).show();
                            intent.putExtra("or", false);
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        intent.putExtra("or",true);
                        finish();
                    }

                });
            }
        }).start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        intent=new Intent();
        intent.putExtra("or",false);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        if(Build.VERSION.SDK_INT>=23){
            //判断是否有这个权限
            if(ContextCompat.checkSelfPermission(PersonInfo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(PersonInfo.this, Manifest.permission.INTERNET)!=PackageManager.PERMISSION_GRANTED){
                //第一请求权限被取消显示的判断，一般可以不写
                if (ActivityCompat.shouldShowRequestPermissionRationale(PersonInfo.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)&&ActivityCompat.shouldShowRequestPermissionRationale(PersonInfo.this,
                        Manifest.permission.INTERNET)){
                }else {
                    //2、申请权限: 参数二：权限的数组；参数三：请求码
                    ActivityCompat.requestPermissions(PersonInfo.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET},1);
                }
            }
        }
        Button takeButton=findViewById(R.id.uploading);
        Button saveButton=findViewById(R.id.save);
        imageView=findViewById(R.id.perface);
        text=findViewById(R.id.name);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getUrlImage("http://www.nowamagic.net/librarys/images/random/rand_11.jpg");
            }
        }).start();
        takeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePic();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("newperson",text.getText().toString());
                setResult(RESULT_OK,intent);
                upload();
            }
        });
        Intent intent=getIntent();
        String data=intent.getStringExtra("data");
        if(!data.equals("no")){
            text.setText(data);
            File appDir = new File(Environment.getExternalStorageDirectory(), "FaceOpen");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            File outputimage = new File(appDir,data+".jpg");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageuri = FileProvider.getUriForFile(PersonInfo.this, "com.example.yan.open.fileprovider", outputimage);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else {
                //7.0以下使用这种方式创建一个Uri
                imageuri = Uri.fromFile(outputimage);
            }
            try{
                Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageuri));
                Matrix m = new Matrix();
                m.postRotate(90);
                Bitmap newbitmap=Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                imageView.setImageBitmap(newbitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    protected void takePic(){
        if(!text.getText().toString().equals("")){
            File appDir = new File(Environment.getExternalStorageDirectory(), "FaceOpen");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            File outputimage = new File(appDir,text.getText()+".jpg");
            if(outputimage.exists()){
                outputimage.delete();
            }
            try {
                outputimage.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageuri = FileProvider.getUriForFile(PersonInfo.this, "com.example.yan.open.fileprovider", outputimage);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else {
                //7.0以下使用这种方式创建一个Uri
                imageuri = Uri.fromFile(outputimage);
            }
            Log.d("212", "onClick: "+imageuri.toString());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
            startActivityForResult(intent,1);
        }
        else{
            Toast.makeText(PersonInfo.this,"请输入名字",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case take_photo:
                if(resultCode==RESULT_OK){
                    try{
                        Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageuri));
                        Matrix m = new Matrix();
                        m.postRotate(90);
                        Bitmap newbitmap=Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                        imageView.setImageBitmap(newbitmap);
                        name=text.getText().toString();
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            default:break;
        }
    }
    public void getUrlImage(String url) {
        Bitmap img = null;
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
            imageView.setImageBitmap(img);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
