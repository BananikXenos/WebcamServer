package xyz.synse.datacenter.logger.strategy.converter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.synse.datacenter.logger.internal.Utils;

/**
 * base converter.
 */
public class StringConverterStrategy implements ConverterStrategy {
    @Override
    public String convert(@Nullable String message, @NotNull Object object, int level) {
        return Utils.concat(message, object.toString(), DEFAULT_DIVIDER);
    }

    @Override
    public int priority() {
        // The string converter must be the last one.
        return Integer.MAX_VALUE;
    }
}
