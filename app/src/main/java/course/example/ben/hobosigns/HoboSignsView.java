package course.example.ben.hobosigns;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

public class HoboSignsView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobo_signs_view);
        Intent intent = getIntent();

        ParseQuery<HoboSignsPost> mapQuery = HoboSignsPost.getQuery();
        HoboSignsPost clickedPost = new HoboSignsPost();
        try {
            clickedPost = mapQuery.get(intent.getStringExtra("bitmap"));
        } catch(ParseException e) {
            return;
        }
        ParseFile file = clickedPost.getImageFile();
        byte[] bitMapData = new byte[100];
        try {
            bitMapData = file.getData();
        } catch (ParseException p) {
            return;
        }
        Bitmap photo = BitmapFactory.decodeByteArray(bitMapData, 0, bitMapData.length);


        Log.i("TEST MAP", photo.toString());
                BitmapDrawable ob = new BitmapDrawable(getResources(), photo);
        ImageView imageView =  (ImageView)findViewById(R.id.sign);
        imageView.setBackground(ob);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hobo_signs_view, menu);
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
}
