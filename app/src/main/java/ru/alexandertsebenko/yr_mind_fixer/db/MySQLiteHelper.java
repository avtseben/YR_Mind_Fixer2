package ru.alexandertsebenko.yr_mind_fixer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_TEXT_NOTES = "text_notes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEXT_NOTE = "text_note";
    public static final String COLUMN_NOTE_TITLE = "note_title";
    public static final String COLUMN_TEXT_NOTE_CREATE_DATE = "create_date";
    public static final String COLUMN_NOTE_TYPE = "type";

    private static final String DATABASE_NAME = "yr.db";
    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_TEXT_NOTES + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TEXT_NOTE
            + " text not null, " + COLUMN_NOTE_TITLE
            + " text, " + COLUMN_NOTE_TYPE
            + " text, " + COLUMN_TEXT_NOTE_CREATE_DATE
            + " integer);";
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//Этот методы вызывается при изменении DATABASE_VERSION
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEXT_NOTES);
        onCreate(db);
    }
}
