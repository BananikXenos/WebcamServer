package xyz.synse.datacenter.service.services;

import xyz.synse.datacenter.MantleAPI;
import xyz.synse.datacenter.service.Service;

public class CamerasService extends Service {
    private long expireTime;

    public CamerasService(long expireTime) {
        this.expireTime = expireTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public void doWork() {
        MantleAPI.INSTANCE.camerasManager.removeExpired(expireTime);
    }
}
