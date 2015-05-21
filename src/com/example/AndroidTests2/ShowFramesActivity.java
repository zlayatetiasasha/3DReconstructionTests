package com.example.AndroidTests2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.AndroidTests2.obj.CameraFrame;
import com.example.AndroidTests2.obj.CameraMatFrame;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Sa User on 24.03.2015.
 */
public class ShowFramesActivity extends Activity {

    ImageView imageView;
    TextView textView;
    Communication client;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.showframes);

        //imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.angleTextView);
        client = MyActivity.client;


    }

    @Override
    public void onResume() {
        super.onResume();
       new Runnable(){
           @Override
           public void run() {
               createFramesAndTransmit();
           }
       }.run();
        //    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
        // you may be tempted, to do something here, but it's *async*, and may take some time,
        // so any opencv call here will lead to unresolved native errors.
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                // now we can call opencv code !.
                //displayFrames();

            } else {
                super.onManagerConnected(status);
            }
        }
    };



    public void displayFrames() {
        int i = 0;
        Iterator it = CameraListener.frames.iterator();
        LinearLayout layout = (LinearLayout) findViewById(R.id.imageLayout);
        while (it.hasNext()) {
            if (i < 10) {
                i++;
                CameraFrame frame = (CameraFrame) (it.next());
                if (frame.mat.cols() != 0 && frame.mat.rows() != 0 && i > 0) {
                    ImageView image = new ImageView(this);
                    LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
                    image.setLayoutParams(vp);
                    image.setMaxHeight(600);
                    image.setMaxWidth(700);
                    // other image settings
                    //  final Bitmap bm = Bitmap.createBitmap(frame.mat.cols(), frame.mat.rows(), Bitmap.Config.ARGB_8888);
                    // Utils.matToBitmap(frame.img, bm);
                    //   image.setImageBitmap(frame.img);
                    layout.addView(image);

                    TextView textView = new TextView(this);
                    textView.setLayoutParams(vp);
                    textView.setText(frame.angle.toString());
                    layout.addView(textView);
                }
            } else {
                return;

            }

            //
        }

    }

    // final Bitmap bm = Bitmap.createBitmap(frame.img.cols(), frame.img.rows(), Bitmap.Config.ARGB_8888);
    // Utils.matToBitmap(frame.img, bm);
    // imageView.setImageBitmap(bm);


    public void createFramesAndTransmit() {
        Collection<CameraMatFrame> mats = CameraListener.frames;
        Collection<CameraFrame> frames = new ArrayList<>();
        Iterator it = mats.iterator();
        while (it.hasNext()) {
            CameraMatFrame matFrame = (CameraMatFrame) it.next();
            CameraFrame frame = matFrame.toCameraFrame();
            frames.add(frame);
            textView.setText(textView.getText() + "transforming...<br>\n");
        }

        if (client != null) {
            textView.setText(textView.getText() + "transmitting...<br>\n");
            client.transmitArray(frames);
            textView.setText(textView.getText() + "DONE!...<br>\n");
        }

    }
}
