package com.gamesbykevin.opengameart;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import com.gamesbykevin.opengameart.crawler.Crawler;

import static com.gamesbykevin.opengameart.crawler.Crawler.DOMAIN_URL;
import static com.gamesbykevin.opengameart.crawler.History.LOCATION_FILE;
import static com.gamesbykevin.opengameart.util.Util.DESTINATION_DIRECTORY;
import static com.gamesbykevin.opengameart.util.Util.displayMessage;

public class Main {

    //our web crawler
    private Crawler crawler;

    public static void main(String[] args) throws Exception
    {
        //create new instance
        new Main();
    }

    public Main() throws Exception {

        //load property file first
        readConfig();

        //create our web crawler
        this.crawler = new Crawler();

        //start crawling
        this.crawler.crawl();
    }

    private void readConfig() throws Exception {

        displayMessage("Loading property file");

		Properties prop = new Properties();
		String propFileName = "./config.properties";

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}

		LOCATION_FILE = prop.getProperty("history");
		DOMAIN_URL = prop.getProperty("domain");
        DESTINATION_DIRECTORY = prop.getProperty("destination");

        displayMessage("Properties loaded");
        displayMessage(LOCATION_FILE);
        displayMessage(DOMAIN_URL);
        displayMessage(DESTINATION_DIRECTORY);
    }
}