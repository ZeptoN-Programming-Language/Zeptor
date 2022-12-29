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
package xyz.zepton.zeptor.editor;

import java.awt.Color;

/**
 * a class to define the style of a syntax
 *
 * @author Tan Hong Cheong
 * @version 20021229
 */
public class SyntaxStyle {

    public static final SyntaxStyle New(final String rgbHex, final String boldBit, final String italicBit) {
        try {

            String rgb = rgbHex.trim();

            Color color = new Color((int) Long.parseLong(rgb, 16));
            boolean boldFlag = (Integer.parseInt(boldBit) == 1);
            boolean italicFlag = (Integer.parseInt(italicBit) == 1);

            return new SyntaxStyle(color, boldFlag, italicFlag);
        } catch (Exception ex) {
            System.out.printf("Error: %s%n", ex.getMessage());
            ex.printStackTrace();
        }

        return null;
    }//end New

    /**
     * wether it is bold
     */
    private boolean bold;

    /**
     * the color
     */
    private Color color;

    /**
     * wether it is italics
     */
    private boolean italic;

    /**
     * Constructor
     */
    public SyntaxStyle() {
        setDefaults();
    }

    /**
     * Constructor
     *
     * @param c the color
     * @param b to set bold
     * @param i to set italics
     */
    public SyntaxStyle(Color c, boolean b, boolean i) {
        setColor(c);
        setBold(b);
        setItalic(i);
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return wether to set bold
     */
    public boolean isBold() {
        return bold;
    }

    /**
     * @return wether to set italic
     */
    public boolean isItalic() {
        return italic;
    }

    /**
     * set bold to b
     *
     * @param b wether to set bold
     */
    public void setBold(boolean b) {
        bold = b;
    }

    /**
     * set the color
     *
     * @param c the color
     */
    public void setColor(Color c) {
        color = c;
    }

    public void setDefaults() {
        setBold(false);
        setItalic(false);
        setColor(Color.BLACK);
    }

    /**
     * set italic to b
     *
     * @param b wether to set italic
     */
    public void setItalic(boolean b) {
        italic = b;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append(String.format("%x", color.getRGB()));
        str.append(",");
        if (bold) {
            str.append("1");
        } else {
            str.append("0");
        }

        str.append(",");
        if (italic) {
            str.append("1");
        } else {
            str.append("0");
        }

        return str.toString();
    }//end toString

}//end class SyntaxStyle
