package com.example.octopus.wallpaperhelper.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.octopus.wallpaperhelper.entity.ImageUriVOList;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by octopus on 2019/3/14.
 */

public class SqlLiteStore {
    //创建相册路径存储表
    public static final String CREATE_imageUriData = "CREATE TABLE if not exists imageUriData ("
            + "imageId integer ,"
            + "imageLoc integer ,"
            + "imageUri text)";

    //打开数据库，如果没有就创建
    public static SQLiteDatabase openOrCreate(Context context){
        SQLiteDatabase db = context.openOrCreateDatabase("wallPaperHelper.db",MODE_PRIVATE,null);
        return db;
    }

    //如果没有相应的数据表先新建，在查询
    public static Cursor selectFromCharacter(SQLiteDatabase db){
        db.execSQL(CREATE_imageUriData);

        return db.rawQuery("SELECT * From imageUriData",null);
    }

    //清空所有存档
    public static void deleteAllTable(SQLiteDatabase db){
        db.execSQL("DELETE From imageUriData WHERE 1=1");
    }

    //向数据库储存数据
    public static void saveData(SQLiteDatabase db) {
        deleteAllTable(db);
        //使用ContentValues 对数据进行组装
        ContentValues values = new ContentValues();
        List<ImageUriVOList.imageUriVO> imageUriVOList = ImageUriVOList.getList();
        for (int i = 0; i < imageUriVOList.size(); i++) {
            values = new ContentValues();
            values.put("imageId", imageUriVOList.get(i).getImageId());
            values.put("imageUri", imageUriVOList.get(i).getImageUri());
            values.put("imageLoc", imageUriVOList.get(i).getImageLoc());

            db.insert("imageUriData", null, values);
        }
    }

    //从数据库中获取数据
    public static void getData(SQLiteDatabase db) {
        try {
            //一、获取人物基本属性
            Cursor cursor = db.rawQuery("SELECT * From imageUriData", null);

            List<ImageUriVOList.imageUriVO> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                ImageUriVOList.imageUriVO vo = new ImageUriVOList.imageUriVO();
                vo.setImageId(cursor.getInt(cursor.getColumnIndex("imageId")));
                vo.setImageUri(cursor.getString(cursor.getColumnIndex("imageUri")));
                list.add(vo);
            }
            ImageUriVOList.setList(list);
            cursor.close();
        }catch(Exception e){
            db.execSQL(CREATE_imageUriData);
            getData(db);
        }
    }
}
