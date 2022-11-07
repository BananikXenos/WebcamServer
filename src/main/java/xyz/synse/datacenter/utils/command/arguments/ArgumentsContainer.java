package xyz.synse.datacenter.utils.command.arguments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ArgumentsContainer {
    private final ArrayList<Arguments> arguments = new ArrayList<>();

    public ArgumentsContainer(@NotNull List<String> rawArguments){
        rawArguments.forEach(rawArg -> this.arguments.add(new Arguments(rawArg)));
    }

    public int getArgsCount(){
        return arguments.size();
    }

    @Nullable
    public Arguments getArgument(int index){
        return arguments.get(index);
    }

    public boolean hasAtLeast(int count){
        return arguments.size() >= count;
    }

    public boolean hasLessOrExact(int count){
        return arguments.size() <= count;
    }

    public boolean hasExact(int count){
        return arguments.size() == count;
    }

    public boolean hasLessThan(int count){
        return arguments.size() < count;
    }

    public boolean hasMoreThan(int count){
        return arguments.size() > count;
    }
}
