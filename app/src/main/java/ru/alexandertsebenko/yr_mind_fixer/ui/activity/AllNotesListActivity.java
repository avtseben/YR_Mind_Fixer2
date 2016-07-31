package ru.alexandertsebenko.yr_mind_fixer.ui.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;

import ru.alexandertsebenko.yr_mind_fixer.service.SyncManager;
import ru.alexandertsebenko.yr_mind_fixer.ui.adapter.NoteAdapter;
import ru.alexandertsebenko.yr_mind_fixer.R;
import ru.alexandertsebenko.yr_mind_fixer.ui.fragment.RecordSoundFragment;
import ru.alexandertsebenko.yr_mind_fixer.datamodel.Note;
import ru.alexandertsebenko.yr_mind_fixer.db.NoteDataSource;
import ru.alexandertsebenko.yr_mind_fixer.util.Log_YR;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class AllNotesListActivity extends AppCompatActivity {

    public final static int REQUEST_CODE_WRITE_TEXT_NOTE = 300;
    public final static int REQUEST_CODE_TAKE_FOTO = 301;
    public final static String PUBLIC_APP_DIRECTORY = "MindFixerFiles";
    public final static String FOTO_SUB_DIRECTORY = "foto";
    public final static String AUDIO_SUB_DIRECTORY = "audio";
    public final static String APP_LOG_TAG = "app_log_tag";
    public final static String KEY_TITLE_OF_NOTE = "title";
    public final static String KEY_TEXT_OF_NOTE = "textOfNote";
    public final static String KEY_ID = "ID";
    public final static String NOTE_TYPE_TEXT = "text";
    public final static String NOTE_TYPE_AUDIO = "audio";
    public final static String NOTE_TYPE_FOTO = "foto";
    public final static String NOTE_TYPE_VIDEO = "video";
    public final static String IMAGE_FILE_FORMAT = "jpeg";
    public final static String SOUND_FILE_FORMAT = "3gpp";

    private Log_YR log = new Log_YR(getClass().toString());
    private NoteDataSource datasource;
    private NoteAdapter noteAdapter;
    private Uri uri = null;
    private RecordSoundFragment soundRecordDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_notes);

        datasource = new NoteDataSource(this);
        datasource.open();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.note_creation_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_create_text_note:
                Intent outIntentToWrite = new Intent(this, EditNoteActivity.class);
                startActivityForResult(outIntentToWrite, REQUEST_CODE_WRITE_TEXT_NOTE);
                break;
            case R.id.action_create_foto_note:
                intent = new Intent();
                if (isExternalStorageWritable()) {
                    String randomFileName = UUID.randomUUID().toString();
                    randomFileName = randomFileName + "." + IMAGE_FILE_FORMAT;
                    uri = prepareFileUri(FOTO_SUB_DIRECTORY, randomFileName);//получаем uri, оно нужно нам для ссылки из БД
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//намерение на фотокамеру
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//указываем куда сохранить
                    startActivityForResult(intent, REQUEST_CODE_TAKE_FOTO);//Запускаем фото
                } else {
                    Toast.makeText(AllNotesListActivity.this, "Внешняя память недоступна! Не куда сохранять", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_create_audio_note:
                soundRecordDialog = new RecordSoundFragment();
                soundRecordDialog.setCancelable(false);
                soundRecordDialog.show(getFragmentManager(),"RECORD");
                break;
            case R.id.action_sync:
                //Пример запуска задачи в фоновом потоке чере создание Thread
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SyncManager syncManager = new SyncManager();
                        try {
                            syncManager.makeRequest();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
    public void makeAdapter() {
        List<Note> values = datasource.getAllTextNotes();
        noteAdapter = new NoteAdapter(this, values);
        ListView listView = (ListView)findViewById(R.id.lvMain);
        listView.setAdapter(noteAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Note item = (Note) parent.getItemAtPosition(position);
                Intent intent = new Intent(AllNotesListActivity.this, NoteActivity.class);
                Bundle b = new Bundle();
                b.putLong(KEY_ID, item.getId());
                intent.putExtras(b);
                startActivity(intent);
            }});
        }
    public static Uri prepareFileUri(String album, String filename) {
        Uri uri = null;
        try {
            File path = getAppStorageDir(album);
            File fotoFile = new File(path.getCanonicalPath(), filename);
            uri = Uri.fromFile(fotoFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_TAKE_FOTO:
                    datasource.open();//дополнительно открываем доступ к базе
                    datasource.createTextNote(uri.toString(), null, NOTE_TYPE_FOTO, System.currentTimeMillis());
                    break;
                case REQUEST_CODE_WRITE_TEXT_NOTE:
                    String newTitle = data.getStringExtra(AllNotesListActivity.KEY_TITLE_OF_NOTE);
                    String newText = data.getStringExtra(AllNotesListActivity.KEY_TEXT_OF_NOTE);
                    datasource.open();
                    datasource.createTextNote(newText,newTitle,NOTE_TYPE_TEXT, System.currentTimeMillis());
                    log.v("text note created");
                    break;
            }
        }
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    public static File getAppStorageDir(String albumName) throws IOException {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                PUBLIC_APP_DIRECTORY), albumName);
        if (!file.mkdirs()) {
            Log.v(APP_LOG_TAG, "Directory not created, maybe already exist");
        }
        return file;
    }
    protected void onResume() {
        datasource.open();
        makeAdapter();//формируем list
        super.onResume();
    }
    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (uri != null)
            outState.putString("uri", uri.toString());
    }
    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if (state.getString("uri") != null)
            uri = Uri.parse(state.getString("uri"));
    }
}
