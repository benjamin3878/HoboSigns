package course.example.ben.hobosigns;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private String TAG = "Testing MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "THIS IS IN ONCREATE");
        // The 3 lines below are to initialize Parse
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(HoboSignsPost.class);
        Parse.initialize(this, "icLRaWe8n7wy46STg3LubL3EliVC56yGYJF4lqgZ",
                "29QxzODPy3epUmInINvCBSvwwbvj4PdQx2bcefKS");

        // Check if user is logged in
        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
            Intent i = new Intent(this, Home.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }else{
            startActivity(new Intent(this, WelcomeScreen.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "THIS IS IN ONRESUME");
        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
            Intent i = new Intent(this, Home.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } else {
            startActivity(new Intent(this, WelcomeScreen.class));
        }

    }

}
