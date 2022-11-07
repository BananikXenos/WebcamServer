package xyz.synse.datacenter.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.synse.datacenter.logger.Logger;

import java.util.HashMap;

public class ServicesManager {
    private final HashMap<Class<? extends Service>, Service> services = new HashMap<>();

    /**
     * Starts a service and registers it as running service
     * @param service
     */
    public void startService(@NotNull Service service){
        services.put(service.getClass(), service);
        service.setServicesManager(this);
        Logger.i("Starting service " + service.getClass().getSimpleName());
        service.startService();
    }

    @Nullable
    public <T extends Service> T getService(@NotNull Class<? extends Service> clazz){
        return (T) services.get(clazz);
    }

    public HashMap<Class<? extends Service>, Service> getRunningServices() {
        return services;
    }

    public void stopService(@NotNull Class<? extends Service> clazz){
        Service service = serviceRemove(clazz);
        service.setServicesManager(this);
        Logger.i("Stopping service " + service.getClass().getSimpleName());
        service.stopService();
    }

    @Nullable
    <T extends Service> T serviceRemove(@NotNull Class<? extends Service> clazz){
        return (T) services.remove(clazz);
    }

    public void stopAllServices(){
        HashMap<Class<? extends Service>, Service> copiedServices = new HashMap<>(services);
        services.clear();
        Logger.i("Stopping all services");
        copiedServices.forEach((aClass, service) -> {
            Logger.i("Stopping service " + service.getClass().getSimpleName());
            service.stopService();
        });
        Logger.i("Stopped " + copiedServices.size() + (copiedServices.size() > 1 ? " services" : " service"));
    }
}
