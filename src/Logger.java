/*
 * Copyright (c) 2022 by William F. Gilreath (will@zepton.xyz)
 * All rights reserved.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.  
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details. The license is available at the following 
 * link:  https://www.gnu.org/licenses/gpl-3.0.txt.
 *
 */
package xyz.zepton.zeptor.log;

/**
 * @(#)Logger.java
 *
 * Title: Logger.java - log and report messages and exceptions.
 *
 * Description: Log all messages and exceptions to an external file uniquely
 * named by the date/time of using the logging capability. Each log entry or
 * exception has date/time, number, and the information. For an entry it is the
 * message, for the exception, the trap, trap message, and then the stack trace
 * of the call sequence.
 *
 * Copyright: Copyright (c) October 2021. All Rights Reserved.
 * @author William F. Gilreath (wfgilreath@yahoo.com)
 * @version 1.1
 *
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Logger {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss");

    private static final String FILE_PREFIX = "zeptor";
    private static final String FILE_SUFFIX = ".log";

    public static final Logger LOG = new Logger();

    private static int logEntryCount = 0;

    private static int logErrorCount = 0;
    private static final boolean LOGFILE_NAME_DATE_TIME_FLAG = false;

    private static final void appendFile(final String textData, final String fileNamePath) {

        try {

            File file = new File(fileNamePath);

            if (!file.exists()) {
                file.createNewFile();
            }//end if

            FileWriter fw = new FileWriter(file.getName(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(textData);
            bw.close();

        } catch (Exception ignore) {
        }//end try

    }//end appendFile

    private boolean debugFlag = false;

    private String logFileName = null;

    public Logger() {

        try {
            if (LOGFILE_NAME_DATE_TIME_FLAG) {
                logFileName = FILE_PREFIX + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM_dd_yyyy_HH_mm_ss")) + FILE_SUFFIX;
            } else {
                logFileName = FILE_PREFIX + FILE_SUFFIX;
            }//end if

            //create new log file, clobber existing one
            File file = new File(this.logFileName);

            if (file.exists()) {
                file.delete();
            }//end if

        } catch (Exception ignore) {
        }//end try

    }//end constructor

    public void beginLog() {

        try {
            Logger.appendFile(String.format("%n"), this.logFileName);

            Logger.appendFile(String.format("+>>>>---------- Begin Log: %s ----------<<<<+%n", LocalDateTime.now().format(DATE_TIME_FMT)), this.logFileName);

            Logger.appendFile(String.format("%n"), this.logFileName);

            Logger.appendFile(String.format("    Create Log File: %s.%n", logFileName), this.logFileName);

            Logger.appendFile(String.format("%n"), this.logFileName);

            if (this.debugFlag) {
                System.out.println();
                System.out.printf("+>>>>---------- Begin Log: %s ----------<<<<+%n", LocalDateTime.now().format(DATE_TIME_FMT));
                System.out.println();
                System.out.printf("    Create Log File: %s.%n", logFileName);
                System.out.println();
            }//end if

        } catch (Exception ignore) {
        }

    }//end beginLog

    public void closeLog() {

        try {
            Logger.appendFile(String.format("    -----------------------------------------------------------%n%n"), this.logFileName);

            Logger.appendFile(String.format("    Closed Log File: %s.%n", logFileName), this.logFileName);
            Logger.appendFile(String.format("%n"), this.logFileName);
            Logger.appendFile(String.format("+>>>>---------- Close Log: %s ----------<<<<+%n", LocalDateTime.now().format(DATE_TIME_FMT)), this.logFileName);
            Logger.appendFile(String.format("%n"), this.logFileName);

            if (this.debugFlag) {
                System.out.printf("    -----------------------------------------------------------%n%n");
                System.out.printf("    Closed Log File: %s.%n", logFileName);
                System.out.println();
                System.out.printf("+>>>>---------- Close Log: %s ----------<<<<+%n", LocalDateTime.now().format(DATE_TIME_FMT));
                System.out.println();

            }//end if

        } catch (Exception ignore) {
        }
    }//end closeLog

    //append to text file -- rewrite
    public void logText(final String text) {

        Logger.appendFile(String.format("%n"), this.logFileName);

        Logger.appendFile(String.format("    -----------------------------------------------------------%n%n"), this.logFileName);

        Logger.appendFile(String.format("    [%s] ENTRY #%d: %s%n", LocalDateTime.now().format(DATE_TIME_FMT), Logger.logEntryCount++, text), this.logFileName);
        Logger.appendFile(String.format("%n"), this.logFileName);

        if (this.debugFlag) {
            System.out.printf("    -----------------------------------------------------------%n%n");
            System.out.printf("    [%s] ENTRY #%d: %s%n", LocalDateTime.now().format(DATE_TIME_FMT), Logger.logEntryCount, text);
            System.out.println();
        }//end if

    }//end logText

    //append to text file -- rewrite
    public void logTrap(final Exception trap) {

        Logger.appendFile(String.format("    -----------------------------------------------------------%n%n"), this.logFileName);

        Logger.appendFile(String.format("    [%s] ERROR #%d ; Trap: %s ; Error: %s %n%n", LocalDateTime.now().format(DATE_TIME_FMT), Logger.logErrorCount++, trap.getClass().getName(), trap.getMessage()), this.logFileName);
        Logger.appendFile(String.format("    Stack Trace: "), this.logFileName);

        StringWriter trace = new StringWriter();
        trap.printStackTrace(new PrintWriter(trace));
        Logger.appendFile(String.format("%s", trace.toString()), this.logFileName);

        Logger.appendFile(String.format("%n"), this.logFileName);

        if (this.debugFlag) {
            System.out.printf("    -----------------------------------------------------------%n%n");
            System.out.printf("    [%s] ERROR #%d ; Trap: %s ; Error: %s %n%n", LocalDateTime.now().format(DATE_TIME_FMT), Logger.logErrorCount, trap.getClass().getName(), trap.getMessage());
            System.out.printf("    Stack Trace: ");

            trap.printStackTrace(System.out);
            System.out.println();

        }//end if

    }//end logTrap

    public void setDebug() {
        this.setDebug(true);
    }//end setDebug

    public void setDebug(final boolean flag) {
        this.debugFlag = flag;
    }//end setDebug

}//end class Logger
