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

import java.util.ArrayList;

/**
 * A class that defines the ZeptoN syntax
 * <P>
 * <em>Note:<em> Block comments had priority over everything including cases
 * where it is included in a string
 *
 * @version 20041021
 * @author Tan Hong Cheong
 */
public class ZeptoNSyntax implements Syntax {

    /**
     * Vectors to store all the key words
     */
    private ArrayList<String> keywords;

    /**
     * the maximum length of a key word
     */
    private int maxKey = Integer.MIN_VALUE;

    /**
     * the maximum length of a reserved word
     */
    private int maxReserved = Integer.MIN_VALUE;

    /**
     * the minimum length of a key word
     */
    private int minKey = Integer.MAX_VALUE;

    /**
     * the minimum length of a reserved word
     */
    private int minReserved = Integer.MAX_VALUE;

    /**
     * Vectors to store all the reserved words
     */
    private ArrayList<String> reserved;

    /**
     * Constructor
     */
    public ZeptoNSyntax() {
        initKeywords();
        initReserved();
    }

    /**
     * add a key word
     *
     * @param key the keyword to be added
     */
    private void addKeyword(String key) {
        if (key.length() > maxKey) {
            maxKey = key.length();
        }
        if (key.length() < minKey) {
            minKey = key.length();
        }
        int i = 0;
        int l = keywords.size();
        boolean found = false;
        while ((i < l) && (!found)) {
            String s = keywords.get(i);
            int result = key.compareTo(s);
            if (result < 0) {
                found = true;
            } else if (result == 0) {
                return;  //keyword already exist
            } else {
                i++;
            }
        }
        if (found) {
            keywords.add(i, key);
        } else {
            keywords.add(key); //add to end of list
        }
    }

    /**
     * add a reserved word
     *
     * @param key the keyword to be added
     */
    private void addReserved(String r) {
        if (r.length() > maxReserved) {
            maxReserved = r.length();
        }
        if (r.length() < minReserved) {
            minReserved = r.length();
        }
        int i = 0;
        int l = reserved.size();
        boolean found = false;
        while ((i < l) && (!found)) {
            String s = reserved.get(i);
            int result = r.compareTo(s);
            if (result < 0) {
                found = true;
            } else if (result == 0) {
                return;//reseved word already exist
            } else {
                i++;
            }
        }
        if (found) {
            reserved.add(i, r);
        } else {
            reserved.add(r);//add to end of list
        }
    }

    /**
     * @return the string that indicate start of block comment
     */
    public String getBlockCommentHeader() {
        return "/*";
    }

    /**
     * @return the number of characters used to start a block comment
     */
    public int getBlockCommentHeaderSize() {
        return 2;
    }

    /**
     * @return the string that indicate end of block comment
     */
    public String getBlockCommentTrailer() {
        return "*/";
    }

    /**
     * @return the number of characters used to end a block comment
     */
    public int getBlockCommentTrailerSize() {
        return 2;
    }

    /**
     * @return the string that indicate start and end of char
     */
    public String getCharQuote() {
        char c = 39;//single quote is character 39
        return "" + c;
    }

    /**
     * @return the string that indicate start of line comment
     */
    public String getLineCommentHeader() {
        return "//";
    }

    /**
     * @return the number of characters used to indicate a line comment
     */
    public int getLineCommentHeaderSize() {
        return 2;
    }

    /**
     * @return the string that indicate start and end of string
     */
    public String getStringQuote() {
        char c = 34;//single quote is character 34
        return "" + c;
    }

