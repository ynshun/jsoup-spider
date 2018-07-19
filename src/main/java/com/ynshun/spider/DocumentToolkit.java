package com.ynshun.spider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DocumentToolkit {
	public static final int GET = 0;
	public static final int POST = 1;

	/**
	 * 获取文档内容
	 * 
	 * @param url
	 *            需要抓取的网页地址
	 * @return
	 */
	public static Document getDocument(String url) {
		return getDocument(url, GET);
	}

	public static Document getDocumentBy(String url, String charSet) {
		return getDocumentBy1(url, charSet);
	}

	/**
	 * 获取文档内容
	 * 
	 * @param url
	 *            需要抓取的网页地址
	 * @param method
	 *            获取页面方式（0-get; 1-post）
	 * @return
	 */
	public static Document getDocument(String url, int method) {
		Document document = null;
		try {
			Connection conn = Jsoup.connect(url);
			conn.userAgent(
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.98 Safari/537.36");
			switch (method) {
			case 0: // get方式
				document = conn.get();
				break;
			case 1: // post方式
				document = conn.post();
				break;
			default: // 默认以get方式
				document = conn.get();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}

	public static Document getDocumentBy1(String myUrl, String charSet) {
		StringBuffer sb = new StringBuffer("");
		URL url;
		try {
			url = new URL(myUrl);
			BufferedReader br = new BufferedReader(
					new InputStreamReader(url.openStream(), StringUtils.isBlank(charSet) ? "UTF-8" : charSet));
			String s = "";
			while ((s = br.readLine()) != null) {
				sb.append(s + "\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Jsoup.parse(sb.toString());
	}

	/**
	 * 获取页面中js变量值
	 * 
	 * @param url
	 * @param variableName
	 * @return
	 * @throws Exception
	 */
	public static String getJSVariable(String url, String variableName) {
		if (StringUtils.isBlank(variableName)) {
			return null;
		}
		/* 取得车系参数配置页面文档 */
		Document doc = DocumentToolkit.getDocument(url);
		/* 取得script下面的JS变量 */
		Elements e = doc.getElementsByTag("script");
		/* 循环遍历script下面的JS变量 */
		for (Element element : e) {
			/* 取得JS变量数组 */
			String[] data = element.data().toString().split("var");
			/* 取得单个JS变量 */
			for (String variable : data) {
				/* 过滤variable为空的数据 */
				if (variable.contains("=")) {
					String[] kvp = variable.split("=");
					/* 取得JS变量存入map */
					if (variableName.equals(kvp[0].trim())) {
						return kvp[1].trim().endsWith(";") ? kvp[1].trim().substring(0, kvp[1].trim().length() - 1)
								: kvp[1].trim();
					}
				}
			}
		}
		return null;
	}

	public static String jsSubstringBetween(String url, String startStr, String endStr) {
		/* 取得车系参数配置页面文档 */
		Document doc = DocumentToolkit.getDocument(url);
		/* 取得script下面的JS变量 */
		Elements e = doc.getElementsByTag("script");
		/* 循环遍历script下面的JS变量 */
		for (Element element : e) {
			String js = element.data().toString();
			if (js.contains(startStr)) {
				return StringUtils.substringBetween(js, startStr, endStr);
			}
		}
		return null;
	}

	public static String jsSubstringBetween(Document doc, String startStr, String endStr) {
		/* 取得script下面的JS变量 */
		Elements e = doc.getElementsByTag("script");
		/* 循环遍历script下面的JS变量 */
		for (Element element : e) {
			String js = element.data().toString();
			if (js.contains(startStr)) {
				return StringUtils.substringBetween(js, startStr, endStr);
			}
		}
		return null;
	}

	public static void main(String[] args) {
		String url = "http://v.youku.com/v_show/id_XOTA4OTM5Mjgw.html";
		Document doc = DocumentToolkit.getDocument(url);
		Elements tvlist = doc.getElementsByAttributeValue("name", "tvlist");
		for (Element tv : tvlist) {
			Element a = tv.getElementsByTag("a").first();
			String detailUrl = a.attr("href").startsWith("http") ? a.attr("href") : "http:" + a.attr("href");

			String videoId = tv.attr("id").replaceAll("item_", "");

			System.out
					.println(videoId + " =====> " + tv.attr("title") + " =====> " + a.text() + " =====> " + detailUrl);
		}
	}

}
