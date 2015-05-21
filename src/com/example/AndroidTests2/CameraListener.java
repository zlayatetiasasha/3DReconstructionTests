package com.example.AndroidTests2;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import com.example.AndroidTests2.obj.AccelVectorPath;
import com.example.AndroidTests2.obj.CameraFrame;
import com.example.AndroidTests2.obj.CameraMatFrame;
import com.example.AndroidTests2.obj.GyroVectorAngle;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.*;

/**
 * Created by Sa User on 21.03.2015.
 */
public class CameraListener implements CameraBridgeViewBase.CvCameraViewListener2 {


    public CameraListener() {
        frames = new ArrayList<CameraMatFrame>();
    }

    public void onCameraViewStarted(int width, int height) {
        Log.i("Resolution was= ", MyActivity.mOpenCvCameraView.getResolution().width + "x" + MyActivity.mOpenCvCameraView.getResolution().height);
        List<Camera.Size> resolutionList = MyActivity.mOpenCvCameraView.getResolutionList();
        Iterator it = resolutionList.iterator();
        while (it.hasNext()) {
            Camera.Size s = (Camera.Size) it.next();
            if ((s.width == 640 && s.height == 480) || (s.height == 640 && s.width == 480)) {
                //   if ((s.width == 1280 && s.height == 720) || (s.height == 1280 && s.width == 720)) {
                MyActivity.mOpenCvCameraView.setResolution(s);
                break;
            }
        }
    }

    public void onCameraViewStopped() {

    }

    public static Communication client;

    public static Stack<CameraFrame> stack = new Stack<>();
    public static AccelVectorPath previousPath;

    // public static CameraFrame currentFrame;
    public static CameraMatFrame currentFrame;
    //   public static Collection<CameraFrame> frames = new ArrayList<CameraFrame>();
    public static Collection<CameraMatFrame> frames = new ArrayList<CameraMatFrame>();
    boolean first = true;
    int i = 0;

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        if (!BluetoothComm.connected) {
            i++;
            Mat mRgba = inputFrame.rgba();
            if (i % 5 == 0) {
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                String filename = "calibrateTest" + (i / 5) + ".jpg";
                File file = new File(path, filename);

                //   Highgui.imwrite(file.toString(), mRgba);

                //  currentFrame = new CameraMatFrame(mRgba, MyActivity.currentAngle, MyActivity.currentPath);
                // frames.add(currentFrame);
            }
            return inputFrame.rgba();
            //waiting when bluetooth will connect
        }
        Mat mRgba = inputFrame.rgba();
        //  Mat mRgbaT = mRgba.t();
        //  Core.flip(mRgba.t(), mRgbaT, 1);
        //  Imgproc.resize(mRgbaT, mRgbaT, mRgba.size());

        if (mRgba.rows() == 0 || mRgba.cols() == 0) {
            return mRgba;
        }

        if (first) {
            first = false;
            return mRgba;
        }


        if (frames.size() < 50) {


            //     MatOfByte buf=new MatOfByte();
            //    Highgui.imencode(".jpg", mRgba, buf);
            //   byte[] bufferByte = buf.toArray();

            //   currentFrame = new CameraFrame(/*CameraFrame.matToJson(mRgba)*/ bufferByte, MyActivity.currentAngle);
            //    currentFrame.mat = mRgba;
            //
            //     frames.add(currentFrame);
            //      currentFrame.frameAngles = (ArrayList<GyroVectorAngle>) MyActivity.frameAngles.clone();

            //  currentFrame = new CameraMatFrame(mRgba, MyActivity.currentAngle, MyActivity.currentPath);

            if(i%3==0) {
                currentFrame = new CameraMatFrame(mRgba, MyActivity.currentRotation, MyActivity.currentPath);
                currentFrame.frameAngles = (ArrayList<GyroVectorAngle>) MyActivity.frameAngles.clone();
                frames.add(currentFrame);
                previousPath = MyActivity.currentPath;
            }

            i++;
        }
        //     if(frames.size()==2){
        //        client.transmitArray(frames);
        //        frames.add(currentFrame);
        //     }

        return mRgba;
    }
}
