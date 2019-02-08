package com.aimove.iot.falldetector.utils;

/**
 * Gyrometer Coordinate, that will store accelerometer data
 * @author Kevin Giroux
 */
public class GyrometerCoordinate {
    /**
     * X coordinate
     */
    private double x;

    /**
     * Y coordinate
     */
    private double y;

    /**
     * Z coordinate
     */
    private double z;

    /**
     * @return the X coordinates
     */
    public double getX() {
        return x;
    }

    /**
     *
     * @param x define the X coordinates
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     *
     * @return the Y coordinates
     */
    public double getY() {
        return y;
    }

    /**
     *
     * @param y define the Y coordinates.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return Z coordinates of the accelerometer.
     */
    public double getZ() {
        return z;
    }

    /**
     *
     * @param z define to Z coordinates.
     */
    public void setZ(double z) {
        this.z = z;
    }
}
