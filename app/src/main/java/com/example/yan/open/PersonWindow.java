package com.example.yan.open;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by yan on 2018/1/30.
 */

public class PersonWindow extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personwindow);
        ImageButton imageButton=findViewById(R.id.door);
        ImageButton toinfo=findViewById(R.id.toinfo);
        Button button_exit=findViewById(R.id.exit);
        Button button_mange=findViewById(R.id.mange);
        button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PersonWindow.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PersonWindow.this,DoorControl.class);
                startActivity(intent);
                onBackPressed();
            }
        });
        button_mange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PersonWindow.this,PersonMange.class);
                startActivity(intent);
            }
        });
        toinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PersonWindow.this,Userinfo.class);
                startActivity(intent);
            }
        });
    }
}
