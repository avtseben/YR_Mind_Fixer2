package ru.alexandertsebenko.yr_mind_fixer.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import ru.alexandertsebenko.yr_mind_fixer.R;

public class EditNoteActivity extends Activity {

    EditText editTitleText;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        editTitleText = (EditText) findViewById(R.id.edit_text_title);
        editText = (EditText) findViewById(R.id.edit_text_note);
        Intent inIntent = getIntent();
        //Если в интенте есть текст - значит редактирование, если нет - значит новая запись
        if(inIntent.getStringExtra(AllNotesListActivity.KEY_TEXT_OF_NOTE) != null) {
            editTitleText.setText(
                    getIntent().getStringExtra(AllNotesListActivity.KEY_TITLE_OF_NOTE)
            );
            editText.setText(
                    getIntent().getStringExtra(AllNotesListActivity.KEY_TEXT_OF_NOTE)
            );
        }
    }
    public void onCancel(View view) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
    public void onSave(View view) {
        Intent intent = new Intent();
        intent.putExtra(AllNotesListActivity.KEY_TITLE_OF_NOTE, editTitleText.getText().toString());
        intent.putExtra(AllNotesListActivity.KEY_TEXT_OF_NOTE, editText.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
