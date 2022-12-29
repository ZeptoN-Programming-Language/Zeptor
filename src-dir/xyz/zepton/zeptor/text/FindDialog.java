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
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * <p>
 * A dialog to find dialog
 *
 * @version 20041021
 * @author Tan Hong Cheong
 */
public class FindDialog extends JDialog implements InputMethodListener, KeyListener, WindowFocusListener {

    private static final long serialVersionUID = -2356033847831992393L;

    public final static Point centerWidget(final Dimension sizeWidget, final Dimension sizeScreen) {
        Dimension d1 = sizeWidget;
        Dimension d2 = sizeScreen;
        int w1 = (int) d1.getWidth();
        int h1 = (int) d1.getHeight();
        int w2 = (int) d2.getWidth();
        int h2 = (int) d2.getHeight();
        return new Point((w2 - w1) / 2, (h2 - h1) / 2);
    }//end centerWidget

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
     * The textField
     */
    private JTextField textField;

    /**
     * The textPane
     */
    private MyJTextPane textPane;

    /**
     * Constructor Create a modal find dialog
     *
     * @param owner The owner of this dialog
     * @param t the textPane
     */
    public FindDialog(Frame owner, MyJTextPane t) {
        super(owner);
        setTitle("Find");
        setModal(true);
        textPane = t;
        current = 0;
        init();
    }//end constructor

    /**
     * As defined in InputMethodListener
     */
    public void caretPositionChanged(InputMethodEvent event) {
    }

    /**
     * User pressed find
     */
    private void find() {
        int pos = textPane.find(getSearchString(), isCaseSensitive(), isWholeWord(), current);
        if (pos == -1) {
            JOptionPane.showMessageDialog(this, "Cannot find text.", "Information", JOptionPane.PLAIN_MESSAGE, null);
            setVisible(false);
            current = 0;//reset so that start seeaching from pos 0
        } else {
            //select the text
            current = pos + getSearchString().length();
            textPane.highlight(pos, current);
            textPane.setCaretPosition(current);
        }
    }

    /**
     * @return the text to find
     */
    public String getSearchString() {
        return textField.getText();
    }

    /**
     * Initialize the dialog
     */
    private void init() {
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        //so that dialog would be disposed when it is close
        //i.e by pressing the x button
        this.setSize(350, 150);
        this.setResizable(true);
        this.setModal(false);

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

        JButton findFromStartButton = new JButton("Find from Start");
        findFromStartButton.setMnemonic('S');
        findFromStartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                current = 0;
                find();
            }
        });
        buttonPanel.add(findFromStartButton);

        JButton findButton = new JButton("Find Next");
        findButton.setMnemonic('F');
        findButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                find();
            }
        });
        buttonPanel.add(findButton);

        JButton doneButton = new JButton("Done");
        doneButton.setMnemonic('D');
        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        buttonPanel.add(doneButton);

        pane.add(buttonPanel, BorderLayout.SOUTH);

        JPanel center = new JPanel(new GridLayout(3, 1));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(new JLabel("Find What", JLabel.LEFT));
        textField = new JTextField(20);
        textField.addInputMethodListener(this);

        panel.add(textField);

        center.add(panel);

        matchCaseCheckBox = new JCheckBox("Match Case");
        matchCaseCheckBox.addKeyListener(this);

        center.add(matchCaseCheckBox);

        matchWholeWordCheckBox = new JCheckBox("Match Whole Word");
        matchWholeWordCheckBox.addKeyListener(this);
        center.add(matchWholeWordCheckBox);

        pane.add(center, BorderLayout.CENTER);

        this.addKeyListener(this);
        textField.addKeyListener(this);
        doneButton.addKeyListener(this);
        findButton.addKeyListener(this);

        this.addWindowFocusListener(this);
    }

    /**
     * As defined in InputMethodListener
     */
    public void inputMethodTextChanged(InputMethodEvent event) {
        current = 0;//search text had changed, reset current
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
            setVisible(false);
            current = 0;//reset so that start seeaching from pos 0
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
     * override the setvisibile method
     *
     * @param flag whether to make it visible
     */
    public void setVisible(boolean flag) {
        super.setVisible(flag);
        if (flag) {
            current = textPane.getCaretPosition();
        }
    }

    /**
     * As defined in interface WindowFocusListener
     */
    public void windowGainedFocus(WindowEvent e) {
        //set the focus to textfield
        textField.grabFocus();
        //select the text
        String s = textField.getText();
        textField.setSelectionStart(0);
        textField.setSelectionEnd(s.length());

    }

    public void windowLostFocus(WindowEvent e) {
    }
}
