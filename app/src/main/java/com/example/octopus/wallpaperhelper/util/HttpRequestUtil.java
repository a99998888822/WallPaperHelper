package com.example.octopus.wallpaperhelper.util;

import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.example.octopus.wallpaperhelper.entity.vo.BaseVO;
import com.example.octopus.wallpaperhelper.entity.vo.LoginVO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HttpRequestUtil {
    private JSONObject jsonObject;
    private String url;

    public HttpRequestUtil(String url){
        this.url = url;
    }

    public HttpRequestUtil(String url,JSONObject js){
        this.jsonObject = js;
        this.url = url;
    }

    //发送post请求
    public BaseVO<Object> sendRequestWithHttpClient(){
        BaseVO<Object> baseVO = new BaseVO<>();
        //用HttpClient发送请求，分为五步
        //第一步：创建HttpClient对象
        HttpClient httpCient = new DefaultHttpClient();
        //第二步：创建代表请求的对象,参数是访问的服务器地址
        HttpPost post = new HttpPost(URI.create(url));
        try {
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            for (Iterator stringIterator = jsonObject.keys();stringIterator.hasNext();) {
                String str = (String)stringIterator.next();
                params.add(new BasicNameValuePair(str,jsonObject.getString(str)));
            }

            post.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
            //第三步：执行请求，获取服务器发还的相应对象
            HttpResponse httpResponse = httpCient.execute(post);
            //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                //第五步：从相应对象当中取出数据，放到entity当中
                HttpEntity entity = httpResponse.getEntity();
                String response = EntityUtils.toString(entity,"utf-8");//将entity当中的数据转换为字符串
                //cast response to Base<LoginVO>

                baseVO = JSON.parseObject(response,BaseVO.class);
                return baseVO;
            }

        } catch (Exception e) {
            baseVO.setSuccess(false);
            baseVO.setMessage(e.getMessage());
            e.printStackTrace();
        }

        return baseVO;
    }
}
