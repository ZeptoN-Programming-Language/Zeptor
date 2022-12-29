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
package xyz.zepton.zeptor.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import xyz.zepton.zeptor.log.Logger;

/**
 * A JFrame that has extra behaviour. It will set to maximized size. Its default
 * behaviour will call frameClosed method when window is closed
 *
 * @author Tan Hong Cheong
 * @version 20041210
 */
public class MyJFrame extends JFrame {

    private static final long serialVersionUID = 6713892875501974846L;

    /**
     * constructor
     */
    public MyJFrame() {
        this("");
    }

    /**
     * Constructor
     *
     * @param title the title of the JFrame
     */
    public MyJFrame(String title) {
        this.setTitle(title);
        //set the default size to 800 by 600
        //so that when the user press restore, it is not minimized
        this.setSize(800, 600);
        this.setResizable(true);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                frameClosed();
            }
        });
        //maximize the frame
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        //show the frames
        this.setVisible(true);
    }

    /**
     * frame is closed
     */
    private void frameClosed() {
        Logger.LOG.closeLog();

        System.exit(0);
    }
}
