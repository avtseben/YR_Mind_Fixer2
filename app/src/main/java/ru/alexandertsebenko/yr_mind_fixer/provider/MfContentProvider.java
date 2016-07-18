package ru.alexandertsebenko.yr_mind_fixer.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

import ru.alexandertsebenko.yr_mind_fixer.db.MySQLiteHelper;
import ru.alexandertsebenko.yr_mind_fixer.db.NoteDataSource;
import ru.alexandertsebenko.yr_mind_fixer.util.Log_YR;



public class MfContentProvider extends ContentProvider {


    static final String PROVIDER_NAME = "ru.alexandertsebenko.yr_mind_fixer.provider.notes";
    static final String URL = "content://" + PROVIDER_NAME + "/text_notes";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String _ID = MySQLiteHelper.COLUMN_ID;
    static final String NOTE_TITLE = MySQLiteHelper.COLUMN_NOTE_TITLE;

    private static HashMap<String, String> NOTES_PROJECTION_MAP;

    static final int NOTES = 1;
    static final int NOTE_ID = 2;

    /**
     * DB constants
     */
    static final String NOTES_TABLE_NAME = MySQLiteHelper.TABLE_TEXT_NOTES;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "text_notes", NOTES);
        uriMatcher.addURI(PROVIDER_NAME, "text_notes/#", NOTE_ID);
    }

    /**
     * My fields
     */
    private NoteDataSource mDataSource = null;
    private SQLiteDatabase mDb = null;
    private Log_YR log = new Log_YR(getClass().toString());


    @Override
    public boolean onCreate() {
        log.d("onCreate");
        try {
            mDataSource = new NoteDataSource(this.getContext());
            mDataSource.open();
            mDb = mDataSource.getDB();
        } catch (NullPointerException e) {
            log.d("База данных недоступна");
        }
        return (mDb == null)? false:true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /**
         * Add a new student record
         */
        long rowID = mDb.insert(	NOTES_TABLE_NAME, "", values);

        /**
         * If record is added successfully
         */

        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(NOTES_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case NOTES:
                qb.setProjectionMap(NOTES_PROJECTION_MAP);
                break;

            case NOTE_ID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (sortOrder == null || sortOrder == ""){
            /**
             * By default sort on student names
             */
            sortOrder = NOTE_TITLE;
        }
        Cursor c = qb.query(mDb,	projection,	selection, selectionArgs,null, null, sortOrder);

        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case NOTES:
                count = mDb.delete(NOTES_TABLE_NAME, selection, selectionArgs);
                break;

            case NOTE_ID:
                String id = uri.getPathSegments().get(1);
                count = mDb.delete( NOTES_TABLE_NAME, _ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case NOTES:
                count = mDb.update(NOTES_TABLE_NAME, values, selection, selectionArgs);
                break;

            case NOTE_ID:
                count = mDb.update(NOTES_TABLE_NAME, values, _ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            /**
             * Get all student records
             */
            case NOTES:
                return "vnd.android.cursor.dir/vnd.example.text_notes";

            /**
             * Get a particular student
             */
            case NOTE_ID:
                return "vnd.android.cursor.item/vnd.example.text_notes";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
