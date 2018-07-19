/**
 * File Info：com.ynshun.spider.Spider17369.java
 * Created Date：2017年5月27日 下午2:20:16
 * Created User: ynshun
 */
package com.ynshun.spider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ynshun.data.MapData;
import com.ynshun.jdbc.ConnectionMysql;

/**
 * @description
 * 
 * @author ynshun
 * @version 1.0
 *
 */
public class Spider17369 {
	
	String urls[] = {
		"https://www.pornjam.com/sites/videos/",
		"https://www.sexoquente.tv",
		"https://www.sexoquente.tv/mais-vistos/m/",
		"https://www.sexoquente.tv/mais-votados/m/",
		"https://www.sexoquente.tv/pornstars/",
		"https://www.sexoquente.tv/sitios/videos/"
	};
	
	public static final ExecutorService threadPool = Executors.newFixedThreadPool(30);
	
	
	public static void main(String[] args) {
		String rootUrl = "https://www.pornjam.com/top-rated/m/";
		String basePath = "https://www.pornjam.com";
		
		start(rootUrl, basePath);
	}
	
	
	private static void start(String rootUrl, String basePath) {
		System.err.println("开始采集该地址数据：" + rootUrl);
		
		Document root = DocumentToolkit.getDocument(rootUrl);
		
		Elements elements = root.getElementsByClass("muestra-canal");
		if (elements == null || elements.size() == 0) {
			elements = root.getElementsByClass("muestra-chicas");
		}
		
		for (Element element : elements) {
			try {
				Element link = element.getElementsByClass("thumb").first();
				String videoId = link.attr("data-stats-video-id");
				String videoName = link.attr("data-stats-video-name");
				
				if (exists(videoId)) {
					System.out.println("【" + videoName + "】 已经采集过了~~");
					continue;
				}
				
				String detailUrl = link.attr("href");
				
				String thumbImg = link.getElementsByTag("img").first().attr("src");
				
				MapData data = MapData.getInstance();
				data.put("videoId", videoId);
				data.put("videoName", videoName);
				data.put("thumbImg", thumbImg);
				data.put("detailUrl", basePath + detailUrl);
				
				getMP4URL(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Element paginacion = root.getElementsByClass("paginacion").first();
		if (paginacion != null) {
			Elements allPage = paginacion.getElementsByTag("a");
			if (allPage != null && allPage.size() > 0) {
				for (Element page : allPage) {
					if (page.text().equals("Next »")) {
						String url = basePath + page.attr("href");
						start(url, basePath);
						break;
					}
				}
			}
		}
	}
	
	
	private static void getMP4URL(MapData data) {
		String url = data.getString("detailUrl");
		Document root = DocumentToolkit.getDocument(url);
		
		Element video = root.getElementById("player-videojs_html5_api");
		String playerThumbImg = null;
		
		if (video == null) {
			video = root.getElementById("player-videojs");
			playerThumbImg = video.attr("poster");
		}
		
		Element source = video.getElementsByTag("source").first();
		String videoType = source.attr("type");
		String videoUrl = source.attr("src");
		String playerHtml = video.outerHtml();
		
		data.put("playerThumbImg", playerThumbImg);
		data.put("videoUrl", videoUrl);
		data.put("videoType", videoType);
		data.put("playerHtml", playerHtml);
		
		insert(data);
		
		System.out.println("【" + data.getString("videoName") + "】 采集完成！");
		
	}
	
	
	private static boolean exists(String videoId) {
		return ConnectionMysql.countSql("select count(1) from videos where video_id = " + videoId) > 0;
	}
	
	private static void insert(MapData data) {
		String sql = "insert into videos(id, video_id, video_name, thumb_img, detail_url, player_thumb_img, video_type, video_url, player_html) values(null, ?, ?, ?, ?, ?, ?, ?, ?)";
		List<Object> params = new ArrayList<Object>();
		params.add(data.getInt("videoId"));
		params.add(data.getString("videoName"));
		params.add(data.getString("thumbImg"));
		params.add(data.getString("detailUrl"));
		params.add(data.getString("playerThumbImg"));
		params.add(data.getString("videoType"));
		params.add(data.getString("videoUrl"));
		params.add(data.getString("playerHtml"));
		
		ConnectionMysql.insertSql(sql, params);
	}
}
