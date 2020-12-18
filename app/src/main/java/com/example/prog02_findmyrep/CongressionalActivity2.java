package com.example.prog02_findmyrep;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class CongressionalActivity2 extends AppCompatActivity {

    String party_a = null;
    String party_b = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.congressional_activity2);

        int defaultImage = getResources().getIdentifier("na_photo_foreground", "mipmap", getPackageName());

        TextView nameA = findViewById(R.id.textV_nameA);
        TextView partyA = findViewById(R.id.textV_partyA);
        ImageView photoA = findViewById(R.id.photoA);

        TextView nameB = findViewById(R.id.textV_nameB);
        TextView partyB = findViewById(R.id.textV_partyB);
        ImageView photoB = findViewById(R.id.photoB);

        Intent altIntent = getIntent();

        //LAYOUT & COLLECT STUFF
        nameA.setText(altIntent.getStringExtra("Name_0"));
        if(altIntent.getStringExtra("Party_0").equals("Republican Party")){
            partyA.setTextColor(0xFFFF0F0F);
            party_a = "Republican";
        }
        else{
            partyA.setTextColor(0xFF3E74FF);
            party_a = "Democrat";
        }
        partyA.setText(party_a);

        if(!altIntent.getStringExtra("Photo_0").equals("NA")) {
            Picasso.get()
                    .load(altIntent.getStringExtra("Photo_0"))
                    .placeholder(defaultImage)
                    .error(defaultImage)
                    .into(photoA);
        }
        photoA.setClipToOutline(true);

        nameB.setText(altIntent.getStringExtra("Name_1"));
        if(altIntent.getStringExtra("Party_1").equals("Republican Party")){
            partyB.setTextColor(0xFFFF0F0F);
            party_b = "Republican";
        }
        else{
            partyB.setTextColor(0xFF3E74FF);
            party_b = "Democrat";
        }
        partyB.setText(party_b);

        if(!altIntent.getStringExtra("Photo_1").equals("NA")) {
            Picasso.get()
                    .load(altIntent.getStringExtra("Photo_1"))
                    .placeholder(defaultImage)
                    .error(defaultImage)
                    .into(photoB);
        }
        photoB.setClipToOutline(true);


        //Passthrough logic
        TextView clickA = findViewById(R.id.textV_moreInfoA);
        clickA.setOnClickListener((v) ->{
            sendDetails(altIntent.getStringExtra("Photo_0"), party_a,"Senator", altIntent.getStringExtra("Name_0"),
                    altIntent.getStringExtra("Website_0"), altIntent.getStringExtra("Phone_0"),
                    altIntent.getStringExtra("Twitter_0"), altIntent.getStringExtra("Youtube_0"));
        });
        TextView clickB = findViewById(R.id.textV_moreInfoB);
        clickB.setOnClickListener((v) ->{
            sendDetails(altIntent.getStringExtra("Photo_1"), party_b,"Senator", altIntent.getStringExtra("Name_1"),
                    altIntent.getStringExtra("Website_1"), altIntent.getStringExtra("Phone_1"),
                    altIntent.getStringExtra("Twitter_1"), altIntent.getStringExtra("Youtube_1"));
        });
    }// onCreate()

    public void sendDetails(String photo, String party, String title, String name, String website,
                            String phone, String twitter, String youtube) {
        Intent detailedIntent = new Intent(CongressionalActivity2.this, DetailedActivity.class);
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