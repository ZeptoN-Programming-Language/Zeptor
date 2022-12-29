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
package xyz.zepton.zeptor.text;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Reader;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;

import xyz.zepton.zeptor.editor.EditorOptions;
import xyz.zepton.zeptor.gui.WordWrapOptional;
import xyz.zepton.zeptor.log.Logger;
import xyz.zepton.zeptor.syntax.ZeptoNSyntax;

/**
 * My own JTextPane
 *
 * @author Tan Hong Cheong
 * @version 20050515
 */
class MyJTextPane extends WordWrapOptional {

    private static final long serialVersionUID = 5940739533123428364L;
    /**
     * wether it is colour highlight
     */
    private boolean colourHighlight;
    private boolean compileFlag = false;
    /**
     * the document
     */
    private ProgrammingDocument doc;
    /**
     * the editor options
     */
    private EditorOptions options;

    public MyJTextPane(EditorOptions o) {
        super(false);
        options = o;
        init();
    }

    /**
     * Add a document listener
     *
     * @param lsitener the document listener
     */
    public void addDocumentListener(DocumentListener listener) {
        doc.addDocumentListener(listener);
    }

    /**
     * Add a undoable edit listener
     *
     * @param listener the undoable edit listener
     */
    public void addUndoableEditListener(UndoableEditListener listener) {
        doc.addUndoableEditListener(listener);
    }

    /**
     * Method to delete selected text
     */
    public void delete() {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        try {
            doc.remove(start, end - start + 1);
        } catch (BadLocationException ex) {
            Logger.LOG.logTrap(ex);
        }
    }

    /**
     * @param searchText the search text
     * @param caseSensitive wether to be case sensitive
     * @param whole whether to find whole word only, whole word means left and
     * right have a space
     * @param start the start position
     * @return position of search string
     */
    public int find(String searchText, boolean caseSensitive, boolean whole, int start) {
        return MyTextUtil.find(getText(), searchText, caseSensitive, whole, start, getText().length() - 1);
    }

    /**
     * @return the column no.
     */
    public int getColumnNo() {
        return doc.getColumnNo(getCaretPosition());
    }

    public boolean getCompileFlag() {
        return this.compileFlag;
    }

    /**
     * @return the current line number
     */
    public int getLineNo() {
        return doc.getLineNo(getCaretPosition());
    }

    /**
     * Must override getText as default getText seems to spilt a \n into
     * carriage return followed by line feed
     */
    public String getText() {
        String s = null;
        try {
            s = doc.getText(0, doc.getLength());
        } catch (BadLocationException ex) {
            Logger.LOG.logTrap(ex);
        }
        return s;
    }

    /**
     * goto line
     *
     * @param line the line number to go to
     */
    public void gotoLine(int line) {
        setCaretPosition(doc.getLinePos(line));
        doc.highlight(line);
    }

    /**
     * highlight portion of the text
     *
     * @param start the start position 2param end the end position
     */
    public void highlight(int start, int end) {
        doc.highlight(start, end);
    }

    /**
     * highlight a line
     *
     * @param line the line to be highlighted
     */
    public void highlightLine(int line) {
        doc.highlight(line);
    }

    /**
     * Initialization
     */
    private void init() {
        doc = new ProgrammingDocument(new ZeptoNSyntax(), options);
        setDocument(doc);
        setColorHighlight(true);
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                doc.unhighlight();
            }
        });
    }

    /**
     * override default behaviour of read so that document model is saved
     *
     * @param in the stream to read from
     * @param desc an object describing the stream; this might be a string, a
     * File, a URL, etc. Some kinds of documents (such as html for example)
     * might be able to make use of this information; if non-null, it is added
     * as a property of the document
     */
    public void read(Reader in, Object desc) throws IOException {
        char[] temp = new char[10000];
        int len;
        StringBuilder b = new StringBuilder();
        while ((len = in.read(temp)) != -1) { //end of stream is not reached
            b.append(temp, 0, len);
        }

        try {
            doc.insertString(0, b.toString(), null);
        } catch (BadLocationException ex) {
            Logger.LOG.logTrap(ex);
        }
    }

    /**
     * Refresh the textpane
     */
    public void refresh() {
        int i = getCaretPosition();//save cuurent caret position
        doc.refresh();
        setCaretPosition(i);
        //highlight all break points
    }

    /**
     * remove selected text and replace with new text
     *
     * @param replace The text to replace with
     * @return the end position where the replace text end
     */
    public int replace(String replace) {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        try {
            doc.remove(start, end - start);
            doc.insertString(start, replace, null);
            return start + replace.length();
        } catch (BadLocationException ex) {
            Logger.LOG.logTrap(ex);
            return -1;
        }
    }

    /**
     * Select all the text
     */
    public void selectAll() {
        select(0, doc.getLength());
    }

    /**
     * @param c wether the textpane should display color or BW
     */
    public void setColorHighlight(boolean c) {
        colourHighlight = c;
        doc.setColourHighlight(c);
        if (c) {
            this.setBackground(options.getBackgroundColor());
            this.setCaretColor(options.getNormalStyle().getColor());
        } else {
            this.setBackground(Color.WHITE);
            this.setCaretColor(Color.BLACK);
        }
    }

    public void setCompileFlag(final boolean flag) {
        this.compileFlag = flag;
    }

    /**
     * set the editor options
     *
     * @param o the editor options
     */
    public void setEditorOptions(EditorOptions o) {
        options = o;
        if (colourHighlight) {
            this.setBackground(options.getBackgroundColor());
            this.setCaretColor(options.getNormalStyle().getColor());
        } else {
            this.setBackground(Color.WHITE);
            this.setCaretColor(Color.BLACK);
        }
        doc.setEditorOptions(o);
        doc.setColourHighlight(colourHighlight);
    }
}
