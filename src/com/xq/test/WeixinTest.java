package com.xq.test;

import java.io.IOException;

import net.sf.json.JSONObject;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import com.xq.po.AccessToken;
import com.xq.util.WeixinUtil;

public class WeixinTest {
	public static void main(String args[]){
		
		AccessToken token = WeixinUtil.getAccessToken();
		System.out.println("票据："+token.getToken());
		System.out.println("有效时间："+token.getExpiresIn());
		
	}
}
