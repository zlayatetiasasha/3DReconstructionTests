package com.example.AndroidTests2.obj;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import com.example.AndroidTests2.SerializationUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Sa User on 04.04.2015.
 */
public class CameraMatFrame {

    public transient Mat mat;

    public GyroVectorAngle angle; //angle when camera frame is ready and new is beeing captured
    public AccelVectorPath path; //path when camera frame is ready and new is beeing captured
    public GyroVectorAngle previousAngle;
    public ArrayList<GyroVectorAngle> frameAngles;
    public float[][] currentRotation;

    public CameraMatFrame(Mat mat, GyroVectorAngle angle, AccelVectorPath path) {
        this.mat = mat.clone();
        this.previousAngle = this.angle;
        this.angle = angle;
        this.path = path;
    }

    public CameraMatFrame(Mat mat, float[][] currentRotation, AccelVectorPath path) {
        this.mat = mat.clone();
        this.previousAngle = this.angle;

        this.currentRotation=currentRotation;
        this.path = path;
    }


    public CameraFrame toCameraFrame() {
        Mat m = this.mat;
        MatOfByte buf = new MatOfByte();
        Highgui.imencode(".jpg", m, buf);
        byte[] bufferByte = buf.toArray();
        CameraFrame fr=new CameraFrame(bufferByte, this.currentRotation, this.path);
        //CameraFrame fr = new CameraFrame(bufferByte, this.angle, this.path);
        fr.frameAngles = this.frameAngles;
        return fr;

    }

    public Bitmap matToBitmap(Mat tmp) {
        Bitmap bmp = null;

        try {
            //Imgproc.cvtColor(seedsImage, tmp, Imgproc.COLOR_RGB2BGRA);
            bmp = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(tmp, bmp);
            return bmp;
        } catch (CvException e) {
            Log.d("Exception", e.getMessage());
        }
        return null;
    }

    public static String matToJson(Mat mat) {
        JsonObject obj = new JsonObject();

        if (mat.isContinuous()) {
            int cols = mat.cols();
            int rows = mat.rows();
            int elemSize = (int) mat.elemSize();
            int type = mat.type();

            obj.addProperty("rows", rows);
            obj.addProperty("cols", cols);
            obj.addProperty("type", type);

            // We cannot set binary data to a json object, so:
            // Encoding data byte array to Base64.
            String dataString = "";

            //  if (type == CvType.CV_32S || type == CvType.CV_32SC2 || type == CvType.CV_32SC3 || type == CvType.CV_16S) {
            //       int[] data = new int[cols * rows * elemSize];
            //         mat.get(0, 0, data);
            //    dataString = new String(Base64.encode(SerializationUtils.toByteArray(data), Base64.DEFAULT));
            //    } else if (type == CvType.CV_32F || type == CvType.CV_32FC2) {
            //       float[] data1 = new float[cols * rows * elemSize];
            //      mat.get(0, 0, data1);
            //      dataString = new String(Base64.encode(SerializationUtils.toByteArray(data1), Base64.DEFAULT));
            //    } else if (type == CvType.CV_64F || type == CvType.CV_64FC2) {
            //      double[] data2 = new double[cols * rows * elemSize];
            //       mat.get(0, 0, data2);
            //        dataString = new String(Base64.encode(SerializationUtils.toByteArray(data2), Base64.DEFAULT));
            if (type == CvType.CV_8UC4) {
                byte[] data3 = new byte[cols * rows * elemSize];
                mat.get(0, 0, data3);
                dataString = new String(Base64.encode(data3, Base64.DEFAULT));
            }
            //   else {

            //      throw new UnsupportedOperationException("unknown type");
            //  }
            obj.addProperty("data", dataString);

            Gson gson = new Gson();
            String json = gson.toJson(obj);

            return json;
        } else {
            System.out.println("Mat not continuous.");
        }
        return "{}";
    }

    public static Mat matFromJson(String json) {


        JsonParser parser = new JsonParser();
        JsonObject JsonObject = parser.parse(json).getAsJsonObject();

        int rows = JsonObject.get("rows").getAsInt();
        int cols = JsonObject.get("cols").getAsInt();
        int type = JsonObject.get("type").getAsInt();

        Mat mat = new Mat(rows, cols, type);

        String dataString = JsonObject.get("data").getAsString();
        if (type == CvType.CV_32S || type == CvType.CV_32SC2 || type == CvType.CV_32SC3 || type == CvType.CV_16S) {
            int[] data = SerializationUtils.toIntArray(Base64.decode(dataString.getBytes(), Base64.DEFAULT));
            mat.put(0, 0, data);
        } else if (type == CvType.CV_32F || type == CvType.CV_32FC2) {
            float[] data = SerializationUtils.toFloatArray(Base64.decode(dataString.getBytes(), Base64.DEFAULT));
            mat.put(0, 0, data);
        } else if (type == CvType.CV_64F || type == CvType.CV_64FC2) {
            double[] data = SerializationUtils.toDoubleArray(Base64.decode(dataString.getBytes(), Base64.DEFAULT));
            mat.put(0, 0, data);
        } else if (type == CvType.CV_8U) {
            byte[] data = Base64.decode(dataString.getBytes(), Base64.DEFAULT);
            mat.put(0, 0, data);
        } else {

            throw new UnsupportedOperationException("unknown type");
        }
        return mat;
    }
}



