package xyz.synse.datacenter.command.commands;

import xyz.synse.datacenter.MantleAPI;
import xyz.synse.datacenter.utils.command.arguments.ArgumentsContainer;
import xyz.synse.datacenter.command.Command;
import xyz.synse.datacenter.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "usage");
    }

    @Override
    public boolean runCommand(String commandName, ArgumentsContainer argumentsContainer) {
        if(argumentsContainer.hasExact(1)){
            for(Command command : MantleAPI.INSTANCE.commandManager.getCommands()){
                if(command.isMatch(argumentsContainer.getArgument(0).getString())){
                    Logger.i(argumentsContainer.getArgument(0).getString() + "'s Usage:");

                    for(String usage : command.getUsages()){
                        Logger.i(usage);
                    }
                    return true;
                }
            }
            return false;
        }

        return false;
    }

    @Override
    public List<String> getUsages() {
        return new ArrayList<>() {
            {
                add("help <CommandName>");
            }
        };
    }
}
