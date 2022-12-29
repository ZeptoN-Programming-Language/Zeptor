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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class SystemProps implements Runnable, ActionListener {

    JFrame frame = new JFrame("System Properties");

    @Override
    public void actionPerformed(ActionEvent e) {
        frame.setVisible(false);
        frame.dispose();
    }

    private DefaultTableModel createSystemPropertiesTableModel() {
        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("Property");
        model.addColumn("Value");

        Properties p = System.getProperties();
        Set<Object> keys = p.keySet();
        SortedSet<Object> sortedKeys = new TreeSet<Object>(keys);
        Iterator<Object> iter = sortedKeys.iterator();

        while (iter.hasNext()) {
            String key = iter.next().toString();
            String value = p.getProperty(key);
            String[] row = {key, value};
            model.addRow(row);
        }

        return model;
    }

    @Override
    public void run() {
        JButton button = new JButton("Close");

        button.addActionListener(this);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container content = frame.getContentPane();
        JTable table = new JTable(createSystemPropertiesTableModel()) {
            private static final long serialVersionUID = 4957089825220999913L;

            @Override
            public Component prepareRenderer(TableCellRenderer renderer,
                    int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (c instanceof JComponent) {
                    JComponent jc = (JComponent) c;
                    String s = getValueAt(row, column).toString();
                    jc.setToolTipText(s);
                }
                return c;
            }
        };

        table.setShowGrid(true);

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);

        content.add(scrollPane, BorderLayout.CENTER);

        JPanel flowPanel = new JPanel(new FlowLayout());
        flowPanel.add(button);
        content.add(flowPanel, BorderLayout.SOUTH);

        DefaultTableCellRenderer header = new DefaultTableCellRenderer();
        header.setFont(header.getFont().deriveFont(Font.BOLD));

        TableColumnModel model = table.getColumnModel();
        model.getColumn(0).setHeaderRenderer(header);
        model.getColumn(1).setHeaderRenderer(header);

        // make the frame half the height and width
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);

        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

}//end class SystemProps
