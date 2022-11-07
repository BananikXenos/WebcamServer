package xyz.synse.datacenter.command;

import org.jetbrains.annotations.NotNull;
import xyz.synse.datacenter.utils.command.arguments.ArgumentsContainer;
import xyz.synse.datacenter.logger.Logger;
import xyz.synse.datacenter.utils.command.ArgumentTokenizer;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class CommandManager {
    private final HashMap<Class<? extends Command>, Command> commands = new HashMap<>();

    public void registerCommand(@NotNull Command command){
        Logger.i("Registering command " + command.getClass().getSimpleName());
        commands.put(command.getClass(), command);
    }

    public void unregisterCommand(@NotNull Command command){
        Logger.i("Unregistering command " + command.getClass().getSimpleName());
        commands.remove(command.getClass(), command);
    }

    public void unregisterCommand(@NotNull Class<? extends Command> command){
        Logger.i("Unregistering command " + command.getSimpleName());
        commands.remove(command);
    }

    public boolean executeCommand(String rawData){
        List<String> arguments = ArgumentTokenizer.tokenize(rawData);
        String commandName = arguments.remove(0);

        for(Command command : commands.values()){
            if(command.isMatch(commandName)){
                if(!command.runCommand(commandName, new ArgumentsContainer(arguments))){
                    Logger.i("Usages: ");
                    for(String usage : command.getUsages()){
                        Logger.i(usage);
                    }
                }
                return true;
            }
        }

        return false;
    }

    public Collection<Command> getCommands() {
        return commands.values();
    }

    public String extractName(String rawData){
        return ArgumentTokenizer.tokenize(rawData).get(0);
    }
}
