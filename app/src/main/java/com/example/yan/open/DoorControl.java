package com.example.yan.open;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.fingerprint.FingerprintManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yan.open.other.FingerListener;
import com.example.yan.open.other.FingerPrinterView;
import com.example.yan.open.other.InputPwdView;
import com.example.yan.open.other.JsFingerUtils;
import com.example.yan.open.other.MyInputPwdUtil;
import com.example.yan.open.other.SharedPreferencesUtils;
import com.sdsmdg.tastytoast.TastyToast;
import com.skyfishjy.library.RippleBackground;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yan on 2018/1/29.
 */

public class DoorControl extends Fragment {
    private LocationManager locationManager;
    private ImageButton personal;
    private List<String> doorList=new ArrayList<String>();
    private JsFingerUtils jsFingerUtils;
    private AlertDialog dialog;
    private View mView;
    private View line;
    private ImageView imageView;
    private Spinner mySpinner;
    private ImageButton openbutton;
    private MyInputPwdUtil myInputPwdUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.newshowdoor, container, false);
            doorList.add("12#611");
            doorList.add("12#612");
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,doorList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mySpinner=mView.findViewById(R.id.spinner);
            imageView=mView.findViewById(R.id.imageView5);
            line=mView.findViewById(R.id.line);
            mySpinner.setAdapter(adapter);
            if(Bottom_main.kind==0){
                mySpinner.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                line.setVisibility(View.INVISIBLE);
            }
            openbutton=mView.findViewById(R.id.open);
            mView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            myInputPwdUtil=new MyInputPwdUtil(getContext());
            myInputPwdUtil.getMyInputDialogBuilder().setAnimStyle(R.style.dialog_anim);
            myInputPwdUtil.setListener(new InputPwdView.InputPwdListener() {
                @Override
                public void hide() {
                    myInputPwdUtil.hide();
                }

                @Override
                public void forgetPwd() {
                }

                @Override
                public void finishPwd(String pwd) {
                    myInputPwdUtil.hide();
                    openDoor();
                }
            });
            final RippleBackground rippleBackground=mView.findViewById(R.id.content);
            rippleBackground.startRippleAnimation();
            openbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(SharedPreferencesUtils.getData(MyApplication.getContext(),"openFinger",false)){
                        jsFingerUtils = new JsFingerUtils(getActivity());
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("指纹识别");
                        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                        View viewf = LayoutInflater.from(getActivity()).inflate(R.layout.finger_layout, null);
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
                                    dialog.dismiss();
                                    openDoor();
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
                        myInputPwdUtil.show();

                    }
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
    private void first(){
        if (!SharedPreferencesUtils.getData(MyApplication.getContext(), "openFinger", false)){
            dialog = new AlertDialog.Builder(getActivity())
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
    private void openDoor(){
        OkHttpClient client=new OkHttpClient.Builder()
                .connectTimeout(4, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        RequestBody body = RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), "1212");
        Request request=new Request.Builder()
//                .url("http://192.168.0.122:8080/api/open/"+"1010101/"+ Uri.encode("二号楼1单元"))
                .url("http://192.168.0.122:8080/api/open/"+"1010101/"+ "二号楼1单元")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String ans=response.body().string();
                Log.d("onResponse", "onResponse: "+ans);
                if (ans.equals("开门成功")){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TastyToast.makeText(MyApplication.getContext(), "开门成功", TastyToast.LENGTH_LONG,
                                    TastyToast.SUCCESS);
                        }
                    });
                }


            }
        });
    }
}
