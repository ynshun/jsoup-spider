package com.ynshun.spider;

import org.jsoup.nodes.Document;

public class SpiderV5fox {
	public static void main(String[] args) {
		String rootUrl = "http://www.v5fox.com/csgo/0-0?keyword=%E7%88%AA%E5%AD%90%E5%88%80%EF%BC%88%E2%98%85%EF%BC%89%20|%20%E6%B7%B1%E7%BA%A2%E4%B9%8B%E7%BD%91%20(%E5%B4%AD%E6%96%B0%E5%87%BA%E5%8E%82";
		Document root = DocumentToolkit.getDocument(rootUrl);

		System.out.println(root.html());
	}
}