    /**
     * initialize all keywords
     */
    private void initKeywords() {
        keywords = new ArrayList<String>();

        addKeyword("abstract");
        addKeyword("begin"); //ZeptoN
        addKeyword("boolean");
        addKeyword("break");
        addKeyword("byte");
        addKeyword("case");
        addKeyword("catch");
        addKeyword("char");
        addKeyword("class");
        addKeyword("const");
        addKeyword("continue");
        addKeyword("default");
        addKeyword("do");
        addKeyword("double");
        addKeyword("else");
        addKeyword("enum");
        addKeyword("exports");
        addKeyword("extends");
        addKeyword("false");
        addKeyword("final");
        addKeyword("finally");
        addKeyword("float");
        addKeyword("for");
        addKeyword("goto");
        addKeyword("if");
        addKeyword("implements");
        addKeyword("import");
        addKeyword("instanceof");
        addKeyword("int");
        addKeyword("interface");
        addKeyword("long");
        addKeyword("me"); //ZeptoN
        addKeyword("module");
        addKeyword("native");
        addKeyword("new");
        addKeyword("null");
        addKeyword("package");
        addKeyword("private");
        addKeyword("prog"); //ZeptoN
        addKeyword("provides");
        addKeyword("protected");
        addKeyword("public");
        addKeyword("return");
        addKeyword("requires");
        addKeyword("short");
        addKeyword("static");
        addKeyword("strictfp");
        addKeyword("super");
        addKeyword("switch");
        addKeyword("synchronized");
        addKeyword("this");
        addKeyword("throw");
        addKeyword("throws");
        addKeyword("to");
        addKeyword("transient");
        addKeyword("true");
        addKeyword("try");
        addKeyword("uses");
        addKeyword("var");
        addKeyword("void");
        addKeyword("volatile");
        addKeyword("while");
        addKeyword("with");
    }

    /**
     * init reserved words
     */
    private void initReserved() {
        reserved = new ArrayList<String>();

        //predefined constant attributes
        addReserved("EMPTY_CHAR");
        addReserved("EMPTY_STRING");
        addReserved("EOL");
        addReserved("NULL_CHAR");

        //predefined environment methods
        addReserved("arraycopy");
        addReserved("availableProcessors");
        addReserved("clearProperty");
        addReserved("currentTimeMillis");
        addReserved("errorf");
        addReserved("exit");
        addReserved("getArgs");
        addReserved("getenv");
        addReserved("getLocale");
        addReserved("getProperty");
        addReserved("getRuntime");
        addReserved("gc");
        addReserved("freeMemory");
        addReserved("halt");
        addReserved("identityHashCode");
        addReserved("lineSeparator");
        addReserved("maxMemory");
        addReserved("nanoTime");
        addReserved("nop");
        addReserved("print");
        addReserved("printf");
        addReserved("println");
        addReserved("readBigDecimal");
        addReserved("readBigInteger");
        addReserved("readBoolean");
        addReserved("readByte");
        addReserved("readChar");
        addReserved("readDouble");
        addReserved("readFloat");
        addReserved("readInt");
        addReserved("readLine");
        addReserved("readLong");
        addReserved("readPassword");
        addReserved("readShort");
        addReserved("readString");
        addReserved("setProperty");
        addReserved("totalMemory");
        addReserved("toString");
        addReserved("valueOf");

    }//end initReserved

