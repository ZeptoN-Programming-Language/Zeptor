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
import java.util.ArrayList;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import xyz.zepton.zeptor.editor.EditorOptions;
import xyz.zepton.zeptor.log.Logger;
import xyz.zepton.zeptor.syntax.Syntax;

/**
 * A class to define a programming style document
 *
 * @author Tan Hong Cheong
 * @version 20041021
 */
public class ProgrammingDocument extends DefaultStyledDocument {

    private static final long serialVersionUID = 5610879720838024528L;
    /**
     * the end of block comment string
     */
    private String blockComEnd;
    /**
     * The block comment header size
     */
    private int blockCommentHeaderSize;
    /**
     * Style for block comment
     */
    private Style blockCommentStyle;
    /**
     * The block comment trailer size
     */
    private int blockCommentTrailerSize;
    /**
     * the start of block comment string
     */
    private String blockComStart;
    /**
     * the style for break
     */
    private Style breakStyle;
    /**
     * Buffer to store the text
     */
    private char[] buffer;
    /**
     * Style for character
     */
    private Style characterStyle;
    /**
     * the start/end of char
     */
    private String charQuote;
    /**
     * wether to highlight in colour
     */
    private boolean colourHighlight;
    /**
     * Style for hex
     */
    private Style hexStyle;
    /**
     * the line being higlighted
     */
    private int highlighted;
    /**
     * the style for highlighting a line
     */
    private Style highlightStyle;
    /**
     * Style for key word
     */
    private Style keyStyle;
    /**
     * the length of the text
     */
    private int length;
    /**
     * The line comment header size
     */
    private int lineCommentHeaderSize;
    /**
     * Style for line comment
     */
    private Style lineCommentStyle;
    /**
     * the start of line comment string
     */
    private String lineComStart;
    /**
     * A vector to store all the new lines
     */
    private ArrayList<Integer> newlines;
    /**
     * Style for normal word
     */
    private Style normalStyle;
    /**
     * Style for number
     */
    private Style numberStyle;
    /**
     * Style for octal
     */
    private Style octalStyle;
    /**
     * Style for operator
     */
    private Style operatorStyle;
    /**
     * the editor options
     */
    private EditorOptions options;
    /**
     * Style for reserved word
     */
    private Style reservedStyle;
    /**
     * the start/end of String
     */
    private String stringQuote;
    /**
     * Style for string
     */
    private Style stringStyle;
    /**
     * The syntax of the language
     */
    private Syntax syntax;
    /**
     * The text in the document
     */
    private String text;

    /**
     * @param s the syntax of the language
     * <p>
     * Default uses color highlighting
     * <p>
     * For black and white please use setColorHighlight(false) method
     * @param s the syntax
     * @param o the editor options
     */
    public ProgrammingDocument(Syntax s, EditorOptions o) {
        super();
        initStyles();//must call this first
        setSyntax(s);
        setEditorOptions(o);
        setColourHighlight(true);
        newlines = new ArrayList<Integer>();
        highlighted = -1;//no line being highlighed

    }

    /**
     * Add newline to the array of newlines
     *
     * @param offs the offset
     * @param ste ste string added
     */
    private void addNewlines(int offs, String str) {
        //find all new lines position before offs
        //these do not need to be updated
        int i = 0;
        boolean exceed = false;
        while ((!exceed) && (i < newlines.size())) {
            int x = newlines.get(i);
            if (x >= offs) {
                exceed = true;
            } else {
                i++;
            }
        }

        int l = str.length();

        char[] b = str.toCharArray();
        //insert the new line position

        for (int j = 0; j < l; j++) {
            if (b[j] == '\n') {
                newlines.add(i, Integer.valueOf(offs + j));

                i++;
            }
        }
        //update the rest of the new line pos

        for (int j = i; j < newlines.size(); j++) {
            int x = newlines.get(j);
            newlines.remove(j);
            newlines.add(j, x + l);
        }
    }

    /**
     * clear the style at line indicated
     *
     * @param line the line at which the style is reseted to be normal
     */
    private void clearStyle(int line) {
        int totalLines = (getNoOfLines() + 1);
        int start = getLinePos(line);
        int end = getLength();
        if (line < totalLines) {
            end = getLinePos(line + 1);
        }
        try {
            setCharacterAttributes(start, end - start, normalStyle, true);
            refresh(start, end - 1);
        } catch (BadLocationException ex) {
            Logger.LOG.logTrap(ex);
        }
    }

