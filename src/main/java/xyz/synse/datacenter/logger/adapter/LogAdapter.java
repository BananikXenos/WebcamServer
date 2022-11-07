package xyz.synse.datacenter.logger.adapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.synse.datacenter.logger.strategy.format.FormatStrategy;

public interface LogAdapter {
    
    boolean isLoggable(int priority, String tag);
    
    void log(int priority, String tag, String message, @Nullable FormatStrategy strategy, @NotNull Class[] invokeClass);
}