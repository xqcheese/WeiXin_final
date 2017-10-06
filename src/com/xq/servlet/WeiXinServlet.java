package com.xq.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;

import com.xq.po.TextMessage;
import com.xq.util.CheckUtil;
import com.xq.util.MessageUtil;

public class WeiXinServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String signature = req.getParameter("signature");
		String timestamp = req.getParameter("timestamp");
		String nonce = req.getParameter("nonce");
		String echostr = req.getParameter("echostr");
		
		PrintWriter out = resp.getWriter();
		if(CheckUtil.checkSignature(signature, timestamp, nonce)){
			out.print(echostr);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		try {
			Map<String, String> map = MessageUtil.XmlToMap(req);
			String fromUserName = map.get("FromUserName");
			String toUserName = map.get("ToUserName");
			String msgType = map.get("MsgType");
			String content = map.get("Content");
			
			String message = null;
			if(MessageUtil.MESSAGE_TEXT.equals(msgType)){
				if(content.indexOf("晚安")!=-1){
					message = MessageUtil.InitText(toUserName, fromUserName, MessageUtil.GoodNight());
				}else if (content.indexOf("早安")!=-1) {
					message = MessageUtil.InitText(toUserName, fromUserName, MessageUtil.GoodMorning());
				}else if ("?".equals(content)||"？".equals(content)) {
					message = MessageUtil.InitText(toUserName, fromUserName, MessageUtil.MenuText());
				}else if ("吃拌菜".equals(content)) {
					message = MessageUtil.InitEatMessage(toUserName, fromUserName);
				}else if ("一首歌".equals(content)){
					message = MessageUtil.InitMusicMessage(toUserName, fromUserName);
				}else if ("一句话".equals(content)){
					message = MessageUtil.InitText(toUserName, fromUserName, MessageUtil.OneWord());
				}else if ("一篇文".equals(content)){
					message = MessageUtil.InitArticleMessage(toUserName, fromUserName);
				}else if ("学习去".equals(content)){
					message = MessageUtil.InitStudyMessage(toUserName, fromUserName);
				}
				
				else message = MessageUtil.InitText(toUserName, fromUserName, MessageUtil.MenuText());
							
			}else if(MessageUtil.MESSAGE_EVENT.equals(msgType)){
				String eventType = map.get("Event");
				if(MessageUtil.MESSAGE_SUBSCRIBE.equals(eventType)){
					message = MessageUtil.InitText(toUserName, fromUserName, MessageUtil.MenuText());
				}
			}
			
			System.out.println(message);
			
			out.print(message);
			
		} catch (DocumentException e) {
			e.printStackTrace();
		}finally{
			out.close();
		}
		
	}
}
