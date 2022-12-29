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

import java.util.ArrayList;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * A class that define a document that allows only digit,-,+ decimal point
 * (optional)
 *
 * @author Tan Hong Cheong
 * @version 20041210
 */
public class DigitDocument extends PlainDocument {

    /**
     * the serial uid
     */
    private static final long serialVersionUID = -2680248265739977274L;

    /**
     * whether to allow decimal point
     */
    private boolean allowDecimal;

    /**
     * whether to allow - sign
     */
    private boolean allowMinus;

    /**
     * The maximum n. of digits, -1 means no max. integer
     */
    private int max;

    /**
     * Create a new document
     *
     * @param max the maximum characters, including '-','.', <1 to indicate no
     * max no @param allowMinu s whether to allow minus sign @param allowDecimal
     * whether to allow decimal
     */
    public DigitDocument(int max, boolean allowMinus, boolean allowDecimal) {
        super();
        this.max = max;
        this.allowMinus = allowMinus;
        this.allowDecimal = allowDecimal;
    }

    public void insertString(int offs, String str, AttributeSet att) throws BadLocationException {
        if (str == null) {
            return;
        }

        char[] a = str.toCharArray();
        ArrayList<Character> v = new ArrayList<Character>();

        for (int i = 0; i < a.length; i++) {
            if (Character.isDigit(a[i])) {
                v.add(a[i]);
            } else if ((allowDecimal) && ((a[i] == '.'))) {
                v.add(a[i]);
            } else if ((allowMinus) && ((a[i] == '-'))) {
                v.add(a[i]);
            }
        }

        int l = v.size();
        String s = "";
        for (int i = 0; i < l; i++) {
            char c = v.get(i);
            s = s + c;
        }
        if ((max < 1) || ((getLength() + s.length()) <= max)) {
            super.insertString(offs, s, att);
        }
    }
}
