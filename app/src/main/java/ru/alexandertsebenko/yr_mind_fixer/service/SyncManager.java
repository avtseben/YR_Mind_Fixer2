package ru.alexandertsebenko.yr_mind_fixer.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class SyncManager {

    public HttpResponse makeRequest() throws Exception {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://yrsoft.cu.cc:3004/notes");
        String jsonString = "{\"text_note\":\"curli\",\"note_title\":\"curlTest\",\"type\":\"text\",\"create_date\":2342342}";
        httpPost.setEntity(new StringEntity(jsonString));
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        return (HttpResponse)httpClient.execute(httpPost);
    }

}
