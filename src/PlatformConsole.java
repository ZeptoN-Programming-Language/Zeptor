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
package xyz.zepton.zeptor.run;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import xyz.zepton.zeptor.log.Logger;

public class PlatformConsole {

    private final static int DELAY_BEGIN_MSEC = 1100; //1.10 sec

    public static final boolean isLinux = (System.getProperty("os.name").toLowerCase().indexOf("nux") >= 0);

    public static final boolean isMacOS = (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0);

    public static final boolean isWindows = (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0);

    public static final String WIN_HEAD[] = {"cmd", "/c", "start", "/wait", "cmd.exe", "/K", ""};

    public static String[] createCommand(final String[] head, final String[] tail, final String command) {

        final String[] cmd = new String[head.length + tail.length + 1];

        System.arraycopy(head, 0, cmd, 0, head.length);
        cmd[head.length + 1] = command;
        System.arraycopy(tail, 0, cmd, head.length + 2, tail.length);

        return cmd;

    }

    public static void runCommand(final String outputPath, final String packageName, final String programName) {

        String outPath = outputPath;
        String execCmd = "";

        if (isWindows) {
            outPath = String.format("\"%s\"", outPath);    //on Windows put directory in double quotes " " 8-27-2022 
        }

        if (packageName == null || packageName.equals("")) {
            execCmd = outPath + " " + programName;
        } else {
            execCmd = outPath + " " + packageName + "." + programName;
        }//end if

        try {
            Thread.sleep(DELAY_BEGIN_MSEC); //DELAY_BEGIN_MSEC
        } catch (Exception ignore) {
        }

        try {
            PlatformConsole.runInConsole("java -cp " + execCmd);

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }

    }//end runCommand

    public static int runInConsole(final String command) throws Exception {

        final String[] wrappedCommand;

        if (isWindows) {
            wrappedCommand = new String[]{"cmd", "/c", "start", "/wait", "cmd.exe", "/K", command + " && pause && exit"};

        } else if (isLinux) {
            wrappedCommand = new String[]{"xterm", "-e", "bash", "-c", command + "&& read -n 1 -s -p 'Press any key to continue . . .'"};

        } else if (isMacOS) {
            wrappedCommand = new String[]{"osascript", "-e", "tell application \"Terminal\" to activate", "-e", "tell application \"Terminal\" to do script \"" + command + "; echo 'Press any key to continue . . .' && read; exit\""};

        } else {
            throw new RuntimeException("Unsupported Operating System!");
        } // end if

        try {

            Process process = new ProcessBuilder(wrappedCommand).redirectErrorStream(true).start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            while ((reader.readLine()) != null) {
                ;
            } // end while
            
            	reader.close();
            return process.waitFor();
        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        } // end try

        return 0;
        
    }// end runInConsole

    private PlatformConsole() {
    }

}// end class OpenPlatformTerimanl
