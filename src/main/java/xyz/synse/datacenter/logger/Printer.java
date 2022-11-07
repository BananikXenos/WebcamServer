package xyz.synse.datacenter.logger;

import org.jetbrains.annotations.Nullable;
import xyz.synse.datacenter.logger.adapter.LogAdapter;
import xyz.synse.datacenter.logger.strategy.format.FormatStrategy;

public interface Printer {
    
    @Deprecated
    Printer t(String tag);
    
    /**
     * set tag
     */
    Printer tag(String tag);
    
    Printer strategy(FormatStrategy strategy);

    Printer invokeClass(Class clazz);

    /**
     * set if is debug mode
     * @param debuggable logs will output if true
     */
    void setDebuggable(boolean debuggable);

    void d(String message, Object... args);
    
    void e(String message, Object... args);
    
    void e(Throwable throwable, String message, Object... args);
    
    void w(String message, Object... args);
    
    void i(String message, Object... args);
    
    void v(String message, Object... args);
    
    void wtf(String message, Object... args);

    void json(String json);

    /**
     * Formats the given json content and print it
     */
    void json(String message, String json);

    void xml(String xml);

    /**
     * Formats the given xml content and print it
     */
    void xml(String message, String xml);
    
    void obj(Object object);

    void obj(String message, Object object);

    void obj(int priority, String message, Object object);

    void log(int priority, String tag, String message, Throwable throwable,
             @Nullable FormatStrategy strategy, @Nullable Class invokeClass);
    
    /**
     * add adapter to process logs
     */
    void addAdapter(LogAdapter adapter);
    
    void clearLogAdapters();

}
