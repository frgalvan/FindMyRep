package com.example.prog02_findmyrep;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class DetailedActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_activity);

        LinearLayout banner = findViewById(R.id.banner_senator);
        ImageView seal = findViewById(R.id.senator_seal);

        int defaultImage = getResources().getIdentifier("na_photo_foreground", "mipmap", getPackageName());

        ImageView photo = findViewById(R.id.photoA2);
        TextView party = findViewById(R.id.textV_partyA2);
        TextView title = findViewById(R.id.textView_a2);
        TextView name = findViewById(R.id.textV_senatorName);
        TextView phone = findViewById(R.id.textV_senatorPhone);
        TextView website = findViewById(R.id.textV_senatorWebsite);
        ImageView twitter = findViewById(R.id.imageView8);
        ImageView youtube = findViewById(R.id.imageView9);

        Intent intent = getIntent();


        //Design Logic//
        if (intent.getStringExtra("title").equals("Representative")) {
            TextView bannerText = findViewById(R.id.banner_text);
            bannerText.setText("Representative"); //default was Senator
            ImageView background = findViewById(R.id.detailed_background);
            int imgID = getResources().getIdentifier("congress_floor_large_foreground", "mipmap", getPackageName());
            background.setImageResource(imgID);
        } else {
            int imgID = getResources().getIdentifier("senator_seal_foreground", "mipmap", getPackageName());
            seal.setImageResource(imgID);
        }

        if (intent.getStringExtra("party").equals("Republican")) {
            banner.setBackgroundColor(0xFFFF0F0F); //default was blue
            party.setTextColor(0xFFFF0F0F);
        } else {
            findViewById(R.id.dividerLeft).setBackgroundColor(0xFF3E74FF); // default was red
            findViewById(R.id.dividerRight).setBackgroundColor(0xFF3E74FF);
            party.setTextColor(0xFF3E74FF);
        }

        //Layout
        Picasso.get()
                .load(intent.getStringExtra("photo"))
                .placeholder(defaultImage)
                .error(defaultImage)
                .into(photo);
        photo.setClipToOutline(true);
        party.setText(intent.getStringExtra("party"));
        title.setText(intent.getStringExtra("title"));
        name.setText(intent.getStringExtra("name"));
        //do linkage with phone number
        phone.setPaintFlags(phone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        phone.setText(intent.getStringExtra("phone"));
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + intent.getStringExtra("phone")));
        phone.setOnClickListener((v) -> {
            startActivity(callIntent);
        });
        // do linkage with website
        website.setPaintFlags(website.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        website.setText(intent.getStringExtra("website"));
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(intent.getStringExtra("website")));
        website.setOnClickListener((v) -> {
            startActivity(browserIntent);
        });
        //do stuff with twitter
        if (intent.getStringExtra("twitter").equals("NA")) {
            twitter.setVisibility(View.GONE);
            twitter.setClickable(false);
        }
        twitter.setOnClickListener((v) -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + intent.getStringExtra("twitter"))));
        });
        //do stuff with youtube
        if (intent.getStringExtra("youtube").equals("NA")) {
            youtube.setVisibility(View.GONE);
            youtube.setClickable(false);
        }
        youtube.setOnClickListener((v) -> {
            if (intent.getStringExtra("youtube").substring(0, 2).equals("UC")) {  //the links provided were not equal paths
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/" + intent.getStringExtra("youtube"))));
            } else
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/user/" + intent.getStringExtra("youtube"))));
        });
    }// on Create()

}