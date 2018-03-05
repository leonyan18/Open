package com.example.yan.open;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.DisposableObserver;
import zwh.com.lib.FPerException;
import zwh.com.lib.RxFingerPrinter;

/**
 * Created by yan on 2018/1/29.
 */

public class DoorControl extends AppCompatActivity {
    private ImageButton personal;
    private List<Door> doorList=new ArrayList<>();
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
                final AlertDialog.Builder builder = new AlertDialog.Builder(DoorControl.this);
                builder.setTitle("指纹识别");
                //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                View viewf = LayoutInflater.from(DoorControl.this).inflate(R.layout.finger_layout, null);
                //    设置我们自己定义的布局文件作为弹出框的Content
                builder.setView(viewf);

                final FingerPrinterView fingerPrinterView=(FingerPrinterView)viewf.findViewById(R.id.finger);
                final AlertDialog dia = builder.show();
                fingerPrinterView.setOnStateChangedListener(new FingerPrinterView.OnStateChangedListener() {
                    @Override
                    public void onChange(int state) {
                        if (state == FingerPrinterView.STATE_CORRECT_PWD) {
                            Toast.makeText(DoorControl.this, "指纹识别成功", Toast.LENGTH_SHORT).show();
                            dia.dismiss();
                        }
                        if (state == FingerPrinterView.STATE_WRONG_PWD) {
                            Toast.makeText(DoorControl.this, "指纹识别失败，请重试",
                                    Toast.LENGTH_SHORT).show();
                            fingerPrinterView.setState(FingerPrinterView.STATE_NO_SCANING);
                        }
                    }
                });
                DisposableObserver<Boolean> observer = new DisposableObserver<Boolean>() {

                    @Override
                    protected void onStart() {
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
                    public void onError(Throwable e) {
                        if(e instanceof FPerException){
                            Toast.makeText(MyApplication.getContext(),((FPerException) e).getDisplayMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if(aBoolean){
                            fingerPrinterView.setState(FingerPrinterView.STATE_CORRECT_PWD);
                        }else{
                            fingerPrinterView.setState(FingerPrinterView.STATE_WRONG_PWD);
                        }
                    }
                };
                choose(observer);
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
    private void initDoor(){
        Door door1=new Door("12#611");
        doorList.add(door1);
        Door door2=new Door("12#612");
        doorList.add(door2);
    }
    private void choose(DisposableObserver<Boolean> observer){
        RxFingerPrinter rxFingerPrinter = new RxFingerPrinter(this);
        rxFingerPrinter.begin().subscribe(observer);//RxfingerPrinter会自动在onPause()时暂停指纹监听，onResume()时恢复指纹监听)
        rxFingerPrinter.addDispose(observer);//由RxfingerPrinter管理(会在onDestroy()生命周期时自动解除订阅)，已可以不调用该方法，自己解除订阅
    }
}
