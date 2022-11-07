package xyz.synse.datacenter.command.commands;

import xyz.synse.datacenter.MantleAPI;
import xyz.synse.datacenter.utils.command.arguments.ArgumentsContainer;
import xyz.synse.datacenter.command.Command;

import java.util.ArrayList;
import java.util.List;

public class StopCommand extends Command {
    public StopCommand() {
        super("Stop", "exit", "shutdown");
    }

    @Override
    public boolean runCommand(String commandName, ArgumentsContainer argumentsContainer) {
        MantleAPI.INSTANCE.onShutdown();
        return true;
    }

    @Override
    public List<String> getUsages() {
        return new ArrayList<>() {
            {
                add("stop");
                add("exit");
                add("shutdown");
            }
        };
    }
}
