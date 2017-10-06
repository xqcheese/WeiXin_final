package com.xq.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;
import com.xq.po.News;
import com.xq.po.NewsMessage;
import com.xq.po.TextMessage;

public class MessageUtil {
	
	public static final String MESSAGE_TEXT = "text";
	public static final String MESSAGE_NEWS = "news";
	public static final String MESSAGE_IMAGE = "image";
	public static final String MESSAGE_VOICE = "voice";
	public static final String MESSAGE_VIDEO = "video";
	public static final String MESSAGE_MUSIC = "music";
	public static final String MESSAGE_SHORTVIDEO = "shortvideo";
	public static final String MESSAGE_LINK = "link";
	public static final String MESSAGE_LOCATION = "location";
	public static final String MESSAGE_EVENT = "event";
	public static final String MESSAGE_SUBSCRIBE = "subscribe";
	public static final String MESSAGE_UNSUBSCRIBE = "unsubscribe";
	public static final String MESSAGE_CLICK = "CLICK";
	public static final String MESSAGE_VIEW = "VIEW";
	
	
	/**
	 * xml转为map集合
	 * @param request
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 */
	public static Map<String, String> XmlToMap(HttpServletRequest request) throws DocumentException, IOException{
		Map<String, String> map = new HashMap<String, String>();
		SAXReader reader = new SAXReader();
		
		InputStream ins = request.getInputStream();
		Document doc = reader.read(ins);
		
		Element root = doc.getRootElement();
		
		List<Element> list = root.elements();
		
		for(Element e:list){
			map.put(e.getName(), e.getText());
		}
		ins.close();
		return map;
	}
	
	
	/**
	 * 将文本消息对象转为xml
	 * @param textMessage
	 * @return
	 */
	public static String TextMessageToXml(TextMessage textMessage) {
		XStream xstream = new XStream();
		xstream.alias("xml", textMessage.getClass());
		return xstream.toXML(textMessage);
		
	}
	
	
	/**
	 * 图文消息转为xml
	 * @param newsMessage
	 * @return
	 */
	public static String NewsMessageToXml(NewsMessage newsMessage) {
		XStream xstream = new XStream();
		xstream.alias("xml", newsMessage.getClass());
		xstream.alias("item", new News().getClass());
		return xstream.toXML(newsMessage);
	}
	
	
	/**
	 * 文本消息的组装
	 * @param toUserName
	 * @param fromUserName
	 * @param content
	 * @return
	 */
	public static String InitText(String toUserName,String fromUserName,String content){
		TextMessage text = new TextMessage();
		text.setFromUserName(toUserName);
		text.setToUserName(fromUserName);
		text.setMsgType(MessageUtil.MESSAGE_TEXT);
		text.setCreateTime(new Date().getTime());
		text.setContent(content);
		return TextMessageToXml(text);
	}
	
	
	/**
	 * 	吃拌菜的回复【图文消息】
	 * @param toUserName
	 * @param fromUserName
	 * @return
	 */
	public static String InitEatMessage(String toUserName,String fromUserName){
		String message = null;
		List<News> newsList = new ArrayList<News>();
		NewsMessage newsMessage = new NewsMessage();
		
		News news00 = new News();
		news00.setTitle("吃拌菜啦~");
		news00.setDescription("嘿，来盘牛肉丝不，口味超好软硬适中不硌牙；来盘花生米不，就着啤的白的都特别棒；要不，再来份肚丝？？？那，干豆腐丝豆皮海桔梗呢，进来瞧瞧啊？");
		news00.setPicUrl("http://xqcheese.ngrok.cc/WeiXin/image/yingzi.jpg");
		news00.setUrl("https://weidian.com/?userid=922941756&wfr=wx_profile");
		
		newsList.add(news00);
		
		newsMessage.setToUserName(fromUserName);
		newsMessage.setFromUserName(toUserName);
		newsMessage.setCreateTime(new Date().getTime());
		newsMessage.setMsgType(MESSAGE_NEWS);
		newsMessage.setArticles(newsList);
		newsMessage.setArticleCount(newsList.size());
		
		message = NewsMessageToXml(newsMessage);
		return message;
	}
	
	
	/**
	 * 一篇文的回复【图文消息】
	 * @param toUserName
	 * @param fromUserName
	 * @return
	 */
	public static String InitArticleMessage(String toUserName,String fromUserName){
		String message = null;
		List<News> newsList = new ArrayList<News>();
		NewsMessage newsMessage = new NewsMessage();
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(date);
		
		String url = "http://v3.wufazhuce.com:8000/api/essay/bymonth/"+dateString+"%2000:00:00?channel=wdj&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android";
		
		String imgurl = "http://v3.wufazhuce.com:8000/api/hp/bymonth/"+dateString+"%2000:00:00?channel=wdj&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android";
		
		News news02 = new News();
		
		JSONObject jsonObject = WeixinUtil.doGetStr(url);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		String str = jsonArray.get(0).toString();
		JSONObject json = JSONObject.fromObject(str);
		
		JSONObject imgObject = WeixinUtil.doGetImg(imgurl);
		JSONArray imgArray = imgObject.getJSONArray("data");
		String imgstr = imgArray.get(0).toString();
		JSONObject imgjson = JSONObject.fromObject(imgstr);
		
		news02.setTitle(json.getString("hp_title"));
		news02.setDescription(json.getString("guide_word"));
		news02.setPicUrl(imgjson.getString("hp_img_url"));
		news02.setUrl("http://m.wufazhuce.com/article/"+json.getString("content_id"));
		
		newsList.add(news02);
		
		newsMessage.setToUserName(fromUserName);
		newsMessage.setFromUserName(toUserName);
		newsMessage.setCreateTime(new Date().getTime());
		newsMessage.setMsgType(MESSAGE_NEWS);
		newsMessage.setArticles(newsList);
		newsMessage.setArticleCount(newsList.size());
		
		message = NewsMessageToXml(newsMessage);
		return message;
	}
	
	
	public static String InitMusicMessage(String toUserName,String fromUserName){
		String message = null;
		List<News> newsList = new ArrayList<News>();
		NewsMessage newsMessage = new NewsMessage();
		
		News musicNews = new News();
		
		musicNews.setTitle("阿婆说");
		musicNews.setDescription("囡囡哟");
		musicNews.setPicUrl("http://xqcheese.ngrok.cc/WeiXin/image/cat.jpg");
		musicNews.setUrl("http://music.163.com/m/song/?id=479422013");
		
		newsList.add(musicNews);
		
		newsMessage.setToUserName(fromUserName);
		newsMessage.setFromUserName(toUserName);
		newsMessage.setCreateTime(new Date().getTime());
		newsMessage.setMsgType(MESSAGE_NEWS);
		newsMessage.setArticles(newsList);
		newsMessage.setArticleCount(newsList.size());
		
		message = NewsMessageToXml(newsMessage);
		return message;
	}
	
	
	/**
	 * 学习去的回复【图文消息】
	 * @param toUserName
	 * @param fromUserName
	 * @return
	 */
	public static String InitStudyMessage(String toUserName,String fromUserName){
		String message = null;
		List<News> newsList = new ArrayList<News>();
		NewsMessage newsMessage = new NewsMessage();
		
		News news01 = new News();
		news01.setTitle("到点儿学习了");
		news01.setDescription("学习一波，约不");
		news01.setPicUrl("http://xqcheese.ngrok.cc/WeiXin/image/xuexi.jpg");
		news01.setUrl("https://xqcheese.github.io/");
		
		newsList.add(news01);
		
		newsMessage.setToUserName(fromUserName);
		newsMessage.setFromUserName(toUserName);
		newsMessage.setCreateTime(new Date().getTime());
		newsMessage.setMsgType(MESSAGE_NEWS);
		newsMessage.setArticles(newsList);
		newsMessage.setArticleCount(newsList.size());
		
		message = NewsMessageToXml(newsMessage);
		return message;
	}
	
	
	
