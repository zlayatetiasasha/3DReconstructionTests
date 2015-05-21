package com.example.AndroidTests2.test;

import java.io.FileOutputStream;
 import java.util.List;

         import org.opencv.android.JavaCameraView;

 import android.content.Context;
 import android.hardware.Camera;
 import android.hardware.Camera.PictureCallback;
 import android.hardware.Camera.Size;
 import android.util.AttributeSet;
 import android.util.Log;

/**
 * Created by Sa User on 23.04.2015.
 */
public class CameraView extends JavaCameraView {
    public CameraView(Context context, int cameraId) {
        super(context, cameraId);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

  /*  public void setResolution(Camera.Size resolution) {
        disconnectCamera();
        connectCamera((int) resolution.width, (int) resolution.height);

    } */
    public Camera.Size getResolution() {

        Camera.Parameters params = mCamera.getParameters();

        Camera.Size s = params.getPreviewSize();
        return s;
    }

    protected org.opencv.core.Size calculateCameraFrameSize(List<?> supportedSizes, ListItemAccessor accessor, int surfaceWidth, int surfaceHeight) {
      //  return new org.opencv.core.Size(960, 540);
       // return new org.opencv.core.Size(1280, 720);
        return new org.opencv.core.Size(640, 480);
    }

    public void setResolution(android.hardware.Camera.Size resolution) {
               disconnectCamera();
               mMaxHeight = resolution.height;
                mMaxWidth = resolution.width;
               connectCamera(getWidth(), getHeight());
            }
    public List<Size> getResolutionList() {
                 return mCamera.getParameters().getSupportedPreviewSizes();
             }



}
