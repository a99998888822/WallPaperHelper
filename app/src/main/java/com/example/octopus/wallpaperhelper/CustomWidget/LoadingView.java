package com.example.octopus.wallpaperhelper.CustomWidget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.octopus.wallpaperhelper.R;

/**
 * Created by octopus on 2019/3/29.
 */
public class LoadingView extends ProgressDialog {
    public LoadingView(Context context) {
        super(context);
    }
    public LoadingView(Context context, int theme) {
        super(context, theme);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }
    private void init(Context context) {
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.loading_view);//loading的xml文件
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }
    @Override
    public void show() {//开启
        super.show();
    }
    @Override
    public void dismiss() {//关闭
        super.dismiss();
    }
}