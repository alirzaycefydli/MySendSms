package com.example.alirz.mysendsms;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.alirz.mysendsms.MainActivity.MY_PERMISSIONS_REQUEST_SEND_SMS;

public class FilteredMessageActivity extends AppCompatActivity {
    //wids
    private Toolbar mToolBar;
    private TextView totalContact;
    private EditText mInputMessage, mMessage;
    private Button btnSend, btnFilter;


    //vars
    private List<ModelContact> listContact;
    private String input;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_message);

        mToolBar = findViewById(R.id.filtered_tool_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(getString(R.string.mesaj_filtrele));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listContact = new ArrayList<>();
        btnFilter = findViewById(R.id.button_filter);
        btnSend = findViewById(R.id.send_filtered_message);
        totalContact = findViewById(R.id.filtered_total_size);
        mInputMessage = findViewById(R.id.filtered_input);
        mMessage = findViewById(R.id.filtered_input_message);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input = mInputMessage.getText().toString().trim();
                filtrele(input);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = mMessage.getText().toString();
                if (ContextCompat.checkSelfPermission(FilteredMessageActivity.this,
                        Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(FilteredMessageActivity.this,
                            Manifest.permission.SEND_SMS)) {
                    } else {
                        ActivityCompat.requestPermissions(FilteredMessageActivity.this ,
                                new String[]{Manifest.permission.SEND_SMS},
                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                    }
                }else{
                    if (listContact.size() > 0) {
                        topluGonder(listContact);
                    }
                }

            }
        });

    }

    // bu method listeyi filtreye göre ayırıyor.
    private void filtrele(String filtre) {
        listContact.clear();
        for (int i = 0; i < StaticClass.listContact.size(); i++) {
            if (StaticClass.listContact.get(i).getName().toLowerCase().trim().contains(filtre.toLowerCase().trim())) {
                listContact.add(StaticClass.listContact.get(i));
            }
        }
        totalContact.setText(getString(R.string.toplam_gönderilecek_sayisi) + listContact.size());
        if (listContact.size() > 0) {
            btnSend.setEnabled(true);
        } else {
            btnSend.setEnabled(false);
        }
    }


    // Bu method listedeki tüm kullanıcılara sms gönderiyor
    private void topluGonder(List<ModelContact> list) {
        for (int i = 0; i < list.size(); i++) {
            message = mMessage.getText().toString();
            //sendSMS(list.get(i).getNumberEdit(),StaticClass.message);
            sendSMS(list.get(i).getNumberEdit(), message);
        }
        Toast.makeText(FilteredMessageActivity.this, getString(R.string.sms_gönderimi_tamamlandı), Toast.LENGTH_SHORT).show();
    }

    // Bu method Sms gönderme işlemini yapıyor
    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
