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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import xyz.zepton.zeptor.gui.JIntegerField;

/**
 * A dialog to allow user to set editor options
 *
 * @author Tan Hong Cheong
 * @version 20041021
 */
@SuppressWarnings("serial")
public class EditorOptionsDialog extends JDialog {

    /**
     * A panel to let user select the style for syntax highlighting
     */
    private class SyntaxOptionsPanel extends JPanel {

        private static final long serialVersionUID = 9186290342230287659L;

        /**
         * the checkbox to select wether to bold
         */
        private JCheckBox boldCheckBox;

        /**
         * A panel to display the colour
         */
        private JPanel colorPanel;

        /**
         * the checkbox to select wether to italics
         */
        private JCheckBox italicCheckBox;

        /**
         * the label
         */
        private JLabel label;

        /**
         * constructor
         *
         * @param l the label
         * @param c the current color
         * @param b wether it is bold
         * @param i wether it is italic
         */
        public SyntaxOptionsPanel(String l, Color c, boolean b, boolean i) {
            super(new GridLayout(1, 4));
            label = new JLabel(l, JLabel.LEFT);
            colorPanel = new JPanel();
            colorPanel.setBackground(c);
            boldCheckBox = new JCheckBox("Bold");
            boldCheckBox.setMargin(new Insets(1, 1, 1, 1));
            boldCheckBox.setSelected(b);
            italicCheckBox = new JCheckBox("Italic");
            italicCheckBox.setSelected(i);

            JButton setColourButton = new JButton("Set Color");
            setColourButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setColour();
                }
            });

            this.add(boldCheckBox);
            this.add(italicCheckBox);
            this.add(colorPanel);
            this.add(setColourButton);

            MouseListener mouseListener = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() >= 1) {
                        setColour();
                    }
                }
            };
            colorPanel.addMouseListener(mouseListener);
        }

        /**
         * @return the colour
         */
        public Color getColor() {
            return colorPanel.getBackground();
        }

        /**
         * @return the style
         */
        public SyntaxStyle getStyle() {
            return new SyntaxStyle(getColor(), isBold(), isItalic());
        }

        /**
         * @return wether to bold
         */
        public boolean isBold() {
            return boldCheckBox.isSelected();
        }

        /**
         * @return wether to italic
         */
        public boolean isItalic() {
            return italicCheckBox.isSelected();
        }

        /**
         * set the colour
         */
        private void setColour() {
            Color c = JColorChooser.showDialog(this, label.getText() + " Color", colorPanel.getBackground());
            if (c != null) {
                colorPanel.setBackground(c);
            }
        }

        /**
         * set the values
         *
         * @param c the current color
         * @param b wether it is bold
         * @param i wether it is italic
         */
        public void setValues(Color c, boolean b, boolean i) {
            colorPanel.setBackground(c);
            boldCheckBox.setSelected(b);
            italicCheckBox.setSelected(i);
        }
    }

    /**
     * the background color panel
     */
    private JPanel backgroundColorPanel;

    /**
     * the block comment style panel
     */
    private SyntaxOptionsPanel blockCommentPanel;

    /**
     * wether the user pressed cancel
     */
    private boolean cancel;

    /**
     * the character style panel
     */
    private SyntaxOptionsPanel characterPanel;

    /**
     * the font size
     */
    private JIntegerField fontSizeField;

    /**
     * the hex style panel
     */
    private SyntaxOptionsPanel hexPanel;

    /**
     * the highlight colour panel
     */
    private JPanel highlightColorPanel;

    /**
     * the key style panel
     */
    private SyntaxOptionsPanel keyPanel;

    /**
     * the line comment style panel
     */
    private SyntaxOptionsPanel lineCommentPanel;

    /**
     * the normal style panel
     */
    private SyntaxOptionsPanel normalPanel;

    /**
     * the number style panel
     */
    private SyntaxOptionsPanel numberPanel;

    /**
     * the octal style panel
     */
    private SyntaxOptionsPanel octalPanel;

    /**
     * the operator style panel
     */
    private SyntaxOptionsPanel operatorPanel;

    /**
     * the editor options
     */
    private EditorOptions options;

    /**
     * the reserved style panel
     */
    private SyntaxOptionsPanel reservedPanel;

    /**
     * the string style panel
     */
    private SyntaxOptionsPanel stringPanel;

    /**
     * the tab size
     */
    private JIntegerField tabSizeField;

    /**
     * Constructor
     *
     * @param zeptor the editor
     */
    public EditorOptionsDialog(final Zeptor zeptor) {
        super(zeptor);
        options = zeptor.getEditorOptions();
        init();
    }

    /**
     * user cancelled operation
     */
    private void cancel() {
        cancel = true;
        dispose();
    }

    /**
     * close the dialog
     */
    private void closeDialog() {
        cancel = false;
        options.setFontSize(fontSizeField.getInteger());
        options.setTabSize(tabSizeField.getInteger());
        options.setBackgroundColor(backgroundColorPanel.getBackground());
        options.setHighlightColor(highlightColorPanel.getBackground());
        options.setNormalStyle(normalPanel.getStyle());
        options.setKeyStyle(keyPanel.getStyle());
        options.setReservedStyle(reservedPanel.getStyle());
        options.setLineCommentStyle(lineCommentPanel.getStyle());
        options.setBlockCommentStyle(blockCommentPanel.getStyle());
        options.setNumberStyle(numberPanel.getStyle());
        options.setHexStyle(hexPanel.getStyle());
        options.setOctalStyle(octalPanel.getStyle());
        options.setStringStyle(stringPanel.getStyle());
        options.setCharacterStyle(characterPanel.getStyle());
        options.setOperatorStyle(operatorPanel.getStyle());
        dispose();

    }

    /**
     * initialize the dialog
     */
    private void init() {
        this.setTitle("Source Syntax Preferences");
        this.setSize(500, 500);
        this.setResizable(true);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Dimension d1 = this.getSize();
        Dimension d2 = Toolkit.getDefaultToolkit().getScreenSize();
        int w1 = (int) d1.getWidth();
        int h1 = (int) d1.getHeight();
        int w2 = (int) d2.getWidth();
        int h2 = (int) d2.getHeight();
        this.setLocation(new Point((w2 - w1) / 2, (h2 - h1) / 2));

        this.setModal(true);

        Container pane = this.getContentPane();
        pane.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton okButton = new JButton("OK");
        okButton.setMnemonic('O');
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeDialog();
            }
        });
        buttonPanel.add(okButton);

        JButton undoButton = new JButton("Undo");
        undoButton.setMnemonic('D');
        undoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                undoChanges();
            }
        });
        buttonPanel.add(undoButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setMnemonic('C');
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
        buttonPanel.add(cancelButton);

        JPanel center = new JPanel(new GridLayout(19, 1));

        JPanel row1 = new JPanel(new GridLayout(1, 4));

        fontSizeField = new JIntegerField(2, false);
        fontSizeField.setInteger(options.getFontSize());
        row1.add(fontSizeField);
        row1.add(new JPanel());
        row1.add(new JPanel());
        row1.add(new JPanel());

        JPanel row2 = new JPanel(new GridLayout(1, 4));

        tabSizeField = new JIntegerField(2, false);
        tabSizeField.setInteger(options.getTabSize());
        row2.add(tabSizeField);
        row2.add(new JPanel());
        row2.add(new JPanel());
        row2.add(new JPanel());

        JPanel row5 = new JPanel(new GridLayout(1, 4));
        row5.add(new JPanel());
        row5.add(new JPanel());
        backgroundColorPanel = new JPanel();
        backgroundColorPanel.setBackground(options.getBackgroundColor());
        row5.add(backgroundColorPanel);
        JButton setBackgroundColourButton = new JButton("Set Color");
        setBackgroundColourButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setBackgroundColor();
            }
        });
        row5.add(setBackgroundColourButton);

        blockCommentPanel = new SyntaxOptionsPanel("Block Comment",
                options.getBlockCommentStyle().getColor(),
                options.getBlockCommentStyle().isBold(),
                options.getBlockCommentStyle().isItalic());

        characterPanel = new SyntaxOptionsPanel("Character Literal",
                options.getCharacterStyle().getColor(),
                options.getCharacterStyle().isBold(),
                options.getCharacterStyle().isItalic());

        hexPanel = new SyntaxOptionsPanel("Hexadecimal Number",
                options.getHexStyle().getColor(),
                options.getHexStyle().isBold(),
                options.getHexStyle().isItalic());

        JPanel row8 = new JPanel(new GridLayout(1, 4));
        row8.add(new JPanel());
        row8.add(new JPanel());
        highlightColorPanel = new JPanel();
        highlightColorPanel.setBackground(options.getHighlightColor());
        row8.add(highlightColorPanel);
        JButton setHighlightColourButton = new JButton("Set Color");
        setHighlightColourButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setHighlightColor();
            }
        });
        row8.add(setHighlightColourButton);

        keyPanel = new SyntaxOptionsPanel("Keyword",
                options.getKeyStyle().getColor(),
                options.getKeyStyle().isBold(),
                options.getKeyStyle().isItalic());

        lineCommentPanel = new SyntaxOptionsPanel("Line Comment",
                options.getLineCommentStyle().getColor(),
                options.getLineCommentStyle().isBold(),
                options.getLineCommentStyle().isItalic());

        normalPanel = new SyntaxOptionsPanel("Normal Text",
                options.getNormalStyle().getColor(),
                options.getNormalStyle().isBold(),
                options.getNormalStyle().isItalic());

        numberPanel = new SyntaxOptionsPanel("Decimal Number",
                options.getNumberStyle().getColor(),
                options.getNumberStyle().isBold(),
                options.getNumberStyle().isItalic());

        octalPanel = new SyntaxOptionsPanel("Octal Number",
                options.getOctalStyle().getColor(),
                options.getOctalStyle().isBold(),
                options.getOctalStyle().isItalic());

        operatorPanel = new SyntaxOptionsPanel("Operator",
                options.getOperatorStyle().getColor(),
                options.getOperatorStyle().isBold(),
                options.getOperatorStyle().isItalic());

        reservedPanel = new SyntaxOptionsPanel("Reserved Word",
                options.getReservedStyle().getColor(),
                options.getReservedStyle().isBold(),
                options.getReservedStyle().isItalic());

        stringPanel = new SyntaxOptionsPanel("String Literal",
                options.getStringStyle().getColor(),
                options.getStringStyle().isBold(),
                options.getStringStyle().isItalic());

        center.add(row1);
        center.add(row2);
        center.add(new JPanel());

        center.add(row5);
        center.add(blockCommentPanel);
        center.add(characterPanel);
        center.add(hexPanel);

        center.add(row8);
        center.add(keyPanel);
        center.add(lineCommentPanel);
        center.add(normalPanel);
        center.add(numberPanel);
        center.add(octalPanel);
        center.add(operatorPanel);
        center.add(reservedPanel);
        center.add(stringPanel);
        center.add(new JPanel());

        JPanel west = new JPanel(new GridLayout(19, 1));
        west.add(new JLabel("Font Size", JLabel.LEFT));
        west.add(new JLabel("Tab Size", JLabel.LEFT));
        west.add(new JPanel());

        west.add(new JLabel("Background", JLabel.LEFT));
        west.add(new JLabel("Block Comment ", JLabel.LEFT));
        west.add(new JLabel("Character Literal", JLabel.LEFT));
        west.add(new JLabel("Hexadecimal Number", JLabel.LEFT));
        west.add(new JLabel("Highlight Text", JLabel.LEFT));
        west.add(new JLabel("Keyword", JLabel.LEFT));
        west.add(new JLabel("Line Comment", JLabel.LEFT));
        west.add(new JLabel("Normal Text", JLabel.LEFT));
        west.add(new JLabel("Number", JLabel.LEFT));
        west.add(new JLabel("Octal Number", JLabel.LEFT));
        west.add(new JLabel("Operator", JLabel.LEFT));
        west.add(new JLabel("Reserved Word", JLabel.LEFT));
        west.add(new JLabel("String Literal", JLabel.LEFT));

        JPanel main = new JPanel(new BorderLayout());

        pane.add(new JPanel(), BorderLayout.NORTH);
        pane.add(new JPanel(), BorderLayout.EAST);
        pane.add(new JPanel(), BorderLayout.WEST);
        main.add(west, BorderLayout.WEST);
        main.add(center, BorderLayout.CENTER);

        pane.add(main, BorderLayout.CENTER);
        pane.add(buttonPanel, BorderLayout.SOUTH);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeDialog();
            }
        });

        this.pack();
        this.setVisible(true);
    }//end init

    /**
     * @return wether the user pressed cancel
     */
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * set the background colour
     */
    private void setBackgroundColor() {
        Color c = JColorChooser.showDialog(this, "Background Color", backgroundColorPanel.getBackground());
        if (c != null) {
            backgroundColorPanel.setBackground(c);
        }
    }

    /**
     * set the highlight colour
     */
    private void setHighlightColor() {
        Color c = JColorChooser.showDialog(this, "Highlight Text Color", highlightColorPanel.getBackground());
        if (c != null) {
            highlightColorPanel.setBackground(c);
        }
    }

    /**
     * set to value s before changes
     */
    private void undoChanges() {
        EditorOptions o = new EditorOptions();
        fontSizeField.setInteger(o.getFontSize());
        tabSizeField.setInteger(o.getTabSize());
        SyntaxStyle s;
        s = o.getNormalStyle();
        normalPanel.setValues(s.getColor(), s.isBold(), s.isItalic());
        s = o.getKeyStyle();
        keyPanel.setValues(s.getColor(), s.isBold(), s.isItalic());
        s = o.getReservedStyle();
        reservedPanel.setValues(s.getColor(), s.isBold(), s.isItalic());
        s = o.getLineCommentStyle();
        lineCommentPanel.setValues(s.getColor(), s.isBold(), s.isItalic());
        s = o.getBlockCommentStyle();
        blockCommentPanel.setValues(s.getColor(), s.isBold(), s.isItalic());
        s = o.getNumberStyle();
        numberPanel.setValues(s.getColor(), s.isBold(), s.isItalic());
        s = o.getHexStyle();
        hexPanel.setValues(s.getColor(), s.isBold(), s.isItalic());
        s = o.getOctalStyle();
        octalPanel.setValues(s.getColor(), s.isBold(), s.isItalic());
        s = o.getStringStyle();
        stringPanel.setValues(s.getColor(), s.isBold(), s.isItalic());
        s = o.getCharacterStyle();
        characterPanel.setValues(s.getColor(), s.isBold(), s.isItalic());
        s = o.getOperatorStyle();
        operatorPanel.setValues(s.getColor(), s.isBold(), s.isItalic());
        this.repaint();
    }
}
