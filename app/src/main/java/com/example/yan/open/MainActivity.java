package com.example.yan.open;

import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private Button login;
    private EditText user,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login=findViewById(R.id.login);
        user=findViewById(R.id.user);
        password=findViewById(R.id.password);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this);
                Intent intent=new Intent(MainActivity.this,Bottom_main.class);
                startActivity(intent,oc2.toBundle());
                finish();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        OkHttpClient client =client = new OkHttpClient.Builder()
//                                .connectTimeout(4, TimeUnit.SECONDS)
//                                .readTimeout(20, TimeUnit.SECONDS)
//                                .build();  //创建OkHttpClient对象。
//                        RequestBody body = new MultipartBody.Builder("AaB03x")
//                                .setType(MultipartBody.FORM)
//                                .addFormDataPart("user",user.getText().toString())
//                                .addFormDataPart("password",password.getText().toString())
//                                .build();
//                        Request request = new Request.Builder()
//                                .url("http://192.168.0.122:8080/api/upload")
//                                .post(body)
//                                .build();
//                        client.newCall(request).enqueue(new Callback() {
//                            @Override
//                            public void onFailure(Call call, IOException e) {
//                                Log.d("onFailure", e.toString());
//                                if(e.getCause().equals(SocketTimeoutException.class)){
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toast.makeText(MyApplication.getContext(),"连接超时请检查网络",Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                                else {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toast.makeText(MyApplication.getContext(),"密码错误",Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//
//                            }
//                            @Override
//                            public void onResponse(Call call, Response response) throws IOException {
//                                Log.d("onResponse", response.body().string());
//                                if(response.body().string().equals("success")){
//                                    SharedPreferencesUtils.saveData(MyApplication.getContext(),"user",user.getText().toString());
//                                    ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this);
//                                    Intent intent=new Intent(MainActivity.this,DoorControl.class);
//                                    startActivity(intent,oc2.toBundle());
//                                    finish();
//                                }
//                            }
//
//                        });
//                    }
//                }).start();
            }
        });
    }
}
