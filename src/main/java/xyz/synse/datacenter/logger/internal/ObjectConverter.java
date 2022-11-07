package xyz.synse.datacenter.logger.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.synse.datacenter.logger.strategy.converter.ConverterStrategy;
import xyz.synse.datacenter.logger.strategy.converter.DefaultLogConverter;
import xyz.synse.datacenter.logger.strategy.converter.LogConverter;

/**
 * convert object to string.
 */
public class ObjectConverter {

    private static LogConverter converter = new DefaultLogConverter();

    public static void converter(LogConverter converter) {
        ObjectConverter.converter = converter;
    }

    public static void add(ConverterStrategy strategy) {
        converter.add(strategy);
    }

    public static ConverterStrategy remove(ConverterStrategy strategy) {
        return converter.remove(strategy);
    }

    public static void clear() {
        converter.clear();
    }

    public static String convert(@Nullable String message, @NotNull Object object, int level) {
        return converter.convert(message, object, level);
    }

}