	/**
	 * 主菜单
	 * @return
	 */
	public static String MenuText() {
		StringBuffer sb = new StringBuffer();
		sb.append("欢迎您的关注，以下为本号的食用方法：\n\n");
		sb.append("如果您想购买拌菜，请发送'吃拌菜';\n");
		sb.append("如果想要恢复元气，给自己奶一口，您可以发送'一首歌'、'一句话'或者'一篇文';\n");
		sb.append("如果您想看看我的个人网站，内容包括html和css的基础、java的知识点，请发送'学习去'，还能顺便看看我的简笔画;\n\n");
		sb.append("回复？调出此菜单；\n\n");
		sb.append("来说一句 早安 或者晚安\n");
		return sb.toString();
	}
	
	
	/**
	 * 晚安的回复
	 * @return
	 */
	public static String GoodNight() {
		StringBuffer sb = new StringBuffer();
		sb.append("晚安，好梦");
		return sb.toString();
	}
	
	
	/**
	 * 早安的回复
	 * @return
	 */
	public static String GoodMorning() {
		StringBuffer sb = new StringBuffer();
		sb.append("早啊，记得给自己一个拥抱");
		return sb.toString();
	}
	
	
	/**
	 * 一句话的回复
	 * @return
	 */
	public static String OneWord(){
		String[] word = new String[40];
		word[0] = "每个人都急着讲话，每个人都没把话讲完。--《孤独六讲》";
		word[1] = "语言本来就是两面的刀，存在一种吊诡：一方面在传达，一方面在造成传达的障碍。--《孤独六讲》";
		word[2] = "人生恰如监狱中的窳（yu，三声）劣伙食，心中骂，嘴里嚼。--木心《素履之往》";
		word[3] = "我救了你之后会发生什么？然后你回答，-我也把你救回来。--even小天使";
		word[4] = "世上没有什么好的话或不好的话，如果是你特别想讲的，就都是对的。--黄磊";
		word[5] = "赞美可以脱口而出，伤害要三思而后行。";
		word[6] = "规则就是：没有规则。--《速度与激情8》";
		word[7] = "不如意事常八九，可与人言无二三。--《闲情偶寄》";
		word[8] = "我来是因为如果你意识到你要和某个人共度余生，你会想你的余生能越快开始越好。--《当哈利遇上莎莉》";
		word[9] = "明日隔山岳，世事两茫茫。--杜甫";
		word[10] = "宁捉襟肘以露贫，不借丧马以彰富。有则还吾故有，无则安其本无。--《闲情偶寄》";
		word[11] = "觅应得之利，谋有道之生，即是人间大隐。--《闲情偶寄》";
		word[12] = "凡事物之理，简斯可继，繁则难久。--《闲情偶寄》";
		word[13] = "故善行乐者，必先知足。--《闲情偶寄》";
		word[14] = "止忧之法有五：一曰谦以省过，二曰勤以砺身，三曰俭以储费，四曰恕以息争，五曰宽以弥谤。--《闲情偶寄》";
		word[15] = "很多时候，我们之所以动辄就为某件事物纠结、争吵，或顾影自怜或歇斯底里，只是某种怯于深思的表现罢了。--《爱欲与哀矜》";
		word[16] = "女孩拢头发时斜眼一笑很好看。--木心";
		word[17] = "遇事多与自己商量。--木心";
		word[18] = "其实孤独感是一种快感。--木心";
		word[19] = "也有一种淡淡的鱼肚白色的华丽。--木心";
		word[20] = "你背后有个微笑的我。";
		word[21] = "蠢  都是资深的  --木心";
		word[22] = "无审美力者必无情  --木心";
		word[23] = "花已不香了  人装出闻嗅的样子  --木心";
		word[24] = "黎明  天上几朵嫩云  --木心";
		word[25] = "忧来无方  但是也有乐不可支呀  --木心";
		word[26] = "人类是一种喜欢看戏的动物  --木心";
		word[27] = "风把地上的落叶吹起来  像是补充了一句话";
		word[28] = "行人匆匆  全不知路上发生过的悲欢离合  --木心";
		word[29] = "玩物丧志  其志小  志大者玩物养志  --木心";
		word[30] = "天鹅谈飞行术  麻雀说哪有那么多讲究  --木心";
		word[31] = "岁月不饶人  我亦未曾饶过岁月  --木心";
		word[32] = "你的口唇极美  可惜你自己不能吻它";
		word[33] = "岂只是艺术家孤独   艺术品更孤独";
		word[34] = "冰是睡熟了的水";
		word[35] = "树啊  水啊   都很悲伤的   它们忍得住就是了";
		word[36] = "世上多的是无缘之缘";
		word[37] = "就此快快乐乐地苦度光阴";
		word[38] = "春天应该是晴   你说呢";
		word[39] = "没什么的";
		
		int index = (int) (Math.random() * word.length);
		String random = word[index];
		StringBuffer sb = new StringBuffer();
		sb.append(random);
		return sb.toString();
	}
	
	
}


