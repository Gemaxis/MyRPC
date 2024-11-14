package com.custom.common.logger;


/**
 * 用到了装饰器模式
 * 扩展了对 错误类型、原因 和 扩展信息 的支持，使得日志记录可以携带更详细的上下文信息。
 */
public interface ErrorTypeAwareLogger extends Logger{
    /**
     * Logs a message with warn log level.
     *
     * @param code error code
     * @param cause error cause
     * @param extendedInformation extended information
     * @param msg log this message
     */
    void warn(String code, String cause, String extendedInformation, String msg);

    /**
     * Logs a message with warn log level.
     *
     * @param code error code
     * @param cause error cause
     * @param extendedInformation extended information
     * @param msg log this message
     * @param e log this cause
     */
    void warn(String code, String cause, String extendedInformation, String msg, Throwable e);

    /**
     * Logs a message with error log level.
     *
     * @param code error code
     * @param cause error cause
     * @param extendedInformation extended information
     * @param msg log this message
     */
    void error(String code, String cause, String extendedInformation, String msg);

    /**
     * Logs a message with error log level.
     *
     * @param code error code
     * @param cause error cause
     * @param extendedInformation extended information
     * @param msg log this message
     * @param e log this cause
     */
    void error(String code, String cause, String extendedInformation, String msg, Throwable e);
}
