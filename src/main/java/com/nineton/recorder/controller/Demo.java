package com.nineton.recorder.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;


import org.apache.http.ssl.SSLContextBuilder;  
import org.apache.http.ssl.TrustStrategy;  
import org.apache.http.util.EntityUtils;  
import java.security.cert.CertificateException;  
import java.security.cert.X509Certificate;  
import javax.net.ssl.SSLContext;  
import javax.net.ssl.TrustManager;  
import javax.net.ssl.X509TrustManager;  

public class Demo {

	private static final String CHARSET_UTF8 = "UTF-8";

	private static final String SERVICE_URL = "https://api.iflyrec.com";
	private static String accessKeyId = "";
	private static String accessKeySecret = "";
	
	public static void main(String[] args) {
		
		Demo demo = new Demo();
//		demo.uplaod();
		demo.step02_getResult();
	}
	
	public void uplaod() {
		File file = new File("D:/centos_share/www/Recorder/runtime/download/1527677937.5273.2879.aac");
//		InputStream fileIn = Demo.class.getClassLoader().getResourceAsStream(file.getPath());
		InputStream fileIn = null;
		try {
			fileIn = new FileInputStream("D:/centos_share/www/Recorder/runtime/download/1527677937.5273.2879.aac");
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}  ;
		byte[] content = null;
		try {
//			System.out.println(fileIn);
//			System.exit(0);
			content = IOUtils.toByteArray(fileIn);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// ☆☆☆使用TreeMap对内容根据Key进行自然排序
		Map<String, Object> map = new TreeMap<String, Object>();
		map.put("dateTime", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
		map.put("accessKeyId", accessKeyId);
		map.put("signatureRandom", UUID.randomUUID().toString());
		map.put("fileName", file.getName());
		map.put("fileSize", content.length);
		map.put("duration", 10000);//真实的音频时长
		map.put("language", "cn");//目前服务支持 cn-中文, en-英文两个语种
		String formUrlString = null;
		try {
			formUrlString = NRTSignature.formUrlEncodedValueParameters(map);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		String result = requestPost(SERVICE_URL + "/v2/upload" + "?" + formUrlString, map, content);
		System.out.println("ResultInfo = " + result);
	}

	/**
	 * 在获取某个订单的识别结果之前最好设置一定的等待时间，我们服务针对不同时长的订单
	 * ，处理的时间也会不同。当然我们服务也提供了订单识别完成的回调功能（详见用户文档）
	 * ，当订单处理完成后会通知客户，用户可通过订单ID获取相应的转写结果。
	 */
	public void step02_getResult() {
		// ☆☆☆使用TreeMap对内容根据Key进行自然排序
		Map<String, Object> map = new TreeMap<String, Object>();
//		map.put("dateTime", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
		map.put("dateTime", "2018-06-05T19:01:59+0800");
//		System.out.println(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
//		System.exit(0);
//		map.put("signatureRandom", UUID.randomUUID().toString());
		map.put("signatureRandom", "be31b083-10e3-f316-e47d-db2ae7731f58");
		map.put("accessKeyId", accessKeyId);
		map.put("orderId", "DKHJQ201806051700005A");//订单ID
		String formUrlString = null;
		try {
			formUrlString = NRTSignature.formUrlEncodedValueParameters(map);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String result = requestGet(SERVICE_URL + "/v2/getResult" + "?" + formUrlString, map);
		System.out.println("ResultInfo = " + result);
	}

	private String requestGet(String url, Map<String, Object> map) {
		String signature = null;
		System.out.println(map);
		try {
			signature = NRTSignature.gernerateSignature(map, accessKeySecret);
			signature = "v2LrYBbVET8OtxhUJcz7PlTYYDs=";
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		CloseableHttpClient client = HttpClients.createDefault();
		
		// 依次是目标请求地址，端口号,协议类型  
//        HttpHost target = new HttpHost("10.10.100.102:8080/mytest", 8080,  
//                "http");  
        // 依次是代理地址，代理端口号，协议类型  
//        HttpHost proxy = new HttpHost("192.168.13.30", 8888, "http");  
//        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();  
		System.out.println("-------------------------------");
		System.out.println(url);
		System.out.println(signature);
		System.out.println("-------------------------------");
		HttpGet httpGet = new HttpGet(url);
//		httpGet.setConfig(config);
		httpGet.setHeader("signature", signature);
		CloseableHttpResponse response = null;
		String responseString = null;
		try {
			response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				String message = "call servie failed: " + response.getStatusLine();
				System.out.println(message);
			}
			HttpEntity entity = response.getEntity();
			byte[] responseContent = IOUtils.toByteArray(entity.getContent());
			responseString = IOUtils.toString(responseContent, CHARSET_UTF8);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(response);
		}
		return responseString;

	}

	private String requestPost(String url, Map<String, Object> map, byte[] uploadContent) {
		String signature = null;
		try {
			// 生成signature
			signature = NRTSignature.gernerateSignature(map, accessKeySecret);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);
		httppost.setHeader("signature", signature);
		HttpEntity reqEntity = EntityBuilder.create().setBinary(uploadContent)
				.setContentType(ContentType.create("application/json", CHARSET_UTF8)).build();
		httppost.setEntity(reqEntity);
		CloseableHttpResponse response = null;
		String responseString = null;
		try {
			response = client.execute(httppost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				String message = "call servie failed: " + response.getStatusLine();
				System.out.println(message);
			}
			HttpEntity entity = response.getEntity();
			byte[] responseContent = IOUtils.toByteArray(entity.getContent());
			responseString = IOUtils.toString(responseContent, CHARSET_UTF8);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(response);
		}
		return responseString;
	}

	
	public static void configureHttpClient(HttpClientBuilder clientBuilder)   
    {  
        try  
        {  
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy()  
            {  
                // 信任所有  
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException   
                {  
                    return true;  
                }  
            }).build();  
              
            clientBuilder.setSSLContext(sslContext);  
              
        }catch(Exception e)  
        {  
            e.printStackTrace();  
        }  
    }  
}
