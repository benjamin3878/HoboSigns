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
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import android.app.Dialog;
import android.graphics.ColorMatrix;
import android.graphics.Shader;
import android.graphics.RectF;
import android.graphics.SweepGradient;


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
    ColorPickerDialog dialog;

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

        dialog = new ColorPickerDialog(CreatePost.this, new OnColorChangedListener(), 0);
        //dialog.show();

        Button colorPickButton = (Button) findViewById(R.id.camera_button);
        colorPickButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                dialog.show();
            }
        });

        Log.i("createPost", "in OnCreate");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(imageName));
        startActivityForResult(intent, CAMERA_REQUEST);


//        dialog.show();

//        dv = new DrawingView(this);
        dv = (DrawingView) findViewById(R.id.drawingView);
//        setContentView(dv);
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
            Toast.makeText(getApplicationContext(), "Image was not captured", Toast.LENGTH_SHORT);
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
            init(c);
        }

        public DrawingView(Context c, AttributeSet attrs) {
            super(c, attrs);
            init(c);
        }

        public DrawingView(Context c, AttributeSet attrs, int defStyle) {
            super(c, attrs, defStyle);
            init(c);
        }

        void setmBitmap(Bitmap b) {
            mBitmap = b;
        }

        private void init(Context c) {
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

            canvas.drawBitmap(mBitmap, 0, 0, null);
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


    public class OnColorChangedListener {

        void colorChanged(int color){
            mPaint.setColor(color);
        };
    }



    public class ColorPickerDialog extends Dialog{

        private OnColorChangedListener mListener;
        private int mInitialColor;

        private class ColorPickerView extends View {
            private Paint mPaint;
            private Paint mCenterPaint;
            private final int[] mColors;
            private OnColorChangedListener mListener;

            ColorPickerView(Context c, OnColorChangedListener l, int color) {
                super(c);
                mListener = l;
                mColors = new int[] {
                        0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00,
                        0xFFFFFF00, 0xFFFF0000
                };
                Shader s = new SweepGradient(0, 0, mColors, null);

                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mPaint.setShader(s);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(32);

                mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mCenterPaint.setColor(color);
                mCenterPaint.setStrokeWidth(5);
            }

            private boolean mTrackingCenter;
            private boolean mHighlightCenter;

            @Override
            protected void onDraw(Canvas canvas) {
                float r = CENTER_X - mPaint.getStrokeWidth()*0.5f;

                canvas.translate(CENTER_X, CENTER_X);

                canvas.drawOval(new RectF(-r, -r, r, r), mPaint);
                canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);

                if (mTrackingCenter) {
                    int c = mCenterPaint.getColor();
                    mCenterPaint.setStyle(Paint.Style.STROKE);

                    if (mHighlightCenter) {
                        mCenterPaint.setAlpha(0xFF);
                    } else {
                        mCenterPaint.setAlpha(0x80);
                    }
                    canvas.drawCircle(0, 0,
                            CENTER_RADIUS + mCenterPaint.getStrokeWidth(),
                            mCenterPaint);

                    mCenterPaint.setStyle(Paint.Style.FILL);
                    mCenterPaint.setColor(c);
                }
            }

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                setMeasuredDimension(CENTER_X*2, CENTER_Y*2);
            }

            private static final int CENTER_X = 100;
            private static final int CENTER_Y = 100;
            private static final int CENTER_RADIUS = 32;

            private int floatToByte(float x) {
                int n = java.lang.Math.round(x);
                return n;
            }

            private int pinToByte(int n) {
                if (n < 0) {
                    n = 0;
                } else if (n > 255) {
                    n = 255;
                }
                return n;
            }

            private int ave(int s, int d, float p) {
                return s + java.lang.Math.round(p * (d - s));
            }

            private int interpColor(int colors[], float unit) {
                if (unit <= 0) {
                    return colors[0];
                }
                if (unit >= 1) {
                    return colors[colors.length - 1];
                }

                float p = unit * (colors.length - 1);
                int i = (int)p;
                p -= i;

                // now p is just the fractional part [0...1) and i is the index
                int c0 = colors[i];
                int c1 = colors[i+1];
                int a = ave(Color.alpha(c0), Color.alpha(c1), p);
                int r = ave(Color.red(c0), Color.red(c1), p);
                int g = ave(Color.green(c0), Color.green(c1), p);
                int b = ave(Color.blue(c0), Color.blue(c1), p);

                return Color.argb(a, r, g, b);
            }

            private int rotateColor(int color, float rad) {
                float deg = rad * 180 / 3.1415927f;
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);

                ColorMatrix cm = new ColorMatrix();
                ColorMatrix tmp = new ColorMatrix();

                cm.setRGB2YUV();
                tmp.setRotate(0, deg);
                cm.postConcat(tmp);
                tmp.setYUV2RGB();
                cm.postConcat(tmp);

                final float[] a = cm.getArray();

                int ir = floatToByte(a[0] * r +  a[1] * g +  a[2] * b);
                int ig = floatToByte(a[5] * r +  a[6] * g +  a[7] * b);
                int ib = floatToByte(a[10] * r + a[11] * g + a[12] * b);

                return Color.argb(Color.alpha(color), pinToByte(ir),
                        pinToByte(ig), pinToByte(ib));
            }

            private static final float PI = 3.1415926f;

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                float x = event.getX() - CENTER_X;
                float y = event.getY() - CENTER_Y;
                boolean inCenter = java.lang.Math.sqrt(x*x + y*y) <= CENTER_RADIUS;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTrackingCenter = inCenter;
                        if (inCenter) {
                            mHighlightCenter = true;
                            invalidate();
                            break;
                        }
                    case MotionEvent.ACTION_MOVE:
                        if (mTrackingCenter) {
                            if (mHighlightCenter != inCenter) {
                                mHighlightCenter = inCenter;
                                invalidate();
                            }
                        } else {
                            float angle = (float)java.lang.Math.atan2(y, x);
                            // need to turn angle [-PI ... PI] into unit [0....1]
                            float unit = angle/(2*PI);
                            if (unit < 0) {
                                unit += 1;
                            }
                            mCenterPaint.setColor(interpColor(mColors, unit));
                            invalidate();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mTrackingCenter) {
                            if (inCenter) {
                                mListener.colorChanged(mCenterPaint.getColor());
                            }
                            mTrackingCenter = false;    // so we draw w/o halo
                            invalidate();
                        }
                        break;
                }
                return true;
            }
        }

        public ColorPickerDialog(Context context,
                                 OnColorChangedListener listener,
                                 int initialColor) {
            super(context);

            mListener = listener;
            mInitialColor = initialColor;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            OnColorChangedListener l = new OnColorChangedListener() {
                public void colorChanged(int color) {
                    mListener.colorChanged(color);
                    dismiss();
                }
            };

            setContentView(new ColorPickerView(getContext(), l, mInitialColor));
            setTitle("Pick a Color");
        }
    }


}
