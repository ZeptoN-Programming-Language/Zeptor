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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JTextPane;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledDocument;

/**
 * My own JTextPane that can disable/enable word wrapped part of code copied
 * from http://www.java2s.com/ExampleCode/Swing-JFC/NonWrappingWrapTextPane.htm
 *
 * @author Tan Hong Cheong
 * @version 20050516
 */
public class WordWrapOptional extends JTextPane {

    /**
     * overrides the caret so that at the end line, the caret can be seen
     */
    private class ACaret extends DefaultCaret {

        private static final long serialVersionUID = 6737218760194667925L;

        protected void adjustVisibility(Rectangle nloc) {
            nloc.width = 5;//default width is 0, put as 5 so that it is visible
            super.adjustVisibility(nloc);
        }
    }

    private static final long serialVersionUID = -5776612823175045801L;

    /**
     * whether to enable word wrap
     */
    private boolean wordWrap;

    /**
     * create a JTextPane with word wrap enabled
     */
    public WordWrapOptional() {
        this(true);
    }

    /**
     * create a textPane
     *
     * @param wrap whether to enable wordWrap
     */
    public WordWrapOptional(boolean wrap) {
        super();
        wordWrap = wrap;
        ACaret newCaret = new ACaret();
        Caret currentCaret = getCaret();
        newCaret.setBlinkRate(currentCaret.getBlinkRate());
        setCaret(newCaret);
    }

    /**
     * create a JTextpane with word wrap enabled
     *
     * @param doc the document model
     */
    public WordWrapOptional(StyledDocument doc) {
        this(doc, true);
    }

    /**
     * create a JTextpane with word wrap enabled
     *
     * @param doc the document model
     * @param wrap whether to enable wordWrap
     */
    public WordWrapOptional(StyledDocument doc, boolean wrap) {
        super(doc);
        wordWrap = wrap;
        ACaret newCaret = new ACaret();
        Caret currentCaret = getCaret();
        newCaret.setBlinkRate(currentCaret.getBlinkRate());
        setCaret(newCaret);
    }

    /**
     * overrides the getScrollableTracksViewportWidth when there is need to
     * disable word wrap
     */
    public boolean getScrollableTracksViewportWidth() {
        if (wordWrap) {
            //same as JTextPane
            return super.getScrollableTracksViewportWidth();
        }

        Component parent = getParent(); //the parent
        if (parent == null) {
            //no parent, use own width
            return false;
        }
        javax.swing.plaf.ComponentUI ui = getUI();

        int parentWidth = parent.getSize().width;
        int myWidth = ui.getPreferredSize(this).width;
        int myHeight = ui.getPreferredSize(this).height;
        //need to ensure that the textpane fills the parent area
        if (myWidth < parentWidth) {
            //text pane width less than parent
            //use parent's width
            return true;
        } else {
            //set the textpane size to be 10 more else the caert will not be seen
            this.setPreferredSize(new Dimension(myWidth + 10, myHeight));
            return false;
        }
    }

    /**
     * set the word wrap style
     *
     * @param state whether to enable word wrap
     */
    public void setWrapStyleWord(boolean state) {
        wordWrap = state;
    }
}
