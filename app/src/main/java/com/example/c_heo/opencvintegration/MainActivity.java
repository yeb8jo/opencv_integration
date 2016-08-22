package com.example.c_heo.opencvintegration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String TAG = MainActivity.class.getSimpleName();

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button)findViewById(R.id.camera_test)).setOnClickListener(this);
        ((Button)findViewById(R.id.argb_test)).setOnClickListener(this);
        ((Button)findViewById(R.id.binarization_test)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_test :
                intent = new Intent(this, CameraActivity.class);
                startActivity(intent);
                break;

            case R.id.argb_test :
                intent = new Intent(this, ARGBActivity.class);
                startActivity(intent);
                break;

            case R.id.binarization_test :
                intent = new Intent(this, BinarizationActivity.class);
                startActivity(intent);
                break;
        }
    }
}
