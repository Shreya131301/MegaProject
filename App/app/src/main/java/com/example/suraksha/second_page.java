package com.example.suraksha;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.suraksha.Data.contactDbHelper;
import com.example.suraksha.Data.surakshaContracter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static android.Manifest.permission.CALL_PHONE;


public class second_page extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    surakshaCursorAdapter mcursorAdapter;
    MediaPlayer mediaPlayer;
    ImageView b2;
    private FusedLocationProviderClient client;
    contactDbHelper myDB;
    private final int REQUEST_CHECK_CODE = 8989;
    private LocationSettingsRequest.Builder builder;
    String x = "", y = "";
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    Intent mIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_page);
        b2 = findViewById(R.id.help);
        myDB = new contactDbHelper(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        client= LocationServices.getFusedLocationProviderClient(this);

        mediaPlayer = MediaPlayer.create(this, R.raw.siren);


        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onGPS();
        } else {
            startTrack();
        }

        b2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadData();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(second_page.this, third.class);
                startActivity(intent);
            }
        });

        ListView contactListView = (ListView) findViewById(R.id.list);


        View emptyView = findViewById(R.id.empty_view);
        contactListView.setEmptyView(emptyView);
        mcursorAdapter = new surakshaCursorAdapter(this, null);
        contactListView.setAdapter(mcursorAdapter);
        getSupportLoaderManager().initLoader(0, null, this);


        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(second_page.this, third.class);
                Uri currentContactUri = ContentUris.withAppendedId(surakshaContracter.contactentry.CONTENT_URI, id);
                intent.setData(currentContactUri);
                startActivity(intent);
            }
        });


    }
    public void play() {

            if (mediaPlayer.isPlaying()) {
              mediaPlayer.pause();
                Toast.makeText(second_page.this, "pause", Toast.LENGTH_SHORT).show();;
            } else {
                mediaPlayer.start();
                Toast.makeText(second_page.this, "play", Toast.LENGTH_SHORT).show();
                mediaPlayer.setLooping(true);
            }

    }

    private void startTrack() {
        if(ActivityCompat.checkSelfPermission(second_page.this, Manifest.permission.ACCESS_FINE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED&& ActivityCompat.
                checkSelfPermission(second_page.this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        else {
        client.getLastLocation().addOnSuccessListener(second_page.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location locationGPS) {
                if(locationGPS!=null){
                    double lat=locationGPS.getLatitude();
                    double lon=locationGPS.getLongitude();
                    x=String.valueOf(lat);
                    y=String.valueOf(lon);
            }
                else {
                    Toast.makeText(second_page.this, "Unable to find the location", Toast.LENGTH_SHORT).show();
                }

            }


        });
        }
}

    private void onGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("yes", (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    final AlertDialog alertDialog = builder.create();
    alertDialog.show();
    }

    private void loadData() {
        ArrayList<String> thelist = new ArrayList<>();
        Cursor data =myDB.getListContents();
        if (data.getCount()==0){
            Toast.makeText(this,"Please add contact Name and contact Number",Toast.LENGTH_SHORT).show();
        }
        else{
            String number ="";
            while(data.moveToNext()) {
                boolean b=true;
                thelist.add(data.getString(1));
                number = number + data.getString(2) + (data.isLast() ? "" : ";");
                sendSms( number, b);
                number="";

            }
            data.moveToFirst();
            thelist.add(data.getString(1));
            number = number + data.getString(2) + (data.isLast() ? "" : ";");
            call(number);
            number="";
                if(!thelist.isEmpty()){
                    sendSms(number,true);
            }
        }
    }

    private void sendSms(String number,boolean b) {
        int permissioncheck = ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS);
        if( permissioncheck==PackageManager.PERMISSION_GRANTED) {

            SmsManager smsManager =SmsManager.getDefault();
            smsManager.sendTextMessage(number,null, " I NEED HELP."+" \nhttp://maps.google.com/maps?saddr=" + x + "," + y,null,null);
            Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},0);
        }
    }


    private void call(String number) {
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:"+number));
        if(ContextCompat.checkSelfPermission(getApplicationContext(),CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
            startActivity(i);
        }
        else {
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                requestPermissions(new String[]{CALL_PHONE},1);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_second_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_delete_all_entries:
                deleteAllContacts();
                return true;
            case R.id.play:
                play();
                return true;
            case R.id.more:
            Intent intent = new Intent(second_page.this, More.class);
                startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllContacts() {
        int rowsDeleted = getContentResolver().delete(surakshaContracter.contactentry.CONTENT_URI, null, null);
        Toast.makeText(this, "All contacts deleted!", Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                surakshaContracter.contactentry._ID,
                surakshaContracter.contactentry.COLUMN_CONTACT_NAME,
                surakshaContracter.contactentry.COLUMN_CONTACT_NUMBER};


        return new CursorLoader(this,
                surakshaContracter.contactentry.CONTENT_URI,
                projection,             // The columns to return for each row
                null,                   // Selection criteria
                null,                   // Selection criteria
                null);                  // The sort order for the returned rows
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mcursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mcursorAdapter.swapCursor(null);
    }


}