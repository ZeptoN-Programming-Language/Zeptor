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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import xyz.zepton.zeptor.log.Logger;

/**
 * A class to specify the editor options
 *
 * @author Tan Hong Cheong
 * @version 20040617
 */
public class EditorOptions {

    private static final HashMap<String, Integer> configMap = new HashMap<>();
    public static final Color DEFAULT_BACKGROUND_COLOR = Color.BLUE.darker();
    public static final int DEFAULT_FONT_SIZE = 18;
    public static final Color DEFAULT_HIGHLIGHT_COLOR = Color.PINK;
    public static final int DEFAULT_TAB_SIZE = 2;
    public static final SyntaxStyle STYLE_BLOCK_COMMENT = new SyntaxStyle(Color.PINK, true, true);
    public static final SyntaxStyle STYLE_CHARACTER = new SyntaxStyle(Color.CYAN, false, true);
    public static final SyntaxStyle STYLE_HEX = new SyntaxStyle(Color.ORANGE, false, false);
    public static final SyntaxStyle STYLE_KEY = new SyntaxStyle(Color.WHITE, true, false);
    public static final SyntaxStyle STYLE_LINE_COMMENT = new SyntaxStyle(Color.PINK, false, true);
    public static final SyntaxStyle STYLE_NORMAL = new SyntaxStyle(Color.YELLOW, false, false);

    public static final SyntaxStyle STYLE_NUMBER = new SyntaxStyle(Color.GREEN, false, false);
    public static final SyntaxStyle STYLE_OCTAL = new SyntaxStyle(Color.ORANGE, false, false);
    public static final SyntaxStyle STYLE_OPERATOR = new SyntaxStyle(Color.YELLOW, false, false);
    public static final SyntaxStyle STYLE_RESERVED = new SyntaxStyle(Color.WHITE, false, false);

    public static final SyntaxStyle STYLE_STRING = new SyntaxStyle(Color.CYAN, false, true);
    private static final HashMap<String, SyntaxStyle> styleMap = new HashMap<>();

    public final static EditorOptions fileImportYAML(final File file) {

        final EditorOptions editorOptions = new EditorOptions();

        try {

            List<String> yamlLine = Files.readAllLines(Paths.get(file.getCanonicalPath()));

            //remove comments and blank lines
            for (int x = 0; x < yamlLine.size(); x++) {

                String line = yamlLine.get(x);
                if (line.length() == 0) {
                    yamlLine.remove(x);
                    x--;
                    continue;
                }

                if (line.charAt(0) == '#') {
                    yamlLine.remove(x);
                    x--;
                }

            }//end for

            //check first lineText for '---' in YAML
            String lineText = yamlLine.get(0).trim();

            if (!lineText.equals("---")) {
                throw new RuntimeException("InvalidYAMLException: Invalid YAML file; first line does not begin with '---' header.");
            }

            yamlLine.remove(0);

            int len = yamlLine.size();

            lineText = yamlLine.get(len - 1).trim();

            //check last lineText for '...' in YAML
            if (!lineText.equals("...")) {
                throw new RuntimeException("InvalidYAMLException: Invalid YAML file; last line line does not close with '...' footer.");
            }

            yamlLine.remove(len - 1);

            boolean styleFlag = false;
            boolean configFlag = false;

            for (String line : yamlLine) {

                if (line.equals("Style:")) {
                    styleFlag = true;
                    configFlag = false;
                    continue;
                }

                if (line.equals("Config:")) {
                    styleFlag = false;
                    configFlag = true;
                    continue;
                }

                if (line.startsWith("  ")) {

                    String[] lineKeyValue = line.split(":");
                    String key = lineKeyValue[0];

                    if (styleFlag) {
                        String[] values = lineKeyValue[1].split(",");
                        SyntaxStyle style = SyntaxStyle.New(values[0], //hex color 
                                values[1], //bold 0,1
                                values[2]); //italic 0,1

                        EditorOptions.styleMap.put(key.trim(), style);
                    }//end if

                    if (configFlag) {
                        Integer value = (int) Long.parseLong(lineKeyValue[1].trim(), 16);
                        configMap.put(key.trim(), value);
                    }//end if

                } else {
                    Logger.LOG.logText("Line does not contain indentation for key to value mapping.");
                }//end if

            }//end for

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);

            //initiate with defaults
            editorOptions.initializeDefaultSyntaxConfig();
        }//end try

