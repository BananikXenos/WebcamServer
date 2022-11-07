package xyz.synse.datacenter.command.commands;

import xyz.synse.datacenter.MantleAPI;
import xyz.synse.datacenter.utils.command.arguments.ArgumentsContainer;
import xyz.synse.datacenter.cameras.Camera;
import xyz.synse.datacenter.command.Command;
import xyz.synse.datacenter.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class CamerasCommand extends Command {
    public CamerasCommand() {
        super("cameras", "cams");
    }

    @Override
    public boolean runCommand(String commandName, ArgumentsContainer argumentsContainer) {
        if(argumentsContainer.hasExact(1) && argumentsContainer.getArgument(0).getString().equalsIgnoreCase("list")){
            Logger.i("Current cameras:");
            for(Camera camera : MantleAPI.INSTANCE.camerasManager.getCameras().values()){
                Logger.i("Cam " + camera.getCameraID() + " - lastTimeSent: " + camera.getDelay() + "ms");
            }

            return true;
        }
        return false;
    }

    @Override
    public List<String> getUsages() {
        return new ArrayList<>() {
            {
                add("cameras list");
            }
        };
    }
}
