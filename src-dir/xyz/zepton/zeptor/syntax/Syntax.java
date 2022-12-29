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
package xyz.zepton.zeptor.syntax;

/**
 * <p>
 * An interface to test a string.
 * <p>
 * For case insensitive syntax, use String class equalsIngnoreCase method
 * instead of equals
 *
 * @version 20040510
 * @author Tan Hong Cheong
 */
public interface Syntax {

    /**
     * @return the string that indicate start of block comment
     */
    public String getBlockCommentHeader();

    /**
     * @return the number of characters used to start a block comment
     */
    public int getBlockCommentHeaderSize();

    /**
     * @return the string that indicate end of block comment
     */
    public String getBlockCommentTrailer();

    /**
     * @return the number of characters used to end a block comment
     */
    public int getBlockCommentTrailerSize();

    /**
     * @return the string that indicate start and end of char
     */
    public String getCharQuote();

    /**
     * @return the string that indicate start of line comment
     */
    public String getLineCommentHeader();

    /**
     * @return the number of characters used to indicate a line comment
     */
    public int getLineCommentHeaderSize();

    /**
     * @return the string that indicate start and end of string
     */
    public String getStringQuote();

    /**
     * @param s the string to be tested
     * @return true if s is start of block comment
     */
    public boolean isBlockCommentHeader(char[] s);

    /**
     * @param s the string to be tested
     * @return true if s is end of block comment
     */
    public boolean isBlockCommentTrailer(char[] s);

    /**
     * @param c the character to be tested
     * @return true if c is a start/end of a char
     */
    public boolean isChar(char c);

    /**
     * @param s the string to be tested
     * @return true if s is float
     */
    public boolean isFloat(String s);

    /**
     * @param s the string to be tested
     * @return true if s is a hexadecimal
     */
    public boolean isHex(String s);

    /**
     * @param s the string to be tested
     * @return true if s is a key word
     */
    public boolean isKey(String s);

    /**
     * @param s the string to be tested
     * @return true if s is start of line comment
     */
    public boolean isLineCommentHeader(char[] s);

    /**
     * @param s the string to be tested
     * @return true if s is numeric
     */
    public boolean isNumber(String s);

    /**
     * @param s the string to be tested
     * @return true if s is a octal
     */
    public boolean isOctal(String s);

    /**
     * @param c the character to be tested
     * @return true if c is a operator
     */
    public boolean isOperator(char c);

    /**
     * Some programming languages like pascal define AND, OR NOT as operator
     *
     * @param s the string to be tested
     * @return true if s is operator
     */
    public boolean isOperator(String s);

    /**
     * @param s the string to be tested
     * @return true if s is a reserved word
     */
    public boolean isReserved(String s);

    /**
     * @param c the character to be tested
     * @return true if c is a start/end of a string
     */
    public boolean isString(char c);

    /**
     * @param c the character to be tested
     * @return true if c is a valid variable character
     */
    public boolean isVariable(char c);
}
