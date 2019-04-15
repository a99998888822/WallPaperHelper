package com.example.octopus.wallpaperhelper;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TestActivity extends AppCompatActivity {
    private Button button1;
    private Button button2;
    private TextView textView;
    private Handler handler1;
    private Handler handler2;

    private static final String TAG = "SubMain1Activity";
    private ThreadLocal<Integer> mThreadLocal = new ThreadLocal<>();

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Log.d(TAG, "onCreate: "+mThreadLocal.get());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        textView = findViewById(R.id.textView);
        mThreadLocal.set(5);

        Thread1 thread1 = new Thread1();
        thread1.start();

        Thread2 thread2 = new Thread2();
        thread2.start();

        Thread3 thread3 = new Thread3();
        thread3.start();

        //所有子线程执行完后输出mThreadLocal中的结果
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mHandler.sendEmptyMessage(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class Thread1 extends Thread {

        @Override
        public void run() {
            super.run();
            mThreadLocal.set(1);
            Log.d(TAG, "mThreadLocal1: "+ mThreadLocal.get());
        }
    }

    class Thread2 extends Thread {

        @Override
        public void run() {
            super.run();
            mThreadLocal.set(2);
            Log.d(TAG, "mThreadLocal2: "+ mThreadLocal.get());
        }
    }

    class Thread3 extends Thread {

        @Override
        public void run() {
            super.run();
            mThreadLocal.set(3);
            Log.d(TAG, "mThreadLocal3: "+ mThreadLocal.get());
        }
    }
}
