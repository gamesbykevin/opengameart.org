package com.gamesbykevin.opengameart.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class Util {

    //location where we will save all our content to
    public static String DESTINATION_DIRECTORY;

    //the calendar instance
    public static final Calendar CALENDAR = Calendar.getInstance();

    public static void displayMessage(final String message) {

        if (message != null)
            System.out.println(message);
    }

    public static String getDestination(String foldername) {
        //return destination_directory + calendar.get(Calendar.YEAR) + "\\" + (calendar.get(Calendar.MONTH) + 1) + "\\" + calendar.get(Calendar.DAY_OF_MONTH) + "\\" + foldername + "\\";
        return DESTINATION_DIRECTORY + "\\" + foldername + "\\";
    }

    public static boolean download(String href, String filename, String folderName) {

        try {

            //get the destination directory
            String destination = getDestination(folderName);

            //create our reference
            File tmp = new File(destination);

            //if the directory doesn't exist, create it
            if (!tmp.exists())
                tmp.mkdirs();

            //create our url that we want to download
            URL url = new URL(href);

            int size = 1;
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("HEAD");
                conn.getInputStream();
                size = conn.getContentLength();

                if (size == 0)
                    size = -1;
            } catch (IOException e) {
                size = -1;
            } finally {
                conn.disconnect();
            }

            InputStream in = url.openStream();

            OutputStream out = new BufferedOutputStream(new FileOutputStream(destination + filename));
            final int BUFFER_SIZE = 1024 * 4;
            byte[] buffer = new byte[BUFFER_SIZE];
            BufferedInputStream bis = new BufferedInputStream(in);
            int length;

            int progress = 0;

            Calendar calendar = Calendar.getInstance();

            while ((length = bis.read(buffer)) > 0 ) {
                out.write(buffer, 0, length);
                progress += length;

                Calendar current = Calendar.getInstance();

                if (current.getTimeInMillis() - calendar.getTimeInMillis() >= 5000) {
                    calendar = Calendar.getInstance();

                    int result = (int) (((float)progress / (float)size) * 100);
                    System.out.println("Progress: " + result + "%");
                }
            }

            System.out.println("Progress: " + "100%");

            out.close();
            in.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void saveHtmlPage(String folderName, String body) {

        try {

            String fileName = "page_" + CALENDAR.get(Calendar.YEAR) + "_" + (CALENDAR.get(Calendar.MONTH) + 1) + "_" + CALENDAR.get(Calendar.DAY_OF_MONTH) + ".html";
            String destination = getDestination(folderName);

            File htmlFile = new File(destination);

            if (!htmlFile.exists())
                htmlFile.mkdirs();

            //add to existing file
            FileOutputStream out = new FileOutputStream(destination + fileName, true);
            out.write(body.getBytes());
            out.write(System.getProperty("line.separator").getBytes());
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}