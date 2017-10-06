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
	 * xmlתΪmap����
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
	 * ���ı���Ϣ����תΪxml
	 * @param textMessage
	 * @return
	 */
	public static String TextMessageToXml(TextMessage textMessage) {
		XStream xstream = new XStream();
		xstream.alias("xml", textMessage.getClass());
		return xstream.toXML(textMessage);
		
	}
	
	
	/**
	 * ͼ����ϢתΪxml
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
	 * �ı���Ϣ����װ
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
	 * 	�԰�˵Ļظ���ͼ����Ϣ��
	 * @param toUserName
	 * @param fromUserName
	 * @return
	 */
	public static String InitEatMessage(String toUserName,String fromUserName){
		String message = null;
		List<News> newsList = new ArrayList<News>();
		NewsMessage newsMessage = new NewsMessage();
		
		News news00 = new News();
		news00.setTitle("�԰����~");
		news00.setDescription("�٣�����ţ��˿������ζ������Ӳ���в����������̻����ײ�������ơ�İ׵Ķ��ر����Ҫ���������ݶ�˿�������ǣ��ɶ���˿��Ƥ���۹��أ��������ư���");
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
	 * һƪ�ĵĻظ���ͼ����Ϣ��
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
		
		musicNews.setTitle("����˵");
		musicNews.setDescription("����Ӵ");
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
	 * ѧϰȥ�Ļظ���ͼ����Ϣ��
	 * @param toUserName
	 * @param fromUserName
	 * @return
	 */
	public static String InitStudyMessage(String toUserName,String fromUserName){
		String message = null;
		List<News> newsList = new ArrayList<News>();
		NewsMessage newsMessage = new NewsMessage();
		
		News news01 = new News();
		news01.setTitle("�����ѧϰ��");
		news01.setDescription("ѧϰһ����Լ��");
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
	 * ���˵�
	 * @return
	 */
	public static String MenuText() {
		StringBuffer sb = new StringBuffer();
		sb.append("��ӭ���Ĺ�ע������Ϊ���ŵ�ʳ�÷�����\n\n");
		sb.append("������빺���ˣ��뷢��'�԰��';\n");
		sb.append("�����Ҫ�ָ�Ԫ�������Լ���һ�ڣ������Է���'һ�׸�'��'һ�仰'����'һƪ��';\n");
		sb.append("������뿴���ҵĸ�����վ�����ݰ���html��css�Ļ�����java��֪ʶ�㣬�뷢��'ѧϰȥ'������˳�㿴���ҵļ�ʻ�;\n\n");
		sb.append("�ظ��������˲˵���\n\n");
		sb.append("��˵һ�� �簲 ������\n");
		return sb.toString();
	}
	
	
	/**
	 * ���Ļظ�
	 * @return
	 */
	public static String GoodNight() {
		StringBuffer sb = new StringBuffer();
		sb.append("��������");
		return sb.toString();
	}
	
	
	/**
	 * �簲�Ļظ�
	 * @return
	 */
	public static String GoodMorning() {
		StringBuffer sb = new StringBuffer();
		sb.append("�簡���ǵø��Լ�һ��ӵ��");
		return sb.toString();
	}
	
	
	/**
	 * һ�仰�Ļظ�
	 * @return
	 */
	public static String OneWord(){
		String[] word = new String[40];
		word[0] = "ÿ���˶����Ž�����ÿ���˶�û�ѻ����ꡣ--���¶�������";
		word[1] = "���Ա�����������ĵ�������һ�ֵ��һ�����ڴ��һ��������ɴ�����ϰ���--���¶�������";
		word[2] = "����ǡ������е�����yu���������ӻ�ʳ��������������--ľ�ġ�����֮����";
		word[3] = "�Ҿ�����֮��ᷢ��ʲô��Ȼ����ش�-��Ҳ����Ȼ�����--evenС��ʹ";
		word[4] = "����û��ʲô�õĻ��򲻺õĻ�����������ر��뽲�ģ��Ͷ��ǶԵġ�--����";
		word[5] = "���������ѿڶ������˺�Ҫ��˼�����С�";
		word[6] = "������ǣ�û�й���--���ٶ��뼤��8��";
		word[7] = "�������³��˾ţ����������޶�����--������ż�ġ�";
		word[8] = "��������Ϊ�������ʶ����Ҫ��ĳ���˹�����������������������Խ�쿪ʼԽ�á�--������������ɯ��";
		word[9] = "���ո�ɽ����������ãã��--�Ÿ�";
		word[10] = "��׽������¶ƶ������ɥ�����ø�����������У������䱾�ޡ�--������ż�ġ�";
		word[11] = "��Ӧ��֮����ı�е�֮���������˼������--������ż�ġ�";
		word[12] = "������֮����˹�ɼ̣������Ѿá�--������ż�ġ�";
		word[13] = "���������ߣ�����֪�㡣--������ż�ġ�";
		word[14] = "ֹ��֮�����壺һԻǫ��ʡ������Ի����������Ի���Դ��ѣ���Իˡ��Ϣ������Ի�����ְ���--������ż�ġ�";
		word[15] = "�ܶ�ʱ������֮���Զ�����Ϊĳ��������ᡢ���������Ӱ������Ъ˹���ֻ��ĳ��������˼�ı��ְ��ˡ�--�������밧�桷";
		word[16] = "Ů��£ͷ��ʱб��һЦ�ܺÿ���--ľ��";
		word[17] = "���¶����Լ�������--ľ��";
		word[18] = "��ʵ�¶�����һ�ֿ�С�--ľ��";
		word[19] = "Ҳ��һ�ֵ�������ǰ�ɫ�Ļ�����--ľ��";
		word[20] = "�㱳���и�΢Ц���ҡ�";
		word[21] = "��  ���������  --ľ��";
		word[22] = "���������߱�����  --ľ��";
		word[23] = "���Ѳ�����  ��װ�����������  --ľ��";
		word[24] = "����  ���ϼ�������  --ľ��";
		word[25] = "�����޷�  ����Ҳ���ֲ���֧ѽ  --ľ��";
		word[26] = "������һ��ϲ����Ϸ�Ķ���  --ľ��";
		word[27] = "��ѵ��ϵ���Ҷ������  ���ǲ�����һ�仰";
		word[28] = "���˴Ҵ�  ȫ��֪·�Ϸ������ı������  --ľ��";
		word[29] = "����ɥ־  ��־С  ־����������־  --ľ��";
		word[30] = "���̸������  ��ȸ˵������ô�ི��  --ľ��";
		word[31] = "���²�����  ����δ���Ĺ�����  --ľ��";
		word[32] = "��Ŀڴ�����  ��ϧ���Լ���������";
		word[33] = "��ֻ�������ҹ¶�   ����Ʒ���¶�";
		word[34] = "����˯���˵�ˮ";
		word[35] = "����  ˮ��   ���ܱ��˵�   �����̵�ס������";
		word[36] = "���϶������Ե֮Ե";
		word[37] = "�ʹ˿�����ֵؿ�ȹ���";
		word[38] = "����Ӧ������   ��˵��";
		word[39] = "ûʲô��";
		
		int index = (int) (Math.random() * word.length);
		String random = word[index];
		StringBuffer sb = new StringBuffer();
		sb.append(random);
		return sb.toString();
	}
	
	
}


