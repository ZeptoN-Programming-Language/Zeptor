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

import java.io.File;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;

/**
 * A simple file filter Most of the code is copied from java demo
 * FileChooserDemo
 *
 * @author Tan Hong Cheong
 * @version 20041210
 */
public class MyFileFilter extends FileFilter {

    /**
     * Description
     */
    private String description;

    /**
     * Vector to store all the extension
     */
    private ArrayList<String> extensions;

    /**
     * A file filter that accepts all files and no description Use addExtension
     * to specify extension
     */
    public MyFileFilter() {
        extensions = new ArrayList<>();
        description = "No Description";
    }

    /**
     * @param ext the extension
     * @param des the description
     */
    public MyFileFilter(String ext, String des) {
        description = des;
        extensions = new ArrayList<>();
        extensions.add(ext);
    }

    /**
     * @param exts the array of extensions
     * @param des the description
     */
    public MyFileFilter(String[] exts, String des) {
        description = des;
        extensions = new ArrayList<>();
        for (int i = 0; i < exts.length; i++) {
            extensions.add(exts[i]);
        }
    }

    public boolean accept(File file) {

        if (file.isDirectory()) {
            return true;
        } else if (file.getName().toLowerCase().endsWith(".zep")) {
            return true;
        }

        return false;

    }//end accept

    public void addExtension(String ext) {
        extensions.add(ext);
    }

    /**
     * Defined in interface FileFilter
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the extension of file f
     * @param f the file the extension to get
     */
    public String getExtension(File f) {

        String[] str = f.getName().split("\\.");

        System.out.printf("getExtension: %s%n", str[str.length - 1]);

        return str[str.length - 1];

    }

    /**
     * Set the description
     *
     * @param s the description
     */
    public void setDescription(String s) {
        description = s;
    }

}
