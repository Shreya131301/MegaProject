package com.example.suraksha;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.suraksha.Data.contactDbHelper;
import com.example.suraksha.Data.surakshaContracter;


public class third extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int EDIT_CONTACT_CASE = 0;

    private static final int ADD_CONTACT_CASE = 1;

    private static int mActivityCase = -1;
    private boolean mContactHasChanged;
    private EditText mNameEditText;
    Uri currentContactUri;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mContactHasChanged = true;
            return false;
        }
    };


    private EditText mcontactnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third);
        Intent intent = getIntent();
        currentContactUri = intent.getData();
        if (currentContactUri == null) {
            mActivityCase= ADD_CONTACT_CASE;
            setTitle("Add a Contact");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit contact");
            mActivityCase=EDIT_CONTACT_CASE;
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(0, null, this);
        }


        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_contact_name);
        mcontactnumber = (EditText) findViewById(R.id.edit_contact_number);

        mNameEditText.setOnTouchListener(mTouchListener);
        mcontactnumber.setOnTouchListener(mTouchListener);



    }
    @Override
    public void onBackPressed() {
        if(!mContactHasChanged){
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener dicardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        showUnsavedChangesDialog(dicardButtonClickListener);
    }
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.third_page, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_save:
                saveContact();
                finish();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if(!mContactHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(third.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveContact() {

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String contact = mcontactnumber.getText().toString();
        if (mActivityCase == ADD_CONTACT_CASE) {
            if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(contact)) {
                return;
            }
        }

        contactDbHelper mDbHelper = new contactDbHelper(this);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(surakshaContracter.contactentry.COLUMN_CONTACT_NAME, nameString);
        values.put(surakshaContracter.contactentry.COLUMN_CONTACT_NUMBER, contact);

        if (mActivityCase == ADD_CONTACT_CASE) {
            Uri newUri = getContentResolver().insert(surakshaContracter.contactentry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "contact not saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "contact added successfully", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentContactUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Contact not updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "contact updated successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mActivityCase == ADD_CONTACT_CASE){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                surakshaContracter.contactentry._ID,
                surakshaContracter.contactentry.COLUMN_CONTACT_NAME,
                surakshaContracter.contactentry.COLUMN_CONTACT_NUMBER};


        return new CursorLoader(this,   // Parent activity context
                currentContactUri,       // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }


    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(surakshaContracter.contactentry.COLUMN_CONTACT_NAME);
            int numberColumnIndex = cursor.getColumnIndex(surakshaContracter.contactentry.COLUMN_CONTACT_NUMBER);


            String name = cursor.getString(nameColumnIndex);
            String number = cursor.getString(numberColumnIndex);
            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mcontactnumber.setText(number);
        }

    }


    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mNameEditText.setText("");
        mcontactnumber.setText("");

    }
    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteContact();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteContact() {
        if(mActivityCase == EDIT_CONTACT_CASE){
            int rowsDeleted = getContentResolver().delete(currentContactUri, null, null);
            if(rowsDeleted == 0){
                Toast.makeText(this, "fail to delete", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,"contact deleted", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }
}



