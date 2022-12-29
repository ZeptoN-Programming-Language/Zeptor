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

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class CreditsDialog extends JFrame {

    private final static String APP_NAME = "    Acknowledgement and credit to those that made Zeptor possible. Your efforts and help are appreciated... Thank-you!!!    \n\r";
    private final static String BLANK_LINE = "                                                                             \n\r";
    private final static String CREDIT0 = "        \u2022 Tan Hong Cheong                                 \n\r";
    private final static String CREDIT1 = "        \u2022 William F. Gilreath                             \n\r";

    private final static String CREDIT2 = "        \u2022 Mahdi Chaker                                    \n\r";

    private final static String CREDIT3 = "        \u2022 Andrew Gauger                                   \n\r";
    private final static int TEXTAREA_COLUMNS = 55;
    private final static String TEXTAREA_FONT_NAME = "Arial";
    private final static int TEXTAREA_FONT_SIZE = 18;

    private final static String TEXTAREA_TITLE = "Credits for Zeptor";
    private final JFrame jframe;
    private final JTextArea textArea = new JTextArea();

    public CreditsDialog() {

        this.jframe = new JFrame("Credits");

        textArea.setColumns(CreditsDialog.TEXTAREA_COLUMNS);
        textArea.setLineWrap(false);
        textArea.setWrapStyleWord(false);
        textArea.setFont(new Font(CreditsDialog.TEXTAREA_FONT_NAME, Font.BOLD, CreditsDialog.TEXTAREA_FONT_SIZE));
        textArea.setBackground(Color.WHITE);
        textArea.setForeground(Color.DARK_GRAY);

        textArea.append(CreditsDialog.BLANK_LINE);
        textArea.append(CreditsDialog.BLANK_LINE);

        textArea.append(CreditsDialog.APP_NAME);

        textArea.append(CreditsDialog.BLANK_LINE);
        textArea.append(CreditsDialog.BLANK_LINE);
        textArea.append(CreditsDialog.CREDIT0);
        textArea.append(CreditsDialog.BLANK_LINE);
        textArea.append(CreditsDialog.CREDIT1);
        textArea.append(CreditsDialog.BLANK_LINE);
        textArea.append(CreditsDialog.CREDIT2);
        textArea.append(CreditsDialog.BLANK_LINE);
        textArea.append(CreditsDialog.CREDIT3);

        textArea.append(CreditsDialog.BLANK_LINE);
        textArea.append(CreditsDialog.BLANK_LINE);

        textArea.setSize(textArea.getPreferredSize().width, 1);

        this.jframe.pack();

    }//end CreditsDialog

    public void setVisible(final boolean b) {
        if (b) {
            JOptionPane.showMessageDialog(this.jframe, this.textArea, CreditsDialog.TEXTAREA_TITLE, JOptionPane.PLAIN_MESSAGE, null);
        }

    }//end setVisible

}//end CreditsDialog
