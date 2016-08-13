package com.coding4fun.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by coding4fun on 23-Jul-16.
 */

public class HttpManager {

    public static String getData(RequestPackage p){
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        String url = p.getUrl();
        if(p.getMethod().equals("GET"))
            url += "?"+p.getEncodedParams();
        try {
            URL u = new URL(url);
            HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod(p.getMethod());

            if(p.getMethod().equals("POST")){
                con.setDoInput(true);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(p.getEncodedParams());
                writer.flush();
            }

            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while((line=reader.readLine()) != null)
                sb.append(line);
            reader.close();
        } catch (Exception e) {}
        return  sb.toString();
    }

}