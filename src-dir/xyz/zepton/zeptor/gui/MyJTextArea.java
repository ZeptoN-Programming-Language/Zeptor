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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyListener;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * A text area that behaves like awt text are i.e automatically scroll
 * vertically to last line position Constraints the autoscrolling only works if
 * the text area is visble before adding text
 *
 * @author Tan Hong Cheong
 * @version 20041210
 */
public class MyJTextArea extends JScrollPane {

    /**
     * the serialVersionUID
     */
    private static final long serialVersionUID = 5015895003354744225L;

    /**
     * The text area
     */
    protected JTextArea textArea;

    /**
     * Create a new SystemTextArea
     */
    public MyJTextArea() {
        super();
        textArea = new JTextArea();
        //
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        this.setViewportView(textArea);
    }

    /**
     * override addKeyListener method so that it is added to both textArea and
     * scroll pane
     *
     * @param listener the key listener;
     */
    public void addKeyListener(KeyListener listener) {
        super.addKeyListener(listener);
        textArea.addKeyListener(listener);
    }

    /**
     * add a char
     *
     * @param c the character to be appended
     */
    public void append(char c) {
        this.append(c + "");
    }

    /**
     * add a text
     *
     * @param s the text to be appended
     */
    public void append(String s) {
        textArea.append(s);
        textArea.setCaretPosition(textArea.getText().length());
    }
    /**
     * Set if the text area is editable
     *
     * @param text are is editable
     */
    public void setEditable(boolean editable) {
        textArea.setEditable(editable);
    }

    /**
     * Overide update method so that it will not clear the panel first
     */
    public void update(Graphics g) {
        paint(g);
    }
}
