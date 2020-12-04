package com.example.suraksha.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class surakshaContracter {
    private surakshaContracter() {
    }
    public static final String CONTENT_AUTHORITY = "com.example.suraksha";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CONTACTS = "contacts";


    public static final class contactentry implements BaseColumns {


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CONTACTS);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;


        public final static String TABLE_NAME = "contacts";


        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_CONTACT_NAME = "name";


        public final static String COLUMN_CONTACT_NUMBER = "number";


    }
}
