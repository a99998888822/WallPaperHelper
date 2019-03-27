package com.example.octopus.wallpaperhelper;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.octopus.wallpaperhelper.Entity.imageUriVOList;
import com.example.octopus.wallpaperhelper.Util.ScreenListener;
import com.example.octopus.wallpaperhelper.Util.sqlLiteStore;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LinearLayout package_layout;
    private TextView main_package_id;
    private TextView main_package_text;
    //SQLIte数据库对象
    private SQLiteDatabase db;

    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        //获取数据库对象
        db = sqlLiteStore.openOrCreate(this);
        sqlLiteStore.getData(db);
        main_package_id = findViewById(R.id.main_package_id);
        package_layout = findViewById(R.id.package_layout);
        main_package_text = findViewById(R.id.main_package_text);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);

        //初始化相册名字
        SharedPreferences userSettings= getSharedPreferences("setting", 0);
        String name = userSettings.getString("wallPaperHelper_name1","相册名字");
        main_package_id.setText(name);

        package_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,packageActivity.class);
                startActivity(intent);
            }
        });

        main_package_text.setText("共"+imageUriVOList.getList().size()+"张,点击查看详细信息...");

        initImage(image1,0);
        initImage(image2,1);
        initImage(image3,2);
        initImage(image4,3);
    }

    //初始化缩略图
    private void initImage(ImageView imageview,int i){
        List<imageUriVOList.imageUriVO> list = imageUriVOList.getList();
        if(list.size()<=i){
            imageview.setBackgroundColor(Color.WHITE);
        }else {
            Bitmap bitmap = packageActivity.getImageThumbnail(list.get(i).getImageUri(),200,200);
            imageview.setImageBitmap(bitmap);
            if(bitmap == null || list.size()<i) {
//            BitmapFactory.Options opts = new BitmapFactory.Options();
//            opts.inMutable = true;
//            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.error, opts);
                imageview.setBackgroundColor(Color.WHITE);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
}
