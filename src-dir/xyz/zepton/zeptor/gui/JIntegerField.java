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

import javax.swing.JTextField;

/**
 * A class that implements a text field that allows only digit to be keyed in
 *
 * @author Tan Hong Cheong
 * @version 20041210
 */
public class JIntegerField extends JTextField {

    /**
     * the serial uid
     */
    private static final long serialVersionUID = -250002401835917443L;

    /**
     * Create a new integer field that allow minus value
     */
    public JIntegerField() {
        this(-1, true);
    }

    /**
     * Create a new integer field that allows a max. of i digits
     *
     * @param allowMinus whether to allow minus number
     */
    public JIntegerField(boolean allowMinus) {
        this(-1, allowMinus);
    }

    /**
     * Create a new integer field that allows a max. of i digits
     *
     * @param max the max. no of digits , <0 for no max number @param allow
     * Minus whether to allow minus number
     */
    public JIntegerField(int max, boolean allowMinus) {
        super();
        setDocument(new DigitDocument(max, allowMinus, false));
    }

    /**
     * @return the int value
     */
    public int getInteger() {
        return Integer.parseInt(getText());
    }

    /**
     * set the int value
     *
     * @param i the int value
     */
    public void setInteger(int i) {
        setText(Integer.toString(i));
    }//end setInteger
}
