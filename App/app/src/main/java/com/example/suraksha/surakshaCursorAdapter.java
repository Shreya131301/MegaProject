package com.example.suraksha;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.suraksha.Data.surakshaContracter;
public class surakshaCursorAdapter extends CursorAdapter {


    public surakshaCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.number);

        int nameColumnIndex = cursor.getColumnIndex(surakshaContracter.contactentry.COLUMN_CONTACT_NAME);
        int breedColumnIndex = cursor.getColumnIndex(surakshaContracter.contactentry.COLUMN_CONTACT_NUMBER);

        String contactName = cursor.getString(nameColumnIndex);
        String contactNumber = cursor.getString(breedColumnIndex);

        nameTextView.setText(contactName);
        summaryTextView.setText(contactNumber);
    }
}