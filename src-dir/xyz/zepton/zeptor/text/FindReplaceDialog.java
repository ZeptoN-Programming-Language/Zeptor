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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * <p>
 * A dialog to find and replace dialog
 *
 * @author Tan Hong Cheong
 * @version 20040705
 */
public class FindReplaceDialog extends JDialog implements KeyListener {

    private static final long serialVersionUID = -8433105992689634312L;
    /**
     * The current position to start searching
     */
    private int current;
    /**
     * The match case check box
     */
    private JCheckBox matchCaseCheckBox;
    /**
     * The match whole word check box
     */
    private JCheckBox matchWholeWordCheckBox;
    /**
     * The replace button
     */
    private JButton replaceButton;
    /**
     * The replace text Field
     */
    private JTextField replaceTextField;
    /**
     * The search text Field
     */
    private JTextField searchTextField;
    /**
     * The textPane
     */
    private final MyJTextPane textPane;

    /**
     * Constructor Create a modal find and replace dialog
     *
     * @param owner The owner of this dialog
     * @param t the textPane
     */
    public FindReplaceDialog(Frame owner, MyJTextPane t) {
        //super(owner,"Find and replace",false);
        super(owner);
        setTitle("Replace");
        setModal(true);
        textPane = t;
        current = 0;
        init();
    }

    /**
     * User pressed find
     */
    private void find() {
        int pos = textPane.find(getSearchString(), isCaseSensitive(), isWholeWord(), current);
        if (pos == -1) {
            replaceButton.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Cannot find " + getSearchString() + ".", "Information", JOptionPane.PLAIN_MESSAGE, null);
            textPane.highlight(0, 0);
        } else {
            //select the text
            current = pos + getSearchString().length();
            textPane.highlight(pos, current);
            textPane.setCaretPosition(current);
            replaceButton.setEnabled(true);
        }
    }

    /**
     * @return the text to replace
     */
    public String getReplaceString() {
        return replaceTextField.getText();
    }

    /**
     * @return the text to find
     */
    public String getSearchString() {
        return searchTextField.getText();
    }

    /**
     * hide the dialog and enable the text fields
     */
    private void hideDialog() {
        setVisible(false);
        searchTextField.setEditable(true);
        replaceTextField.setEditable(true);
        current = 0;//reset so that start seeaching from pos 0
    }

    /**
     * Initialize the dialog
     */
    private void init() {
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        //so that dialog would be disposed when it is close
        //i.e by pressing the x button
        this.setSize(450, 150);
        this.setResizable(true);

        Dimension d1 = this.getSize();
        Dimension d2 = Toolkit.getDefaultToolkit().getScreenSize();
        int w1 = (int) d1.getWidth();
        int h1 = (int) d1.getHeight();
        int w2 = (int) d2.getWidth();
        int h2 = (int) d2.getHeight();
        this.setLocation(new Point((w2 - w1) / 2, (h2 - h1) / 2));

        Container pane = this.getContentPane();
        pane.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton findButton = new JButton("Find Next");
        findButton.setMnemonic('F');
        findButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchTextField.setEditable(false);
                replaceTextField.setEditable(false);
                find();
            }
        });
        buttonPanel.add(findButton);

        replaceButton = new JButton("Replace");
        replaceButton.setMnemonic('R');
        replaceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textPane.select(current - getSearchString().length(), current);
                current = textPane.replace(getReplaceString());
                find();
            }
        });
        buttonPanel.add(replaceButton);
        replaceButton.setEnabled(false);

        JButton replaceAllButton = new JButton("Replace All");
        replaceAllButton.setMnemonic('A');
        replaceAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                replaceAll();
            }
        });
        buttonPanel.add(replaceAllButton);

        JButton doneButton = new JButton("Done");
        doneButton.setMnemonic('D');
        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hideDialog();
            }
        });
        buttonPanel.add(doneButton);

        pane.add(buttonPanel, BorderLayout.SOUTH);

        JPanel center = new JPanel(new GridLayout(4, 1));

        JPanel p1 = new JPanel(new GridLayout(1, 2));

        //  Find What:__
        //  Replace With:__
        p1.add(new JLabel(" Find What:  ", JLabel.LEFT));
        searchTextField = new JTextField(20);
        searchTextField.addKeyListener(this);
        p1.add(searchTextField);

        center.add(p1);

        JPanel p2 = new JPanel(new GridLayout(1, 2));
        p2.add(new JLabel(" Replace With:  ", JLabel.LEFT));
        replaceTextField = new JTextField(20);
        replaceTextField.addKeyListener(this);
        p2.add(replaceTextField);

        center.add(p2);

        matchCaseCheckBox = new JCheckBox("Match Case");
        matchCaseCheckBox.addKeyListener(this);
        center.add(matchCaseCheckBox);

        matchWholeWordCheckBox = new JCheckBox("Match Whole Word");
        matchWholeWordCheckBox.addKeyListener(this);
        center.add(matchWholeWordCheckBox);

        pane.add(center, BorderLayout.CENTER);

        this.addKeyListener(this);
    }

    /**
     * @return wether to search for word that match case
     */
    public boolean isCaseSensitive() {
        return matchCaseCheckBox.isSelected();
    }

    /**
     * @return wether to search for whole word only
     */
    public boolean isWholeWord() {
        return matchWholeWordCheckBox.isSelected();
    }

    /**
     * As defined in interface keyListener
     */
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            find();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            hideDialog();
        }
    }

    /**
     * As defined in interface keyListener
     */
    public void keyReleased(KeyEvent e) {
    }

    /**
     * As defined in interface keyListener
     */
    public void keyTyped(KeyEvent e) {
    }

    /**
     * User pressed replace all
     */
    public void replaceAll() {
        current = 0;
        int pos = textPane.find(getSearchString(), isCaseSensitive(), isWholeWord(), current);
        while (pos != -1) {
            //select the text
            current = pos + getSearchString().length();
            textPane.highlight(pos, current);
            textPane.select(current - getSearchString().length(), current);
            current = textPane.replace(getReplaceString());
            pos = textPane.find(getSearchString(), isCaseSensitive(), isWholeWord(), current);
        }

        JOptionPane.showMessageDialog(this, "Done", "Information", JOptionPane.PLAIN_MESSAGE, null);

        hideDialog();
    }
}
