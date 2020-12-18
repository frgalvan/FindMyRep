package com.example.prog02_findmyrep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MainActivity extends AppCompatActivity {

    Boolean hasGPS = false;

    //API URLs
    String CIVIC_URL = "https://www.googleapis.com/civicinfo/v2/representatives";
    String GEO_URL = "https://maps.googleapis.com/maps/api/geocodeson";
    String RANDOM_URL = "https://api.3geonames.org/?randomland=US&json=1";      // CALL AND PARSE FOR RANDOM GEOLOCATION WITHIN US.
    String API_KEY  = "AIzaSyA4G8iFITKpW-X5muFK6wigky7uQP3EnIM";

    //Location vars
    double lat = 0;
    double lng = 0;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    LocationManager mLocManager = null;
    LocationListener mLocListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestLocationPermission();

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        // BEGIN "SHARE YOUR LOCATION" GROUP
        ImageButton goLocate = findViewById(R.id.share_button_map);
        //goLocate will also trigger since Arrow PNG is not clickable
        goLocate.setOnClickListener((v) -> {
            if(hasGPS && lat != 0){ //checking lat is to suppress putting in zero coords
                String latlng = lat + "," + lng;
                getAddress(latlng);
            }
            else{
                Toast.makeText(context, "You didn't provide us access to your location!  Please restart the application to Allow" , duration).show();
            }
        });//Locate

        //Cases are: No photo (for 94706 this would be Kamala) and photo (Dianne Fienstien)
        //TO-DO, I just found another Case to check for if/when there is a missing Congressperson.
        Button goZIP = findViewById(R.id.buttonZip);
        goZIP.setOnClickListener((v) -> {
            EditText textZIP = findViewById(R.id.editText_ZIP);
            String input_zip = textZIP.getText().toString();// might have to check for valid input here, unsure yet.
            String zip_url = "?address=" + input_zip;
            String scopeIn = "&includeOffices=true&levels=country&roles=legislatorUpperBody&roles=legislatorLowerBody";
            String full_url = CIVIC_URL + zip_url + scopeIn + "&key=" + API_KEY;

            sendCivicInfo(full_url);
        });//on click ZIP

        Button goAddress = findViewById(R.id.buttonAddress);
        goAddress.setOnClickListener((v) -> {
            EditText address1 = findViewById(R.id.editText_address1);
            EditText city = findViewById(R.id.editText_city);
            EditText state = findViewById(R.id.editText_state);
            String address = address1.getText().toString().replace(" ", "%20") + "%20";
            String cityState = city.getText().toString() + "%20" + state.getText().toString();
            String scopeIn = "&includeOffices=true&levels=country&roles=legislatorUpperBody&roles=legislatorLowerBody";
            String full_url = CIVIC_URL + "?address=" + address + cityState + scopeIn + "&key=" + API_KEY;

            sendCivicInfo(full_url);
        });// on click Address

        ImageButton wildCard = findViewById(R.id.wildCard);
        wildCard.setOnClickListener((v) ->{
            getRandom();
        });// on click WildCard
    }//onCreate()
    //Get a random longitude within the US
    public void getRandom(){
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a JSON response from the provided URL.
        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.GET, RANDOM_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String lat = response.getJSONObject("nearest").getString("latt");
                            String lng = response.getJSONObject("nearest").getString("longt");
                            getAddress(lat + "," + lng);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Could not retrieve Address", Toast.LENGTH_SHORT).show();
                getRandom(); //repeating call here might automatically make it happen B/C success rate of this call is roughly 50/50
            }
        });
        queue.add(jRequest);
    }

    //REVERSE GEOCODE LOOKUP
    public void getAddress(String latlng){
        String url = GEO_URL + "?latlng=" + latlng +"&result_type=postal_code"+ "&key=" + API_KEY;
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a JSON response from the provided URL.
        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject results = response.getJSONArray("results").getJSONObject(0);
                            JSONObject address = results.getJSONArray("address_components").getJSONObject(0);
                            String zipCode = address.getString("long_name");
                            //build full_url for direct CIVIC INFO call
                            String zip_url = "?address=" + zipCode;
                            String scopeIn = "&includeOffices=true&levels=country&roles=legislatorUpperBody&roles=legislatorLowerBody";
                            String full_url = CIVIC_URL + zip_url + scopeIn + "&key=" + API_KEY;
                            sendCivicInfo(full_url);  //make API call to civic info, directly

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Could not retrieve Address", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jRequest);
    }

    public void sendCivicInfo(String url){ // could possibly add some logic here to handle different activities depending on HouseRep availability?
        RequestQueue queue = Volley.newRequestQueue(this);
        Intent intent = new Intent(MainActivity.this, CongressionalActivity.class); //All 3 are applicable from here
        Intent altIntent = new Intent(MainActivity.this, CongressionalActivity2.class);
        // Request a JSON response from the provided URL.
        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //JSONArray offices = response.getJSONArray("offices");
                            JSONArray officials = response.getJSONArray("officials");

                            int numOfficials = officials.length();
                            if(numOfficials == 3) { // 2 Senators + 1 House Rep
                                for (int i = 0; i < numOfficials; i++) {
                                    JSONObject officialsObject = officials.getJSONObject(i); // Senators: 0 & 1, HouseRep: 2
                                    intent.putExtra("Name_" + String.valueOf(i), officialsObject.getString("name"));
                                    intent.putExtra("Party_" + String.valueOf(i), officialsObject.getString("party"));
                                    try { //handle case where no photo is available through API
                                        intent.putExtra("Photo_" + String.valueOf(i), officialsObject.getString("photoUrl"));
                                    } catch (JSONException j) {
                                        intent.putExtra("Photo_" + String.valueOf(i), "NA");
                                    }
                                    //sending (4) attributes to pass through for detailed view..
                                    String website = "NA";
                                    String phone = "NA";
                                    String twitterLink = "NA";
                                    String youtubeLink = "NA";

                                    website = officialsObject.getJSONArray("urls").getString(0);
                                    phone = officialsObject.getJSONArray("phones").getString(0);

                                    //get social media links if they exist for this representative:
                                    JSONArray channelsArr =  officialsObject.getJSONArray("channels");
                                    for(int index = 0; index < channelsArr.length(); index++){
                                        if (channelsArr.getJSONObject(index).getString("type").equals("Twitter")){
                                            twitterLink = channelsArr.getJSONObject(index).getString("id");
                                        }
                                        else if(channelsArr.getJSONObject(index).getString("type").equals("YouTube")){
                                            youtubeLink = channelsArr.getJSONObject(index).getString("id");
                                        }
                                    }
                                    intent.putExtra("Website_" + String.valueOf(i), website);
                                    intent.putExtra("Phone_" + String.valueOf(i), phone);
                                    intent.putExtra("Twitter_" + String.valueOf(i), twitterLink);
                                    intent.putExtra("Youtube_" + String.valueOf(i), youtubeLink);
                                    intent.putExtra("numOfficials", numOfficials);  // to loop & extract in successive activities.
                                    //Toast.makeText(getApplicationContext(), website + ", " + phone + ", " + twitterLink + ", " + youtubeLink, Toast.LENGTH_LONG).show();

                                }
                                startActivity(intent);//Call CongressionalActivity
                            }
                            else{               // 2 Senators only  LOGIC HAD TO BE SPLIT FOR THE CALL TO SEPARATE INTENTS
                                for (int i = 0; i < numOfficials; i++){
                                    JSONObject officialsObj = officials.getJSONObject(i);
                                    altIntent.putExtra("Name_" + String.valueOf(i), officialsObj.getString("name"));
                                    altIntent.putExtra("Party_" + String.valueOf(i), officialsObj.getString("party"));
                                    try{
                                        altIntent.putExtra("Photo_" + String.valueOf(i), officialsObj.getString("photoUrl"));
                                    } catch (JSONException j){
                                        altIntent.putExtra("Photo_" + String.valueOf(i), "NA");
                                    }

                                    //sending (4) attributes to pass through for detailed view..
                                    String website = "NA";
                                    String phone = "NA";
                                    String twitterLink = "NA";
                                    String youtubeLink = "NA";

                                    website = officialsObj.getJSONArray("urls").getString(0);
                                    phone = officialsObj.getJSONArray("phones").getString(0);

                                    //get social media links if they exist for this representative:
                                    JSONArray channelsArr =  officialsObj.getJSONArray("channels");
                                    for(int index = 0; index < channelsArr.length(); index++){
                                        if (channelsArr.getJSONObject(index).getString("type").equals("Twitter")){
                                            twitterLink = channelsArr.getJSONObject(index).getString("id");
                                        }
                                        else if(channelsArr.getJSONObject(index).getString("type").equals("YouTube")){
                                            youtubeLink = channelsArr.getJSONObject(index).getString("id");
                                        }
                                    }
                                    altIntent.putExtra("Website_" + String.valueOf(i), website);
                                    altIntent.putExtra("Phone_" + String.valueOf(i), phone);
                                    altIntent.putExtra("Twitter_" + String.valueOf(i), twitterLink);
                                    altIntent.putExtra("Youtube_" + String.valueOf(i), youtubeLink);
                                }
                                startActivity(altIntent);//Call CongressionalActivity2
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Could not retrieve Data. Please check your address", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jRequest);
    }//sendCivicInfo()

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
            hasGPS = true;

            mLocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            mLocListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }
            };
            mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocListener);
            Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }
}

