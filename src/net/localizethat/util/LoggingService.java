/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.util;

import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import net.localizethat.Main;
import net.localizethat.util.gui.JStatusBar;
import net.localizethat.util.gui.JStatusBar.LogMsgType;

/**
 * This Singleton class provides the infrastructure to do logging in both a typical log file (using a custom formatter)
 * and a GUI, through a status bar with a progress indicator and the ability to launch a JDialog for expanded messages.
 *
 * @author rpalomares
 */
public class LoggingService {
    private static final LoggingService instance = new LoggingService();
    private static final Logger logger = Logger.getLogger("");

    /**
     * Returns the singleton instance of this class
     *
     * @return The singleton instance of this class
     */
    public static LoggingService getInstance() {
        return instance;
    }
    JStatusBar statusBar;
    LogMsgType logLevel;

    /**
     * Creates a new instance of LoggingService
     */
    private LoggingService() {
        // We default to log warnings
        logger.setLevel(Level.WARNING);
        logLevel = LogMsgType.WARNING;

        // TODO: this is non-portable
        statusBar = Main.mainWindow.getStatusBar();
    }

    /**
     * Sets the logging level. It is pretty much like the Java Logging API Level, but uses an Enum type (LogMsgType)
     * with conversion to actual java.util.logging.Levels
     *
     * @param level The LogMsgType level
     */
    public void setLogLevel(final LogMsgType level) {
        logLevel = level;

        switch (level) {
            case DEBUG:
                logger.setLevel(Level.FINE);
                break;
            case INFO:
                logger.setLevel(Level.INFO);
                break;
            case WARNING:
                logger.setLevel(Level.WARNING);
                break;
            case ERROR:
                logger.setLevel(Level.SEVERE);
                break;
            case STOP:
                // We won't log anything unless a STOP message arrives
                logger.setLevel(Level.OFF);
                break;
            default:
                logger.setLevel(Level.WARNING);
        }
    }

    /**
     * Sets the log filename
     *
     * @param filename String with the filename; "_%g.log" is added to this string to form the real filename
     */
    public void setLogFileName(final String filename) {
        try {
            final FileHandler fh = new FileHandler(filename + "_%g.log", 65535, 5, false);
            fh.setFormatter(new MyFormatter());

            logger.addHandler(fh);
        } catch (IOException e) {
            System.err.println("Error setting the log filename. The exception follows:");
            e.printStackTrace();
        }
    }

    /**
     * Logs a message in both file and GUI
     *
     * @param msgType the message type/category
     * @param shortMsg the short message
     * @param longMsg the long message
     * @param e the exception causing the message
     */
    public void logMessage(final LogMsgType msgType, final String shortMsg,
            final String longMsg, final Exception e) {

        // We always filter 'log' to screen based on LogMsgType.WARNING
        if ((msgType.compareTo(LogMsgType.WARNING) >= 0) && (statusBar != null)) {
            statusBar.setText(msgType, longMsg);
        }

        logger.log(msgType.getLoggerLevel(), shortMsg + "\n\n" + longMsg, e);
    }
}

class MyFormatter extends Formatter {

    private static final String LINE_START = "=== ";
    private static final String LINE_END = " ===\n";
    private static final String LINE_SEP = "===";
    private static final String NEWLINE = "\n";

    MyFormatter() {
        // Default constructor;
        super();
    }

    @Override
    public String format(final LogRecord record) {
        final StringBuilder sb = new StringBuilder(100);

        sb.append(LINE_START);
        sb.append(record.getLevel());
        sb.append(" " + LINE_SEP + " ");
        sb.append(new Date().toString());
        sb.append(LINE_END);
        sb.append(record.getMessage());
        sb.append(NEWLINE);
        if (record.getThrown() != null) {
            sb.append(record.getThrown().getMessage());
            sb.append(NEWLINE);
            final StackTraceElement[] st = record.getThrown().getStackTrace();
            for (StackTraceElement st1 : st) {
                sb.append("    ").append(st1.toString());
                sb.append(NEWLINE);
            }
        }

        sb.append(LINE_START).append(LINE_SEP).append(LINE_SEP);
        sb.append(LINE_SEP).append(LINE_SEP);
        sb.append("(end of message)");
        sb.append(LINE_SEP).append(LINE_SEP).append(LINE_SEP);
        sb.append(LINE_SEP).append(LINE_END).append(NEWLINE);

        return sb.toString();
    }
}
