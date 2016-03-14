package com.example.khan.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;


/**
 * Created by Khan on 12/03/2016.
 */
public class FaceOverlayView extends View {

    SharedPreferences sharedPreferences;

    private static final String TAG = "shahjahan";
    private Bitmap mBitmap;
    private SparseArray mFaces;

    public FaceOverlayView(Context context) {
        this(context, null);
        sharedPreferences = context.getSharedPreferences("APPLICATION",Context.MODE_PRIVATE);
    }

    public FaceOverlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBitmap( Bitmap bitmap ) {
        mBitmap = bitmap;
        FaceDetector detector = new FaceDetector.Builder( getContext() )
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();

        if (!detector.isOperational()) {
            //Handle contingency
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            mFaces = detector.detect(frame);
            detector.release();
        }
        invalidate();

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if ((mBitmap != null) && (mFaces != null)) {
            double scale = drawBitmap(canvas);
            drawFaceBox(canvas, scale);
            drawFaceLandmarks(canvas,scale);
        }
    }

    private double drawBitmap( Canvas canvas ) {
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = mBitmap.getWidth();
        double imageHeight = mBitmap.getHeight();
        double scale = Math.min( viewWidth / imageWidth, viewHeight / imageHeight );

        Rect destBounds = new Rect( 0, 0, (int) ( imageWidth * scale ), (int) ( imageHeight * scale ) );
        canvas.drawBitmap(mBitmap, null, destBounds, null);
        return scale;
    }

    private void drawFaceBox(Canvas canvas, double scale) {
        //paint should be defined as a member variable rather than
        //being created on each onDraw request, but left here for
        //emphasis.
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        float left = 0;
        float top = 0;
        float right = 0;
        float bottom = 0;

        for( int i = 0; i < mFaces.size(); i++ ) {
            Face face = (Face) mFaces.valueAt(i);

            left = (float) ( face.getPosition().x * scale );
            top = (float) ( face.getPosition().y * scale );
            right = (float) scale * ( face.getPosition().x + face.getWidth() );
            bottom = (float) scale * ( face.getPosition().y + face.getHeight() );

            canvas.drawRect( left, top, right, bottom, paint );
        }
    }

    private void drawFaceLandmarks( Canvas canvas, double scale ) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth( 5 );

//        int mouth_width,mouth_height,cheek_width;

        int left_mouth_x = 0,right_mouth_x= 0,left_cheek_x= 0,right_cheek_x= 0,bottom_mouth_y= 0,nose_base_y= 0;

        for( int i = 0; i < mFaces.size(); i++ ) {
            Face face = (Face) mFaces.valueAt(i);

            for ( Landmark landmark : face.getLandmarks() ) {

                int cx = (int) ( landmark.getPosition().x * scale );
                int cy = (int) ( landmark.getPosition().y * scale );

                switch (landmark.getType())
                {
                    case 0:
                        Log.d(TAG, "bottom lip: x:" + cx + " y:" + cy);
                        canvas.drawCircle(cx, cy, 10, paint );
                        bottom_mouth_y = cy;
                        break;
                    case 1:
                        Log.d(TAG,"Left cheek: x:" + cx + " y:" + cy);
                        canvas.drawCircle(cx, cy, 10, paint);
                        left_cheek_x = cx;
                        break;
                    case 5:
                        Log.d(TAG,"Left mouth: x:" + cx + " y:" + cy);
                        canvas.drawCircle(cx, cy, 10, paint);
                        left_mouth_x = cx;
                        break;
                    case 6:
                        Log.d(TAG,"Nose center: x:" + cx + " y:" + cy);
                        canvas.drawCircle(cx, cy, 10, paint );
                        nose_base_y = cy;
                        break;
                    case 7:
                        Log.d(TAG,"Right Cheek: x:" + cx + " y:" + cy);
                        canvas.drawCircle(cx, cy, 10, paint );
                        right_cheek_x = cx;
                        break;
                    case 11:
                        Log.d(TAG,"Right mouth: x:" + cx + " y:" + cy);
                        canvas.drawCircle(cx, cy, 10, paint);
                        right_mouth_x = cx;
                        break;
                    default:
                        break;
                }

            }

        }

        Log.d(TAG," the mouth legth is: " + ( left_mouth_x - right_mouth_x));
        Log.d(TAG," the cheek legth is: " + ( left_cheek_x - right_cheek_x));
        Log.d(TAG," the mouth height is: " + ( bottom_mouth_y - nose_base_y));
    }
}
