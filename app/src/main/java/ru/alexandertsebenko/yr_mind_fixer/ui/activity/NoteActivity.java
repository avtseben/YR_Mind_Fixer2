package ru.alexandertsebenko.yr_mind_fixer.ui.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import ru.alexandertsebenko.yr_mind_fixer.R;
import ru.alexandertsebenko.yr_mind_fixer.db.NoteDataSource;
import ru.alexandertsebenko.yr_mind_fixer.ui.fragment.ImageFragment;
import ru.alexandertsebenko.yr_mind_fixer.ui.fragment.PlaySoundFragment;
import ru.alexandertsebenko.yr_mind_fixer.ui.fragment.TextNoteFragment;
import ru.alexandertsebenko.yr_mind_fixer.util.DateBuilder;
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
    private DateBuilder mDateSubTitleBuilder = new DateBuilder(this);
    private boolean mEditTitleMode;
    private ViewSwitcher mEditTitleSwitcher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null ) mEditTitleMode = savedInstanceState.getBoolean("EDIT_TITLE_STATE");
        setContentView(R.layout.activity_note);
        b = getIntent().getExtras();
        tnoteID = b.getLong(AllNotesListActivity.KEY_ID);
        datasource = new NoteDataSource(this);
        datasource.open();
        noteType = datasource.getNoteTypeByID(tnoteID);
        tnote = datasource.getNoteTextByID(tnoteID);
        setNoteTitle();
        if(mEditTitleMode) setEditButtonAsSaveButton();
        //Fragment works
        setFragment(noteType, tnote);
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
        //Set Title.Если не в режиме редактирования заголовка тогда
        if(!mEditTitleMode) {
            mTitleTextView = (TextView) findViewById(R.id.note_title_in_note_activity);
            mNoteTitle = datasource.getTitleByID(tnoteID);
            if(mNoteTitle == null) {
                switch (noteType) {
                    case AllNotesListActivity.NOTE_TYPE_FOTO:
                        mTitleTextView.setText(R.string.default_title_in_foto_note);
                        break;
                    case AllNotesListActivity.NOTE_TYPE_AUDIO:
                        mTitleTextView.setText(R.string.default_title_in_sound_note);
                        break;
                }
            } else {
                mTitleTextView.setText(mNoteTitle);
            }
        } else {
            mEditTitleSwitcher = (ViewSwitcher) findViewById(R.id.title_switcher);
            mEditTitleSwitcher.showNext();
        }
        //Set Date subtitle
        mDateSubtitle= (TextView)findViewById(R.id.date_subtitle);
        long noteCreationTime = datasource.getCreationDateByID(tnoteID);
        mDateSubtitle.setText(mDateSubTitleBuilder
                .timeTitleBuilder(noteCreationTime));
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
                //Фиксируем ланшафтный вид
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                log.v("1.setFragment ImageFragment");
                uri = tnote;//Если заметка фотографическая то в качестве текста франиться uri ссылка на файл фотографии
                ImageFragment imageFragment = new ImageFragment();
                transaction.replace(R.id.fragment_container_in_note_activity, imageFragment);
                transaction.commit();
                log.v("2.setFragment Transaction commited");
                imageFragment.setFileByStringUri(uri);
                log.v("3.setFragment setFileByStringUri called");
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
                log.v("mEditTitleMode in onClick(): " + mEditTitleMode);
                if(mEditTitleMode) saveTitle();
                else {
                    if(noteType.equals(AllNotesListActivity.NOTE_TYPE_TEXT)) {
                        Intent intentToEdit = new Intent(this, EditNoteActivity.class);
                        intentToEdit.putExtra(AllNotesListActivity.KEY_TEXT_OF_NOTE, tnote);
                        intentToEdit.putExtra(AllNotesListActivity.KEY_TITLE_OF_NOTE, mNoteTitle);
                        startActivityForResult(intentToEdit, REQUEST_CODE_ACTIVITY_EDIT_TEXT);
                    }
                    //Если заметка не текстовая то редакитровать можно только заголовок.
                    else {
                        titleEdit(findViewById(R.id.note_title_in_note_activity));
                    }
                }
                break;
        }
    }
    private void setEditButtonAsSaveButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_edit_in_note_activity);
        fab.setImageResource(R.drawable.check);
    }
    private void setEditButtonToNormallState() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_edit_in_note_activity);
        fab.setImageResource(R.drawable.pencil);
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
    public void createDialog(View view) {
//        final Intent intentBack = new Intent(this, AllNotesListActivity.class);
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
            file.delete();
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    /**
     * При повороте экрана важно запомнить ID заметки
     * и статус режима редактирования заголовка(TextView или EditTextView)
     * @param outState
     */
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Сохранем ссылку на заметку, только ID
        if (tnoteID > 0) outState.putLong(NOTE_ID_KEY, tnoteID);
        log.v("mEditTitleMode: " + mEditTitleMode);
        outState.putBoolean("EDIT_TITLE_STATE",mEditTitleMode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if (state.getLong(NOTE_ID_KEY) > 0) tnoteID = state.getLong(NOTE_ID_KEY);
        log.v("restored mEditTitleMode: " + mEditTitleMode);
    }

    /**
     * Вызывается принажатии на залоговок
     * Или (если этоне текстовая заметка)
     * при нажатии на кнопку редактирования
     *
     * @param view
     */
    public void titleEdit(View view) {
        mEditTitleMode = true;
        mEditTitleSwitcher = (ViewSwitcher) findViewById(R.id.title_switcher);
        TextView title = (TextView)findViewById(R.id.note_title_in_note_activity);
        EditText editTitle = (EditText) findViewById(R.id.hidden_edit_note_title_in_note_activity);
        editTitle.setText(title.getText());
        mEditTitleSwitcher.showNext(); //or switcher.showPrevious();
        setEditButtonAsSaveButton();
    }

    /**
     * При нажатии на кнопку редактирования, которая в данном режиме
     * является кнопкой "сохранить"
     */
    public void saveTitle() {
        mEditTitleMode = false;
        mEditTitleSwitcher = (ViewSwitcher) findViewById(R.id.title_switcher);
        TextView title = (TextView)findViewById(R.id.note_title_in_note_activity);
        EditText editTitle = (EditText) findViewById(R.id.hidden_edit_note_title_in_note_activity);
        String newText = editTitle.getText().toString();
        title.setText(newText);
        setEditButtonToNormallState();
        datasource.updateTitleByID(tnoteID, newText);
        mEditTitleSwitcher.showPrevious(); //or switcher.showPrevious();
    }
}
