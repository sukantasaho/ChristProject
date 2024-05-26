package com.christ.erp.services.controllers.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.springframework.stereotype.Component;

import com.christ.erp.services.common.Utils;

@Component
public class ValidateReCaptcha {
	private final static String secretKey;
	private final static String url;
	
	static {
		secretKey = "6LdbBdwUAAAAAPcmTbbLYmbS0lQBFaMWZPqQfMVp";
		url = "https://www.google.com/recaptcha/api/siteverify";
//		secretKey = AppProperties.get("security.recaptcha.secretkey");
//		url = AppProperties.get("security.recaptcha.url");
	}
	
	public boolean validateCaptchaNew(String recaptchaResponse) {
		boolean isValid = false;
		JsonObject jsonObject = null;
	    URLConnection connection = null;
	    InputStream inputStream = null;
	    try {            
	        connection = new URL(ValidateReCaptcha.url + "?secret=" +ValidateReCaptcha.secretKey+ "&response=" + recaptchaResponse).openConnection();
	        inputStream = connection.getInputStream();
	        JsonReader jsonReader = Json.createReader(inputStream);
	        jsonObject = jsonReader.readObject();
	        System.out.println("--------------------");
			System.out.println("token : "+ recaptchaResponse);
			System.out.println("success : "+ jsonObject.get("success").toString());
			System.out.println("challenge_ts : "+ jsonObject.get("challenge_ts").toString());
			System.out.println("score : "+ jsonObject.get("score").toString());
			System.out.println("action : "+ jsonObject.get("action").toString());
			System.out.println("--------------------");
			if(!Utils.isNullOrEmpty(jsonObject) && !Utils.isNullOrEmpty(jsonObject.get("score")) && !Utils.isNullOrEmpty(jsonObject.get("score").toString())){
				if(Double.valueOf(jsonObject.get("score").toString()) >= 0.5) {
					isValid = true;
				}
			}
	    } catch (IOException ex) { 
	    	
	    }
	    finally {
	        if (inputStream != null) {
	            try {
	            	inputStream.close();
	            } catch (IOException e) { }
	        }
	    }
		return isValid;
	}
	
	/*
	@Autowired
	RestTemplate restTemplate;
	
	public boolean validateCaptcha(String recaptchaResponse) throws Exception{
		String params = "?secret="+ValidateReCaptcha.secretKey+"&response="+recaptchaResponse;
		ReCaptchaResponse response = restTemplate.exchange(url+params, HttpMethod.POST, null, ReCaptchaResponse.class).getBody();
		System.out.println("--------------------");
		System.out.println(recaptchaResponse);
		System.out.println("success : "+response.success);
		System.out.println("challenge_ts : "+response.challenge_ts);
		System.out.println("score : "+response.score);
		System.out.println("errorCodes : "+response.errorCodes);
		System.out.println("action : "+response.action);
		System.out.println("--------------------");
		if(!Utils.isNullOrEmpty(response.score) && response.score >= 0.5) {
			return true;
		}
		return false;
	}
	
	public boolean validateCaptchaNew(String recaptchaResponse) {
		String params = "?secret="+ValidateReCaptcha.secretKey+"&response="+recaptchaResponse;
		boolean isValid = false;
		JsonObject jsonObject = null;
	    URLConnection connection = null;
	    InputStream inputStream = null;
	    //String charset = java.nio.charset.StandardCharsets.UTF_8.name();
	    try {            
//	        String query = String.format("secret=%s&response=%s", 
//	        URLEncoder.encode(ValidateReCaptcha.secretKey, charset), 
//	        URLEncoder.encode(recaptchaResponse, charset));
//	        connection = new URL(ValidateReCaptcha.url + "?" + query).openConnection();
	        connection = new URL(ValidateReCaptcha.url+params).openConnection();
	        inputStream = connection.getInputStream();
	        JsonReader jsonReader = Json.createReader(inputStream);
	        jsonObject = jsonReader.readObject();
	        System.out.println("--------------------");
			System.out.println("token : "+ recaptchaResponse);
			System.out.println("success : "+ jsonObject.get("success").toString());
			System.out.println("challenge_ts : "+ jsonObject.get("challenge_ts").toString());
			System.out.println("score : "+ jsonObject.get("score").toString());
			//System.out.println("errorCodes : "+ jsonObject.get("errorCodes").toString());
			System.out.println("action : "+ jsonObject.get("action").toString());
			System.out.println("--------------------");
			if(!Utils.isNullOrEmpty(jsonObject) && !Utils.isNullOrEmpty(jsonObject.get("score")) && !Utils.isNullOrEmpty(jsonObject.get("score").toString())){
				if(Double.valueOf(jsonObject.get("score").toString()) >= 0.5) {
					isValid = true;
				}
			}
	    } catch (IOException ex) {
	        
	    }
	    finally {
	        if (inputStream != null) {
	            try {
	            	inputStream.close();
	            } catch (IOException e) { }
	        }
	    }
		return isValid;
	}*/
}
