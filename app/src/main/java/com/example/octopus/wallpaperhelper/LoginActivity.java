package com.example.octopus.wallpaperhelper;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.octopus.wallpaperhelper.entity.vo.BaseVO;
import com.example.octopus.wallpaperhelper.entity.vo.LoginVO;
import com.example.octopus.wallpaperhelper.util.HttpRequestUtil;
import com.example.octopus.wallpaperhelper.util.HttpUrl;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText login_userid;
    private EditText login_userpassword;
    private Button login_button;

    Handler loginHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    BaseVO<LoginVO> baseVO = (BaseVO<LoginVO>)msg.obj;
                    //存储登陆信息并跳转界面
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        init();
    }

    private void init(){
        login_userid = findViewById(R.id.login_userid);
        login_userpassword = findViewById(R.id.login_userpassword);
        login_button = findViewById(R.id.login_button);

        //登陆按钮
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userid = login_userid.getText().toString();
                String userpassword = login_userpassword.getText().toString();
                if(!userid.equals("") && !userpassword.equals("")){
                    try{
                        final JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userid",userid);
                        jsonObject.put("userpassword",userpassword);

                        new Thread(new Runnable() {
                            public void run() {
                                Looper.prepare();//启用Looper。
                                HttpRequestUtil util = new HttpRequestUtil(HttpUrl.loginUrl,jsonObject);
                                BaseVO<Object> baseVO = util.sendRequestWithHttpClient();
                                //handle baseVO
                                if(baseVO.isSuccess()){
                                    Message message = new Message();
                                    message.what =1;
                                    message.obj = baseVO;
                                    loginHandler.sendMessage(message);
                                }else{
                                    Toast.makeText(LoginActivity.this,"登陆失败："+baseVO.getMessage(),Toast.LENGTH_LONG).show();
                                }
                                Looper.loop();//让Looper开始工作，从消息队列里取消息，处理消息，让消息处理在该线程中完成。
                            }
                        }).start();
                    }catch(Exception e){
                        Toast.makeText(LoginActivity.this,"登陆发生错误："+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this,"请输入账号和密码",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //离开界面后回收垃圾
    }
}