    /**
     * find the search string in the text
     *
     * @param searchString the string to be searched
     * @param start the start position
     * @param end the end position
     * @return the position in the string, -1 id it dooesn't exist
     */
    private int find(String searchString, int start, int end) {
        int a = text.indexOf(searchString, start);
        if (a < end) {
            return a;
        } else {
            return -1;
        }
    }

    /**
     * @param pos the position in the text
     * @return the columno
     */
    public int getColumnNo(int pos) {
        int lineNo = getLineNo(pos);
        if (lineNo == 1) {
            //on first line
            return pos + 1;
        } else {
            int lineStart = newlines.get(lineNo - 2);
            return pos - lineStart;
        }
    }

    /**
     * @param pos the position in the text
     * @return the line number of pos
     */
    public int getLineNo(int pos) {
        int l = newlines.size();
        int i = 0;
        boolean found = false;
        while ((!found) && (i < l)) {
            int x = newlines.get(i);
            if (x >= pos) {
                found = true;
            } else {
                i++;
            }
        }
        return i + 1;
    }

    /**
     * @param line the line number to get position
     * @return the newline position of line
     */
    public int getLinePos(int line) {
        if (line - 1 > newlines.size()) {
            line = newlines.size();
        }
        if (line == 1) {
            return 0;
        } else {
            return newlines.get(line - 2) + 1;
        }
    }

    /**
     * @param a an int
     * @param b an int
     * @param c an int
     * @param d an int
     * @return the min of a,b,c,d that is not -1 else -1 if all -1
     */
    private int getMin(int a, int b, int c, int d) {
        if ((a == -1) && (b == -1) && (c == -1) && (d == -1)) {
            return -1;
        }
        if (a == -1) {
            a = Integer.MAX_VALUE;
        }
        if (b == -1) {
            b = Integer.MAX_VALUE;
        }
        if (c == -1) {
            c = Integer.MAX_VALUE;
        }
        if (d == -1) {
            d = Integer.MAX_VALUE;
        }
        if ((a <= b) && (a <= c) && (a <= d)) {
            return a;
        }
        if ((b <= a) && (b <= c) && (b <= d)) {
            return b;
        }
        if ((c <= a) && (c <= b) && (c <= d)) {
            return c;
        }
        if ((d <= a) && (d <= b) && (d <= c)) {
            return d;
        }
        return -1;
    }

    /**
     * return the position of a newline or -1 is doesn't exist
     *
     * @param start the start position to search
     * @param end the end position to search
     */
    private int getNewLine(int start, int end) {
        int a = text.indexOf('\n', start);
        if (a <= end) {
            return a;
        } else {
            return -1;
        }
    }

    /**
     * @return the no of lines
     */
    public int getNoOfLines() {
        return newlines.size();
    }

    /**
     * hightlight a line
     *
     * @param line the line to be highlighted
     */
    public void highlight(int line) {
        if (highlighted > 0) {
            clearStyle(highlighted);
        }
        highlighted = line;
        setStyle(line, highlightStyle);
    }

    /**
     * highlight portion of the text
     *
     * @param start the start position
     * @param end the end position
     */
    public void highlight(int start, int end) {
        //unhighlight first
        unhighlight();
        highlighted = getLineNo(start);
        ;
        setCharacterAttributes(start, end - start, highlightStyle, true);
    }

    /**
     * Insert a word into the document
     *
     * @param offs the offs to insert into the document
     * @param word the word to be inserted
     */
    private void highlightWord(int offs, String word) throws BadLocationException {
        //NOTE the order of the if else statement is important!

        ////System.out.println("Highlighting = "+word);
        if (syntax.isKey(word)) {
            setCharacterAttributes(offs, word.length(), keyStyle, false);
        } else if (syntax.isReserved(word)) {
            setCharacterAttributes(offs, word.length(), reservedStyle, false);
        } else if (syntax.isFloat(word))//need to check for float first or 0.0123 will have 0123 highlight as octal
        {
            setCharacterAttributes(offs, word.length(), numberStyle, false);
        } else if (syntax.isOperator(word))//need to check operator before number or . will be highlighted as number
        {
            setCharacterAttributes(offs, word.length(), operatorStyle, false);
        } else if (syntax.isHex(word)) {
            setCharacterAttributes(offs, word.length(), hexStyle, false);
        } else if (syntax.isOctal(word))//need to check octal first before number so taht 012 will be octal and not number
        {
            setCharacterAttributes(offs, word.length(), octalStyle, false);
        } else if (syntax.isNumber(word)) {
            setCharacterAttributes(offs, word.length(), numberStyle, false);
        } else {
            setCharacterAttributes(offs, word.length(), normalStyle, false);
        }
    }

