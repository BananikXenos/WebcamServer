package xyz.synse.datacenter.utils.numbers;

import java.util.ArrayList;

public class ArrayUtils {
    public static void addAndUpdate(ArrayList<Float> list, float newValue, int maxValues){
        if(list.size() > maxValues)
            list.remove(list.size() - 1);

        list.add(newValue);
    }

    public static void addAndUpdate(ArrayList<Integer> list, int newValue, int maxValues){
        if(list.size() > maxValues)
            list.remove(list.size() - 1);

        list.add(newValue);
    }
}
