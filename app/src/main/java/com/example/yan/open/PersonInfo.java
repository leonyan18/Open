package com.example.yan.open;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.FaceDetector;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.guo.android_extend.image.ImageConverter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
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
    private String name=null,data;
    private Intent intent;
    private CheckBox checkBox1L,checkBox2S;
    private Button dbutton;
    private File outputimage;
    private LoadingAlertDialog dialog;
    private boolean uppermission;
    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int years, int monthOfYear, int dayOfMonth) {
            dbutton.setText(years+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
        }
    };
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
                        .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\""+name+".jpg\""),
                                        RequestBody.create(MediaType.parse("image/jpg"),file))
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.0.110:8888/UpLoadFile")
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("onFailure", e.toString());
                        dialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MyApplication.getContext(),"连接超时请检查网络",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        intent.putExtra("or",true);
                        Log.d("onResponse", response.body().string());
                        finish();
                    }

                });
            }
        }).start();
        dialog = new LoadingAlertDialog(PersonInfo.this);
        dialog.show("上传中...");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        intent=new Intent();
        intent.putExtra("or",false);
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
//        builder.detectFileUriExposure();
        if(Build.VERSION.SDK_INT>=23){
            //判断是否有这个权限
            if(ContextCompat.checkSelfPermission(PersonInfo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(PersonInfo.this, Manifest.permission.INTERNET)!=PackageManager.PERMISSION_GRANTED){
                //第一请求权限被取消显示的判断，一般可以不写
                if (ActivityCompat.shouldShowRequestPermissionRationale(PersonInfo.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)&&ActivityCompat.shouldShowRequestPermissionRationale(PersonInfo.this,
                        Manifest.permission.INTERNET)){
                }else {
                    //2、申请权限: 参数二：权限的数组；参数三：请求码
                    ActivityCompat.requestPermissions(PersonInfo.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET,Manifest.permission.CAMERA},1);
                }
            }
        }
        Button takeButton=findViewById(R.id.uploading);
        Button saveButton=findViewById(R.id.save);
        checkBox1L=findViewById(R.id.always);
        checkBox2S=findViewById(R.id.shtime);
        checkBox1L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox2S.isChecked()){
                    checkBox2S.setChecked(false);
                }
            }
        });
        checkBox2S.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox1L.isChecked()){
                    checkBox1L.setChecked(false);
                }
            }
        });
        imageView=findViewById(R.id.perface);
        text=findViewById(R.id.name);
        dbutton=findViewById(R.id.dbutton);
        dbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = null;//取得资源对象
                            url = new URL("http://www.baidu.com");
                            URLConnection uc = url.openConnection();//生成连接对象
                            uc.connect(); //发出连接
                            long ld = uc.getDate();
                            Calendar calendar=Calendar.getInstance();
                            calendar.setTimeInMillis(ld);
                            showDate(calendar);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();//没有同步
                Calendar calendar=Calendar.getInstance();
                showDate(calendar);
            }
        });

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
                if(uppermission)
                upload();
                else{
                    Toast.makeText(MyApplication.getContext(),"请上传一张有效照片",Toast.LENGTH_SHORT).show();
                }
            }
        });
        Intent intent=getIntent();
        data=intent.getStringExtra("data");
        if(!data.equals("no")){
            text.setText(data);
            File appDir = new File(Environment.getExternalStorageDirectory(), "FaceOpen");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            outputimage = new File(appDir,data+".jpg");
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
    public static File getFileFromBytes(byte[] b, File file) {
        BufferedOutputStream stream = null;
        try {
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }
    private void showDate(Calendar calendar){
        int year = calendar.get(Calendar.YEAR);//当前年
        int month = calendar.get(Calendar.MONTH);//当前月
        int day = calendar.get(Calendar.DAY_OF_MONTH);//当前日
        DatePickerDialog dialog = new DatePickerDialog(this, mdateListener, year, month, day);
        dialog.show();
    }
    protected void takePic(){
        uppermission=false;
        if(!text.getText().toString().equals("")){
            File appDir = new File(Environment.getExternalStorageDirectory(), "FaceOpen");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            name=text.getText().toString();
            outputimage = new File(appDir,name+".jpg");
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
    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    public  int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     * @param angle 被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case take_photo:
                if(resultCode==RESULT_OK){
                        Bitmap newbitmap=rotaingImageView(readPictureDegree(Environment.getExternalStorageDirectory()+"/FaceOpen/"+name+".jpg"),getSmallBitmap(outputimage,800,400));
                        imageView.setImageBitmap(newbitmap);
                        compressImage(newbitmap);
                        String string=FaceHelper.facefind(newbitmap);
                        Toast.makeText(MyApplication.getContext(),string+"",Toast.LENGTH_SHORT).show();
                        if(string.equals("检测到人脸")){
                            uppermission=true;
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
    public Bitmap getSmallBitmap(File file, int reqWidth, int reqHeight) {
        try {
            String filePath = file.getAbsolutePath();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;//开始读入图片，此时把options.inJustDecodeBounds 设回true了
            BitmapFactory.decodeFile(filePath, options);//此时返回bm为空
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);//设置缩放比例
            options.inJustDecodeBounds = false;//重新读入图片，注意此时把options.inJustDecodeBounds 设回false了
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            //压缩好比例大小后不进行质量压缩
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputimage));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到bos中
            //压缩好比例大小后再进行质量压缩
            compressImage(bitmap);
            return bitmap;
        } catch (Exception e) {
            Log.d("wzc", "类:" + this.getClass().getName() + " 方法：" + Thread.currentThread()
                    .getStackTrace()[0].getMethodName() + " 异常 " + e);
            return null;
        }
    }
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        try {
            int height = options.outHeight;
            int width = options.outWidth;
            int inSampleSize = 1;  //1表示不缩放
            if (height > reqHeight || width > reqWidth) {
                int heightRatio = Math.round((float) height / (float) reqHeight);
                int widthRatio = Math.round((float) width / (float) reqWidth);
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }
            return inSampleSize;
        } catch (Exception e) {
            Log.d("wzc", "类:" + this.getClass().getName() + " 方法：" + Thread.currentThread()
                    .getStackTrace()[0].getMethodName() + " 异常 " + e);
            return 1;
        }
    }
    private Bitmap compressImage(Bitmap image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > 100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                options -= 10;//每次都减少10
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

            }
            //压缩好后写入文件中
            FileOutputStream fos = new FileOutputStream(outputimage);
            Log.d("path", imageuri.getPath());
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
