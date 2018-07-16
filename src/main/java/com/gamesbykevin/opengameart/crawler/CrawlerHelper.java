package com.gamesbykevin.opengameart.crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlerHelper {

    protected static String getFormattedUrl(String url) {
        if (url == null || url.trim().length() < 1)
            return null;

        if (url.trim().endsWith("/")) {
            return url.trim().substring(0, url.trim().length() - 2);
        } else {
            return url.trim();
        }
    }

    protected static String getCategoryName(Document doc) {

        Elements elements = doc.select("div.field-item");

        for (Element element : elements) {

            if (element.text() != null) {
                if (element.text().trim().equalsIgnoreCase("2d art"))
                    return element.text();
                if (element.text().trim().equalsIgnoreCase("3d art"))
                    return element.text();
                if (element.text().trim().equalsIgnoreCase("concept art"))
                    return element.text();
                if (element.text().trim().equalsIgnoreCase("music"))
                    return element.text();
                if (element.text().trim().equalsIgnoreCase("sound effect"))
                    return element.text();
                if (element.text().trim().equalsIgnoreCase("document"))
                    return element.text();
                if (element.text().trim().equalsIgnoreCase("texture"))
                    return element.text();
            }

            //System.out.println("Hello: " + element.text());//.html());
        }

        throw new RuntimeException("Category not found: " + doc.baseUri());
    }
}