package xyz.synse.datacenter.logger.strategy.converter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.synse.datacenter.logger.internal.Utils;

public class PrimaryConverterStrategy implements ConverterStrategy {

    private static final int DEFAULT_PRIORITY = 100;

    @Override
    public String convert(@Nullable String message, @NotNull Object object, int level) {
        if (object.getClass().isPrimitive()) {
            return Utils.concat(message, String.valueOf(object), DEFAULT_DIVIDER);
        }
        return null;
    }

    @Override
    public int priority() {
        return DEFAULT_PRIORITY;
    }
}
