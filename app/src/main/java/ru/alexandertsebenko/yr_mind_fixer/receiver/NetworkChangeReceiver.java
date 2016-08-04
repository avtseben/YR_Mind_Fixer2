package ru.alexandertsebenko.yr_mind_fixer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import ru.alexandertsebenko.yr_mind_fixer.service.SyncHandler;

public class NetworkChangeReceiver extends BroadcastReceiver {

    boolean mIsConnected;
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        mIsConnected = (netInfo != null && netInfo.isConnected());
        if(mIsConnected) {
            Toast.makeText(context, getClass().getSimpleName() + ": Connected", Toast.LENGTH_SHORT).show();
            Intent serviceIntent = new Intent(context, SyncHandler.class);
            context.startService(serviceIntent);
        } else {
            Toast.makeText(context, getClass().getSimpleName() + ": Disconnected", Toast.LENGTH_SHORT).show();
        }
    }
}
