package com.example.yan.open;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yan.open.other.LoadingAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    public static final int choose_photo =2;
    private Uri imageuri;
    private  EditText name,phone,password;
    private ImageView imageView;
    private Intent intent;
    private CheckBox checkBox1L,checkBox2S;
    private Button dbutton;
    private int kind;
    private File outputimage;
    private LoadingAlertDialog dialog;
    private boolean goodpic;
    private String data,methodId="1";
    private Boolean me;
    private Person person;
    private String id;
    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int years, int monthOfYear, int dayOfMonth) {
            dbutton.setText(years+"-"+(monthOfYear+1)+"-"+dayOfMonth);
        }
    };
    public void upload(){
        dialog = new LoadingAlertDialog(PersonInfo.this);
        dialog.show("上传中...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client =client = new OkHttpClient.Builder()
                        .connectTimeout(4, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .build();  //创建OkHttpClient对象。
                File appDir = new File(Environment.getExternalStorageDirectory(), "FaceOpen");
                File file = new File(appDir,id.toString()+".jpg");
                RequestBody  body = new MultipartBody.Builder("AaB03x")
                        .setType(MultipartBody.FORM)
                        .addPart(Headers.of("Content-Disposition", "form-data; name=\"image\"; filename=\""+id+".jpg\""),
                                        RequestBody.create(MediaType.parse("image/jpg"),file))
                        .addFormDataPart("methodId",methodId)
                        .addFormDataPart("tel", phone.getText().toString())
                        .addFormDataPart("endDate",dbutton.getText().toString())
                        .addFormDataPart("password",password.getText().toString())
                        .addFormDataPart("username",name.getText().toString())
                       // .addFormDataPart("id",SharedPreferencesUtils.getData(MyApplication.getContext(),"user"," "))
                        .addFormDataPart("id","1010101")
                        .build();
                Request request;
                if(data.equals("no")){
                    if(me==false){
                        request = new Request.Builder()
                                .url(Data.getAddress()+"/api/user")
                                .post(body)
                                .build();
                    }
                    else {
                        request = new Request.Builder()
                                .url(Data.getAddress()+"/api/users")
                                .post(body)
                                .build();
                    }

                }
                else{
                    request = new Request.Builder()
                            .url(Data.getAddress()+"/api/userUpdate/"+id)
                            .post(body)
                            .build();
                }
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("onFailure", e.toString());
                        dialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TastyToast.makeText(MyApplication.getContext(), "连接超时", TastyToast.LENGTH_LONG,
                                        TastyToast.ERROR);
                            }
                        });

                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String message=response.body().string();

                        String cz=null;
                        String id=null;
                        JSONObject json=null;
                        Log.d("onResponse", "onResponse: "+message);
                        try {
                            json=new JSONObject(message);
                            cz=json.getString("message");
                            id=json.getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(message.equals("更新成功")){
                            setResult(-1);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TastyToast.makeText(MyApplication.getContext(), message, TastyToast.LENGTH_LONG,
                                            TastyToast.SUCCESS);
                                    dialog.dismiss();
                                }
                            });
                            finish();
                        }
                        else if(cz!=null&&cz.equals("success")){
                            final String yid=id;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TastyToast.makeText(MyApplication.getContext(),"您的id："+yid, TastyToast.LENGTH_LONG,
                                            TastyToast.SUCCESS);
                                    dialog.dismiss();
                                }
                            });
                        }
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    TastyToast.makeText(MyApplication.getContext(), message, TastyToast.LENGTH_LONG,
                                            TastyToast.WARNING);
                                }
                            });
                        }

                    }

                });
            }
        }).start();
    }
    private Boolean check(){
        if(goodpic&&!name.getText().equals("")&&isMobile(phone.getText().toString())&&!phone.getText().equals("")&&!password.getText().equals("")){
            if(checkBox1L.isChecked()){
                kind=0;
                return true;
            }
            else if(checkBox2S.isChecked()&&!dbutton.getText().equals("日期选择")){
                kind=1;
                return true;
            }
            return false;
        }
        else
            return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.info);
        password=findViewById(R.id.password);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(" ");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Button takeButton=findViewById(R.id.uploading);
        Button saveButton=findViewById(R.id.save);
        imageView=findViewById(R.id.perface);
        name=findViewById(R.id.name);
        phone=findViewById(R.id.phone);
        dbutton=findViewById(R.id.dbutton);
        checkBox1L=findViewById(R.id.always);
        checkBox2S=findViewById(R.id.shtime);
        checkBox1L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox2S.isChecked()){
                    checkBox2S.setChecked(false);
                }
                if(checkBox1L.isChecked()){
                    dbutton.setText("9999-12-31");
                    dbutton.setClickable(false);
                }
            }
        });
        checkBox2S.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox1L.isChecked()){
                    checkBox1L.setChecked(false);
                }
                if(checkBox2S.isChecked()){
                    dbutton.setText("日期选择");
                    dbutton.setClickable(true);
                }
            }
        });
        dbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            URL url = null;//取得资源对象
