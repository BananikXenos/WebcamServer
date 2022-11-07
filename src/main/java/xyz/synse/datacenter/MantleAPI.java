package xyz.synse.datacenter;

import xyz.synse.datacenter.cameras.CamerasManager;
import xyz.synse.datacenter.command.CommandManager;
import xyz.synse.datacenter.command.commands.*;
import xyz.synse.datacenter.hardware.CPU;
import xyz.synse.datacenter.hardware.Memory;
import xyz.synse.datacenter.hardware.OperatingSystem;
import xyz.synse.datacenter.logger.Logger;
import xyz.synse.datacenter.logger.adapter.DefaultLogAdapter;
import xyz.synse.datacenter.logger.strategy.converter.DefaultLogConverter;
import xyz.synse.datacenter.service.services.CamerasService;
import xyz.synse.datacenter.service.services.PNetService;
import xyz.synse.datacenter.service.ServicesManager;
import xyz.synse.datacenter.utils.numbers.NumberUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public enum MantleAPI {
    INSTANCE;

    // Atomic boolean if mantle is running
    public final AtomicBoolean running = new AtomicBoolean(false);
    // Services manager
    public final ServicesManager servicesManager = new ServicesManager();
    // Command manager
    public final CommandManager commandManager = new CommandManager();
    // Camera system
    public final CamerasManager camerasManager = new CamerasManager();

    /**
     * Called on Mantle boot
     */
    public void onBoot() {
        // Set running
        this.running.set(true);
        // Initialize logger
        Logger.addLogAdapter(new DefaultLogAdapter(""));
        Logger.setLogConverter(new DefaultLogConverter());

        Logger.i("Starting Mantle");
        Logger.i("Registering commands");
        // Register all commands
        this.commandManager.registerCommand(new HelpCommand());
        this.commandManager.registerCommand(new StopCommand());
        this.commandManager.registerCommand(new ServicesCommand());
        this.commandManager.registerCommand(new RecordCommand());
        this.commandManager.registerCommand(new CamerasCommand());
        // Enable or disable OpenGL
        if (CPU.getAvailableProcessors() < 8) {
            System.setProperty("sun.java2d.opengl", "true");
        }
        // Print hardware info
        Logger.i("Hardware info:");
        Logger.i("CPU:");
        Logger.i("  Available Processors: " + CPU.getAvailableProcessors());
        Logger.i("System:");
        Logger.i("  Arch: " + OperatingSystem.getArch());
        Logger.i("  Name: " + OperatingSystem.getName());
        Logger.i("  Version: " + OperatingSystem.getVersion());
        Logger.i("Memory:");
        Logger.i("  Total: " + NumberUtils.round(Memory.getTotal() / 1_000_000D, 2) + "MB");
        Logger.i("OpenGL: "+ (CPU.getAvailableProcessors() > 12 ? "Enabled" : "Disabled"));

        Logger.i("Starting services");
        // Start all services
        this.servicesManager.startService(new CamerasService(30_000));
        this.servicesManager.startService(new PNetService(4477));
    }

    /**
     * Called to shut down Mantle
     */
    public void onShutdown() {
        this.running.set(false);
        Logger.i("Stopping Mantle");
        this.servicesManager.stopAllServices();
    }
}
