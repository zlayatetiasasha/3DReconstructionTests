package com.example.AndroidTests2;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Alexandra Malakhova
 * Date: 23.02.14
 * Time: 19:50
 */
public class BluetoothTransmitter {

    OutputStreamWriter fileStream;

    public BluetoothTransmitter(Context context) {
        try {

            FileOutputStream fOut = context.openFileOutput("coord.txt", Context.MODE_APPEND);
            fileStream = new OutputStreamWriter(fOut);
            // Write the string to the file


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void transmit(float x, Context context) {
        try {
            fileStream.write("" + x);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
