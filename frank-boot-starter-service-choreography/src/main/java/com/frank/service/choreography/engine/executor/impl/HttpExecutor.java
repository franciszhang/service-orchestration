package com.frank.service.choreography.engine.executor.impl;

import com.alibaba.fastjson.JSONObject;
import com.frank.service.choreography.engine.executor.Executor;
import com.frank.service.choreography.engine.pojo.Task;
import org.apache.http.*;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static org.apache.http.protocol.HTTP.CONTENT_TYPE;


/**
 * @author francis
 * @version 2022-03-22
 */
@Component
public class HttpExecutor implements Executor {
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();

    @Override
    public String invoke(Task task) {
        String method = task.getMethod();
        Map<String, Object> inputs = task.getInputs();
        String response = null;
        try {
            URL url;
            url = new URL(task.getUrl());
            if ("post".equalsIgnoreCase(method)) {
                response = doPost(url.getHost(), url.getPort(), url.getProtocol(), url.getPath(), inputs);
            } else if ("get".equalsIgnoreCase(method)) {
                response = doGet(url.getHost(), url.getPort(), url.getProtocol(), url.getPath(), inputs);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;

    }


    private String doPost(String host, int port, String scheme, String uri, Map<String, Object> params) throws Exception {
        HttpHost httpHost = new HttpHost(host, port, scheme);
        HttpPost httpPost = new HttpPost(uri);
        StringEntity stringEntity = new StringEntity(JSONObject.toJSONString(params));
        httpPost.setEntity(stringEntity);
        httpPost.setHeader(CONTENT_TYPE, "application/json;charset=UTF-8");
        CloseableHttpResponse response = httpClient.execute(httpHost, httpPost);
        return handleResponse(response);
    }

    private String doGet(String host, int port, String scheme, String uri, Map<String, Object> params) throws Exception {
        HttpHost httpHost = new HttpHost(host, port, scheme);
        StringBuilder uriBuilder = new StringBuilder(uri.contains("?") ? uri : uri + "?");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            uriBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        uriBuilder.deleteCharAt(uriBuilder.length() - 1);
        HttpGet httpGet = new HttpGet(uriBuilder.toString());
        CloseableHttpResponse response = httpClient.execute(httpHost, httpGet);
        return handleResponse(response);
    }

//    public static void main(String[] args) throws IOException {
//        HttpHost httpHost = new HttpHost("test.bsjiot.com", 9030, "http");
//        HttpPost httpPost = new HttpPost("/core/api/v1/open/zf211/getIdCardByImei");
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("imei", "866136056322378");
//        StringEntity stringEntity = new StringEntity(jsonObject.toString());
//        httpPost.setEntity(stringEntity);
//        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");

//        HttpHost httpHost = new HttpHost("xc-pc.huayaoedu.com.cn",443,"https");
//        HttpPost httpPost = new HttpPost("/api.php/api/course-myself/get-course-myself-info");
//        ArrayList<NameValuePair> list = new ArrayList<>();
//        BasicNameValuePair pair = new BasicNameValuePair("courseMyselfId", "5309681206948790273");
//        list.add(pair);
//        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(list);
//        httpPost.setEntity(formEntity);

//        CloseableHttpResponse response = httpClient.execute(httpHost, httpPost);
//        String responseContent = handleResponse(response);
//        System.out.println(responseContent);
//    }

    public static String handleResponse(HttpResponse response) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.toString());
        } else {
            return entity == null ? null : EntityUtils.toString(entity, Consts.UTF_8);
        }
    }

    @Override
    public String getType() {
        return HTTP;
    }


}
