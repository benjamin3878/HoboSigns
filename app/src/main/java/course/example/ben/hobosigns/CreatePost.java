package course.example.ben.hobosigns;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.ParseUser;

/**
 * Created by Ben on 11/19/2015.
 */
public class CreatePost extends AppCompatActivity {

//    ImageView iv;
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_post);

        Log.i("createPost", "in OnCreate");
        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
//        startActivity(new Intent(this, CameraFragment.class));

//        Intent showContent = new Intent(getApplicationContext(),
//                CameraFragment.class);
//        startActivity(showContent);




        Button cameraButton = (Button) findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent,0);
            }
        });
    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        Bitmap bp = (Bitmap) data.getExtras().get("data");
//        iv.setImageBitmap(bp);
//    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("createPost", "in OnActivityResult");
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }


    private void logOut(){
        ParseUser.logOut();
        Intent intent = new Intent(CreatePost.this, WelcomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

//    private void releaseCameraAndPreview() {
//        myCameraPreview.setCamera(null);
//        if (mCamera != null) {
//            mCamera.release();
//            mCamera = null;
//        }


}