    /**
     * Initialize the styles to be used
     */
    private void initStyles() {
        //Style for normal word
        normalStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        //Style for key word
        keyStyle = addStyle("key", normalStyle);

        //Style for reserved word
        reservedStyle = addStyle("reserved", normalStyle);

        //Style for line comment
        lineCommentStyle = addStyle("line comment", normalStyle);

        //Style for block comment
        blockCommentStyle = addStyle("block comment", normalStyle);

        //Style for number
        numberStyle = addStyle("number", normalStyle);

        //Style for hex
        hexStyle = addStyle("hex", normalStyle);

        //Style for octal
        octalStyle = addStyle("octal", normalStyle);

        //Style for string
        stringStyle = addStyle("string", normalStyle);

        //Style for character
        characterStyle = addStyle("character", normalStyle);

        //Style for operator
        operatorStyle = addStyle("operator", normalStyle);

        //break style
        breakStyle = addStyle("break", normalStyle);

        //highlight style
        highlightStyle = addStyle("highlight", normalStyle);
    }

    /**
     * Override the default insertString to insert string with style
     *
     * @param offset the offset into the document to insert the content >= 0.
     * All positions that track change at or after the given location will move.
     * @param str the string to insert
     * @param a the attributes to associate with the inserted content. This may
     * be null if there are no attributes.
     */
    public void insertString(int offs, String str, AttributeSet att) throws BadLocationException {
        int l;//the length of the string
        //convert all \t to tabSize space
        String spaces = "";
        int noOfSpaces = options.getTabSize();
        for (int i = 0; i < noOfSpaces; i++) {
            spaces = spaces + ' ';
        }
        StringBuilder tempStr = new StringBuilder(str);
        int pos = 0;
        while (pos < tempStr.length()) {
            if (tempStr.charAt(pos) == '\t') {
                tempStr.replace(pos, pos + 1, spaces);
                pos = pos + noOfSpaces - 1;
                pos++;
            } else {
                pos++;
            }
        }
        str = tempStr.toString();
        StringBuilder b;
        //check if it is inserting only a new line
        if (str.equals("\n")) {
            l = getLength();
            b = new StringBuilder(getText(0, l));
            int previous = 0;//the previous new line
            int space = 0;//the white space to insert
            boolean found = false;
            int temp = offs - 1;
            while ((!found) && (temp >= 0)) {
                if (b.charAt(temp) == '\n') {
                    found = true;
                    previous = temp;
                }
                temp--;
            }
            if (!found) {
                previous = 0;
            }
            temp = previous + 1;
            found = false;
            while ((!found) && (temp < offs)) {
                if (b.charAt(temp) != ' ') {
                    found = true;
                } else {
                    space++;
                }
                temp++;
            }
            for (int i = 0; i < space; i++) {
                str = str + ' ';
            }
        }
        super.insertString(offs, str, normalStyle);

        //update these attributes so that other functions can use them
        length = getLength();
        text = getText(0, length);
        buffer = text.toCharArray();

        //update the newline position
        addNewlines(offs, str);

        int a1 = getLineNo(offs) - 2;
        int a2 = getLineNo(offs + str.length() - 1) - 1;
        int startPos = 0;//the start position to udpate
        int endPos = length - 1;//the end position to update

        try {
            if (a1 > -1) {
                startPos = newlines.get(a1) + 1;
            }
            //+1 because do not need to update the \n char
        } catch (ArrayIndexOutOfBoundsException ex) {
            Logger.LOG.logTrap(ex);
        }

        try {
            if (a2 >= 0 && a2 > newlines.size()) 
            {
                endPos = newlines.get(a2);
            }

        } catch (ArrayIndexOutOfBoundsException ex) {
            Logger.LOG.logTrap(ex);
        } finally {
            refresh(startPos, endPos);
        }
    }//end insertString

