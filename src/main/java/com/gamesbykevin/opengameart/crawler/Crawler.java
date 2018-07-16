package com.gamesbykevin.opengameart.crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Date;

import static com.gamesbykevin.opengameart.crawler.CrawlerHelper.getCategoryName;
import static com.gamesbykevin.opengameart.crawler.CrawlerHelper.getFormattedUrl;
import static com.gamesbykevin.opengameart.util.Util.*;

public class Crawler {

    //keep track of our history
    private History history;

    /**
     * Where do we start crawling?
     */
    public static String DOMAIN_URL;

    public Crawler() throws Exception {
        this.history = new History();
        this.history.load();
    }

    public History getHistory() {
        return history;
    }

    public void crawl() {

        try {

            //add the domain url as something we have yet to discover
            getHistory().getDiscovered().add(DOMAIN_URL);

            //object to reference pages
            Document doc;

            //continue as long as we have pages that are not yet discovered
            while (!getHistory().getDiscovered().isEmpty()) {

                //get the first url
                String tmpUrl = getHistory().getDiscovered().get(0);

                //now remove it as we are going to discover it
                getHistory().getDiscovered().remove(0);

                //print our current progress
                System.out.println("Left: " + getHistory().getDiscovered().size() + ", Checking: " + tmpUrl);

                try {

                    doc = Jsoup.connect(tmpUrl).get();

                    //add to the current list of checked pages
                    getHistory().getChecked().add(tmpUrl);

                    //get all links on the page
                    Elements links = doc.select("a[href]");

                    //check every link
                    for (Element link : links) {

                        //get the url from the link
                        String url = link.attr("abs:href");

                        if (url == null || url.trim().length() < 1)
                            continue;
                        if (url.contains("#"))
                            continue;

                        if (url.contains("/content/")) {

                            //make sure we didn't already download it
                            if (!hasContent(url)) {

                                System.out.println("Checking: " + url);

                                //scan and download any content
                                boolean result = scanContentPage(url);

                                //if successful save page to history
                                if (result)
                                    getHistory().save(url);
                            }

                        } else {
                            discover(url);
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //mark the time we finished
        displayMessage("Done " + new Date().toString());
    }

    private void discover(String url) {

        if (url == null || url.trim().length() < 10)
            return;

        //we don't care about urls that are on different websites
        if (!url.trim().toLowerCase().contains("opengameart.org"))
            return;

        if (hasContent(url))
            return;

        if (url.contains("latest") ||
                url.contains("popular") ||
                url.contains("art-search-advanced?keys=") ||
                url.contains("collection") ||
                url.contains("pixel") ||
                url.contains("user") ||
                url.contains("art")) {

            boolean found = false;

            //check if we already have this link
            for (String tmp : getHistory().getChecked()) {

                String str1 = getFormattedUrl(tmp);
                String str2 = getFormattedUrl(url);

                if (str1.equalsIgnoreCase(str2)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                for (String tmp : getHistory().getDiscovered()) {

                    String str1 = getFormattedUrl(tmp);
                    String str2 = getFormattedUrl(url);

                    if (str1.equalsIgnoreCase(str2)) {
                        found = true;
                        break;
                    }
                }
            }

            //if it wasn't found, we can continue
            if (!found)
                getHistory().getDiscovered().add(url);
        }
    }

    private boolean hasContent(String url) {

        try {

            for (String tmp : getHistory().getUrls()) {
                if (getFormattedUrl(url).equalsIgnoreCase(getFormattedUrl(tmp)))
                    return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean scanContentPage(String href) {

        if (href == null || href.trim().length() < 1)
            return false;

        System.out.println("Scanning: " + href);

        boolean success = true;

        boolean dirty = false;

        try {

            //make sure no / on the end
            href = getFormattedUrl(href);

            //load the page
            Document doc = Jsoup.connect(href).get();

            String[] tmp1 = href.split("/");

            //last element will be the folder name
            String folderName = tmp1[tmp1.length - 1];

            //make sure no other text with illegal characters
            int index = folderName.indexOf("?");

            //remove illegal character on end (if exists)
            if (index != -1)
                folderName = folderName.substring(0, index);

            //get all links on the page
            Elements links = doc.select("a[href]");

            String categoryName = null;

            //check every link
            for (Element link : links) {

                //get the url from the link
                String url = link.attr("abs:href");

                //if this is a download link, download the file
                if (url.toLowerCase().trim().contains("/files/") && url.toLowerCase().trim().contains("opengameart.org")) {

                    //add the category name into the folder
                    if (categoryName == null) {

                        categoryName = getCategoryName(doc);

                        folderName = categoryName + "\\" + folderName;
                    }

                    //split into array
                    String[] tmp2 = url.split("/");

                    //the end of the array will be the file name
                    String fileName = tmp2[tmp2.length - 1];

                    System.out.println("Location: " + getDestination(folderName));
                    System.out.println("Downloading: " + url);

                    if (!download(url, fileName, folderName)) {
                        success = false;
                    } else {
                        dirty = true;
                    }
                } else {
                    discover(url);
                }
            }

            if (dirty) {

                //get the response html from the page as well
                Connection.Response html = Jsoup.connect(href).execute();

                //write html file to hard drive
                saveHtmlPage(folderName, html.body());
            }

        } catch (Exception e) {

            e.printStackTrace();

            success = false;
        }

        return success;
    }
}