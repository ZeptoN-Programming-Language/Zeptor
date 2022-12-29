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
 * class that implements a textfield that allows only a double to be keyed in
 *
 * @author Tan Hong Cheong
 * @version 20041210
 */
public class JDoubleField extends JTextField {

    /**
     * the serial uid
     */
    private static final long serialVersionUID = -2430457869584947134L;

    /**
     * Constructor
     */
    public JDoubleField() {
        super();
        setDocument(new DigitDocument(0, true, true));
    }

    /**
     * return the double value
     */
    public double getDouble() {
        return Double.parseDouble(getText());
    }

    /**
     * set the double value
     *
     * @param i the double value
     */
    public void setDouble(double d) {
        setText(Double.toString(d));
    }//end setDouble
}
