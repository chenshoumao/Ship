package com.solar.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
public class PostMethod {
	
	public static void main(String[] args) {
		String url = "http://192.168.3.45:8080/Land/LandListener";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ship", "{\"db\":\"1.0.0.0_db_release_20170713\",\"app\":\"1.0.0.0_app_release_20170713\",\"toUpdate\":\"ditu,db\",\"ditu\":\"1.0.0.0_ditu_release_20170713\",\"haitu\":\"1.0.0.1_haitu_release_20170717\"}");
		httpClientPost(url, params, "utf-8");
	}
	
	public static String httpClientPost(String urlParam, Map<String, Object> params, String charset) {  
        StringBuffer resultBuffer = null;  
        HttpClient client = new DefaultHttpClient();  
        HttpPost httpPost = new HttpPost(urlParam);  
        // 构建请求参数  
        List<NameValuePair> list = new ArrayList<NameValuePair>();  
        Iterator<Entry<String, Object>> iterator = params.entrySet().iterator();  
        while (iterator.hasNext()) {  
            Entry<String, Object> elem = iterator.next();  
            list.add(new BasicNameValuePair(elem.getKey(), String.valueOf(elem.getValue())));  
        }  
        BufferedReader br = null;  
        try {  
            if (list.size() > 0) {  
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);  
                httpPost.setEntity(entity);  
            }  
            HttpResponse response = client.execute(httpPost);  
            // 读取服务器响应数据  
            resultBuffer = new StringBuffer();  
            br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));  
            String temp;  
            while ((temp = br.readLine()) != null) {  
                resultBuffer.append(temp);  
            }  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        } finally {  
            if (br != null) {  
                try {  
                    br.close();  
                } catch (IOException e) {  
                    br = null;  
                    throw new RuntimeException(e);  
                }  
            }  
        }  
        System.out.println(resultBuffer.toString());
        return resultBuffer.toString();  
    }  
}
