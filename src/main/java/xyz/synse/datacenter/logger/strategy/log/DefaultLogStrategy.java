package xyz.synse.datacenter.logger.strategy.log;

import java.io.PrintStream;

/**
 * default log output
 */
public class DefaultLogStrategy implements LogStrategy {
    @Override
    public void log(int priority, String tag, String message) {
        myStream.println(tag + ": " + message);
    }

    private final PrintStream myStream = new PrintStream(System.out) {
        @Override
        public void println(String x) {
            super.println(x);
        }
    };
}
