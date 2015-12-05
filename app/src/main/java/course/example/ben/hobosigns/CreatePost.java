package course.example.ben.hobosigns;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.File;

/**
 * Created by Ben on 11/19/2015.
 */
public class CreatePost extends AppCompatActivity {

//    ImageView iv;
    private final String APP_TAG = "CityCipher";
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private final String imageName = "tempImage.jpg";
    private String imagePath;

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

        imageView = (ImageView) findViewById(R.id.imageView);

        Button cameraButton = (Button) findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent,0);
            }
        });

        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(imageName));
        startActivityForResult(intent, CAMERA_REQUEST);
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
//            Bitmap photo = (Bitmap) data.getExtras().get("data");

//            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            // Limit the file size since 5MP pictures will kill you RAM
//            bitmapOptions.inSampleSize = 6;
            Bitmap photo = BitmapFactory.decodeFile(getPhotoFileUri(imageName).getPath());

            // Rotate Bitmap if it was taken landscape
            if (photo.getHeight() < photo.getWidth()) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);

                Bitmap tempPhoto = photo;
                photo = Bitmap.createBitmap(tempPhoto, 0, 0, tempPhoto.getWidth(), tempPhoto.getHeight(), matrix, true);
            }

            // Load the taken image into a preview
            imageView.setImageBitmap(photo);
        } else {
            Toast.makeText(getApplicationContext(), "Image was not captured", Toast.LENGTH_SHORT);
        }
    }

    // Code from https://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media
    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
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
