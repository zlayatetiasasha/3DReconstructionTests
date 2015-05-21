package com.example.AndroidTests2;

/**
 * Created with IntelliJ IDEA.
 * User: Alexandra Malakhova
 * Date: 01.03.14
 * Time: 16:22
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketComm extends Communication {

    /**
     * Called when the activity is first created.
     */
    public SocketComm() {


    }

    Socket socket = null;
    public DataOutputStream dataOutputStream = null;
    DataInputStream dataInputStream = null;

    public void establishCommunication() throws Exception{
        // TODO Auto-generated method stub


        try {
            socket = new Socket("192.168.1.2", 8888);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            // dataInputStream = new DataInputStream(socket.getInputStream());

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block

        } catch (IOException e) {
            // TODO Auto-generated catch block

        } /*finally {
            if (socket != null) {
                try {
                    socket.close();

                } catch (IOException e) {
                    // TODO Auto-generated catch block

                }
            }

            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }*/


    }

    public void transmit(String s) {
        try {
            dataOutputStream.writeFloat(Float.parseFloat(s));
        } catch (Exception ex) {

        }
    }
}








