package xyz.synse.datacenter.logger.strategy.log;

public interface LogStrategy {

  void log(int priority, String tag, String message);
}
