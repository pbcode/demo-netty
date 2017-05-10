package com.httpclient;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebCrawlerDemo {
    private static final String GEN_TOKEN = "gentoken";

    public static void doSome(String url, String new_url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("ibc", "newspc"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpPost);
            String entity = EntityUtils.toString(closeableHttpResponse.getEntity());
            Map map = JSON.parseObject(entity, Map.class);
            String token = map.get(GEN_TOKEN).toString();
            String newUrl = new_url + token + "&ibc=newspc";
            httpPost = new HttpPost(newUrl);
            list = Lists.newArrayList();
            list.add(new BasicNameValuePair("ntoken", token));
            list.add(new BasicNameValuePair("ibc", "newspc"));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            closeableHttpResponse = httpClient.execute(httpPost);
            System.out.println(closeableHttpResponse.getStatusLine().getStatusCode());
        } catch (IOException e) {
            System.out.println("错误忽略");
        }

    }

    /**
     * 执行CMD命令,并返回String字符串
     */
    public static String executeCmd(String strCmd) throws Exception {
        Process p = Runtime.getRuntime().exec("cmd /c " + strCmd);
        StringBuilder sbCmd = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(p
                .getInputStream(), "GBK"));
        String line;
        while ((line = br.readLine()) != null) {
            sbCmd.append(line + "\n");
        }
        return sbCmd.toString();
    }

    /**
     * 连接ADSL
     */
    public static boolean connAdsl(String adslTitle, String adslName, String adslPass) throws Exception {
        System.out.println("正在建立连接.");
        String adslCmd = "rasdial " + adslTitle + " " + adslName + " "
                + adslPass;
        System.out.println(adslCmd);
        String tempCmd = executeCmd(adslCmd);
        // 判断是否连接成功
        if (tempCmd.indexOf("已连接") > 0) {
            System.out.println("已成功建立连接.");
            return true;
        } else {
            System.err.println(tempCmd);
            System.err.println("建立连接失败");
            return false;
        }
    }

    /**
     * 断开ADSL
     */
    public static boolean cutAdsl(String adslTitle) throws Exception {
        String cutAdsl = "rasdial " + adslTitle + " /disconnect";
        String result = executeCmd(cutAdsl);

        if (result.indexOf("没有连接") != -1) {
            System.err.println(adslTitle + "连接不存在!");
            return false;
        } else {
            System.out.println("连接已断开");
            return true;
        }
    }


    public static void main(String[] args) throws Exception {
        String url = "http://comment.war.163.com/api/v1/products/a2869674571f77b5a0867c3d71db5856/threads/CK2GDLE7000181KT/comments/gentoken?ibc=newspc";
        String new_url = "http://comment.war.163.com/api/v1/products/a2869674571f77b5a0867c3d71db5856/threads/CK2GDLE7000181KT/comments/82877467/action/upvote?ntoken=";
        while (true) {
            boolean conn = connAdsl("宽带连接", "sy_88486036", "313131g");
            if (conn) {
                doSome(url, new_url);
            }
            cutAdsl("宽带连接");
            Thread.sleep(2000);
        }
    }

}