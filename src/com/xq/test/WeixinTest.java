package com.xq.test;

import java.io.IOException;

import net.sf.json.JSONObject;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import com.xq.po.AccessToken;
import com.xq.util.WeixinUtil;

public class WeixinTest {
	public static void main(String args[]){
		
		AccessToken token = WeixinUtil.getAccessToken();
		System.out.println("Ʊ�ݣ�"+token.getToken());
		System.out.println("��Чʱ�䣺"+token.getExpiresIn());
		
	}
}
