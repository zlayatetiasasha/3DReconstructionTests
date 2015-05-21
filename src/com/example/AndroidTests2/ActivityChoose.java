package com.example.AndroidTests2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
    public void clickBluetooth(View v) {
        MyActivity.clientType = MyActivity.CLIENT_BLUETOOTH;
        intent = new Intent(this, MyActivity.class);
        startActivity(intent);
    }

    public void clickSocket(View v) {
        MyActivity.clientType = MyActivity.CLIENT_SOCKET;
        Intent intent = new Intent(this, MyActivity.class);
        startActivity(intent);
    }




}
