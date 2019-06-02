package com.example.octopus.wallpaperhelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.octopus.wallpaperhelper.customwidget.LoadingView;
import com.example.octopus.wallpaperhelper.entity.ImageUriVOList;
import com.example.octopus.wallpaperhelper.util.SqlLiteStore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PackageActivity extends AppCompatActivity {
    private TextView package_id;
    private LinearLayout linearLayout;
    //SQLIte数据库对象
    private SQLiteDatabase db;
    private DisplayMetrics dm;
    private List<Bitmap> viewList;
    private LoadingView loading;

    //获取新的数据信息后刷新ui控件
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //刷新ui控件
                    refreshLinearLayout();
                    loading.dismiss();
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
        setContentView(R.layout.activity_package);

        init();
    }

    //初始化
    private void init(){
        //获取数据库对象
        db = SqlLiteStore.openOrCreate(PackageActivity.this);
        SqlLiteStore.getData(db);
        dm = getResources().getDisplayMetrics();
        package_id = findViewById(R.id.package_id);
        linearLayout = findViewById(R.id.package_linearlayout);
        SharedPreferences userSettings= getSharedPreferences("setting", 0);
        package_id.setText(userSettings.getString("wallPaperHelper_name1","相册名字"));
        package_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditDialog();
            }
        });

        //打开加载框，style风格为(背景透明+不允许对话框的背景变暗)
        loading = new LoadingView(this,R.style.CustomDialog);
        loading.show();

        //加载ui控件信息，数据库信息等的子线程
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                initLinearLayout();
                Message message = new Message();
                message.what = 1;
                myHandler.sendMessage(message);
            }
        });thread.start();
    }

    //初始化已选择缩略图的layout
    private void initLinearLayout() {
        int width = dm.widthPixels;
        //循环添加相片缩略图
        List<ImageUriVOList.imageUriVO> list = ImageUriVOList.getList();
        viewList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Bitmap bitmap = getImageThumbnail(list.get(i).getImageUri(), (width / 4) - 20, (width / 4) - 20);
            if (bitmap == null) {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inMutable = true;
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.error, opts);
            }
            viewList.add(bitmap);
        }
    }

    //刷新ui界面
    private void refreshLinearLayout(){
        //清空layout中的照片以及按钮
        int count = linearLayout.getChildCount();
        for(int i=count;i>=0;i--){
            View view = linearLayout.getChildAt(i);
            if(view != null){
                linearLayout.removeView(view);
            }
        }
        LinearLayout l1 = new LinearLayout(this);
        l1.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(l1);

        int width = dm.widthPixels;
        for(int i=0;i<viewList.size();i++){
            final int loc = i;

            ViewGroup viewGroup = (ViewGroup) linearLayout.getChildAt(linearLayout.getChildCount()-1);
            int num = viewGroup.getChildCount();
            if(num == 4){
                LinearLayout l = new LinearLayout(this);
                l.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.addView(l);
            }
            viewGroup = (ViewGroup) linearLayout.getChildAt(linearLayout.getChildCount()-1);

            //添加新的照片缩略图
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(viewList.get(loc));
            viewGroup.addView(imageView);
            //params
            LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) imageView.getLayoutParams();
            //获取当前控件的布局对象
            params.setMargins(10,10,10,10);
            //设置当前控件布局的高度、宽度
            params.height=(width/4)-20;
            params.width=(width/4)-20;
            imageView.setLayoutParams(params);//将设置好的布局参数应用到控件中

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(PackageActivity.this);
                    normalDialog.setMessage("是否需要删除此张照片");
                    normalDialog.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteBitmap(loc);
                                }
                            });
                    normalDialog.setNegativeButton("关闭",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    // 显示
                    normalDialog.show();
                }
            });
        }

        ViewGroup viewGroup = (ViewGroup) linearLayout.getChildAt(linearLayout.getChildCount()-1);
        int num = viewGroup.getChildCount();
        if(num == 4){
            LinearLayout l = new LinearLayout(this);
            l.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.addView(l);
        }
        viewGroup = (ViewGroup) linearLayout.getChildAt(linearLayout.getChildCount()-1);

        //尾部添加可以点击的新增相片的功能
        TextView textView = new TextView(this);
        Drawable d=this.getResources().getDrawable(R.drawable.add);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlbum();
            }
        });
        textView.setBackground(d);
        viewGroup.addView(textView);
        LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(textView.getLayoutParams());
        //获取当前控件的布局对象
        params.setMargins(10,10,10,10);
        //设置当前控件布局的高度、宽度
        params.height=(width/4)-20;
        params.width=(width/4)-20;
        textView.setLayoutParams(params);//将设置好的布局参数应用到控件中
    }

    //从相册里删除一张照片
    public void deleteBitmap(int i){
        final int loc = i;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //从数组中删除
                List<ImageUriVOList.imageUriVO> list = ImageUriVOList.getList();
                list.remove(loc);
                ImageUriVOList.setList(list);
                //存储至数据库
                SqlLiteStore.saveData(db);

                initLinearLayout();
                Message message = Message.obtain(myHandler);
                message.what = 1;
                myHandler.sendMessage(message);
            }
        });thread.start();
    }

    //根据uri相对路径获取相册缩略图
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    //打开修改名字的悬浮窗
    private void openEditDialog(){
        final EditText et = new EditText(this);
        new AlertDialog.Builder(this).setTitle("请输入需要修改的相册名")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = et.getText().toString();
                        //提交
                        SharedPreferences userSettings = getSharedPreferences("setting", 0);
                        SharedPreferences.Editor editor = userSettings.edit();
                        editor.putString("wallPaperHelper_name1",name);
                        editor.commit();
                        package_id.setText(name);
                    }
                }).setNegativeButton("取消",null).show();
    }

    //打开相册
    private void openAlbum(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else {
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,200);
        }
    }

    //根据相册返回的结果储存uri
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try{
                final Uri imageUri = data.getData();
                final String selectPhoto = getRealPathFromUri(this,imageUri);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //根据时间生成id
                        Date curDate =  new Date(System.currentTimeMillis());String time = curDate.toString();
                        String time1 = String.valueOf(curDate.getMonth()+1)+String.valueOf(curDate.getDate())+time.substring(11,13)+time.substring(14,16)+time.substring(17,19);
                        ImageUriVOList.imageUriVO vo = new ImageUriVOList.imageUriVO();
                        int Loc = 0;
                        vo.setImageId(Integer.parseInt(time1));
                        vo.setImageUri(selectPhoto);
                        vo.setImageLoc(Loc);

                        //储存新照片的uri
                        List<ImageUriVOList.imageUriVO> list = ImageUriVOList.getList();
                        list.add(vo);
                        ImageUriVOList.setList(list);
                        SqlLiteStore.saveData(db);

                        initLinearLayout();
                        Message message = new Message();
                        message.what = 1;
                        myHandler.sendMessage(message);
                    }
                });thread.start();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(PackageActivity.this,"获取图片出错，请重试",Toast.LENGTH_LONG).show();
            }
        }
    }

    //针对图片URI格式为Uri:: file:///storage/emulated/0/DCIM/Camera/IMG_20170613_132837.jpg
    private static String getRealPathFromUri_Byfile(Context context,Uri uri){
        String uri2Str = uri.toString();
        return uri2Str.substring(uri2Str.indexOf(":") + 3);
    }

    /**
     * 根据图片的Uri获取图片的绝对路径。@uri 图片的uri
     * @return 如果Uri对应的图片存在,那么返回该图片的绝对路径,否则返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        if(context == null || uri == null) {
            return null;
        }
        if("file".equalsIgnoreCase(uri.getScheme())) {
            return getRealPathFromUri_Byfile(context,uri);
        } else if("content".equalsIgnoreCase(uri.getScheme())) {
            return getRealPathFromUri_Api11To18(context,uri);
        }
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion < 11) {
            // SDK < Api11
            return getRealPathFromUri_BelowApi11(context, uri);
        }
        return getRealPathFromUri_AboveApi19(context, uri);
    }

    /**
     * 适配api19以上,根据uri获取图片的绝对路径
     */
    @SuppressLint("NewApi")
    private static String getRealPathFromUri_AboveApi19(Context context, Uri uri) throws java.lang.NullPointerException{
        String filePath = null;
        String wholeID = DocumentsContract.getDocumentId(uri);

        // 使用':'分割
        String id = wholeID.split(":")[1];

        String[] projection = { MediaStore.Images.Media.DATA };
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = { id };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, null);
        int columnIndex = cursor.getColumnIndex(projection[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    /**
     * //适配api11-api18,根据uri获取图片的绝对路径。
     * 针对图片URI格式为Uri:: content://media/external/images/media/1028
     */
    private static String getRealPathFromUri_Api11To18(Context context, Uri uri) {
        String filePath = null;
        String[] projection = { MediaStore.Images.Media.DATA };

        CursorLoader loader = new CursorLoader(context, uri, projection, null,
                null, null);
        Cursor cursor = loader.loadInBackground();

        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }

    /**
     * 适配api11以下(不包括api11),根据uri获取图片的绝对路径
     */
    private static String getRealPathFromUri_BelowApi11(Context context, Uri uri) {
        String filePath = null;
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }
}
