package com.coding4fun.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by coding4fun on 23-Jul-16.
 */

public class RequestPackage {

    private String url,method;
    private Map<String,String> params;


    public RequestPackage() {
        this.method = "GET";
        this.params = new HashMap<>();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod_GET() {
        this.method = "GET";
    }

    public void setMethod_POST() {
        this.method = "POST";
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void addParam(String key,String value){
        params.put(key,value);
    }

    public String getEncodedParams(){
        StringBuilder sb = new StringBuilder();
        for(String key : params.keySet()){
            String encodedValue = null;
            try {
                encodedValue = URLEncoder.encode(params.get(key),"UTF-8");
            } catch (UnsupportedEncodingException e) {}
            if(sb.length() > 0)
                sb.append("&");
            sb.append(key + "=" + encodedValue);
        }
        return sb.toString();
    }

}