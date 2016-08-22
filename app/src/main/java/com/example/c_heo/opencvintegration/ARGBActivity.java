package com.example.c_heo.opencvintegration;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Random;

public class ARGBActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = ARGBActivity.class.getSimpleName();

    static {
        if(!OpenCVLoader.initDebug()) {
            Log.i(TAG, "OpenCV initialization failed");
        } else {
            Log.i(TAG, "OpenCV initialization successful");
        }
    }

    private ImageView mImageView;
    private Bitmap mInputImage;
    private Bitmap mOriginImage;
//    private FeatureDetector mFeatureDetector;

    private SeekBar mSeekBar;

    private enum ModeChange {
        ALPHA,
        RED,
        GREEN,
        BLUE,
    }
    private ModeChange mModeChange;

    private int _alpha, _red, _green, _blue;

    private int[] cars = new int[]{
            R.drawable.test_0562fbv,
            R.drawable.test_2715dtz,
            R.drawable.test_3028bys,
            R.drawable.test_3154ffy,
            R.drawable.test_3266cnt,
            R.drawable.test_3732fww,
            R.drawable.test_4898gxy,
            R.drawable.test_5445bsx,
            R.drawable.test_7215bgn,
            R.drawable.test_8995ccn,
            R.drawable.test_9588dwv,
            R.drawable.test_9773bnb,
    };
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_argb);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        if (mSeekBar != null) {
            mSeekBar.setMax(255);
            mSeekBar.setProgress(0);
            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(mModeChange == ModeChange.ALPHA) {
                        _alpha = progress;
                    } else if(mModeChange == ModeChange.RED) {
                        _red = progress;
                    } else if(mModeChange == ModeChange.GREEN) {
                        _green = progress;
                    } else if(mModeChange == ModeChange.BLUE) {
                        _blue = progress;
                    }

                    shift(_alpha, _red, _green, _blue);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        int len = cars.length;
        int ran = random.nextInt(len);
        int id = cars[ran];

        mOriginImage = BitmapFactory.decodeResource(getResources(), id);
        mImageView = (ImageView) findViewById(R.id.image_view);
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        mImageView.setImageBitmap(mOriginImage);
                        break;

                    case MotionEvent.ACTION_UP :
                        mImageView.setImageBitmap(mInputImage);
                        break;
                }
                return true;
            }
        });
        ((Button)findViewById(R.id.alpha)).setOnClickListener(this);
        ((Button)findViewById(R.id.red)).setOnClickListener(this);
        ((Button)findViewById(R.id.green)).setOnClickListener(this);
        ((Button)findViewById(R.id.blue)).setOnClickListener(this);

//        mFeatureDetector = FeatureDetector.create(FeatureDetector.SIFT);
        grayScale();
    }

    private void grayScale() {
        Mat rgba = new Mat();

        Utils.bitmapToMat(mOriginImage, rgba);
//        MatOfKeyPoint matOfKeyPoint = new MatOfKeyPoint();
        Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGBA2GRAY);
//        mFeatureDetector.detect(rgba, matOfKeyPoint);
//        Features2d.drawKeypoints(rgba, matOfKeyPoint, rgba);
        Utils.matToBitmap(rgba, mOriginImage);
//        Utils.matToBitmap(rgba, mInputImage, true);

        mImageView.setImageBitmap(mOriginImage);
        rgba.release();

        mInputImage = mOriginImage.copy(Bitmap.Config.ARGB_8888, true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alpha :
                mModeChange = ModeChange.ALPHA;
                mSeekBar.setProgress(_alpha);
                break;

            case R.id.red :
                mModeChange = ModeChange.RED;
                mSeekBar.setProgress(_red);
                break;

            case R.id.green :
                mModeChange = ModeChange.GREEN;
                mSeekBar.setProgress(_green);
                break;

            case R.id.blue :
                mModeChange = ModeChange.BLUE;
                mSeekBar.setProgress(_blue);
                break;
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    private void shift(int alpha, int red, int green, int blue) {
        Mat rgba = new Mat();
        Mat rgba2 = new Mat();
        Utils.bitmapToMat(mOriginImage, rgba); // Bitmap -> Mat
        Core.absdiff(rgba, new Scalar(red, green, blue, alpha), rgba2); // ネガポジ変換
        Utils.matToBitmap(rgba2, mInputImage); // Mat -> Bitmap
        mImageView.setImageBitmap(mInputImage); // ImageViewに描画
        rgba.release();
        rgba2.release();

    }


}
