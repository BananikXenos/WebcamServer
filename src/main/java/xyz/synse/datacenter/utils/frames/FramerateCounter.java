package xyz.synse.datacenter.utils.frames;

import java.util.ArrayList;

public class FramerateCounter {
    private final ArrayList<Long> fps = new ArrayList<>();

    public void onFrame(){
        fps.removeIf(time -> (System.currentTimeMillis() - time) > 1000);
        fps.add(System.currentTimeMillis());
    }

    public int getFps() {
        return fps.size();
    }
}
