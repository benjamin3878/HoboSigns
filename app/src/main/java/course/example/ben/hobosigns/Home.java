package course.example.ben.hobosigns;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.FindCallback;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by Ben on 11/17/2015.
 * Home is the main interaction with a user.
 * The user will view post of other user in the area via map or list mode
 */
public class Home extends AppCompatActivity implements OnMapReadyCallback {
    // The Map Object
    private GoogleMap mMap;

    private final double COLLEGE_PARK_LATITUDE = 38.9967;
    private final double COLLEGE_PARK_LONGITUDE = -76.9275;

    private SupportMapFragment mapFragment;
    private LocationRequest locationRequest;
    private String TAG = "Testing HOME";
    private ParseGeoPoint geoPoint;
    private double longitude;
    private double latitude;
    private boolean paused = false;

    private final Map<String, Marker> mapMarkers = new HashMap<String, Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        Log.i(TAG, "THIS IS IN ONCREATE");

        Button newPostButton = (Button) findViewById(R.id.camera_button);
        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, CreatePost.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 11);
            }
        });

        locationRequest = LocationRequest.create();

        // Set up the map fragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mMap = mapFragment.getMap();
        mapFragment.getMapAsync(this);
        Log.i(TAG, mapFragment.toString());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "FUCK ME");
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 11) {
            if (requestCode == RESULT_OK) {
                Log.i(TAG, "FILEPATH: " + data.getStringExtra("filepath"));
                BitmapDescriptor icon1 = BitmapDescriptorFactory.fromPath(data.getStringExtra("filepath"));
                mMap.addMarker(new MarkerOptions().position(generate()).title("generate").icon(icon1));
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.log_out:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logOut(){
        ParseUser.logOut();
        Intent intent = new Intent(Home.this, WelcomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private LatLng generate() {
        Random rn = new Random();
        float xDis = rn.nextFloat()/100;
        float yDis = rn.nextFloat()/100;
        Log.i(TAG, "GEN X: " + xDis);
        Log.i(TAG, "GEN Y: " + yDis);
        return new LatLng(38.99 + xDis, -76.95 + yDis);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location location;
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        } else {
            longitude = COLLEGE_PARK_LONGITUDE;
            latitude = COLLEGE_PARK_LATITUDE;
        }


        LatLng collegePark = new LatLng(latitude, longitude);
        LatLng test = new LatLng(38.99, -76.82);
        //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.test);
        //BitmapDescriptor icon1 = BitmapDescriptorFactory.fromResource(R.drawable.test1);
        map.addMarker(new MarkerOptions().position(collegePark).title("Test Marker"));//.icon(icon));
        map.addMarker(new MarkerOptions().position(test).title("Test 1"));//.icon(icon1));
        for(int i = 0; i < 10; i ++) {
            map.addMarker(new MarkerOptions().position(generate()).title("generate"));
        }
        map.moveCamera(CameraUpdateFactory.newLatLng(collegePark));
        map.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
        doMapQuery();
    }

    /*
   * Set up the query to update the map view
   */
    private void doMapQuery() {
        //Since we can't grab gps coords
        Location mLocation = new Location("College Park");
        mLocation.setLatitude(38.99);
        mLocation.setLongitude(-76.82);
        final ParseGeoPoint myPoint = new ParseGeoPoint(mLocation.getLatitude(), mLocation.getLongitude());
        // Create the map Parse query
        ParseQuery<HoboSignsPost> mapQuery = HoboSignsPost.getQuery();
        // Set up additional query filters
        /*
        mapQuery.whereWithinKilometers("location", myPoint, MAX_POST_SEARCH_DISTANCE);
        mapQuery.include("user");
        mapQuery.orderByDescending("createdAt");
        mapQuery.setLimit(MAX_POST_SEARCH_RESULTS);
        */
        // Kick off the query in the background
        Log.i(TAG, "WE GOT HERE");
        mapQuery.findInBackground(new FindCallback<HoboSignsPost>() {
            @Override
            public void done(List<HoboSignsPost> objects, ParseException e) {
                Log.i(TAG, "DONE");
                if (e != null) {
                    Log.i(TAG, "An error occurred while querying for map posts.", e);
                    return;
                }
                // Posts to show on the map
                Set<String> toKeep = new HashSet<String>();
                // Loop through the results of the search
                Log.i(TAG, objects.toString());
                for (HoboSignsPost post : objects) {
                    // Add this post to the list of map pins to keep
                    toKeep.add(post.getObjectId());
                    // Check for an existing marker for this post
                    Marker oldMarker = mapMarkers.get(post.getObjectId());
                    // Set up the map marker's location
                    MarkerOptions markerOpts = new MarkerOptions().position(new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude()));
                    //BitmapDescriptorFactory.fromBitmap();
                    // Add a new marker
                    Marker marker = mapFragment.getMap().addMarker(markerOpts);
                }
            }
        });

    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
    };

    @Override
    protected void onPause(){
        super.onPause();
        Log.i(TAG, "THIS IS IN ONPAUSE");
        paused = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume(){
        super.onResume();
        /*
        if(paused) {
            //setContentView(R.layout.home);

            Button newPostButton = (Button) findViewById(R.id.camera_button);
            newPostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Home.this, CreatePost.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent, 11);
                }
            });

            locationRequest = LocationRequest.create();

            // Set up the map fragment
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
            mapFragment.getMapAsync(this);
            Log.i(TAG, "THIS IS IN ONRESUME");
            paused = false;
        }
        */
    }

    @Override
    protected void onPostResume(){
        super.onPostResume();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
