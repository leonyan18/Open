package com.example.yan.open;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by yan on 2018/1/29.
 */

public class DoorControl extends AppCompatActivity {
    private ImageButton personal;
    private List<Door> doorList=new ArrayList<>();
    private JsFingerUtils jsFingerUtils;
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showdoor);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initDoor();
        DoorAdapter doorAdapter=new DoorAdapter(DoorControl.this,R.layout.door_item,doorList);
        ListView listView=findViewById(R.id.doorlist);
        listView.setAdapter(doorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Door door=doorList.get(i);
                if(SharedPreferencesUtils.getData(MyApplication.getContext(),"openFinger",false)){
                    jsFingerUtils = new JsFingerUtils(DoorControl.this);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(DoorControl.this);
                    builder.setTitle("指纹识别");
                    //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                    View viewf = LayoutInflater.from(DoorControl.this).inflate(R.layout.finger_layout, null);
                    //    设置我们自己定义的布局文件作为弹出框的Content
                    builder.setView(viewf);
                    final TextView textView=viewf.findViewById(R.id.result);
                    final FingerPrinterView fingerPrinterView=viewf.findViewById(R.id.finger);
                    dialog = builder.show();
                    jsFingerUtils.startListening(new FingerListener() {
                        @Override
                        public void onStartListening() {
                            if (fingerPrinterView.getState() == FingerPrinterView.STATE_SCANING) {
                                return;
                            } else if (fingerPrinterView.getState() == FingerPrinterView.STATE_CORRECT_PWD
                                    || fingerPrinterView.getState() == FingerPrinterView.STATE_WRONG_PWD) {
                                fingerPrinterView.setState(FingerPrinterView.STATE_NO_SCANING);
                            } else {
                                fingerPrinterView.setState(FingerPrinterView.STATE_SCANING);


                            }
                        }

                        @Override
                        public void onStopListening() {

                        }

                        @Override
                        public void onSuccess(FingerprintManager.AuthenticationResult result) {
                            fingerPrinterView.setState(FingerPrinterView.STATE_CORRECT_PWD);
                        }

                        @Override
                        public void onFail(boolean isNormal, String info) {
                            fingerPrinterView.setState(FingerPrinterView.STATE_WRONG_PWD);
                        }

                        @Override
                        public void onAuthenticationError(int errorCode, CharSequence errString) {
                            Log.d("onAuthenticationError: ",errorCode+"     "+ errString.toString());
                        }

                        @Override
                        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                            Log.d("onAuthenticationError: ",helpCode+"     "+ helpString.toString());
                        }
                    });
                    fingerPrinterView.setOnStateChangedListener(new FingerPrinterView.OnStateChangedListener() {
                        @Override
                        public void onChange(int state) {
                            if (state == FingerPrinterView.STATE_CORRECT_PWD) {
                                TastyToast.makeText(MyApplication.getContext(), "指纹识别成功", TastyToast.LENGTH_LONG,
                                        TastyToast.SUCCESS);
                                dialog.dismiss();
                            }
                            if (state == FingerPrinterView.STATE_WRONG_PWD) {
                                TastyToast.makeText(MyApplication.getContext(), "指纹识别失败", TastyToast.LENGTH_LONG,
                                        TastyToast.ERROR);
                                fingerPrinterView.setState(FingerPrinterView.STATE_NO_SCANING);
                            }
                        }
                    });
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            jsFingerUtils.cancelListening();
                        }
                    });
                }
                else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(DoorControl.this);
                    builder.setTitle("指纹识别");
                    //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                    View viewf = LayoutInflater.from(DoorControl.this).inflate(R.layout.pass_door, null);
                    builder.setTitle("请输入登录密码");
                    builder.setView(viewf);
                    final EditText text=viewf.findViewById(R.id.doorkey);
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                             dialog.cancel();
                        }
                    });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            text.getText();
                        }
                    });
                    builder.show();

                }
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
    private void first(){
        if (!SharedPreferencesUtils.getData(MyApplication.getContext(), "openFinger", false)){
            dialog = new AlertDialog.Builder(DoorControl.this)
                    .setTitle("提示")
                    .setMessage("\n   是否开启指纹开门")
                    .setIcon(R.drawable.finger_red)
                    .setCancelable(false)
                    .setNegativeButton("以后再说", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            SharedPreferencesUtils.saveData(MyApplication.getContext(),"openFinger",false);
                        }
                    })
                    .setPositiveButton("确认设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferencesUtils.saveData(MyApplication.getContext(),"openFinger",true);
                        }
                    })
                    .create();
            dialog.show();
        }
    }
    private void initDoor(){
        Door door1=new Door("12#611");
        doorList.add(door1);
        Door door2=new Door("12#612");
        doorList.add(door2);
    }
}
