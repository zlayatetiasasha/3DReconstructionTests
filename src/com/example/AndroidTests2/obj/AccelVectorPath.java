package com.example.AndroidTests2.obj;

import java.io.Serializable;

/**
 * Created by Sa User on 19.04.2015.
 */
public class AccelVectorPath implements Serializable {
    private static final long serialVersionUID = 7526472295622776149L;

    public float x, y, z;

    public AccelVectorPath(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public AccelVectorPath() {
        this.x = 0.0F;
        this.y = 0.0F;
        this.z = 0.0F;
    }

    public AccelVectorPath sum(AccelVectorPath newPath) {
        float x = this.x + newPath.x;
        float y = this.y + newPath.y;
        float z = this.z + newPath.z;

        return new AccelVectorPath(x, y, z);
    }

    public AccelVectorPath minus(AccelVectorPath newPath) {
        if (this != null && newPath != null) {
            float x = this.x - newPath.x;
            float y = this.y - newPath.y;
            float z = this.z - newPath.z;

            return new AccelVectorPath(x, y, z);
        }
        return null;
    }

    public String toString() {
        return String.format("x=%.4f, y=%.4f, z=%.4f", this.x, this.y, this.z);
    }

}
