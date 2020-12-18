package com.example.prog02_findmyrep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class CongressionalActivity extends AppCompatActivity {
    String party_a = null;
    String party_b = null;
    String party_c = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.congressional_activity);

        int defaultImage = getResources().getIdentifier("na_photo_foreground", "mipmap", getPackageName());

        TextView nameA = findViewById(R.id.textView_A0);
        TextView partyA = findViewById(R.id.textView_A1);
        ImageView photo0 = findViewById(R.id.imageView_A3);

        TextView nameB = findViewById(R.id.textView_B0);
        TextView partyB = findViewById(R.id.textView_B1);
        ImageView photo1 = findViewById(R.id.imageView_B3);

        TextView nameC = findViewById(R.id.textView_C0);
        TextView partyC = findViewById(R.id.textView_C1);
        ImageView photo2 = findViewById(R.id.imageView_C3);


        Intent intent = getIntent();

        nameA.setText(intent.getStringExtra("Name_0"));
        if (intent.getStringExtra("Party_0").equals("Republican Party")) {
            partyA.setTextColor(0xFFFF0F0F);
            party_a = "Republican";
        } else {
            partyA.setTextColor(0xFF3E74FF);
            party_a = "Democrat";
        }
        partyA.setText(party_a);
        if (!intent.getStringExtra("Photo_0").equals("NA"))
            Picasso.get()
                    .load(intent.getStringExtra("Photo_0"))
                    .placeholder(defaultImage)
                    .error(defaultImage)
                    .into(photo0);
        photo0.setClipToOutline(true);

        nameB.setText(intent.getStringExtra("Name_1"));
        if (intent.getStringExtra("Party_1").equals("Republican Party")) {
            partyB.setTextColor(0xFFFF0F0F);
            party_b = "Republican";
        } else {
            partyB.setTextColor(0xFF3E74FF);
            party_b = "Democrat";
        }
        partyB.setText(party_b);
        if (!intent.getStringExtra("Photo_1").equals("NA"))
            Picasso.get()
                    .load(intent.getStringExtra("Photo_1"))
                    .placeholder(defaultImage)
                    .error(defaultImage)
                    .into(photo1);
        photo1.setClipToOutline(true);

        nameC.setText(intent.getStringExtra("Name_2"));
        if (intent.getStringExtra("Party_2").equals("Republican Party")) {
            partyC.setTextColor(0xFFFF0F0F);
            party_c = "Republican";
        } else {
            partyC.setTextColor(0xFF3E74FF);
            party_c = "Democrat";
        }
        partyC.setText(party_c);
        if (!intent.getStringExtra("Photo_2").equals("NA")) {// && intent.getStringExtra("Photo_2") != null)
            Picasso.get()
                    .load(intent.getStringExtra("Photo_2"))
                    .placeholder(defaultImage)
                    .error(defaultImage)
                    .into(photo2);
        }
        photo2.setClipToOutline(true);

        //Passthrough logic
        //Call Send details from the apt. listener to populate with correct Representative
        TextView clickInfo0 = findViewById(R.id.textView_A2);
        clickInfo0.setOnClickListener((v) -> {
            sendDetails(intent.getStringExtra("Photo_0"), party_a, "Senator", intent.getStringExtra("Name_0"),
                    intent.getStringExtra("Website_0"), intent.getStringExtra("Phone_0"),
                    intent.getStringExtra("Twitter_0"), intent.getStringExtra("Youtube_0"));
        });
        TextView clickInfo1 = findViewById(R.id.textView_B2);
        clickInfo1.setOnClickListener((v) -> {
            sendDetails(intent.getStringExtra("Photo_1"), party_b, "Senator", intent.getStringExtra("Name_1"),
                    intent.getStringExtra("Website_1"), intent.getStringExtra("Phone_1"),
                    intent.getStringExtra("Twitter_1"), intent.getStringExtra("Youtube_1"));
        });
        TextView clickInfo2 = findViewById(R.id.textView_C2);
        clickInfo2.setOnClickListener((v) -> {  //special case of the Three, need to edit textView_a2 from detailed view,
            sendDetails(intent.getStringExtra("Photo_2"), party_c, "Representative", intent.getStringExtra("Name_2"),
                    intent.getStringExtra("Website_2"), intent.getStringExtra("Phone_2"),
                    intent.getStringExtra("Twitter_2"), intent.getStringExtra("Youtube_2"));
        });
    } //onCreate()

    public void sendDetails(String photo, String party, String title, String name, String website,
                            String phone, String twitter, String youtube) {
        Intent detailedIntent = new Intent(CongressionalActivity.this, DetailedActivity.class);
        detailedIntent.putExtra("photo", photo);
        detailedIntent.putExtra("party", party);
        detailedIntent.putExtra("title", title);
        detailedIntent.putExtra("name", name);
        detailedIntent.putExtra("website", website);
        detailedIntent.putExtra("phone", phone);
        detailedIntent.putExtra("twitter", twitter);
        detailedIntent.putExtra("youtube", youtube);

        startActivity(detailedIntent);
    }
}

