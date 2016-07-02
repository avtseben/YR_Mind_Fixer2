package ru.alexandertsebenko.yr_mind_fixer.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ru.alexandertsebenko.yr_mind_fixer.R;
import ru.alexandertsebenko.yr_mind_fixer.db.NoteDataSource;

public class WriteNoteActivity extends Activity {

    private NoteDataSource datasource;
    EditText edtext;
    EditText edtextTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_note);

        datasource = new NoteDataSource(this);
        datasource.open();

    }
    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        Intent intentBack = new Intent(this,AllNotesListActivity.class);
        switch (view.getId()) {
            case R.id.add:
                edtext = (EditText) findViewById(R.id.editText);
                edtextTitle = (EditText) findViewById(R.id.edit_text_title);
                String note = edtext.getText().toString();
                String noteTitle = edtextTitle.getText().toString();
                long currentUnixTime = System.currentTimeMillis();
                if (note.length() != 0) {
                    Toast.makeText(this, R.string.toast_note_write_ok,Toast.LENGTH_SHORT).show();
                    if(noteTitle.length() != 0) {
                        datasource.createTextNote(note, noteTitle, AllNotesListActivity.NOTE_TYPE_TEXT, currentUnixTime);//Заносим в БД
                    } else
                        datasource.createTextNote(note, null, AllNotesListActivity.NOTE_TYPE_TEXT, currentUnixTime);//Заносим в БД
                    onBackPressed();
                } else {
                    Toast.makeText(this, R.string.toast_note_is_null,Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

