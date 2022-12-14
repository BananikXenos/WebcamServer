package xyz.synse.datacenter.logger.strategy.converter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.synse.datacenter.logger.internal.Utils;

/**
 * convert throwable to string.
 */
public class ThrowableConverterStrategy implements ConverterStrategy {
    private static final int DEFAULT_PRIORITY = 500;

    @Override
    public String convert(@Nullable String message, @NotNull Object object, int level) {
        if (object instanceof Throwable) {
            Throwable t = (Throwable) object;
            String stackTraceString = Utils.getStackTraceString(t);

            if (level <= 0) {
                return stackTraceString;
            }

            String[] lines = stackTraceString.split(DEFAULT_DIVIDER);

            StringBuilder builder = new StringBuilder();
            String space = Utils.getSpace(level);
            for (int i = 0; i < lines.length; i ++) {
                if (i > 0) {
                    builder.append(space);
                }
                builder.append(lines[i]);
                if (i < lines.length - 1) {
                    builder.append(DEFAULT_DIVIDER);
                }
            }

            return Utils.concat(message, builder.toString(), DEFAULT_DIVIDER);
        }
        return null;
    }

    @Override
    public int priority() {
        return DEFAULT_PRIORITY;
    }
}