    /**
     * refresh the document
     */
    public void refresh() {
        try {
            refresh(0, getLength() - 1);
        } catch (BadLocationException ex) {
            Logger.LOG.logTrap(ex);

        }
    }

    /**
     * refresh the document from startPos to endPos
     *
     * @param startPos the start position to start refreshing
     * @param endPos the end position to start refreshing
     */
    private void refresh(int startPos, int endPos) throws BadLocationException {
        if (startPos > endPos) { //nothing to do
            return;
        }
        boolean previousBlock = false;//true if previous style if block
        if (startPos > 0) {
            AttributeSet s = getCharacterElement(startPos - 1).getAttributes();
            if (s.isEqual(blockCommentStyle)) {
                previousBlock = true;
            }
        }

        int temp = startPos;
        //since previous style is block comment
        //insert as block comment
        if (previousBlock) {

            previousBlock = false;
            //find the end of blocks comment
            //search until end pos
            //since block comment affects more than 1 lines

            int a = find(blockComEnd, startPos, endPos);
            if (a == -1) {
                //cannot find the end of block comment
                //update till the end pos as block comment
                setCharacterAttributes(startPos, endPos - startPos + 1, blockCommentStyle, false);
                temp = endPos + 1;//exit loop
            } else {
                setCharacterAttributes(startPos, a - startPos + blockCommentTrailerSize,
                        blockCommentStyle, false);
                temp = a + blockCommentTrailerSize;

                //find the next start of block comment and refresh to there
                //muts do this because it is possible that user inserted a "*/"
                //in the middle
                //do not need to find next end block comment as next start block comment
                //to next end block comment is still block comment
                a = find(blockComEnd, temp, endPos);
                if (a == -1) {
                    //must refresh till end of text
                    endPos = length - 1;
                } else if (a > endPos) {
                    //next block comment start after endpos
                    //refresh till there
                    endPos = a - 1;
                }

            }
        }
        //must update the these attributes so that other functions can use them
        //correctly

        while (temp <= endPos) {
            //inform all progress listeners to update
            int character = find(charQuote, temp, endPos);
            int line = find(lineComStart, temp - lineCommentHeaderSize + 1, endPos);
            int block = find(blockComStart, temp - blockCommentHeaderSize + 1, endPos);
            int string = find(stringQuote, temp, endPos);
            //find out which one start first, line comment, block comment
            //characters or string
            int min = getMin(line, block, string, character);

            if (min == -1) {
                //there is no string, comment, characters
                //update words
                refreshWords(temp, endPos);
                temp = endPos + 1;//exit the while loop
            } else {
                //update those part that that are not string, comment or character
                refreshWords(temp, min - 1);

                if (min == character) {
                    temp = min + 1;//since character header is size 1
                    //find the end of character
                    //since character start and end with the same character
                    int a = find(charQuote, temp, endPos);
                    if (a == -1) {
                        //cannot find the end of character
                        //update till the end as end of character
                        setCharacterAttributes(min, endPos - min + 1, characterStyle, false);
                        temp = endPos + 1;//exit loop
                    } else {
                        setCharacterAttributes(min, a - min + 1, characterStyle, false);
                        temp = a + 1;
                    }
                } else if (min == line) {
                    temp = min + lineCommentHeaderSize;//since line header is size 2
                    //find the end of line comment
                    int a = getNewLine(temp, endPos);
                    if (a == -1) {
                        //cannot find the end of character
                        //update till the end as end of character
                        setCharacterAttributes(min, endPos - min + 1, lineCommentStyle, false);
                        temp = endPos + 1;//exit loop
                    } else {
                        setCharacterAttributes(min, a - min + 1, lineCommentStyle, false);
                        temp = a + 1;
                    }
                } else if (min == string) {
                    temp = min + 1;//since string header is size 1
                    //find the end of string
                    //since string start and end with the same character
                    int a = find(stringQuote, temp, endPos);
                    if (a == -1) {
                        //cannot find the end of character
                        //update till the end as end of character
                        setCharacterAttributes(min, endPos - min + 1, stringStyle, false);
                        temp = endPos + 1;//exit loop
                    } else {
                        setCharacterAttributes(min, a - min + 1, stringStyle, false);
                        temp = a + 1;
                    }
                } else {
                    //check if it is already highlighted as block comment
                    //this is possible as usually refresh a line
                    AttributeSet s = getCharacterElement(min).getAttributes();
                    if (s.isEqual(blockCommentStyle)) {
                        //alread highlight as block comment, can skip all the steps in else
                        //do the same as previous is block

                        temp = endPos + 1;//end this iteraction
                        ////System.out.println("already highlighted as block!");
                        ////System.out.println("Temp = "+temp);
                        refresh(min + blockCommentHeaderSize, endPos);
                    } else {
                        temp = min + blockCommentHeaderSize;//since block header is size 2
                        //find the end of blocks comment
                        //search until the end of all text
                        //since block comment affects more than 1 lines
                        int a = find(blockComEnd, temp, length - 1);
                        if (a == -1) {
                            //cannot find the end of character
                            //update till the end as end of character
                            setCharacterAttributes(min, length, blockCommentStyle, false);
                            temp = length;//exit loop
                        } else {
                            setCharacterAttributes(min, a - min + blockCommentTrailerSize,
                                    blockCommentStyle, false);
                            temp = a + blockCommentTrailerSize;
                        }
                    }
                }
            }
        }
    }

