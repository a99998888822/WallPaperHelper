package com.example.octopus.wallpaperhelper.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.octopus.wallpaperhelper.Entity.imageUriVOList;
import com.example.octopus.wallpaperhelper.SubMain1Activity;
import com.example.octopus.wallpaperhelper.R;
import com.example.octopus.wallpaperhelper.Util.ScreenListener;
import com.example.octopus.wallpaperhelper.Util.sqlLiteStore;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by octopus on 2019/3/27.
 */

public class WallPaperHelperService extends Service {
    //SQLIte数据库对象
    private SQLiteDatabase db;
    private Context context;

    //定义系统的壁纸管理服务
    WallpaperManager wallpaperManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        //获取数据库对象
        db = sqlLiteStore.openOrCreate(this);
        sqlLiteStore.getData(db);
        ScreenListener screenListener = new ScreenListener(this);
        screenListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {

            }

            @Override
            public void onScreenOff() {
                //初始化WallPaperManager
                wallpaperManager = WallpaperManager.getInstance(context);
                try {
                    //改变壁纸
                    List<imageUriVOList.imageUriVO> list = imageUriVOList.getList();
                    String uri = list.get((int)(Math.random()*list.size()+1)).getImageUri();
                    Toast.makeText(context,uri,Toast.LENGTH_LONG).show();
                    Bitmap bitmap = BitmapFactory.decodeFile(uri);
                    wallpaperManager.setBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onUserPresent() {

            }
        });

        //showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void showNotification(){
        Date date = new Date();
        Notification.Builder mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(String.valueOf(date.getTime()))
                .setContentText("瓜皮助手");
        //创建点击跳转Intent
        Intent intent = new Intent(this,SubMain1Activity.class);
        //创建任务栈Builder
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SubMain1Activity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        //设置跳转intent到通知中
        mBuilder.setContentIntent(pendingIntent);
        //获取通知服务
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //构建通知
        Notification notification = mBuilder.build();
        nm.notify(0,notification);
        //启动为前台服务
        startForeground(0,notification);
    }
}
