package com.lufficc.ishuhui.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lufficc on 2016/11/5.
 */

public class FileDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "ishuhui.db";

    private static final String TEXT_TYPE = " TEXT";

    private static final String BOOLEAN_TYPE = " INTEGER";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FilesPersistenceContract.FileEntry.TABLE_NAME + " (" +
                    FilesPersistenceContract.FileEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP +
                    FilesPersistenceContract.FileEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    FilesPersistenceContract.FileEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    FilesPersistenceContract.FileEntry.COLUMN_NAME_URL + TEXT_TYPE + COMMA_SEP +
                    FilesPersistenceContract.FileEntry.COLUMN_NAME_LOCAL_PATH + TEXT_TYPE + COMMA_SEP +
                    FilesPersistenceContract.FileEntry.COLUMN_NAME_CHAPTER_ID + TEXT_TYPE + COMMA_SEP +
                    FilesPersistenceContract.FileEntry.COLUMN_NAME_CHAPTER_NAME + TEXT_TYPE + COMMA_SEP +
                    FilesPersistenceContract.FileEntry.COLUMN_NAME_COMIC_ID + TEXT_TYPE + COMMA_SEP +
                    FilesPersistenceContract.FileEntry.COLUMN_NAME_COMIC_NAME + TEXT_TYPE +
                    " )";


    public FileDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
