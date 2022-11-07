package xyz.synse.datacenter.command.commands;

import xyz.synse.datacenter.MantleAPI;
import xyz.synse.datacenter.utils.command.arguments.ArgumentsContainer;
import xyz.synse.datacenter.cameras.Camera;
import xyz.synse.datacenter.command.Command;
import xyz.synse.datacenter.logger.Logger;
import xyz.synse.datacenter.service.services.RecordService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecordCommand extends Command {
    public RecordCommand() {
        super("record", "capture");
    }

    @Override
    public boolean runCommand(String commandName, ArgumentsContainer argumentsContainer) {
        if(argumentsContainer.hasExact(4) && argumentsContainer.getArgument(1).isLong() && argumentsContainer.getArgument(2).isLong()) {
            String cameraID = argumentsContainer.getArgument(0).getString();
            long seconds = argumentsContainer.getArgument(1).getLong();
            int fps = argumentsContainer.getArgument(2).getInt();
            File file = new File(argumentsContainer.getArgument(3).getString());

            if(!MantleAPI.INSTANCE.camerasManager.getCameras().containsKey(cameraID)){
                Logger.e("No camera by id " + cameraID);
                return true;
            }

            Camera camera = MantleAPI.INSTANCE.camerasManager.getCamera(cameraID);

            Logger.i("Recording");
            MantleAPI.INSTANCE.servicesManager.startService(new RecordService(camera, file, seconds, fps));
            return true;
        }

        return false;
    }

    @Override
    public List<String> getUsages() {
        return new ArrayList<>() {
            {
                add("record <cameraID> <seconds> <fps> <file>");
            }
        };
    }
}
