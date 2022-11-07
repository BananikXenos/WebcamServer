package xyz.synse.datacenter.utils.numbers;

import java.util.ArrayList;

public class DeviceOrientation {
    private int orientation = 0;

    public int getOrientation() {
        return orientation;
    }

    public void calculate(float roll, float pitch){
        if(pitch < -75){
            orientation = 0;
            return;
        }

        if(roll < -65) {
            orientation = -90;
            return;
        }

        if(roll > 65){
            orientation = 90;
            return;
        }

        orientation = 0;
    }
}