    /**
     * refresh the document words from startPos to endPos those part of the
     * document that was not covered by comment, string and characters
     *
     * @param startPos the start position to start refreshing
     * @param endPos the end position to start refreshing, exact position in the
     * string
     */
    private void refreshWords(int startPos, int endPos) throws BadLocationException {
        if (endPos < startPos) {
            return;//do nothing
        }
        String word = "";
        int temp = startPos;
        char c;
        int i = startPos;
        //insert the characters as usual
        while (i <= endPos) {
            c = buffer[i];
            boolean isVariable = syntax.isVariable(c);
            //special case
            if (c == '.') {
                if (i > startPos) {
                    char previousChar = buffer[i - 1];
                    if ((previousChar >= '0') && (previousChar <= '9'))//the previous char is a number
                    {
                        //it is varible since it is possibily [number].[number]
                        isVariable = true;
                    } else if (syntax.isVariable(previousChar)) {
                        //[varible]. is not a number hence not a variable
                        isVariable = false;
                    } else {
                        //it is varible since it is possibily [not variable].[number]
                        isVariable = true;
                    }
                } else {
                    //it is varible since it is possibily .[number]
                    isVariable = true;
                }
            }
            if (!isVariable) {
                //found a word
                word = text.substring(temp, i);
                highlightWord(temp, word);
                //even if c is not variable, still must highlight as it may be operator
                highlightWord(i, "" + c);
                temp = i + 1;
            }
            i++;
        }
        if (temp <= endPos) {
            //highlight the rest of the word
            word = text.substring(temp, endPos + 1);
            highlightWord(temp, word);
        }
    }

