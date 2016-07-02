package ru.alexandertsebenko.yr_mind_fixer.ui.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import ru.alexandertsebenko.yr_mind_fixer.R;
import ru.alexandertsebenko.yr_mind_fixer.db.NoteDataSource;
import ru.alexandertsebenko.yr_mind_fixer.ui.fragment.ImageFragment;
import ru.alexandertsebenko.yr_mind_fixer.ui.fragment.PlaySoundFragment;
import ru.alexandertsebenko.yr_mind_fixer.ui.fragment.TextNoteFragment;
import ru.alexandertsebenko.yr_mind_fixer.util.Log_YR;


public class NoteActivity extends AppCompatActivity {

    private NoteDataSource datasource;

    private Bundle b;
    private String tnote = null;
    private TextView mTitleTextView;
    private TextView mDateSubtitle;
    private String uri = null;
    private String noteType = null;
    private long tnoteID;
    private String mNoteTitle;
    private final int REQUEST_CODE_ACTIVITY_EDIT_TEXT = 300;
    private final String NOTE_ID_KEY = "NOTE_ID";
    private final String TEXT_FRAGMENT_TAG = "textFragmentTag";
    private Log_YR log = new Log_YR(getClass().toString());
    private final long MINUTE_IN_SECS = 60;
    private final long HOUR_IN_SECS = 3600;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.v("NoteActivity created");
        setContentView(R.layout.activity_note);
        b = getIntent().getExtras();
        tnoteID = b.getLong(AllNotesListActivity.KEY_ID);
        datasource = new NoteDataSource(this);
        datasource.open();
        noteType = datasource.getNoteTypeByID(tnoteID);
        tnote = datasource.getNoteTextByID(tnoteID);
        setNoteTitle();
        //Fragment works
        setFragment(noteType, tnote);
        //Если заметка не текстовая то редакитровать нечего. Прячем кнопку
        if(!noteType.equals(AllNotesListActivity.NOTE_TYPE_TEXT)) {
            findViewById(R.id.button_edit_in_note_activity).setVisibility(View.INVISIBLE);
        }

    }
    private void setNoteTitle() {
        //Set Icon. Text type note is default
        ImageView icon = (ImageView)findViewById(R.id.icon_in_note_activity);
        switch(noteType) {
            case(AllNotesListActivity.NOTE_TYPE_FOTO):
                icon.setImageResource(R.drawable.camera);
                break;
            case(AllNotesListActivity.NOTE_TYPE_AUDIO):
                icon.setImageResource(R.drawable.microphone);
                break;
        }
        //Set Title
        mTitleTextView= (TextView)findViewById(R.id.note_title_in_note_activity);
        mNoteTitle = datasource.getTitleByID(tnoteID);
        if(mNoteTitle == null) {
            mTitleTextView.setText(R.string.default_note_title);
        } else {
            mTitleTextView.setText(mNoteTitle);
        }
        //Set Date subtitle
        mDateSubtitle= (TextView)findViewById(R.id.date_subtitle);
        long noteCreationTime = datasource.getCreationDateByID(tnoteID);
        mDateSubtitle.setText(timeTitleBuilder(noteCreationTime));
    }
    private String timeTitleBuilder(long noteCreationTime) {
        long currentTime = System.currentTimeMillis();
        if(currentTime >= noteCreationTime) {
            long timeDeltaInSecs = Math.round((currentTime - noteCreationTime) / 1000);
            if (timeDeltaInSecs >= HOUR_IN_SECS * 24) {
                Date simpleDate = new Date(noteCreationTime);
                String dateFormat = DateFormat.getInstance().format(simpleDate);
                return dateFormat;
            } else if (timeDeltaInSecs >= HOUR_IN_SECS) {
                return Math.round(timeDeltaInSecs / HOUR_IN_SECS) + getString(R.string.hour_ago);
            } else if (timeDeltaInSecs >= MINUTE_IN_SECS) {
                return Math.round(timeDeltaInSecs / MINUTE_IN_SECS) + getString(R.string.minutes_ago);
            }
            return timeDeltaInSecs + getString(R.string.seconds_ago);
        }
        return getString(R.string.note_made_in_future_humor);
    }

    public void setFragment(String noteType, String noteText) {
        log.v("setFragment called");
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        switch (noteType) {
            case AllNotesListActivity.NOTE_TYPE_TEXT:
                log.v("setFragment TEXT TYPE");
                TextNoteFragment tf = new TextNoteFragment();
                transaction.add(R.id.fragment_container_in_note_activity, tf, TEXT_FRAGMENT_TAG);
                transaction.commit();
                tf.setTextOfNote(tnote);//Отдаём текст заметки фрагменту
                break;
            case AllNotesListActivity.NOTE_TYPE_FOTO://TODO разобраться почему при смене ориентации выскакивает NullPointException в ImageFragment.onStart
                uri = tnote;//Если заметка фотографическая то в качестве текста франиться uri ссылка на файл фотографии
                ImageFragment imageFragment = new ImageFragment();
                imageFragment.setFileByStringUri(uri);
                transaction.add(R.id.fragment_container_in_note_activity, imageFragment);
                transaction.commit();
                break;
            case AllNotesListActivity.NOTE_TYPE_AUDIO:
                log.v("set fragment audio");
                uri = tnote;
                PlaySoundFragment psf = new PlaySoundFragment();
                transaction.add(R.id.fragment_container_in_note_activity, psf);
                transaction.commit();
                psf.setMediaPlayerSource(uri);
                break;
            case AllNotesListActivity.NOTE_TYPE_VIDEO:
                break;

        }
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_delete_in_note_activity:
                createDialog(view);
                break;
            case R.id.button_later_in_note_activity:
                onBackPressed();
                break;
            case R.id.button_edit_in_note_activity:
                Intent intentToEdit = new Intent(this, EditNoteActivity.class);
                intentToEdit.putExtra(AllNotesListActivity.KEY_TEXT_OF_NOTE, tnote);
                intentToEdit.putExtra(AllNotesListActivity.KEY_TITLE_OF_NOTE, mNoteTitle);
                startActivityForResult(intentToEdit, REQUEST_CODE_ACTIVITY_EDIT_TEXT);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ACTIVITY_EDIT_TEXT:
                    String newTitle = data.getStringExtra(AllNotesListActivity.KEY_TITLE_OF_NOTE);
                    String newText = data.getStringExtra(AllNotesListActivity.KEY_TEXT_OF_NOTE);
                    log.v("Edit text Result");
                    //Update in storage
                    datasource.open();
                    mTitleTextView.setText(newTitle);
                    datasource.updateTextNoteById(tnoteID, newText);
                    datasource.updateTitleByID(tnoteID, newTitle);
                    //Refresh current text to see it
                    TextNoteFragment tf = (TextNoteFragment) getSupportFragmentManager().findFragmentByTag(TEXT_FRAGMENT_TAG);
                    tf.setTextOfNote(newText);
                    break;
            }
        }

    }
   @Override
    protected void onStart() {
        log.v("NoteActivity started");
        super.onStart();
    }
    protected void onResume() {
        log.v("NoteActivity resumed");
        super.onResume();
    }
    @Override
    protected void onPause() {
        datasource.close();
        log.v("NoteActivity paused");
        super.onPause();
    }
    @Override
    protected void onStop() {
        log.v("NoteActivity stoped");
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        log.v("NoteActivity destroyed");
        super.onDestroy();
    }
    public void createDialog(View view) {
        final Intent intentBack = new Intent(this, AllNotesListActivity.class);
        new AlertDialog.Builder(view.getContext())
                .setTitle(getString(R.string.dialog_alert))
                .setMessage(getString(R.string.dialog_mess_ask_for_delete))
                .setPositiveButton(getString(R.string.text_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        log.v("Delete note clicked");
                        if(noteType.equals(AllNotesListActivity.NOTE_TYPE_AUDIO) || noteType.equals(AllNotesListActivity.NOTE_TYPE_FOTO))
                            deleteFileByURI(Uri.parse(tnote));
                        datasource.deleteTextNoteByID(tnoteID);
                        Toast.makeText(NoteActivity.this, R.string.toast_delete, Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                })
                .setNegativeButton(getString(R.string.text_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(NoteActivity.this, R.string.toast_dont_delete, Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
    public static void deleteFileByURI(Uri uri){
        File file = new File(uri.getPath());
        try {
            boolean fileDeleted = file.delete();
        } catch (Exception e) {
            System.out.print(e);
        }
    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Сохранем ссылку на заметку, только ID
        if (tnoteID > 0)
            outState.putLong(NOTE_ID_KEY, tnoteID);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if (state.getLong(NOTE_ID_KEY) > 0) {
            tnoteID = state.getLong(NOTE_ID_KEY);
            Toast.makeText(NoteActivity.this, "note id " + tnoteID +" restored",Toast.LENGTH_SHORT).show();
        }
    }
}