    /**
     * @param s the string to be tested
     * @return true if s is start of block comment
     */
    public boolean isBlockCommentHeader(char[] s) {
        if (s.length != 2) {
            return false;
        }
        if ((s[0] == '/') && (s[1] == '*')) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param s the string to be tested
     * @return true if s is end of block comment
     */
    public boolean isBlockCommentTrailer(char[] s) {
        if (s.length != 2) {
            return false;
        }
        if ((s[0] == '*') && (s[1] == '/')) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param c the character to be tested
     * @return true if c is a start/end of a char
     */
    public boolean isChar(char c) {
        if (c == 39)//single quote is character 39
        {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param s the string to be tested
     * @return true if s is float
     */
    public boolean isFloat(String s) {
        StringBuilder b = new StringBuilder(s);
        int l = b.length();
        if (l < 2) //float must be at least 2 char
        {
            return false;
        }
        char c;
        for (int i = 0; i < l; i++) {
            c = b.charAt(i);
            if (!((c == '.') || ((c >= '0') && (c <= '9')))) {
                if ((i == (l - 1)) && ((c == 'f') || (c == 'd'))) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        //successfully exited for loop
        return true;
    }

    /**
     * @param s the string to be tested
     * @return true if s is a hexadecimal
     */
    public boolean isHex(String s) {
        StringBuilder b = new StringBuilder(s);
        int l = b.length();
        char c;
        //if length less than 3, it is not a hex
        if (l < 3) {
            return false;
        }
        //check if start with 0x or 0X
        if (b.charAt(0) != '0') {
            return false;
        }
        if (!((b.charAt(1) == 'x') || (b.charAt(1) == 'X'))) {
            return false;
        }

        for (int i = 2; i < l; i++) {
            c = b.charAt(i);
            if (!(((c >= '0') && (c <= '9'))
                    || ((c >= 'a') && (c <= 'f'))
                    || ((c >= 'A') && (c <= 'F')))) {
                return false;
            }
        }
        //successfully exited for loop
        return true;
    }

    /**
     * @param s the string to be tested
     * @return true if s is a key word
     */
    public boolean isKey(String s) {
        int length = s.length();
        if (length < minKey) {
            return false;
        }
        if (length > maxKey) {
            return false;
        }

        int l = keywords.size();
        String key;
        for (int i = 0; i < l; i++) {
            key = keywords.get(i);
            if (key.equals(s)) {
                return true;
            }
        }
        //cannot find a match
        return false;
    }

    /**
     * @param s the string to be tested
     * @return true if s is start of line comment
     */
    public boolean isLineCommentHeader(char[] s) {
        if (s.length != 2) {
            return false;
        }
        if ((s[0] == '/') && (s[1] == '/')) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param s the string to be tested
     * @return true if s is numeric
     */
    public boolean isNumber(String s) {
        StringBuilder b = new StringBuilder(s);
        int l = b.length();
        char c;
        for (int i = 0; i < l; i++) {
            c = b.charAt(i);
            if (!((c >= '0') && (c <= '9'))) {
                if ((l > 1) && (i == (l - 1)) && (c == 'L')) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        //successfully exited for loop
        return true;
    }

    /**
     * @param s the string to be tested
     * @return true if s is a octal
     */
    public boolean isOctal(String s) {
        StringBuilder b = new StringBuilder(s);
        int l = b.length();
        char c;
        //if length less than 2, it is not a octal
        if (l < 2) {
            return false;
        }
        //check if start with 0
        if (b.charAt(0) != '0') {
            return false;
        }

        for (int i = 1; i < l; i++) {
            c = b.charAt(i);
            if (!(((c >= '0') && (c <= '7')))) {
                return false;
            }
        }
        //successfully exited for loop
        return true;
    }

    /**
     * @param c the character to be tested
     * @return true if c is a operator
     */
    public boolean isOperator(char c) {
        if ((c == '+')
                || (c == '-')
                || (c == '*')
                || (c == '/')
                || (c == '&')
                || (c == '|')
                || (c == '!')
                || (c == '?')
                || (c == ':')
                || (c == '=')
                || (c == ';')
                || (c == '>')
                || (c == '<')
                || (c == '.')
                || (c == '{')
                || (c == '}')
                || (c == '[')
                || (c == ']')
                || (c == ')')
                || (c == '(')) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * do not need to implement for java since operator like &&, ||, << are
     * already operator by its own character @param s the string to be tested
     *
     * @
     * return true if s is operator
     */
    public boolean isOperator(String s) {
        if (s.length() == 1) {
            return isOperator(s.charAt(0));
        } else {
            return false;
        }
    }

    /**
     * @param s the string to be tested
     * @return true if s is a reserved word
     */
    public boolean isReserved(String s) {
        int length = s.length();
        if (length < minReserved) {
            return false;
        }
        if (length > maxReserved) {
            return false;
        }

        int l = reserved.size();
        String r;
        for (int i = 0; i < l; i++) {
            r = reserved.get(i);
            if (r.equals(s)) {
                return true;
            }
        }
        //cannot find a match
        return false;
    }

    /**
     * @param c the character to be tested
     * @return true if c is a start/end of a string
     */
    public boolean isString(char c) {

        if (c == 34)//double quote is character 34
        {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param c the character to be tested
     * @return true if c is a valid variable character
     */
    public boolean isVariable(char c) {
        if ((c == '_')
                || ((c >= 'A') && (c <= 'Z'))
                || ((c >= 'a') && (c <= 'z'))
                || ((c >= '0') && (c <= '9'))) {
            return true;
        } else {
            return false;
        }
    }
}
