package com.example.AndroidTests2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.*;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.AndroidTests2.obj.AccelVectorPath;
import com.example.AndroidTests2.obj.GyroVectorAngle;
import com.example.AndroidTests2.ShowFramesActivity;
import com.example.AndroidTests2.test.CameraView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import org.opencv.core.Scalar;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import android.hardware.Camera.Size;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Alexandra Malakhova
 * Date: 30.01.14
 * Time: 23:12
 */

public class MyActivity extends Activity {
    final static int CLIENT_BLUETOOTH = 1;
    final static int CLIENT_SOCKET = 2;

    final static int ACCELEROMETER = 1;
    final static int GYROSCOPE = 2;

    final static float NSEC_TO_SEC = 1.0f / 1000000000.0f;

    public SensorManager sensorManager;
    Sensor accelerometer;
    Sensor gyroscope;
    Sensor gravitySensor;
    SensorEventListener listenerAccelerometer, listenerGyroscope, listenerGravity;
    public static TextView textView;
    public static ImageView im;

    BluetoothTransmitter bluetoothTransmitter;
    static int n = 0;
    static Communication client;
    /*to change comm type*/
    public static int clientType = CLIENT_BLUETOOTH;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentAngle = new GyroVectorAngle();
        currentPath = new AccelVectorPath();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.capturevideo);
        // mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        mOpenCvCameraView = (CameraView) findViewById(R.id.HelloOpenCvView);


        // mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_native_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.enableView();
        CameraListener list = new CameraListener();
        mOpenCvCameraView.setCvCameraViewListener(list);

        // setContentView(R.layout.main);
        textView = (TextView) findViewById(R.id.textViewcv);
        textView.setText("BLUETOOTH started! Play!");


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //ускорение без гравитации
        // accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        if (accelerometer == null) {
            showAlertDialog();
        }

        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        // gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (clientType == CLIENT_BLUETOOTH) {
            client = new BluetoothComm();
            list.client = client;
            Thread run = new Thread() {
                @Override
                public void run() {
                    try {
                        client.establishCommunication();
                    } catch (Exception ex) {
                        Log.i(getClass().getName(), ex.getMessage());
                        //MyActivity.textView.setText(ex.getMessage());

                    }

                }
            };
            run.start();

        }
        if (clientType == CLIENT_SOCKET) {
            client = new SocketComm();
            Thread run = new Thread() {
                @Override
                public void run() {
                    try {
                        client.establishCommunication();
                    } catch (Exception ex) {
                    }
                }
            };
            run.start();
        }

    }

    static float[] gravity = new float[]{0f, 9.8f, 0f};
    float[] values = new float[3];

    float[] aOld = new float[3]; //ускорение в предыдущей точке
    float[] a = new float[3]; //ax,ay,az - ускорение
    float[] a0 = new float[3];

    float[] coord = new float[3];//x, y, z- координаты конца отрезка
    float[] coord0 = new float[3];//x0, y0, z0 - координаты начала отрезка

    float[] v = new float[3];
    float[] v0 = new float[3];

    float[] s = new float[3];


    static float t = 0;

    static float t0 = -1;
    static float deltaT = 0;
    static float deltaTFloat = 0F;
    float lastUpdate;
    float actualTime;

    float[] rotateV = new float[3];
    float[] rotateV0 = new float[3];
    float rotateLastUpdate;
    float rotateActualTime;
    float rotateT0;
    float rotateT = 0;
    float rotateDeltaT = -1;
    float[] angle = new float[3];
    float[] angle0 = new float[3];

    static GyroVectorAngle currentAngle = new GyroVectorAngle();
    static AccelVectorPath currentPath = new AccelVectorPath();


    static float sOld_X = 0;
    static float sOld_Y = 0;
    static float sOld_Z = 0;

    boolean first_angle = true;
    boolean first_move = true;
    public static ArrayList<GyroVectorAngle> frameAngles = new ArrayList<GyroVectorAngle>();
    public static ArrayList<AccelVectorPath> framePaths = new ArrayList<AccelVectorPath>();


    public void onResume() {
        super.onResume();
        mOpenCvCameraView.enableView();

        listenerAccelerometer = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                    //  if (BluetoothComm.connected) {
                    actualTime = event.timestamp * NSEC_TO_SEC;//System.currentTimeMillis();

                    // if (actualTime - lastUpdate < 1f) { /*1 секунда*/
                    //Если с момента начала тряски прошло меньше 100
                    // миллисекунд=0.1сек - выходим из обработчика
                    //   return;
                    //  }
                    lastUpdate = actualTime;

                    if (t0 == -1) {
                        t0 = event.timestamp * NSEC_TO_SEC;

                    }

                    if (first_move) {
                        a[0] = 0f;
                        a[1] = 0f;
                        a[2] = 0f;
                        v[0] = 0f;
                        v[1] = 0f;
                        v[2] = 0f;
                        first_move = false;

                    } else {

                        gravity[0] = (float) (0.1f * event.values[0] + 0.9f * gravity[0]);
                        // гравитация в начальном положении для y=9.8 т.к. начальное положение телефона всегда известно
                        gravity[1] = (float) (0.1f * event.values[1] + 0.9f * gravity[1]);
                        gravity[2] = (float) (0.1f * event.values[2] + 0.9f * gravity[2]);


                        a[0] = event.values[0] - gravity[0];
                        a[1] = event.values[1] - gravity[1];
                        a[2] = event.values[2] - gravity[2];


                        //Kalman filter
                        float K = 0.4f;
                        a[0] = K * a[0] + (1 - K) * a0[0];
                        a[1] = K * a[1] + (1 - K) * a0[1];
                        a[2] = K * a[2] + (1 - K) * a0[2];


                        if (Math.abs(a[0]) <= 0.05f)
                            a[0] = 0f;
                        if (Math.abs(a[1]) <= 0.05f)
                            a[1] = 0f;
                        if (Math.abs(a[2]) <= 0.05f)
                            a[2] = 0f;


                        t = event.timestamp * NSEC_TO_SEC;
                        deltaTFloat = t - t0;

                        Mat a0m = new Mat(3, 1, CvType.CV_64FC1);
                        a0m.put(0, 0, a0[0]);
                        a0m.put(1, 0, a0[1]);
                        a0m.put(2, 0, a0[2]);

                        Mat tmpR = new Mat(3, 1, CvType.CV_64FC1);
                        Core.gemm(currentRotation_mat, a0m, 1, Mat.zeros(3, 1, CvType.CV_64FC1), 1, tmpR);
                        a0[0] = (float) tmpR.get(0, 0)[0];
                        a0[1] = (float) tmpR.get(1, 0)[0];
                        a0[2] = (float) tmpR.get(2, 0)[0];


                        v[0] = a0[0] * deltaTFloat;
                        v[1] = a0[1] * deltaTFloat;
                        v[2] = a0[2] * deltaTFloat;



                        s[0] = v0[0] * deltaTFloat + (float) ((float) 1.0 / (float) 2.0) * a0[0] * deltaTFloat * deltaTFloat;
                        s[1] = v0[1] * deltaTFloat + (float) ((float) 1.0 / (float) 2.0) * a0[1] * deltaTFloat * deltaTFloat;
                        s[2] = v0[2] * deltaTFloat + (float) ((float) 1.0 / (float) 2.0) * a0[2] * deltaTFloat * deltaTFloat;
                        // textView.setText(textView.getText() + " s= " + String.format("%1.3f", (sOld_X - s[0])) + "\n");


                        if (Math.abs(s[0]) < 0.00004f)
                            s[0] = 0f;
                        if (Math.abs(s[1]) < 0.00004f)
                            s[1] = 0f;
                        if (Math.abs(s[2]) < 0.005f)
                            s[2] = 0f;

                    }

                    //  client.transmit(ACCELEROMETER, (-1f) * s[0], s[1], (-1f) * s[2]);

                    currentPath = currentPath.sum(new AccelVectorPath((-1f) * s[0], s[1], (-1f) * s[2]));

                    sOld_X = s[0];
                    sOld_Y = s[1];
                    sOld_Z = s[2];

                    v0[0] = v[0];
                    v0[1] = v[1];
                    v0[2] = v[2];

                    a0[0] = a[0];
                    a0[1] = a[1];
                    a0[2] = a[2];

                    t0 = t;

                }
                //  }


            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        listenerGyroscope = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (BluetoothComm.connected) {
                    calculateRotationMatrixGotFromSite(event);
//                    rotateActualTime = event.timestamp * NSEC_TO_SEC;
//                 /*   if (rotateActualTime - rotateLastUpdate < 100) {
//                        //Если с момента начала тряски прошло меньше 100
//                        // миллисекунд - выходим из обработчика
//                        return;
//                    }  */
//
//                    rotateLastUpdate = rotateActualTime;
//
//                    if (rotateT0 == -1) {
//                        rotateT0 = event.timestamp * NSEC_TO_SEC;
//                    }
//
//                    rotateV[0] = event.values[0];
//                    rotateV[1] = event.values[1];
//                    rotateV[2] = event.values[2];
//
//                    if (first_angle) {
//                        currentAngle = new GyroVectorAngle();
//                        rotateV[0] = 0f;
//                        rotateV[1] = 0f;
//                        rotateV[2] = 0f;
//                        angle0[0] = 0f;
//                        angle0[1] = 0f;
//                        angle0[2] = 0f;
//                        first_angle = false;
//                    } else {
//                        if (Math.abs(rotateV[0]) >= 50f) rotateV[0] = 0f;
//                        if (Math.abs(rotateV[1]) >= 50f) rotateV[1] = 0f;
//                        if (Math.abs(rotateV[2]) >= 50f) rotateV[2] = 0f;
//
//
//                        float K = 0.4f;
//                      //  rotateV[0] = K * rotateV[0] + (1 - K) * rotateV0[0];
//                      //  rotateV[1] = K * rotateV[1] + (1 - K) * rotateV0[1];
//                      //  rotateV[2] = K * rotateV[2] + (1 - K) * rotateV0[2];
//
//                        if (Math.abs(rotateV[0]) <= 0.02f)
//                            rotateV[0] = 0f;
//                        if (Math.abs(rotateV[1]) <= 0.035f)
//                            rotateV[1] = 0f;
//                        if (Math.abs(rotateV[2]) <= 0.012f)
//                            rotateV[2] = 0f;
//
//
//                        rotateV0[0] = rotateV[0];
//                        rotateV0[1] = rotateV[1];
//                        rotateV0[2] = rotateV[2];
//
//                        // Log.i("GYROSCOPE:", "" + rotateV[0] + ", " + rotateV[1] + ", " + rotateV[2]);
//                    /*Test transmit*/
//                        //client.transmit(GYROSCOPE, rotateV[0], rotateV[1], rotateV[2]);
//
//                        rotateT = event.timestamp * NSEC_TO_SEC;
//                        rotateDeltaT = (float) (rotateT - rotateT0);
//
//                        angle[0] = rotateV[0] * rotateDeltaT;
//                        angle[1] = rotateV[1] * rotateDeltaT;
//                        angle[2] = rotateV[2] * rotateDeltaT;
//                        if (Math.abs(angle[0]) >= 7f) angle[0] = 0f;
//                        if (Math.abs(angle[1]) >= 7f) angle[1] = 0f;
//                        if (Math.abs(angle[2]) >= 7f) angle[2] = 0f;
//                    }
//
//                    angle0[0] = angle[0];
//                    angle0[1] = angle[1];
//                    angle0[2] = angle[2];
//
//                    //client.transmit(GYROSCOPE, angle[0], angle[1], angle[2]);
//
//                    frameAngles.add(new GyroVectorAngle(angle0[0], angle0[1], angle0[2]));
//                    currentAngle = currentAngle.sum(new GyroVectorAngle(angle0[0], angle0[1], angle0[2]));
//
//                    rotateT0 = rotateT;
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        listenerGravity = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager.registerListener(listenerAccelerometer, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listenerGyroscope, gyroscope, SensorManager.SENSOR_DELAY_GAME);
        // sensorManager.registerListener(listenerGravity, gravitySensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listenerAccelerometer);
        sensorManager.unregisterListener(listenerGyroscope);

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    public void showAlertDialog() {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("Warning!");
        alert.setMessage("@string/textAlert_noAccelerometer");
        alert.setButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }

    public void showAlertDialogNoBluetooth(String message) {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("Error!");
        alert.setMessage("Omg! " + message);
        alert.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alert.show();
    }




    //private CameraBridgeViewBase mOpenCvCameraView;
    public static CameraView mOpenCvCameraView;

    //private VideoCapture mOpenCvCameraView;


    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void clickShowFrames(View v) {
        n = 0;
        textView.setText("");
        Intent intent = new Intent(this, ShowFramesActivity.class);
        startActivity(intent);

    }

    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;

    public static float[][] currentRotation = new float[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
public static Mat currentRotation_mat=new Mat(3,3, CvType.CV_64FC1);
public static boolean first_rot=true;
    public void calculateRotationMatrixGotFromSite(SensorEvent event) {
        if(first_rot){
            first_rot=false;
            currentRotation_mat.put(0,0,1);
            currentRotation_mat.put(0,1,0);
            currentRotation_mat.put(0,2,0);
            currentRotation_mat.put(1,0,0);
            currentRotation_mat.put(1,1,1);
            currentRotation_mat.put(1,2,0);
            currentRotation_mat.put(2,0,0);
            currentRotation_mat.put(2,1,0);
            currentRotation_mat.put(2,2,1);
        }
        if (timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S;
            // Axis of the rotation sample, not normalized yet.
            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];

            // Calculate the angular speed of the sample
            // magnitude = величина
            float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

            // Normalize the rotation vector if it's big enough to get the axis
            // (that is, EPSILON should represent your maximum allowable margin of error)
            if (omegaMagnitude > 0.001) {
                axisX /= omegaMagnitude;
                axisY /= omegaMagnitude;
                axisZ /= omegaMagnitude;
            }

            // Integrate around this axis with the angular speed by the timestep
            // in order to get a delta rotation from this sample over the timestep
            // We will convert this axis-angle representation of the delta rotation
            // into a quaternion before turning it into the rotation matrix.

            // все это - просто формула составления кватерниона
            float thetaOverTwo = omegaMagnitude * dT / 2.0f;
            float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
            float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
            deltaRotationVector[0] = sinThetaOverTwo * axisX;
            deltaRotationVector[1] = sinThetaOverTwo * axisY;
            deltaRotationVector[2] = sinThetaOverTwo * axisZ;
            deltaRotationVector[3] = cosThetaOverTwo;
            float[] deltaRotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
            // User code should concatenate the delta rotation we computed with the current rotation
            // in order to get the updated rotation.
            if (currentRotation != null) {
                float[][] rot = new float[][]{{deltaRotationMatrix[0], deltaRotationMatrix[1], deltaRotationMatrix[2]},
                        {deltaRotationMatrix[3], deltaRotationMatrix[4], deltaRotationMatrix[5]},
                        {deltaRotationMatrix[6], deltaRotationMatrix[7], deltaRotationMatrix[8]}};

                currentRotation = times(currentRotation, rot);

                float[] rot1 = new float[]{currentRotation[0][0], currentRotation[0][1], currentRotation[0][2],
                        currentRotation[1][0], currentRotation[1][1], currentRotation[1][2],
                        currentRotation[2][0], currentRotation[2][1], currentRotation[2][2],};


                currentRotation_mat.put(0, 0, deltaRotationMatrix[0]);
                currentRotation_mat.put(0, 1, deltaRotationMatrix[1]);
                currentRotation_mat.put(0, 2, deltaRotationMatrix[2]);
                currentRotation_mat.put(1, 0, deltaRotationMatrix[3]);
                currentRotation_mat.put(1, 1, deltaRotationMatrix[4]);
                currentRotation_mat.put(1, 2, deltaRotationMatrix[5]);
                currentRotation_mat.put(2, 0, deltaRotationMatrix[6]);
                currentRotation_mat.put(2, 1, deltaRotationMatrix[7]);
                currentRotation_mat.put(2, 2, deltaRotationMatrix[8]);

            }
        }
        timestamp = event.timestamp;

        //  currentRotation = currentRotation * deltaRotationMatrix;


    }

    public float[][] times(float[][] A, float[][] B) {
        if (A[0].length != B.length) {
            throw new IllegalArgumentException("inner dimensions must agree.");
        }
        int m = A.length, n = A[0].length, Bm = B.length, Bn = B[0].length;
        float[][] C = new float[m][Bn];
        float[] colj = new float[n];
        for (int j = 0; j < Bn; j++) {
            for (int k = 0; k < n; k++) {
                colj[k] = B[k][j];
            }
            for (int i = 0; i < m; i++) {
                float[] rowi = A[i];
                float s = 0;
                for (int k = 0; k < n; k++) {
                    s += rowi[k] * colj[k];
                }
                C[i][j] = s;
            }
        }
        return C;
    }




}

