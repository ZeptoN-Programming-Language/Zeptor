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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import xyz.zepton.zeptor.gui.JIntegerField;
import xyz.zepton.zeptor.log.Logger;

/**
 * A dialog to let user select the line number to go to
 *
 * @author Tan Hong Cheong
 * @version 20041215
 */
public class GotoDialog extends JDialog implements KeyListener {

    private static final long serialVersionUID = -1924936173811146109L;
    /**
     * wether the user pressed cancel
     */
    private boolean cancel;
    /**
     * A digit field
     */
    private JIntegerField digitField;

    /**
     * @param owner the owner of this dialog
     */
    public GotoDialog(Frame owner) {
        super(owner);
        setTitle("Goto Line");
        init();
    }

    private void cancel() {
        cancel = true;
        this.dispose();
    }

    /**
     * @return the digit field value
     */
    public int getLineNo() {
        try {
            return digitField.getInteger() + 1;
        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }

        return 1;
    }

    private void init() {
        this.setModal(true);
        cancel = true;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        //so that dialog would be disposed when it is close
        //i.e by pressing the x button
        this.setSize(200, 100); //width x height
        this.setResizable(false);
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

        JButton okButton = new JButton("OK");
        okButton.setMnemonic('O');
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok();
            }
        });
        buttonPanel.add(okButton);
        this.getRootPane().setDefaultButton(okButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setMnemonic('C');
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
        buttonPanel.add(cancelButton);

        pane.add(buttonPanel, BorderLayout.SOUTH);

        //Box center = Box.createHorizontalBox();
        JPanel center = new JPanel(new GridLayout(1, 2));
        center.add(new JLabel("Goto Line: ", JLabel.LEFT));

        digitField = new JIntegerField(false);
        center.add(digitField);

        pane.add(center, BorderLayout.CENTER);
        pane.add(new JPanel(), BorderLayout.NORTH);
        pane.add(new JPanel(), BorderLayout.EAST);
        pane.add(new JPanel(), BorderLayout.WEST);

        this.addKeyListener(this);
        digitField.addKeyListener(this);
        okButton.addKeyListener(this);
        cancelButton.addKeyListener(this);
        this.setVisible(true);
    }

    /**
     * wether the user pressed cancel
     */
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * As defined in interface keyListener
     */
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            ok();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            cancel();
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
     * user pressed ok
     */
    private void ok() {
        //check if there is value value in the digit field
        try {
            cancel = false;
        } catch (NumberFormatException ex) {
            cancel = true;
            Logger.LOG.logTrap(ex);
        }
        this.dispose();
    }
}
