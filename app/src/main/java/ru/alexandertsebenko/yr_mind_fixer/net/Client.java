package ru.alexandertsebenko.yr_mind_fixer.net;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.Header;
import ru.alexandertsebenko.yr_mind_fixer.datamodel.Note;
import ru.alexandertsebenko.yr_mind_fixer.util.Log_YR;

public class Client {

    private boolean successTransfer = false;
    private static AsyncHttpClient client = new AsyncHttpClient();
    private Log_YR log = new Log_YR(getClass().getSimpleName());
    private static final String URL = "http://yrsoft.cu.cc:3004/notes";
    private Context context;

    public Client(Context context){
        this.context = context;
    }
    public boolean post(List<Note> noteList) {
        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                log.i("Notes Sync Success!!!");//TODO почему это не выполняется даже при onSuccess
                successTransfer = true;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                log.e("Notes Sync Failure");
            }
        };
        String jsonString = makeJsonFromListOfNotes(noteList);
        StringEntity se = null;
        try {
            se = new StringEntity(jsonString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(context, URL, se, "application/json",
                responseHandler);
        log.d("Notes Sync: " + successTransfer);
        return successTransfer;
    }
    private String makeJsonFromListOfNotes(List<Note> noteList) {
        StringBuilder sb = new StringBuilder();
        Gson gson = new GsonBuilder().create();
        sb.append("[");
        for(Note note : noteList){
            sb.append(gson.toJson(note));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        log.d(sb.toString());
        return sb.toString();
    }
}
