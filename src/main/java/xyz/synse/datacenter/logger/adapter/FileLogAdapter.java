package xyz.synse.datacenter.logger.adapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.synse.datacenter.logger.strategy.format.FormatStrategy;
import xyz.synse.datacenter.logger.strategy.format.PrettyFormatStrategy;
import xyz.synse.datacenter.logger.strategy.log.FileLogStrategy;

import java.io.File;

public class FileLogAdapter implements LogAdapter {

    private final FormatStrategy formatStrategy;

    /**
     * @param globalTag if you have not specified a tag, globalTag will be used.
     * @param logFile file where log is saved
     */
    public FileLogAdapter(@Nullable String globalTag, File logFile) {
        this.formatStrategy =
                PrettyFormatStrategy
                        .newBuilder()
                        .tag(globalTag)
                        .methodCount(1)
                        .showThreadInfo(false)
                        .optimizeSingleLine(true)
                        .logStrategy(new FileLogStrategy(logFile))
                        .build();
    }

    public FileLogAdapter(FormatStrategy formatStrategy) {
        this.formatStrategy = formatStrategy;
    }
    
    @Override
    public boolean isLoggable(int priority, String tag) {
        return true;
    }
    
    @Override
    public void log(int priority, String tag, String message, @Nullable FormatStrategy strategy, @NotNull Class[] invokeClass) {
        if (strategy != null) {
            strategy.log(priority, tag, message, invokeClass);
        } else {
            formatStrategy.log(priority, tag, message, invokeClass);
        }
    }
}