package course.example.ben.hobosigns;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

/**
 * Created by Ben on 11/19/2015.
 */
public class CreatePost extends AppCompatActivity {

//    ImageView iv;
    private final String APP_TAG = "CityCipher";
    private static final int CAMERA_REQUEST = 1888;
    private final String imageName = "tempImage.jpg";

    DrawingView dv;
    private Paint mPaint;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_draw, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.post:
                String filePath = postDrawing();
                Intent data = new Intent();
                data.putExtra("filePath", filePath);
                setResult(Activity.RESULT_OK, data);
                finish();
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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(imageName));
        startActivityForResult(intent, CAMERA_REQUEST);

        dv = new DrawingView(this);
        setContentView(dv);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }

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
                matrix.postRotate(-90);

                Bitmap tempPhoto = photo;
                photo = Bitmap.createBitmap(tempPhoto, 0, 0, tempPhoto.getWidth(), tempPhoto.getHeight(), matrix, true);
            }

            // Load the taken image into a preview
            dv.setmBitmap(photo);
        } else {
            Toast.makeText(getApplicationContext(), "Image was not captured", Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
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
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
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

    private String postDrawing() {
        Log.i(APP_TAG, "Posting drawing!");
        FileOutputStream imageOutputStream;

        try {
            imageOutputStream = new FileOutputStream(getPhotoFile(imageName));
            Bitmap image = dv.saveImage();
            image.compress(Bitmap.CompressFormat.PNG, 100, imageOutputStream);
            return getPhotoFileUri(imageName).getPath();
        } catch (NullPointerException e) {
            Toast.makeText(this, "Image could not be saved", Toast.LENGTH_LONG);
            Log.d(APP_TAG, "NullPointerException");
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Image could not be saved", Toast.LENGTH_LONG);
            Log.d(APP_TAG, "FileNotFoundException");
        } catch (IOException e) {
            Toast.makeText(this, "Image could not be saved", Toast.LENGTH_LONG);
            Log.d(APP_TAG, "IOException");
        }

        return null;
    }

    // Code adapted from http://stackoverflow.com/questions/16650419/draw-in-canvas-by-finger-android
    public class DrawingView extends View {
        private Bitmap  mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint   mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context=c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);
        }

        public DrawingView(Context c, Bitmap b) {
            super(c);
            context=c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);
            mBitmap = b;
        }

        void setmBitmap(Bitmap b) {
            mBitmap = b;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            if (mBitmap != null) {
                mBitmap = Bitmap.createScaledBitmap(mBitmap, w, h, true);
            } else {
                mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            }
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap( mBitmap, 0, 0, null);
            canvas.drawPath( mPath,  mPaint);
            canvas.drawPath( circlePath,  circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath,  mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }

        Bitmap saveImage() {
            this.setDrawingCacheEnabled(true);
            this.buildDrawingCache();
            Bitmap image = Bitmap.createBitmap(this.getDrawingCache());
            this.setDrawingCacheEnabled(false);

            return image;
        }
    }

}
