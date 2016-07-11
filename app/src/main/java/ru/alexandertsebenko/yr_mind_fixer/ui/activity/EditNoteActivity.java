package ru.alexandertsebenko.yr_mind_fixer.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ru.alexandertsebenko.yr_mind_fixer.R;

public class EditNoteActivity extends Activity {

    public static final int MAX_TITLE_SIZE = 24;
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
        } else {
            editTitleText.setHint("Заголовок");
            editText.setHint("Если есть что написать - пиши!");
        }
    }
    public void onCancel(View view) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
    //Сохраняем заметку при условии что текст не пуст,заголовок не слишко большой
    //Если заголовок пуст то не возвращаем в интенте null
    public void onSave(View view) {
        boolean titleTooBig = checkTitleSizeTooBig();
        boolean textEmpty = checkTextIsEmpty();
        boolean titleEmpty = checkTitleIsEmpty();
        if(!titleTooBig && !textEmpty) {
            Intent intent = new Intent();
            if(!titleEmpty)
                intent.putExtra(AllNotesListActivity.KEY_TITLE_OF_NOTE, editTitleText.getText().toString());
            intent.putExtra(AllNotesListActivity.KEY_TEXT_OF_NOTE, editText.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        } else if(titleTooBig){
            Toast.makeText(EditNoteActivity.this,getString(R.string.toast_title_too_big) + MAX_TITLE_SIZE, Toast.LENGTH_LONG ).show();
        } else {
            Toast.makeText(EditNoteActivity.this, R.string.toast_text_is_empty, Toast.LENGTH_LONG ).show();
        }
    }
    private boolean checkTitleSizeTooBig(){
            return editTitleText.getText().length() > MAX_TITLE_SIZE;
    }
    private boolean checkTextIsEmpty(){
        return editText.getText().length() < 1;
    }
    private boolean checkTitleIsEmpty(){
        return editTitleText.getText().length() < 1;
    }
}
