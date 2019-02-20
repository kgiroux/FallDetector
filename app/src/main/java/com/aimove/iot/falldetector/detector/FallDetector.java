package com.aimove.iot.falldetector.detector;

import com.aimove.iot.falldetector.utils.AccelerometerCoordinate;
import com.aimove.iot.falldetector.utils.GyrometerCoordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Fall Detector, will store and run the analysis in a way to detect falling
 */
public class FallDetector {

    /**
     * CopyOnWriteArrayList in a way to write and read in the same time.
     */
    private CopyOnWriteArrayList<AccelerometerCoordinate> accelerometerCoordinates;

    /**
     * CopyOnWriteArrayList in a way to write and read in the same time.
     */
    private CopyOnWriteArrayList<GyrometerCoordinate> gyrometerCoordinates;

    public FallDetector(){
        accelerometerCoordinates = new CopyOnWriteArrayList<>();
        gyrometerCoordinates = new CopyOnWriteArrayList<>();
    }

    public void addDataToList(final AccelerometerCoordinate accelerometerCoordinate){
        this.accelerometerCoordinates.add(accelerometerCoordinate);
    }

    public void addDataToListGyroMeterList(final GyrometerCoordinate gyrometerCoordinate){
        gyrometerCoordinates.add(gyrometerCoordinate);
    }

    public void removeElementToList(final int indexToRemove){
        this.accelerometerCoordinates.remove(indexToRemove);
    }
    public int runAnalysis(final AccelerometerCoordinate pAccelerometerCoordinate){
        AccelerometerCoordinate accelerometerCoordinate = new AccelerometerCoordinate();

        double a= Math.sqrt(
                        Math.pow(pAccelerometerCoordinate.getX(),2) +
                        Math.pow(pAccelerometerCoordinate.getY(),2) +
                        Math.pow(pAccelerometerCoordinate.getZ(),2)
        );
        if (a>0.3 && a <0.5)
        {
           return -1;
        }else {
           return 0;
        }
    }

    public List<AccelerometerCoordinate> retrieveListOfCoordinates(){
        List<AccelerometerCoordinate> data = new ArrayList<>(this.accelerometerCoordinates);
        this.accelerometerCoordinates.clear();
        return data;
    }

    public List<GyrometerCoordinate> retrieveListOfGyrometerCoordinates(){
        List<GyrometerCoordinate> data = new ArrayList<>(this.gyrometerCoordinates);
        this.gyrometerCoordinates.clear();
        return data;
    }

}
