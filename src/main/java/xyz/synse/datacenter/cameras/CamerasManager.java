package xyz.synse.datacenter.cameras;

import xyz.synse.datacenter.logger.Logger;
import xyz.synse.datacenter.utils.utilities.GC;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CamerasManager {
    private final Map<String, Camera> cameras = Collections.synchronizedMap(new HashMap<String, Camera>());

    public Camera getCamera(String cameraID){
        Camera cachedCamera = cameras.get(cameraID);

        if(cachedCamera != null)
            return cachedCamera;

        return newCamera(cameraID);
    }

    public Map<String, Camera> getCameras() {
        return cameras;
    }

    public Camera newCamera(String cameraID){
        Camera camera = new Camera(cameraID);
        this.cameras.put(cameraID, camera);

        return camera;
    }

    public void removeExpired(long milliseconds){
        /*for(Iterator<Map.Entry<String, Camera>> it = cameras.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Camera> entry = it.next();
            if(entry.getValue().getDelay() > milliseconds) {
                Logger.i("Expiring camera " + entry.getValue().getCameraID());
                entry.getValue().dispose();
                it.remove();
                entry.setValue(null);
                GC.runGC();
            }
        }*/
    }
}
