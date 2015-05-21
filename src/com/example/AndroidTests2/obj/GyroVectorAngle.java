package com.example.AndroidTests2.obj;

import android.util.Log;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by Sa User on 21.03.2015.
 */
public class GyroVectorAngle implements Serializable{
    private static final long serialVersionUID = 7526472295622776148L;
    public float x, y, z;

    public GyroVectorAngle(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public GyroVectorAngle() {
        this.x = 0.0F;
        this.y = 0.0F;
        this.z = 0.0F;
    }

    public GyroVectorAngle sum(GyroVectorAngle newAngle) {
        float x = this.x + newAngle.x;
        float y = this.y + newAngle.y;
        float z = this.z + newAngle.z;

        return new GyroVectorAngle(x, y, z);
    }

    public String toString() {

        float x1 = (180.0f / (float) Math.PI) * this.x;
        float y1 = (180.0f / (float) Math.PI) * this.y;
        float z1 = (180.0f / (float) Math.PI) * this.z;

        return String.format("x=%.4f, y=%.4f, z=%.4f", x1, y1, z1);
    }

    public void toGrad() {
        float x1 = (180.0f / (float) Math.PI) * this.x;
        float y1 = (180.0f / (float) Math.PI) * this.y;
        float z1 = (180.0f / (float) Math.PI) * this.z;

        this.x = x1;
        this.y = y1;
        this.z = z1;
    }

    public static float toGrad(float x) {
        return (180.0f / (float) Math.PI) * x;
    }
}

