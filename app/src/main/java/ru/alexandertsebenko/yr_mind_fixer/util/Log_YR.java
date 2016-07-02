package ru.alexandertsebenko.yr_mind_fixer.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Log_YR {

    private String TAG;

    public Log_YR(String TAG) {
        this.TAG = TAG;
    }
    public void v(String message) {
        Log.v(TAG, message);
    }
    public void d(String message) {
        Log.d(TAG, message);
    }
    public void i(String message) {
        Log.i(TAG, message);
    }
    public void w(String message) {
        Log.w(TAG, message);
    }
    public void e(String message) {
        Log.e(TAG, message);
    }
}