    /**
     * Override the default behaviour of remove
     *
     * @param offs the offset from the beginning >= 0
     * @param len the number of characters to remove >= 0
     */
    public void remove(int offs, int len) throws BadLocationException {
        if (len == 0) {
            return;
        }
        super.remove(offs, len);
        removeNewlines(offs, len);
        //update these attributes so that other functions can use them
        length = getLength();
        if (length == 0) {
            return;
        }

        text = getText(0, length);
        buffer = text.toCharArray();

        int a1 = getLineNo(offs) - 2;
        int a2 = getLineNo(offs + len - 1) - 1;
        int startPos = 0;//the start position to udpate
        int endPos = length - 1;//the end position to update
        try {
            startPos = newlines.get(a1) + 1;
            //+1 since do not need to update the \n char
        } catch (ArrayIndexOutOfBoundsException ex) {
            Logger.LOG.logTrap(ex);
        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }

        try {

            if (a2 >= 0 && a2 > newlines.size()) {
                endPos = newlines.get(a2) - 1;
            }

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);

        } finally {
            refresh(startPos, endPos);
        }//end try

    }//remove

    /**
     * remove newline to the array of newlines
     *
     * @param offs the offset
     * @param len the length of characters removed
     */
    private void removeNewlines(int offs, int len) {
        //find all new lines position before offs
        //these do not need to be updated
        int i = 0;
        boolean exceed = false;
        while ((!exceed) && (i < newlines.size())) {
            int x = newlines.get(i);
            if (x >= offs) {
                exceed = true;
            } else {
                i++;
            }
        }

        //remove the newline that are between offs to offs+len-1
        int end = offs + len - 1;
        int j = i;

        while (j < newlines.size()) {
            int x = newlines.get(j);
            newlines.remove(j);
            if (x > end) {
                //update position and add them back
                newlines.add(j, (x - len));
                j++;
            }
        }
    }

    /**
     * set bold
     */
    private void setBold() {
        //bold
        StyleConstants.setBold(normalStyle, options.getNormalStyle().isBold());
        StyleConstants.setBold(keyStyle, options.getKeyStyle().isBold());
        StyleConstants.setBold(reservedStyle, options.getReservedStyle().isBold());

        StyleConstants.setBold(lineCommentStyle,
                options.getLineCommentStyle().isBold());

        StyleConstants.setBold(blockCommentStyle, options.getBlockCommentStyle().isBold());
        StyleConstants.setBold(numberStyle, options.getNumberStyle().isBold());
        StyleConstants.setBold(hexStyle, options.getHexStyle().isBold());
        StyleConstants.setBold(octalStyle, options.getOctalStyle().isBold());
        StyleConstants.setBold(stringStyle,
                options.getStringStyle().isBold());
        StyleConstants.setBold(characterStyle, options.getCharacterStyle().isBold());
        StyleConstants.setBold(operatorStyle,
                options.getOperatorStyle().isBold());
    }

    /**
     * highlight a line in the document to indicate that the line is breakpoint
     *
     * @param line the line to be bolded
     */
    public void setBreakpoint(int line) {
        setStyle(line, breakStyle);
    }

    /**
     * set black and white basically it is for printing black and white purpose
     */
    private void setBW() {
        //forecolour
        StyleConstants.setForeground(normalStyle, Color.BLACK);
        StyleConstants.setForeground(keyStyle, Color.BLACK);
        StyleConstants.setForeground(reservedStyle, Color.BLACK);
        StyleConstants.setForeground(lineCommentStyle, Color.BLACK);
        StyleConstants.setForeground(blockCommentStyle, Color.BLACK);
        StyleConstants.setForeground(numberStyle, Color.BLACK);
        StyleConstants.setForeground(hexStyle, Color.BLACK);
        StyleConstants.setForeground(octalStyle, Color.BLACK);
        StyleConstants.setForeground(stringStyle, Color.BLACK);
        StyleConstants.setForeground(characterStyle, Color.BLACK);
        StyleConstants.setForeground(operatorStyle, Color.BLACK);
        StyleConstants.setBackground(breakStyle, Color.WHITE);//so that it will not be printed
    }

    /**
     * set color
     */
    private void setColor() {
        //forecolour
        StyleConstants.setForeground(normalStyle, options.getNormalStyle().getColor());
        StyleConstants.setForeground(keyStyle, options.getKeyStyle().getColor());
        StyleConstants.setForeground(reservedStyle, options.getReservedStyle().getColor());
        StyleConstants.setForeground(lineCommentStyle, options.getLineCommentStyle().getColor());
        StyleConstants.setForeground(blockCommentStyle, options.getBlockCommentStyle().getColor());
        StyleConstants.setForeground(numberStyle, options.getNumberStyle().getColor());
        StyleConstants.setForeground(hexStyle, options.getHexStyle().getColor());
        StyleConstants.setForeground(octalStyle, options.getOctalStyle().getColor());
        StyleConstants.setForeground(stringStyle, options.getStringStyle().getColor());
        StyleConstants.setForeground(characterStyle, options.getCharacterStyle().getColor());
        StyleConstants.setForeground(operatorStyle, options.getOperatorStyle().getColor());
        StyleConstants.setBackground(highlightStyle, options.getHighlightColor());
    }

    /**
     * @param color wether to have colour highlighting
     */
    public void setColourHighlight(boolean colour) {
        colourHighlight = colour;
        if (colour) {
            setColor();
        } else {
            setBW();
        }
    }

    /**
     * set the editor options
     *
     * @param o the editor options
     */
    public void setEditorOptions(EditorOptions o) {
        options = o;
        setFontSize(options.getFontSize());
        setFontFamily();
        setBold();
        setItalic();
        if (colourHighlight) {
            setColor();
        } else {
            setBW();
        }
    }

    /**
     * set the font family
     */
    private void setFontFamily() {
        //font
        //set to Monospaced font so that it will have fixed width
        StyleConstants.setFontFamily(normalStyle, "Monospaced");
        StyleConstants.setFontFamily(keyStyle, "Monospaced");
        StyleConstants.setFontFamily(reservedStyle, "Monospaced");
        StyleConstants.setFontFamily(lineCommentStyle, "Monospaced");
        StyleConstants.setFontFamily(blockCommentStyle, "Monospaced");
        StyleConstants.setFontFamily(numberStyle, "Monospaced");
        StyleConstants.setFontFamily(hexStyle, "Monospaced");
        StyleConstants.setFontFamily(octalStyle, "Monospaced");
        StyleConstants.setFontFamily(stringStyle, "Monospaced");
        StyleConstants.setFontFamily(characterStyle, "Monospaced");
        StyleConstants.setFontFamily(operatorStyle, "Monospaced");
    }

    /**
     * Set the font size of the documenet
     *
     * @param size the size of the font
     */
    public void setFontSize(int size) {
        StyleConstants.setFontSize(normalStyle, size);
        StyleConstants.setFontSize(keyStyle, size);
        StyleConstants.setFontSize(reservedStyle, size);
        StyleConstants.setFontSize(lineCommentStyle, size);
        StyleConstants.setFontSize(blockCommentStyle, size);
        StyleConstants.setFontSize(numberStyle, size);
        StyleConstants.setFontSize(hexStyle, size);
        StyleConstants.setFontSize(octalStyle, size);
        StyleConstants.setFontSize(stringStyle, size);
        StyleConstants.setFontSize(characterStyle, size);
        StyleConstants.setFontSize(operatorStyle, size);
    }

    /**
     * set italics
     */
    private void setItalic() {
        //italics
        StyleConstants.setItalic(normalStyle, options.getNormalStyle().isItalic());
        StyleConstants.setItalic(keyStyle, options.getKeyStyle().isItalic());
        StyleConstants.setItalic(reservedStyle, options.getReservedStyle().isItalic());
        StyleConstants.setItalic(lineCommentStyle, options.getLineCommentStyle().isItalic());
        StyleConstants.setItalic(blockCommentStyle, options.getBlockCommentStyle().isItalic());
        StyleConstants.setItalic(numberStyle, options.getNumberStyle().isItalic());
        StyleConstants.setItalic(hexStyle, options.getHexStyle().isItalic());
        StyleConstants.setItalic(octalStyle, options.getOctalStyle().isItalic());
        StyleConstants.setItalic(stringStyle, options.getStringStyle().isItalic());
        StyleConstants.setItalic(characterStyle, options.getCharacterStyle().isItalic());
        StyleConstants.setItalic(operatorStyle, options.getOperatorStyle().isItalic());
    }

    /**
     * highlight a line in the document with indicated style
     *
     * @param line the line to be highlighted
     * @param style the style
     */
    private void setStyle(int line, Style style) {
        int totalLines = (getNoOfLines() + 1);
        int start = getLinePos(line);
        int end = getLength();
        if (line < totalLines) {
            end = getLinePos(line + 1);
        }
        setCharacterAttributes(start, end - start, style, false);
    }

    /**
     * Set the syntax of the programming language
     *
     * @param s the syntax
     */
    public void setSyntax(Syntax s) {
        syntax = s;
        lineCommentHeaderSize = syntax.getLineCommentHeaderSize();
        blockCommentHeaderSize = syntax.getBlockCommentHeaderSize();
        blockCommentTrailerSize = syntax.getBlockCommentTrailerSize();
        blockComStart = syntax.getBlockCommentHeader();
        blockComEnd = syntax.getBlockCommentTrailer();
        lineComStart = syntax.getLineCommentHeader();
        stringQuote = syntax.getStringQuote();
        charQuote = syntax.getCharQuote();
    }

    /**
     * unhighlight the line being highlighted
     */
    public void unhighlight() {
        if (highlighted != -1) {
            clearStyle(highlighted);
            highlighted = -1;
        }
    }
}
