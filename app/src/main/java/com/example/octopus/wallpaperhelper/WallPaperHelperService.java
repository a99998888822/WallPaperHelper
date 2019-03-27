package com.example.octopus.wallpaperhelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.octopus.wallpaperhelper.Util.ScreenListener;

import java.util.Date;

/**
 * Created by octopus on 2019/3/27.
 */

public class WallPaperHelperService extends Service {
    private Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showNotification();
        bindScreenListener();
    }

    private void showNotification(){
        Date date = new Date();
        Notification.Builder mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(String.valueOf(date.getTime()))
                .setContentText("瓜皮助手");
        //创建点击跳转Intent
        Intent intent = new Intent(this,MainActivity.class);
        //创建任务栈Builder
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
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

    private void bindScreenListener(){
        ScreenListener screenListener = new ScreenListener(this);
        ScreenListener.ScreenStateListener screenStateListener = new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {

            }

            @Override
            public void onScreenOff() {
                //切换壁纸
//                BitmapDrawable bitmap = (BitmapDrawable) getResources().getDrawable(R.drawable.picture);
//                WallpaperManager manager = WallpaperManager.getInstance();
//                manager.setBitmap(bitmap.getBitmap());
            }

            @Override
            public void onUserPresent() {

            }
        };
        screenListener.begin(screenStateListener);
    }
}
