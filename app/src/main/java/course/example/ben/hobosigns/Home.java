package course.example.ben.hobosigns;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

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
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
public class Home extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    // The Map Object
    private GoogleMap mMap;
    private HashMap<Marker, Bitmap> markerToBitmap = new HashMap<Marker, Bitmap>();

    private final double COLLEGE_PARK_LATITUDE = 38.9967;
    private final double COLLEGE_PARK_LONGITUDE = -76.9275;

    private SupportMapFragment mapFragment;
    private LocationRequest locationRequest;
    private String TAG = "Testing HOME";
    private ParseGeoPoint geoPoint;
    private double longitude;
    private double latitude;
    private boolean paused = false;
    private ParseFile photoFile;

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
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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
        Log.i(TAG, "TESTING - REQUESTCODE: " + requestCode + "RESULTCODE: " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "FILEPATH: " + data.getStringExtra("filePath"));
                Bitmap bm = BitmapFactory.decodeFile(data.getStringExtra("filePath"));
                Marker markerToAdd = mMap.addMarker(new MarkerOptions().position(generate()).title("generate"));
                markerToBitmap.put(markerToAdd, bm);
                submitSignToParse(data.getStringExtra("filePath"));
            }
        }
    }

    private void submitSignToParse(String filePath) {
        Bitmap photo = BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        photoFile = new ParseFile("hobosign.png", byteArray);
        photoFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.i(TAG, "Image failed to be saved!");
                    Toast.makeText(getApplicationContext(), "Sign image failed to be saved", Toast.LENGTH_LONG).show();
                } else {
                    HoboSignsPost newPost = new HoboSignsPost();
                    newPost.setImageFile(photoFile);
                    newPost.setUser(ParseUser.getCurrentUser());
                    newPost.setLocation(geoPoint);
                    newPost.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.i(TAG, "ParseObject failed to submit!");
                                Toast.makeText(getApplicationContext(), "Sign failed to submit", Toast.LENGTH_LONG).show();
                            } else {
                                Log.i(TAG, "ParseObject submitted!!");
                                Toast.makeText(getApplicationContext(), "Sign submitted!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
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

    private void logOut() {
        ParseUser.logOut();
        Intent intent = new Intent(Home.this, WelcomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private LatLng generate() {
        Random rn = new Random();
        float xDis = rn.nextFloat() / 100;
        float yDis = rn.nextFloat() / 100;
        Log.i(TAG, "GEN X: " + xDis);
        Log.i(TAG, "GEN Y: " + yDis);
        return new LatLng(38.99 + xDis, -76.95 + yDis);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        map.setOnMarkerClickListener(this);

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

        geoPoint = new ParseGeoPoint(latitude, longitude);


        LatLng collegePark = new LatLng(latitude, longitude);
        LatLng test = new LatLng(38.99, -76.82);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.testing1);
        //BitmapDescriptor icon1 = BitmapDescriptorFactory.fromResource(R.drawable.test1);
        Marker collegeParkMarker = map.addMarker(new MarkerOptions().position(collegePark).title("Test Marker"));//.icon(icon));
        markerToBitmap.put(collegeParkMarker, bm);
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
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "THIS IS IN ONPAUSE");
        paused = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
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

    public boolean onMarkerClick(final Marker marker) {
        Intent intent = new Intent(Home.this, HoboSignsView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.i(TAG, markerToBitmap.get(marker).toString());

        // Store image in memory before starting new activity to view
        intent.putExtra("bitmap", saveDrawing(markerToBitmap.get(marker)));
        startActivity(intent);
        return true;
    }

    // Code from https://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media
    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        return Uri.fromFile(getPhotoFile(fileName));
    }

    public File getPhotoFile(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            return new File(mediaStorageDir.getPath() + File.separator + fileName);
        }
        return null;
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    private String saveDrawing(Bitmap image) {
        Log.i(TAG, "Posting drawing!");
        FileOutputStream imageOutputStream;

        try {
            imageOutputStream = new FileOutputStream(getPhotoFile("tempSignImage"));
            image.compress(Bitmap.CompressFormat.PNG, 100, imageOutputStream);
            return getPhotoFileUri("tempSignImage").getPath();
        } catch (NullPointerException e) {
            Toast.makeText(this, "Image could not be saved", Toast.LENGTH_LONG);
            Log.d(TAG, "NullPointerException");
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Image could not be saved", Toast.LENGTH_LONG);
            Log.d(TAG, "FileNotFoundException");
        } catch (IOException e) {
            Toast.makeText(this, "Image could not be saved", Toast.LENGTH_LONG);
            Log.d(TAG, "IOException");
        }

        return null;
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
