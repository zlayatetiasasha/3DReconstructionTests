package com.example.AndroidTests2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

/**
 * Created with IntelliJ IDEA.
 * User: Alexandra Malakhova
 * Date: 01.03.14
 * Time: 18:32
 */
public class ActivityChoose extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   setContentView(R.layout.opencvtest);
     //   setContentView(R.layout.camera);
        setContentView(R.layout.mainchoose);
    }
public static Intent intent;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    //Log.i(TAG, "OpenCV loaded successfully");
                  //  mOpenCvCameraView.enableView();
                    MyActivity.clientType = MyActivity.CLIENT_BLUETOOTH;
                    startActivity(intent);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    public void clickBluetooth(View v) {
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
        intent = new Intent(this, MyActivity.class);

    }

    public void clickSocket(View v) {
        MyActivity.clientType = MyActivity.CLIENT_SOCKET;
        Intent intent = new Intent(this, MyActivity.class);
        startActivity(intent);
    }




}
