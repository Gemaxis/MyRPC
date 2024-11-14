package com.custom.common.logger;

import java.io.File;

public interface LoggerAdapter {
    Logger getLogger(Class<?> key);
    Logger getLogger(String key);
    Level getLevel();
    void setLevel(Level level);

    /**
     * Get the current logging file
     *
     * @return current logging file
     */
    File getFile();

    /**
     * Set the current logging file
     *
     * @param file logging file
     */
    void setFile(File file);

    /**
     * Return is the current logger has been configured.
     * Used to check if logger is available to use.
     *
     * @return true if the current logger has been configured
     */
    default boolean isConfigured() {
        return true;
    }
}
