package com.example.khan.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private FaceOverlayView mFaceOverlayView1;
    private FaceOverlayView mFaceOverlayView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFaceOverlayView1 = (FaceOverlayView) findViewById( R.id.pout_yes );

        InputStream stream = getResources().openRawResource( R.raw.pout_no );
        Bitmap bitmap = BitmapFactory.decodeStream(stream);

        mFaceOverlayView1.setBitmap(bitmap);


        mFaceOverlayView2 = (FaceOverlayView) findViewById( R.id.pout_no );

        stream = getResources().openRawResource( R.raw.pout_no );
        bitmap = BitmapFactory.decodeStream(stream);

        mFaceOverlayView2.setBitmap(bitmap);

    }


}
