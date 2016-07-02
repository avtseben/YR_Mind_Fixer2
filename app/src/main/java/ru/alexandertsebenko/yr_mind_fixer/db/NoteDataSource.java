package ru.alexandertsebenko.yr_mind_fixer.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.alexandertsebenko.yr_mind_fixer.datamodel.Note;
import ru.alexandertsebenko.yr_mind_fixer.util.Log_YR;

public class NoteDataSource {
    // Database fields
    private Log_YR log = new Log_YR(getClass().toString());
    private SQLiteDatabase database;//обращение к глобальной SQLite БД на Android
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,//массив в которов все имена колонок таблицы
            MySQLiteHelper.COLUMN_TEXT_NOTE, MySQLiteHelper.COLUMN_NOTE_TITLE,MySQLiteHelper.COLUMN_NOTE_TYPE, MySQLiteHelper.COLUMN_TEXT_NOTE_CREATE_DATE };
    public NoteDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        log.v("DataSource opened");
    }

    public void close() {
        dbHelper.close();
        log.v("DataSource closed");
    }
    public SQLiteDatabase getDB() {
        return database;
    }
    public Note createTextNote(String textNote, String textNoteTitle, String textNoteType, long createDate) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TEXT_NOTE, textNote);
        values.put(MySQLiteHelper.COLUMN_NOTE_TITLE, textNoteTitle);
        values.put(MySQLiteHelper.COLUMN_NOTE_TYPE, textNoteType);
        values.put(MySQLiteHelper.COLUMN_TEXT_NOTE_CREATE_DATE, createDate);
        long insertId = database.insert(MySQLiteHelper.TABLE_TEXT_NOTES, null,//Вставляем в таблицу
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TEXT_NOTES,//имя таблицы в Srting
                allColumns,/*какие колонки выбирать аналог select * from*/ MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Note newTextNote = cursorToTextNote(cursor);
        cursor.close();
        return newTextNote;
    }

    public void deleteTextNote(Note textNote) {
        long id = textNote.getId();
        log.v("TextNote deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_TEXT_NOTES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }
    public void deleteTextNoteByText(String textNote) {
        log.v("TextNote deleted with text: " + textNote);
        database.delete(MySQLiteHelper.TABLE_TEXT_NOTES, MySQLiteHelper.COLUMN_TEXT_NOTE
                + " = " + textNote, null);
    }
    public void deleteTextNoteByID(long _id) {
        log.v("TextNote deleted with id: " + _id);
        database.delete(MySQLiteHelper.TABLE_TEXT_NOTES, MySQLiteHelper.COLUMN_ID
                + " = " + _id, null);
    }
    public void updateTextNoteById(long _id, String newText) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(MySQLiteHelper.COLUMN_TEXT_NOTE, newText);
        log.v("TextNote updated with id: " + _id);
        database.update(MySQLiteHelper.TABLE_TEXT_NOTES, updateValues , MySQLiteHelper.COLUMN_ID
                + " = " + _id, null);
    }
    public void updateTitleByID(long _id, String newText) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(MySQLiteHelper.COLUMN_NOTE_TITLE, newText);
        log.v("TextNote updated with id: " + _id);
        database.update(MySQLiteHelper.TABLE_TEXT_NOTES, updateValues , MySQLiteHelper.COLUMN_ID
                + " = " + _id, null);
    }
    public List<Note> getAllTextNotes() {
        log.v("getAllTextNotes");
        List<Note> textNotes = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_TEXT_NOTES,
                allColumns, null, null, null, null, "_id" + " DESC");//Выборка всех колонок в обратном порядке TODO: убрать из hardcode

        cursor.moveToFirst();//cursor это очень похоже на result set
        while (!cursor.isAfterLast()) {
            Note textNote = cursorToTextNote(cursor);
            textNotes.add(textNote);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return textNotes;
    }
    public String getNoteTypeByID(long id) {
        String [] columns = {MySQLiteHelper.COLUMN_NOTE_TYPE};
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TEXT_NOTES,//имя таблицы в Srting
                columns, MySQLiteHelper.COLUMN_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        String s = cursor.getString(0);
        cursor.close();
        return s;
    }
    public String getNoteTextByID(long id) {
        String [] columns = {MySQLiteHelper.COLUMN_TEXT_NOTE};
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TEXT_NOTES,//имя таблицы в Srting
                columns, MySQLiteHelper.COLUMN_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        String s = cursor.getString(0);
        cursor.close();
        return s;
    }
    public String getTitleByID(long id) {
        String [] columns = {MySQLiteHelper.COLUMN_NOTE_TITLE};
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TEXT_NOTES,//имя таблицы в Srting
                columns, MySQLiteHelper.COLUMN_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        String s = cursor.getString(0);
        cursor.close();
        return s;
    }
    public long getCreationDateByID(long id) {
        String [] columns = {MySQLiteHelper.COLUMN_TEXT_NOTE_CREATE_DATE};
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TEXT_NOTES,//имя таблицы в Srting
                columns, MySQLiteHelper.COLUMN_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        long l = cursor.getLong(0);
        cursor.close();
        return l;
    }
    private Note cursorToTextNote(Cursor cursor) {
        Note textNote = new Note();
        textNote.setId(cursor.getLong(0));
        textNote.setTextNote(cursor.getString(1));
        textNote.setNoteTitle(cursor.getString(2));
        textNote.setNoteType(cursor.getString(3));
        textNote.setCreationDate(cursor.getLong(4));
        return textNote;
    }
}
