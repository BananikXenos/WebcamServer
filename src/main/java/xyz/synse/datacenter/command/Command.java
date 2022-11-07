package xyz.synse.datacenter.command;

import xyz.synse.datacenter.utils.command.arguments.ArgumentsContainer;

import java.util.List;

public abstract class Command {
    private final String name;
    private final List<String> aliases;

    protected Command(String name, String... aliases) {
        this.name = name;
        this.aliases = List.of(aliases);
    }

    public abstract boolean runCommand(String commandName, ArgumentsContainer argumentsContainer);

    public abstract List<String> getUsages();

    public boolean isMatch(String str){
        return str.equalsIgnoreCase(name) || aliases.stream().anyMatch(cmd -> cmd.equalsIgnoreCase(str));
    }
}