//                            url = new URL("http://www.baidu.com");
//                            URLConnection uc = url.openConnection();//生成连接对象
//                            uc.connect(); //发出连接
//                            long ld = uc.getDate();
//                            Calendar calendar=Calendar.getInstance();
//                            calendar.setTimeInMillis(ld);
//                            showDate(calendar);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();//没有同步
                Calendar calendar=Calendar.getInstance();
                showDate(calendar);
            }
        });
        takeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                takePic();
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonInfo.this);// 创建alertDialog对象
                builder.setTitle("请上传方式");
                final String[] items = new String[] { "相机拍照", "相册挑选" };
                builder.setItems(items,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            takePic();
                        }
                        else
                            choosephoto();
                    }
                });
                builder.show();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!goodpic)
                    TastyToast.makeText(MyApplication.getContext(), "请上传一张有效照片", TastyToast.LENGTH_LONG,
                            TastyToast.WARNING);
                else if(!check())
                    if(!isMobile(phone.getText().toString()))
                        TastyToast.makeText(MyApplication.getContext(), "请填写正确的手机号", TastyToast.LENGTH_LONG,
                                TastyToast.WARNING);
                    else TastyToast.makeText(MyApplication.getContext(), "请完善信息", TastyToast.LENGTH_LONG,
                            TastyToast.WARNING);
                else{
                    upload();
                }
            }
        });
        Intent intent=getIntent();
        data=intent.getStringExtra("data");
        me=intent.getBooleanExtra("me",false);
        if(!data.equals("no")){
            methodId="2";
            goodpic=true;
            person = DataSupport.where("username = ?", data).findFirst(Person.class);
            dbutton.setText(person.getEnddate());
            id=person.getUserid();
            if(person.getEnddate().equals("9999-12-31")){
                checkBox2S.setChecked(false);
                checkBox1L.setChecked(true);
            }
            else{
                checkBox2S.setChecked(true);
                checkBox1L.setChecked(false);
            }
            id=person.getUserid();
            name.setText(person.getUsername());
            phone.setText(person.getTel());
            password.setText(person.getPassword());
            Log.d("image", "onCreate: "+Data.getAddress()+"/images/"+person.getUserid()+".jpg");
            if(person.getUsername().equals("mark")){
                imageView.setImageResource(R.drawable.people1);
            }
            else if(person.getUsername().equals("dalao")){
                imageView.setImageResource(R.drawable.people2);
            }
            else if(person.getUsername().equals("戴霸天")){
                imageView.setImageResource(R.drawable.people3);
            }
            else if(person.getUsername().equals("Alice")){
                imageView.setImageResource(R.drawable.people4);
            }
            else{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(BitmapFactory.decodeFile((Environment.getExternalStorageDirectory()+"/FaceOpen/"+person.getUserid()+".jpg")));
                    }
                }).start();
            }

        }
        else {
            id="temp";
        }
    }
    private static boolean isMobile(String number) {
        String num = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[34578]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            //matches():字符串是否在给定的正则表达式匹配
            return number.matches(num);
        }
    }
    private void choosephoto(){
        goodpic=false;
        if(!name.getText().toString().equals("")) {
            File appDir = new File(Environment.getExternalStorageDirectory(), "FaceOpen");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            outputimage = new File(appDir,  "temp.jpg");
            if (outputimage.exists()) {
                outputimage.delete();
            }
            try {
                outputimage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");//相片类型
            startActivityForResult(intent,2);
        }
        else{
            TastyToast.makeText(MyApplication.getContext(), "请输入名字", TastyToast.LENGTH_LONG,
                    TastyToast.WARNING);
        }
    }
    private void showDate(Calendar calendar){
        int year = calendar.get(Calendar.YEAR);//当前年
        int month = calendar.get(Calendar.MONTH);//当前月
        int day = calendar.get(Calendar.DAY_OF_MONTH);//当前日
        DatePickerDialog dialog = new DatePickerDialog(this, mdateListener, year, month, day);
        dialog.show();
    }
    protected void takePic(){
        goodpic=false;
        if(!name.getText().toString().equals("")){
            File appDir = new File(Environment.getExternalStorageDirectory(), "FaceOpen");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            outputimage = new File(appDir,"temp.jpg");
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
            TastyToast.makeText(MyApplication.getContext(), "请输入名字", TastyToast.LENGTH_LONG,
                    TastyToast.WARNING);
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
                        Bitmap newbitmap=rotaingImageView(readPictureDegree(Environment.getExternalStorageDirectory()+"/FaceOpen/"+id+".jpg"),getSmallBitmap(outputimage,800,400));
                        imageView.setImageBitmap(newbitmap);
                        compressImage(newbitmap);
                        goodpic=true;
                }
                break;
            case choose_photo:
                if(resultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }
                    else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:break;
        }
    }
    private void displayImage(String imagepath){
        if(imagepath!=null){
            Bitmap bitmap=BitmapFactory.decodeFile(imagepath);
            imageView.setImageBitmap(bitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);//这里压缩options%，把压缩后的数据存放到baos中
            //压缩好后写入文件中
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputimage);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bitmap newbitmap=rotaingImageView(readPictureDegree(Environment.getExternalStorageDirectory()+"/FaceOpen/"+id+".jpg"),getSmallBitmap(outputimage,800,400));
            compressImage(newbitmap);
            goodpic=true;
        }
        else
            TastyToast.makeText(MyApplication.getContext(), "fail to image", TastyToast.LENGTH_LONG,
                    TastyToast.ERROR);
    }
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri=data.getData();
        String imagePath=getImagepath(uri,null);
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagepath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docid=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docid.split(":")[1];
                String seletion=MediaStore.Images.Media._ID+"="+id;
                imagepath=getImagepath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,seletion);
            }
            else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contenuri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docid));
                imagepath=getImagepath(contenuri,null);
            }
        }
        else if("content".equalsIgnoreCase(uri.getScheme())){
            imagepath=getImagepath(uri,null);
        }
        else if("file".equalsIgnoreCase(uri.getScheme())){
            imagepath=uri.getPath();
        }
        displayImage(imagepath);
    }
    private String getImagepath(Uri uri,String seletion){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,seletion,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    public void getUrlImage(String url) {
        try {
            URL picurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection)picurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            final Bitmap img = BitmapFactory.decodeStream(is);
            compressImage(img);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(img);
                }
            });
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
