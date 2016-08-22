package com.example.c_heo.opencvintegration;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class BinarizationActivity extends AppCompatActivity {
    private static final String TAG = BinarizationActivity.class.getSimpleName();

    static {
        if(!OpenCVLoader.initDebug()) {
            Log.i(TAG, "OpenCV initialization failed");
        } else {
            Log.i(TAG, "OpenCV initialization successful");
        }
    }

    private SeekBar mSeekBar;

    private ImageView mImageView;
    private Bitmap mOriginImage;
    private Bitmap mInputImage;

    private int _threshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_binarization);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        if(mSeekBar != null) {
            mSeekBar.setMax(255);
            mSeekBar.setProgress(0);
            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    _threshold = progress;

                    doBinarize(_threshold, mInputImage);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        }


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

        mOriginImage = BitmapFactory.decodeResource(getResources(), R.drawable.test_0562fbv);
        mInputImage = mOriginImage.copy(Bitmap.Config.ARGB_8888, true);

        mImageView.setImageBitmap( mOriginImage );
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


    /**
     * @param src :
     * @param thresholdValue : CV_8U 画像の場合，0 から 255
     *                            CV_16U 画像の場合，0 から 65535
     *                            CV_32F 画像の場合，0 から 1
     * @return :
     */
    private Mat binarize(Mat src, double thresholdValue) {
        Mat grayed = new Mat(src.size(), CvType.CV_8UC1);
        Mat bin = new Mat(src.size(), CvType.CV_8UC1);

        Imgproc.cvtColor(src, grayed, Imgproc.COLOR_RGB2GRAY);//COLOR_BGR2GRAY);

        /* 濃淡画像から2値に変換(大津) */
        Imgproc.threshold(grayed, bin, thresholdValue, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        grayed.release();
        return bin;
    }

    private Mat bitMapToMatEx(Bitmap bitmap) {
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap.copy(Bitmap.Config.ARGB_8888, true), mat);
        return mat;
    }

    private void doBinarize(int threshold, Bitmap bm) {
        Mat src = bitMapToMatEx(bm);
        Mat bined = binarize(src, threshold);
        // Bitmapに変換する前にRGB形式に変換
        Imgproc.cvtColor(bined, bined, Imgproc.COLOR_GRAY2RGBA, 4);
        //  Bitmap dst に空のBitmapを作成
//        Bitmap bitmap = Bitmap.createBitmap(bined.width(), bined.height(), Bitmap.Config.ARGB_8888);
        mInputImage = Bitmap.createBitmap(bined.width(), bined.height(), Bitmap.Config.ARGB_8888);
        //  MatからBitmapに変換
//        Utils.matToBitmap(bined, bitmap);
        Utils.matToBitmap(bined, mInputImage);

        //Viewにセット
//        mImageView.setImageBitmap( bitmap );
        mImageView.setImageBitmap( mInputImage );
        src.release();
        bined.release();
    }
}
