package com.example.AndroidTests2;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import com.example.AndroidTests2.obj.CameraFrame;
import org.opencv.android.CameraBridgeViewBase;

import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: Alexandra Malakhova
 * Date: 01.03.14
 * Time: 18:12
 */
public class BluetoothComm extends Communication {


    private Set<BluetoothDevice> pairedDevices;
    BluetoothSocket btSocket;

    private ObjectOutputStream outStream;

    public static boolean connected = false;
    final static String ACCELEROMETER = "mov";
    final static String GYROSCOPE = "rot";

    /*  public void transmit(int type, float x, float y, float z) {
          try {
              if (outStream == null) {
                  outStream = new DataOutputStream(btSocket.getOutputStream());

              }
              String t = (type == MyActivity.ACCELEROMETER) ? ACCELEROMETER : GYROSCOPE;
              outStream = new DataOutputStream(btSocket.getOutputStream());
              // String message = x + "\n";
              // byte[] msgBuffer = message.getBytes();
              outStream.writeUTF(t + ";" + x + ";" + y + ";" + z);
              outStream.flush();
          } catch (Exception ex) {
              ex.printStackTrace();
          }
      }
  */
    public void transmitArray(Collection<CameraFrame> frames) {
        try {
            if (outStream == null) {
                //      outStream = new ObjectOutputStream(new DataOutputStream(btSocket.getOutputStream()));
            }
            //   outStream = new ObjectOutputStream(new DataOutputStream(btSocket.getOutputStream()));
            // String message = x + "\n";
            // byte[] msgBuffer = message.getBytes();
            if (outStream != null) {
                outStream.writeObject(frames);
                outStream.flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void transmit(CameraFrame frame) {
        try {
            if (outStream == null) {
                outStream = new ObjectOutputStream(new DataOutputStream(btSocket.getOutputStream()));
            }

            // String message = x + "\n";
            // byte[] msgBuffer = message.getBytes();
            outStream.writeObject(frame);
            outStream.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void establishCommunication() throws Exception {
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth == null) {
            //     MyActivity.textView.setText("bluetooth=null");
        } else {
//            MyActivity.textView.setText("bluetooth not null");
        }
        if (bluetooth != null) {

            // BluetoothDevice device = bluetooth.getRemoteDevice("C0:18:85:D0:0E:70");

            BluetoothDevice device = bluetooth.getRemoteDevice("9C:AD:97:CF:AF:F4");
            if (device == null) {
                //  MyActivity.textView.setText("device=null");
            } else {
                // MyActivity.textView.setText("device not null");
            }

            try {
                //  btSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                btSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00011001-0000-1000-8000-00805F9B34FB"));
                bluetooth.cancelDiscovery();

                // Establish the connection.  This will block until it connects.
                try {
                    if (btSocket != null) {
                        btSocket.connect();
                        connected = true;
                        try {
                            //     if (outStream == null) {
                            outStream = new ObjectOutputStream(new DataOutputStream(btSocket.getOutputStream()));
                            //      }

                            //   outStream = btSocket.getOutputStream();

                      /*  outStream.close();
                        btSocket.close();    */
                        } catch (Exception e) {
                            // MyActivity.textView.setText(":(3" + e.getLocalizedMessage());
                        }
                    }
                } catch (Exception ex) {

                    // MyActivity.textView.setText("Error: " + ex.getLocalizedMessage() + (ex.getLocalizedMessage().startsWith("Bluetooth is off") ? ". Please, turn it on!" : ""));

                    throw new Exception("Error: " + ex.getLocalizedMessage() + (ex.getLocalizedMessage().startsWith("Bluetooth is off") ? ". Please, turn it on!" : ""));
                }

            } catch (Exception e) {
                //MyActivity.textView.setText(":(1" + e.getLocalizedMessage());
                throw new Exception(e.getMessage());

            }
        }

    }


}
