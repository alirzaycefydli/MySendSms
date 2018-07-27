package com.example.alirz.mysendsms;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.alirz.mysendsms.StaticClass.listContact;

public class MainActivity extends AppCompatActivity {
    //wids
    private Toolbar mToolBar;
    private List<ModelContact> listContact;
    private EditText mMessage;
    private Button sendMessageButton, filterButton;


    //vars
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 12;
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listContact = new ArrayList<>();
        mToolBar = findViewById(R.id.main_tool_bar);
        setSupportActionBar(mToolBar);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            }
        } else {
            getContacs();

        }

        mMessage = findViewById(R.id.input_message);
        sendMessageButton = findViewById(R.id.send_message_all);
        filterButton = findViewById(R.id.send_message_filter);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FilteredMessageActivity.class));

            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.SEND_SMS)) {
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.SEND_SMS},
                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                    }
                } else {
                    if (listContact.size() > 0) {
                        topluGonder(listContact);
                    }
                }

            }
        });

    }


    private void getContacs() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String phoneNumber = "-";
                Log.i("Names", name);
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    // Query phone here. Covered next
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                    while (phones.moveToNext()) {
                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.i("Number", phoneNumber);
                    }
                    phones.close();
                }
                ModelContact modelContact = new ModelContact();
                modelContact.setName(name);
                modelContact.setNumber(phoneNumber);
                modelContact.setNumberEdit(editNumber(phoneNumber));
                listContact.add(modelContact);
                Log.d("**Phone:", name + " -> " + phoneNumber + " : " + modelContact.getNumberEdit());
            }
            cursor.close();
        }
        StaticClass.listContact = listContact;


    }

    private String editNumber(String number) {
        String numberResult = "";
        for (int i = 0; i < number.length(); i++) {
            String nu = "" + number.charAt(i);
            if (isNumeric(nu)) {
                numberResult += nu;
            }
        }
        return numberResult;
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }



    private void topluGonder(List<ModelContact> list) {
        String message = mMessage.getText().toString();

        if (!TextUtils.isEmpty(message)) {
            for (int i = 0; i < list.size(); i++) {

                // sendSMS(list.get(i).getNumberEdit(), StaticClass.message);

                sendSMS(list.get(i).getNumberEdit(), message);


            }
            Toast.makeText(MainActivity.this, getString(R.string.sms_gönderimi_tamamlandı), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, getString(R.string.alan_bos_birakilamaz), Toast.LENGTH_SHORT).show();
        }





    }

    // Bu method Sms gönderme işlemini yapıyor
    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
