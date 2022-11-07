package xyz.synse.datacenter.utils.numbers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class NumberUtils {
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double calculateAverageInt(List<Integer> marks) {
        int sum = 0;
        if(!marks.isEmpty()) {
            for (Integer mark : marks) {
                if(mark == null)
                    continue;
                sum += mark;
            }
            return (double) sum / marks.size();
        }
        return sum;
    }

    public static float calculateAverageFloat(List<Float> marks) {
        float sum = (float) 0;
        if(!marks.isEmpty()) {
            for (Float mark : marks) {
                if(mark == null)
                    continue;
                sum += mark;
            }
            return sum / marks.size();
        }
        return sum;
    }
}