        return editorOptions;

    }//end fileImportYAML

    public EditorOptions() {
        initializeDefaultSyntaxConfig();
    }//end null constructor

    public final void fileExportYAML(final File file) {

        final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MMM-dd hh:mm");

        try {

            final String user = System.getProperty("user.name");
            final LocalDateTime ldt = LocalDateTime.now();

            FileWriter fw = new FileWriter(file);

            //open file writer
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write("---");
            bw.newLine();

            //write comment username, date
            bw.write(String.format("# Created %s by user %s", ldt.format(dateFormat), user));
            bw.newLine();
            bw.newLine();
            //write comment each map, then values
            bw.write("# Styles for syntax");
            bw.newLine();

            //iterate through hashMap - style
            bw.write("Style:");
            bw.newLine();

            Set<String> keys = null;

            keys = EditorOptions.styleMap.keySet(); //key set is duplicates

            for (String key : keys) {
                bw.write(String.format("    %s: %s", key, EditorOptions.styleMap.get(key)));
                bw.newLine();
            }//end for

            bw.newLine();

            //iterate through config
            bw.write("# Config for editor");
            bw.newLine();

            bw.write("Config:");
            bw.newLine();

            //write key ; value or key : style, 1, 1
            keys = EditorOptions.configMap.keySet();

            for (String key : keys) {
                bw.write(String.format("    %s: %x", key, EditorOptions.configMap.get(key)));
                bw.newLine();
            }//end for

            bw.newLine();

            bw.write("...");
            bw.newLine();

            bw.flush();
            fw.flush();

            bw.close();
            fw.close();

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try

    }//end fileExportYAML

    /**
     * @return the background color
     */
    public Color getBackgroundColor() {

        Integer value = configMap.get("background");
        return new Color(value);
    }

    /**
     * @return the block comment style
     */
    public SyntaxStyle getBlockCommentStyle() {
        SyntaxStyle style = EditorOptions.styleMap.get("block");
        return style;
    }

    /**
     * @return the character style
     */
    public SyntaxStyle getCharacterStyle() {
        SyntaxStyle s = EditorOptions.styleMap.get("character");
        return s;
    }

    /**
     * @return the font size
     */
    public int getFontSize() {
        int size = configMap.get("font");
        return size;
    }

    /**
     * @return the hex style
     */
    public SyntaxStyle getHexStyle() {
        SyntaxStyle s = EditorOptions.styleMap.get("hex");
        return s;
    }

    /**
     * @return the highlight color
     */
    public Color getHighlightColor() {
        int value = configMap.get("highlight");
        Color color = new Color(value);
        return color;
    }

    /**
     * @return the key style
     */
    public SyntaxStyle getKeyStyle() {
        SyntaxStyle s = EditorOptions.styleMap.get("key");
        return s;
    }

    /**
     * @return the lineText comment style
     */
    public SyntaxStyle getLineCommentStyle() {
        SyntaxStyle s = EditorOptions.styleMap.get("line");
        return s;
    }

    public int getLookAndFeel() {
        Integer lafIndex = configMap.get("laf");
        if(lafIndex == null) {
        	lafIndex = 20; //default platform
        }
        return lafIndex;
    }//end getLookAndFeelIndex

    /**
     * @return the normal style
     */
    public SyntaxStyle getNormalStyle() {
        SyntaxStyle s = EditorOptions.styleMap.get("normal");
        return s;
    }

    /**
     * @return the number style
     */
    public SyntaxStyle getNumberStyle() {
        SyntaxStyle s = EditorOptions.styleMap.get("number");
        return s;
    }

    /**
     * @return the octal style
     */
    public SyntaxStyle getOctalStyle() {
        SyntaxStyle s = EditorOptions.styleMap.get("octal");
        return s;
    }

    /**
     * @return the operator style
     */
    public SyntaxStyle getOperatorStyle() {
        SyntaxStyle s = EditorOptions.styleMap.get("operator");
        return s;
    }

    /**
     * @return the reserved style
     */
    public SyntaxStyle getReservedStyle() {
        SyntaxStyle s = EditorOptions.styleMap.get("reserved");
        return s;
    }

    /**
     * @return the string style
     */
    public SyntaxStyle getStringStyle() {
        SyntaxStyle s = EditorOptions.styleMap.get("string");
        return s;
    }

    /**
     * @return the tab size
     */
    public int getTabSize() {
        int value = configMap.get("tab");
        return value;
    }

    /**
     * set all options to default values
     */
    public void initializeDefaultSyntaxConfig() {

        //create defaults class with options
        setTabSize(DEFAULT_TAB_SIZE);
        setFontSize(DEFAULT_FONT_SIZE);

        setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
        setHighlightColor(DEFAULT_HIGHLIGHT_COLOR);

        EditorOptions.styleMap.put("normal", STYLE_NORMAL);

        EditorOptions.styleMap.put("key", STYLE_KEY);

        EditorOptions.styleMap.put("reserved", STYLE_RESERVED);

        EditorOptions.styleMap.put("line", STYLE_LINE_COMMENT);

        EditorOptions.styleMap.put("block", STYLE_BLOCK_COMMENT);

        EditorOptions.styleMap.put("number", STYLE_NUMBER);

        EditorOptions.styleMap.put("octal", STYLE_OCTAL);

        EditorOptions.styleMap.put("hex", STYLE_HEX);

        EditorOptions.styleMap.put("string", STYLE_STRING);

        EditorOptions.styleMap.put("character", STYLE_CHARACTER);

        EditorOptions.styleMap.put("operator", STYLE_OPERATOR);

    }//end initializeDefaultSyntaxConfig

    /**
     * set the background color
     *
     * @param c the background color
     */
    public void setBackgroundColor(Color c) {
        Integer value = c.getRGB();
        configMap.put("background", value);
    }

    /**
     * set block comment style
     *
     */
    public void setBlockCommentStyle(SyntaxStyle style) {
        EditorOptions.styleMap.put("block", style);
    }

    /**
     * set character style
     *
     * @param s the style
     */
    public void setCharacterStyle(SyntaxStyle s) {
        EditorOptions.styleMap.put("character", s);
    }

    /**
     * set the font size
     *
     * @param size the tab size
     */
    public void setFontSize(final int size) {
        configMap.put("font", size);
    }

    /**
     * set hex style
     *
     * @param s the style
     */
    public void setHexStyle(SyntaxStyle s) {
        EditorOptions.styleMap.put("hex", s);
    }

    /**
     * Set the highlight color
     *
     * @param c the highlight color
     */
    public void setHighlightColor(Color c) {
        configMap.put("highlight", c.getRGB());
    }

    /**
     * set key style
     *
     * @param s the style
     */
    public void setKeyStyle(SyntaxStyle s) {
        EditorOptions.styleMap.put("key", s);
    }

    /**
     * set lineText comment style
     *
     * @param s the style
     */
    public void setLineCommentStyle(SyntaxStyle s) {
        EditorOptions.styleMap.put("line", s);
    }

    public void setLookAndFeel(final int lafIndex) {
        configMap.put("laf", lafIndex);
    }//end setLookAndFeelIndex

    /**
     * set normal style
     *
     * @param s the style
     */
    public void setNormalStyle(SyntaxStyle s) {
        styleMap.put("normal", s);
    }

    /**
     * set number style
     *
     * @param s the style
     */
    public void setNumberStyle(SyntaxStyle s) {
        EditorOptions.styleMap.put("number", s);
    }

    /**
     * set octal style
     *
     * @param s the style
     */
    public void setOctalStyle(SyntaxStyle s) {
        EditorOptions.styleMap.put("octal", s);
    }

    /**
     * set operator style
     *
     * @param s the style
     */
    public void setOperatorStyle(SyntaxStyle s) {
        EditorOptions.styleMap.put("operator", s);
    }

    /**
     * set reserved style
     *
     * @param s the style
     */
    public void setReservedStyle(SyntaxStyle s) {
        EditorOptions.styleMap.put("reserved", s);
    }

    /**
     * set string style
     *
     * @param s the style
     */
    public void setStringStyle(SyntaxStyle s) {
        EditorOptions.styleMap.put("string", s);
    }

    /**
     * set the tab size
     *
     * @param size the tab size
     */
    public void setTabSize(int size) {
        configMap.put("tab", size);
    }

}//end class EditorOptions
