package com.aimove.iot.falldetector.utils;

/**
 * Accelerometer Coordinate, that will store accelerometer data
 * @author Kevin Giroux
 */
public class AccelerometerCoordinate {
    /**
     * X coordinate
     */
    private float x;

    /**
     * Y coordinate
     */
    private float y;

    /**
     * Z coordinate
     */
    private float z;

    /**
     * @return the X coordinates
     */
    public float getX() {
        return x;
    }

    /**
     *
     * @param x define the X coordinates
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     *
     * @return the Y coordinates
     */
    public float getY() {
        return y;
    }

    /**
     *
     * @param y define the Y coordinates.
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * @return Z coordinates of the accelerometer.
     */
    public float getZ() {
        return z;
    }

    /**
     *
     * @param z define to Z coordinates.
     */
    public void setZ(float z) {
        this.z = z;
    }
}
