package ru.alexandertsebenko.yr_mind_fixer.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.alexandertsebenko.yr_mind_fixer.R;
import ru.alexandertsebenko.yr_mind_fixer.datamodel.Note;
import ru.alexandertsebenko.yr_mind_fixer.db.NoteDataSource;
import ru.alexandertsebenko.yr_mind_fixer.net.Client;
import ru.alexandertsebenko.yr_mind_fixer.receiver.NetworkChangeReceiver;
import ru.alexandertsebenko.yr_mind_fixer.ui.activity.AllNotesListActivity;
import ru.alexandertsebenko.yr_mind_fixer.util.Log_YR;

public class SyncHandler extends IntentService {

    private List<Note> mNotes;
    public static final int NOTIFICATION_ID = 5453;
    private Log_YR log = new Log_YR(getClass().getSimpleName());

    public SyncHandler() {
        super("SuncHandler");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(isSyncNeeded()) {
            boolean postSuccess = false;
            Client client = new Client(this);
            try {
                postSuccess = client.post(mNotes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            synchronized (this) {
                try {
                    wait(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            if (postSuccess) {
            if (true) {//TODO научиться запускать только при успешной передаче (postSuccess)
                markNotesAsSynced(mNotes);
                showText("Заметки успешно синхронизированы");
            }
        }
    }

    private void markNotesAsSynced(List<Note> notes) {
        NoteDataSource datasource = new NoteDataSource(this);
        log.d("Mark As Sync: " + mNotes.toString());
        datasource.open();
        for(Note note : notes) {
            datasource.markNoteAsSynced(note.getId());
        }
        datasource.close();
    }

    private void showText(final String text) {
        Intent intent = new Intent(this, AllNotesListActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(AllNotesListActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT
                );
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notebook)
                .setContentTitle(getString(R.string.app_name))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .setContentText(text)
                .build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
    private boolean isSyncNeeded(){
        NoteDataSource datasource = new NoteDataSource(this);
        datasource.open();
        mNotes = datasource.getUnsyncedNotes();
        datasource.close();
        log.d("Note need to sync: " + mNotes.size());
        return (mNotes.size() > 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log.d("onDestroy");
    }
}
