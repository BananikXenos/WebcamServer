package xyz.synse.datacenter.logger.border;

/**
 * Log border in order to format logs.
 */
public interface LogBorder {

    String topBorder();

    String middleBorder();

    String bottomBorder();

    String leftBorder();

    boolean showMiddleBorder();

}
