package com.example.octopus.wallpaperhelper;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.octopus.wallpaperhelper.Entity.imageUriVOList;
import com.example.octopus.wallpaperhelper.Util.ScreenListener;
import com.example.octopus.wallpaperhelper.Util.sqlLiteStore;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class SubMain1Activity extends AppCompatActivity {
    private LinearLayout package_layout;
    private TextView main_package_id;
    private TextView main_package_text;
    private BottomNavigationView bottomNavigationView;
    //SQLIte数据库对象
    private SQLiteDatabase db;
    //定义系统的壁纸管理服务
    WallpaperManager wallpaperManager;
    static ScreenListener screenListener;

    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;
    private String name;
    private Bitmap bitmap1,bitmap2,bitmap3,bitmap4;

    //获取新的数据信息后刷新ui控件
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //刷新ui控件
                    main_package_id.setText(name);
                    main_package_text.setText("共"+imageUriVOList.getList().size()+"张,点击查看详细信息...");
                    if(bitmap1 != null){
                        image1.setImageBitmap(bitmap1);
                    }else{image1.setBackgroundColor(Color.WHITE);}
                    if(bitmap2 != null){
                        image2.setImageBitmap(bitmap2);
                    }else{image2.setBackgroundColor(Color.WHITE);}
                    if(bitmap3 != null){
                        image3.setImageBitmap(bitmap3);
                    }else{image3.setBackgroundColor(Color.WHITE);}
                    if(bitmap4 != null){
                        image4.setImageBitmap(bitmap4);
                    }else{image4.setBackgroundColor(Color.WHITE);}
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉顶部标题
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sub_main1);

        init();
    }

    private void init(){
        //去掉调用非官方公开API 方法或接口的警告弹窗
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        main_package_id = findViewById(R.id.main_package_id);
        package_layout = findViewById(R.id.package_layout);
        main_package_text = findViewById(R.id.main_package_text);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);

        package_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubMain1Activity.this,packageActivity.class);
                startActivity(intent);
            }
        });

        main_package_text.setText("共"+imageUriVOList.getList().size()+"张,点击查看详细信息...");

        //加载ui控件信息，数据库信息等的子线程
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                refreshData();
                Message message = new Message();
                message.what = 1;
                myHandler.sendMessage(message);
            }
        });thread.start();

        initScreenListener();

        //超过三个导航栏图标使它们均分底部空间
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        disableShiftMode(bottomNavigationView);
    }

    //初始化缩略图
    private Bitmap initImage(int i){
        List<imageUriVOList.imageUriVO> list = imageUriVOList.getList();
        Bitmap bitmap;
        if(list.size()<=i){
            bitmap = null;
        }else {
            bitmap = packageActivity.getImageThumbnail(list.get(i).getImageUri(),200,200);
            if(bitmap == null || list.size()<i) {
                bitmap = null;
            }
        }
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //加载ui控件信息，数据库信息等的子线程
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                refreshData();
                Message message = new Message();
                message.what = 1;
                myHandler.sendMessage(message);
            }
        });thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        screenListener.unregisterListener();
    }

    private void refreshData(){
        name = null;bitmap1 = null;bitmap2 = null;bitmap3 = null;bitmap4 = null;
        //获取数据库对象
        db = sqlLiteStore.openOrCreate(this);
        sqlLiteStore.getData(db);
        //初始化相册名字
        SharedPreferences userSettings= getSharedPreferences("setting", 0);
        name = userSettings.getString("wallPaperHelper_name1","相册名字");

        bitmap1 = initImage(0);
        bitmap2 = initImage(1);
        bitmap3 = initImage(2);
        bitmap4 = initImage(3);

    }
    //备注：更换壁纸时，如果挑选到已失效的壁纸，不予更换，重新挑选（也可以选择在有效的壁纸间进行挑选）

    public void initScreenListener(){
        screenListener = new ScreenListener(SubMain1Activity.this);
        screenListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {

            }

            @Override
            public void onScreenOff() {
                //初始化WallPaperManager
                wallpaperManager = WallpaperManager.getInstance(SubMain1Activity.this);
                try {
                    //改变壁纸
                    List<imageUriVOList.imageUriVO> list = imageUriVOList.getList();
                    String uri = list.get((int)(Math.random()*list.size())).getImageUri();
                    Bitmap bitmap = BitmapFactory.decodeFile(uri);
                    wallpaperManager.setBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SubMain1Activity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onUserPresent() {

            }
        });
    }

    //优化BottomNavigationView控件
    @SuppressLint("RestrictedApi")
    public void disableShiftMode(BottomNavigationView navigationView) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);

            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
                itemView.setShiftingMode(false);
                itemView.setChecked(itemView.getItemData().isChecked());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
