package xyz.synse.datacenter.service;

import xyz.synse.datacenter.logger.Logger;

public abstract class Service {
    private Thread thread;
    private boolean running = true;
    private final Object syncObject = new Object();
    private ServicesManager servicesManager;
    private long startTime;

    public void startService(){
        if(thread != null && thread.isAlive())
            throw new RuntimeException("Already running");

        this.startTime = System.currentTimeMillis();
        this.running = true;
        this.thread = new Thread(() -> {
            try {
                onStart();
                while (this.running) {
                    doWork();
                    System.out.print("");
                }

                onStop();
            }catch (Exception ex){
                Logger.e(ex, "Exception during service " + this.getClass().getSimpleName() + " execution");
            }
            synchronized (syncObject) {
                syncObject.notify();
            }
            if(servicesManager != null)
                servicesManager.serviceRemove(this.getClass());
            Logger.i("Service " + this.getClass().getSimpleName() + " has ended");
        });
        this.thread.start();
    }

    public boolean isRunning() {
        return running && this.thread != null && this.thread.isAlive();
    }

    public void exit(){
        this.running = false;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getRunningTime() {
        return (System.currentTimeMillis() - startTime);
    }

    public void stopService(){
        this.running = false;

        synchronized(syncObject) {
            try {
                syncObject.wait();
            } catch (InterruptedException ignored) {
            }
        }
        Logger.i("Service " + this.getClass().getSimpleName() + " has stopped");
    }

    public void setServicesManager(ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    public void doWork(){}
    public void onStart(){}
    public void onStop(){}
}
