package com.gamesbykevin.opengameart.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class History {

    /**
     * Where is our history file located?
     */
    public static String LOCATION_FILE;

    //list of pages we already checked
    private List<String> checked;

    //list of pages we have yet to discover
    private List<String> discovered;

    //list of urls already visited
    private List<String> urls;

    public History() {

        //create new lists
        this.checked = new ArrayList<>();
        this.discovered = new ArrayList<>();
        this.urls = new ArrayList<>();
    }

    public void load() throws Exception {

        //our history file reference
        File historyFile = new File(LOCATION_FILE);

        //create the file if it doesn't exist
        if (!historyFile.exists())
            historyFile.createNewFile();

        //create our file reader
        FileReader fileReader = new FileReader(LOCATION_FILE);

        //wrap FileReader in BufferedReader.
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;

        //read the contents of the file
        while((line = bufferedReader.readLine()) != null) {
            if (line != null && line.trim().length() > 1)
                getUrls().add(line);
        }

        //close now that we are done
        bufferedReader.close();
    }

    public List<String> getChecked() {
        return checked;
    }

    public List<String> getDiscovered() {
        return discovered;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void save(String url) throws Exception {

        //add to list
        getUrls().add(url);

        //add to existing file
        FileOutputStream out = new FileOutputStream(LOCATION_FILE, true);
        out.write(url.getBytes());
        out.write(System.getProperty("line.separator").getBytes());
        out.flush();
        out.close();
    }
}