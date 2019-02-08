package com.aimove.iot.falldetector.detector;

import android.util.Log;

import com.aimove.iot.falldetector.utils.AccelerometerCoordinate;
import com.paramsen.noise.Noise;
import com.paramsen.noise.NoiseOptimized;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Fall Detector, will store and run the analysis in a way to detect falling
 */
public class FallDetector {

    /**
     * Library for FFT
     */
    private NoiseOptimized noise;

    /**
     * CopyOnWriteArrayList in a way to write and read in the same time.
     */
    private CopyOnWriteArrayList<Float> xList;


    /**
     * CopyOnWriteArrayList in a way to write and read in the same time.
     */
    private CopyOnWriteArrayList<Float> yList;


    /**
     * CopyOnWriteArrayList in a way to write and read in the same time.
     */
    private CopyOnWriteArrayList<Float> zList;

    public FallDetector(){
        this.xList = new CopyOnWriteArrayList<>();
        this.yList = new CopyOnWriteArrayList<>();
        this.zList = new CopyOnWriteArrayList<>();


    }

    public void addDataToList(final AccelerometerCoordinate accelerometerCoordinate){
        this.xList.add(accelerometerCoordinate.getX());
        this.yList.add(accelerometerCoordinate.getY());
        this.zList.add(accelerometerCoordinate.getZ());
    }

    public void removeElementToList(final int indexToRemove){
        this.xList.remove(indexToRemove);
        this.yList.remove(indexToRemove);
        this.zList.remove(indexToRemove);
    }

    public void runAnalysis(){
        Log.d("DataX", String.valueOf(this.xList.size()));
        Log.d("DataY", String.valueOf(this.yList.size()));
        Log.d("DataZ", String.valueOf(this.zList.size()));
        List<Float> copyList = xList;
        float[] x_data = new float[xList.size()];
        for (int i =0; i<x_data.length; i++){
            x_data[i] = copyList.get(i);
            xList.remove(i);
        }
        this.noise = Noise.real().optimized().init(xList.size(), true);
        this.noise.fft(x_data);
    }
}
