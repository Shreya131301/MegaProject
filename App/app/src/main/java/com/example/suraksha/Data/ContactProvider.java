package com.example.suraksha.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import com.example.suraksha.R;
import com.example.suraksha.third;

public class ContactProvider extends ContentProvider {

    public static final String LOG_TAG = ContactProvider.class.getSimpleName();


    private static final int CONTACT = 100;


    private static final int CONTACT_ID = 101;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {

        sUriMatcher.addURI(surakshaContracter.CONTENT_AUTHORITY, surakshaContracter.PATH_CONTACTS, CONTACT);
        sUriMatcher.addURI(surakshaContracter.CONTENT_AUTHORITY, surakshaContracter.PATH_CONTACTS + "/#", CONTACT_ID);
    }

    contactDbHelper mDbHelper;


    @Override
    public boolean onCreate() {

        mDbHelper = new contactDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();


        Cursor cursor;


        int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACT:

                cursor = database.query(surakshaContracter.contactentry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CONTACT_ID:
                selection = surakshaContracter.contactentry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};


                cursor = database.query(surakshaContracter.contactentry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACT:
                return insertContact(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertContact(Uri uri, ContentValues values) {

        String name = values.getAsString(surakshaContracter.contactentry.COLUMN_CONTACT_NAME);
        if (name.equals("")) {
            throw new IllegalArgumentException("contact requires a name");
        }
        String number = values.getAsString(surakshaContracter.contactentry.COLUMN_CONTACT_NUMBER);
        if (number.equals("") ) {
            throw new IllegalArgumentException("Insert number");
        }


        SQLiteDatabase database = mDbHelper.getWritableDatabase();


        long id = database.insert(surakshaContracter.contactentry.TABLE_NAME, null, values);

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, id);

    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACT:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case CONTACT_ID:
                selection = surakshaContracter.contactentry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(surakshaContracter.contactentry.COLUMN_CONTACT_NAME)) {
            String name = values.getAsString(surakshaContracter.contactentry.COLUMN_CONTACT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("contact requires a name");
            }
        }



        if (values.containsKey(surakshaContracter.contactentry.COLUMN_CONTACT_NUMBER)) {

            String number = values.getAsString(surakshaContracter.contactentry.COLUMN_CONTACT_NUMBER);
            if (number == null) {
                throw new IllegalArgumentException("contact requires valid number");
            }
        }




        if (values.size() == 0) {
            return 0;
        }


        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(surakshaContracter.contactentry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            return database.update(surakshaContracter.contactentry.TABLE_NAME, values, selection, selectionArgs);
        }
        return rowsUpdated;
    }



    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACT:
            rowsDeleted = database.delete(surakshaContracter.contactentry.TABLE_NAME, selection, selectionArgs);
            break;
            case CONTACT_ID:
                selection = surakshaContracter.contactentry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
            rowsDeleted = database.delete(surakshaContracter.contactentry.TABLE_NAME, selection, selectionArgs);
            break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }


        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACT:
                return surakshaContracter.contactentry.CONTENT_LIST_TYPE;
            case CONTACT_ID:
                return surakshaContracter.contactentry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}


