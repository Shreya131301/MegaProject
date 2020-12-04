package com.example.suraksha.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class contactDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = contactDbHelper.class.getSimpleName();


    private static final String DATABASE_NAME = "contactDetails.db";


    private static final int DATABASE_VERSION = 1;


    public contactDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_CONTACTS_TABLE = "CREATE TABLE " + surakshaContracter.contactentry.TABLE_NAME + " ("
                + surakshaContracter.contactentry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + surakshaContracter.contactentry.COLUMN_CONTACT_NAME + " TEXT NOT NULL, "
                + surakshaContracter.contactentry.COLUMN_CONTACT_NUMBER + " INTEGER NOT NULL);";
        db.execSQL(SQL_CREATE_CONTACTS_TABLE);
}


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public Cursor getListContents() {
        String[] projection = {
                surakshaContracter.contactentry._ID,
                surakshaContracter.contactentry.COLUMN_CONTACT_NAME,
                surakshaContracter.contactentry.COLUMN_CONTACT_NUMBER};
      SQLiteDatabase db = this.getWritableDatabase();

        Cursor data = db.query(surakshaContracter.contactentry.TABLE_NAME, projection, null, null,
                null, null, null);
        return data;
    }
}

