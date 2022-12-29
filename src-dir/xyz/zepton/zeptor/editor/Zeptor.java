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
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import xyz.zepton.zeptor.gui.AboutDialog;
import xyz.zepton.zeptor.gui.CompilerDialog;
import xyz.zepton.zeptor.gui.CompilerOptions;
import xyz.zepton.zeptor.gui.CreditsDialog;
import xyz.zepton.zeptor.gui.MyFileFilter;
import xyz.zepton.zeptor.gui.MyJFrame;
import xyz.zepton.zeptor.gui.SystemProps;
import xyz.zepton.zeptor.listener.OpenFileListener;
import xyz.zepton.zeptor.log.Logger;
import xyz.zepton.zeptor.run.PlatformConsole;
import xyz.zepton.zeptor.run.ZeptorConsole;
import xyz.zepton.zeptor.text.MyTextPane;
import xyz.zepton.zeptor.transpiler.ZepT;

/**
 * A editor that tries to emulate Borland DOS IDE
 *
 * @version 20050515
 * @Author Tan Hong Cheong
 */
public final class Zeptor extends MyJFrame implements OpenFileListener, ContainerListener {

    public static final String CODE_EOLN = System.lineSeparator();

    /**
     * the editor title
     */
    public static final String CONST_TITLE = "Zeptor: ZeptoN Code Editor";
    public static final String CONST_USER = System.getProperty("user.name");
    private static final String DEFAULT_ZEPTON_HEADER
            = "/**      " + CODE_EOLN
            + " *       " + CODE_EOLN
            + " * Name: myProgram.zep" + CODE_EOLN
            + " * Date: " + LocalDate.now() + CODE_EOLN
            + " *" + CODE_EOLN
            + " * Description: Simple ZeptoN program that prints \"Hello, World!!!\"" + CODE_EOLN
            + " *       " + CODE_EOLN
            + " */      " + CODE_EOLN
            + "         " + CODE_EOLN
            + "";
    public static String outputPath = System.getProperty("user.home"); // get directory from selected pane??

    private static final long serialVersionUID = 486811121985687647L;

    private static final String STD_ZEPTON_PROGRAM
            = "package zepton.program." + CONST_USER
            + ";" + CODE_EOLN
            + CODE_EOLN
            + "prog myProgram {"
            + CODE_EOLN
            + CODE_EOLN
            + "  begin {"
            + CODE_EOLN
            + CODE_EOLN
            + "    nop();"
            + CODE_EOLN
            + "    println(\"Hello, World!!!\");"
            + CODE_EOLN
            + "    exit(0);"
            + CODE_EOLN
            + CODE_EOLN
            + "  }//end begin" + CODE_EOLN
            + "}//end prog myProgram" + CODE_EOLN
            + CODE_EOLN;
    public static final String getOutputPath() {
        return Zeptor.outputPath;
    }//end getOutputPath
    public static final boolean hasJavac() {

        boolean flag = true;

        try {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                flag = false;
            }
        } catch (Exception ignore) {
            flag = false;
        }//end try

        return flag;

    }//end hasJavac

    public static final void main(final String[] args) {

        try {

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Zeptor zep = new Zeptor();
            zep.setVisible(true);

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
            Logger.LOG.closeLog();
            System.exit(1);
        } // end try

    }// end main

    private AboutDialog aboutDialog = null;

    private JRadioButtonMenuItem acrylRadioMenuItem,
            aeroRadioMenuItem,
            aluminumRadioMenuItem,
//            bernsteinRadioMenuItem,
//            darculaRadioMenuItem,
            fastRadioMenuItem,
            graphiteRadioMenuItem,
            hiFiRadioMenuItem,
            infoNodeRadioMenuItem,
            intellijRadioMenuItem,
            lightRadioMenuItem,
//            lipstikRadioMenuItem,
//            liquidRadioMenuItem,
            lunaRadioMenuItem,
            mcWinRadioMenuItem,
            metalRadioMenuItem,
            mintRadioMenuItem,
            motifRadioMenuItem,
//            nimbusRadioMenuItem,
            noireRadioMenuItem,
            pgsRadioMenuItem,
            plasticRadioMenuItem,
            platformRadioMenuItem,
            smartRadioMenuItem,
            textureRadioMenuItem;
//            tonicRadioMenuItem;

    /**
     * a vector to store all files being edited
     */
    private ArrayList<MyTextPane> allFiles;

    public boolean briefFlag = false;

    private JMenu buildMenu;

    private JMenuItem closeAllMenuItem;

    /**
     * the close menu item
     */
    private JMenuItem closeMenuItem;

    private CompilerOptions compilerOptions;

    private CreditsDialog creditsDialog = null;

    private transient Desktop desktop = null;

    // compiler options
    public boolean dumpFlag = false;

    public boolean echoFlag = true;

    /**
     * the edit menu
     */
    private JMenu editMenu;

    public boolean finalFlag = false;

    public boolean hushFlag = false;

    /**
     * the menu bar
     */
    private JMenuBar menuBar;

    public boolean muteFlag = false;

    /**
     * the number of new files opened
     */
    private int numNewFiles = 0;

    /**
     * editor options
     */
    private transient EditorOptions options;

    private JMenu optionsMenu;

    public boolean panicFlag = false;

    private JMenuItem redoMenuItem;

    private JMenuItem saveAllMenuItem;

    /**
     * the saveAs menu item
     */
    private JMenuItem saveAsMenuItem;

    /**
     * the save menu item
     */
    private JMenuItem saveMenuItem;

    /**
     * the tabbed pane
     */
    private JTabbedPane tabbedPane;

    public boolean timeFlag = true;

    /**
     * the undo menu item
     */
    private JMenuItem undoMenuItem;

    private Zeptor zeptorFrame = this;

    /**
     * Constructor
     */
    public Zeptor() {

        Logger.LOG.beginLog(); //log must be in user.home/Zeptor

        init();

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private final void buildClean() { //rewrite to clear only current selected filename.class

        int fileCounter = 0;
        File folder = new File(outputPath);
        File[] fList = folder.listFiles();

        for (File f : fList) {
            if (f.getName().endsWith(".class")) {
                f.delete();
                fileCounter++;
            }//end if

        }//end for

        JOptionPane.showMessageDialog(this,
                String.format("%nCleaned %d ZeptoN bytecode file(s) from working directory:%n%s%n", fileCounter, Zeptor.outputPath),
                "Clean Files",
                JOptionPane.PLAIN_MESSAGE);

        if (this.dumpFlag) {

            MyTextPane t = allFiles.get(tabbedPane.getSelectedIndex());

            if (t.getFile() != null) {

                String zepSourceFile = t.getFile().getName();

                try {
                    String[] fileName = zepSourceFile.split("[.]", 0);

                    System.out.println(Arrays.toString(fileName));

                    String jvmSourceFile = fileName[0] + ".java";

                    File javaFile = new File(jvmSourceFile);

                    if (javaFile.exists()) {
                        javaFile.delete();
                    }

                } catch (Exception ex) {
                    Logger.LOG.logTrap(ex);
                }

                JOptionPane.showMessageDialog(this,
                        String.format("%nCleaned Java source code file from working directory:%n%s%n", outputPath),
                        "Clean Files",
                        JOptionPane.PLAIN_MESSAGE);

            }//end if   
        }//end if(dumpFlag)

    }// end buildClean

    public void buildMake() {

        try {

            MyTextPane t = allFiles.get(tabbedPane.getSelectedIndex());

            if (t.getText().length() > 0) {
                t.save();
            }

            final CompilerDialog jcon = new CompilerDialog("Transpile ZeptoN Program");

            final String jdkVersionCompiler = String.format("Build JDK %s - %s %s.%n%n%s%n%s%n%n",
                    System.getProperty("java.specification.version"), //jdk-version
                    System.getProperty("java.vendor"),
                    System.getProperty("java.vm.version")//,
                    //System.getProperty("java.runtime.version")
                    	,ZepT.RELEASE
                    	,ZepT.VERSION
            );

            jcon.printf("%s", jdkVersionCompiler);
 
            // create compiler instance
            ZepT zept = new ZepT(jcon);

            zept.setDebug(!this.finalFlag);
            zept.setDump(this.dumpFlag);
            zept.setEcho(this.dumpFlag);
            zept.setPanic(this.panicFlag);
            zept.setTime(this.timeFlag);
            zept.setHush(this.hushFlag);
            zept.setMute(this.muteFlag);
            zept.setBrief(this.briefFlag);

            zept.setOutputDirPath(outputPath);

            zept.setParams();

            jcon.setVisible(true);

            boolean statusFlag = zept.compileFile(t.getFile().getPath());

            t.setCompileStatus(statusFlag);

            jcon.println();

            if (statusFlag) {

                jcon.printfSuccess("ZeptoN Transpile Success!%n");

            } else {

                jcon.printfFailure("ZeptoN Transpile Failure!%n");

            } // end if

            jcon.println();

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try

    }// end buildMake

    public void buildRun() {

        try {

            MyTextPane t = allFiles.get(tabbedPane.getSelectedIndex());

            String fileNameExt = t.getFile().getName();

            CompilerDialog jcon = new CompilerDialog("Run ZeptoN Program");

            if (t.isCompiled()) {
                String progName = ZepT.getProgramName(t.getText(), jcon);

                String packName = ZepT.getPackName(progName);

                try {
                    PlatformConsole.runCommand(outputPath, packName, progName);

                } catch (Exception ex) {
                    Logger.LOG.logTrap(ex);

                }//end try
            } else {
                jcon.setVisible(true);
                jcon.printfFailure("%n%nUnable to run program. File: '%s' has not successfully been compiled!%n",
                        fileNameExt);
            } // end if

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);

        }//end try

    }// end runInTerminal

    public void buildStart() {

        try {

            MyTextPane t = allFiles.get(tabbedPane.getSelectedIndex());

            String fileNameExt = t.getFile().getName();

            CompilerDialog jcon = new CompilerDialog("Run ZeptoN Program");

            if (t.isCompiled()) {
                String progName = ZepT.getProgramName(t.getText(), jcon);

                String packName = ZepT.getPackName(progName);

                try {
                    ZeptorConsole.runCommand(outputPath, packName, progName);

                } catch (Exception ex) {
                    Logger.LOG.logTrap(ex);

                }
            } else {
                jcon.setVisible(true);
                jcon.printfFailure("%n%nUnable to run program. File: '%s' has not successfully been compiled!%n",
                        fileNameExt);
            } // end if

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try

    }//end buildStart

    /**
     * as defined in container listener
     */
    public void componentAdded(ContainerEvent e) {
    }

    /**
     * as defined in container listener
     */
    public void componentRemoved(ContainerEvent e) {
    }

    void editCopy() {
        try {

            MyTextPane t = allFiles.get(tabbedPane.getSelectedIndex());
            t.copy();

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }//end editCopy    

    void editCut() {
        try {

            MyTextPane t = allFiles.get(tabbedPane.getSelectedIndex());
            t.cut();

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }//end editCut    

    void editDelete() {
        try {

            MyTextPane t = allFiles.get(tabbedPane.getSelectedIndex());
            t.clear();

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }

    void editFind() {
        try {

            MyTextPane t = allFiles.get(tabbedPane.getSelectedIndex());
            t.find();

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }

    void editGoto() {
        try {

            MyTextPane t = allFiles.get(tabbedPane.getSelectedIndex());
            t.gotoLine();

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }//end editGoto

    void editPaste() {
        try {

            MyTextPane t = allFiles.get(tabbedPane.getSelectedIndex());
            t.paste();

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }//end editPaste   

    void editRedo() {
        try {

            MyTextPane t = allFiles.get(tabbedPane.getSelectedIndex());
            t.redo();

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }//end editRedo

    void editRefresh() {
        try {
            MyTextPane t = allFiles.get(tabbedPane.getSelectedIndex());

            t.refresh();
            SwingUtilities.updateComponentTreeUI(t);
            SwingUtilities.updateComponentTreeUI(Zeptor.this);

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }//end editRefresh

    void editReplace() {
        try {

            MyTextPane t = allFiles.get(tabbedPane.getSelectedIndex());
            t.findReplace();

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }

    void editSelectAll() {
        try {

            MyTextPane t = allFiles.get(tabbedPane.getSelectedIndex());
            t.selectAll();

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }

    void editUndo() {
        try {

            int idx = tabbedPane.getSelectedIndex();
            if (idx > -1) {
                MyTextPane t = allFiles.get(idx);
                t.undo();
            }

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }

    void fileClose() {
        try {
            MyTextPane t = allFiles.get(tabbedPane.getSelectedIndex());
            t.close();
        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }//end fileClose    
    void fileCloseAll() {
        try {

            while (allFiles.size() > 0) {
                MyTextPane t = allFiles.get(tabbedPane.getSelectedIndex());
                t.close();
            } // end while

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }//end fileCloseAll    

    /**
     * Exit the editor
     */
    public synchronized void fileExit() {

        int choiceVal = JOptionPane.showConfirmDialog(Zeptor.this,
                "Do you Really want to exit Zeptor?",
                "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null);

        if (choiceVal == JOptionPane.NO_OPTION) {
            return;
        } // end if

        this.fileSaveAll();

        // all file size will change as things get remove
        while (allFiles.size() > 0) {
            MyTextPane t = allFiles.get(0);
            t.close();
        }

        this.syntaxExport(); //always export syntax

        dispose();

        Logger.LOG.closeLog();

        System.exit(0);

    }//end fileExit

    /**
     * create a new file
     */
    private void fileNew() {

        numNewFiles++;
        String title = "myProgram.zep";
        MyTextPane textPane = new MyTextPane(this, null, numNewFiles);

        textPane.setTitle(title);

        textPane.setText(DEFAULT_ZEPTON_HEADER + STD_ZEPTON_PROGRAM);

        textPane.setCaretPosition(0);

        tabbedPane.addTab(title, textPane);
        tabbedPane.setForeground(Color.DARK_GRAY);

        allFiles.add(textPane);
        setSelectedIndex(allFiles.size() - 1);

    }//end fileNew

    /**
     * open a file
     */
    private void fileOpen() {
        try {

            JFileChooser chooser = new JFileChooser();

            chooser.setAcceptAllFileFilterUsed(false);

            chooser.setCurrentDirectory(new File(outputPath));

            MyFileFilter filter = new MyFileFilter(".zep", "ZeptoN Files [.zep]");
            chooser.addChoosableFileFilter(filter);
            chooser.setDialogType(JFileChooser.OPEN_DIALOG);
            chooser.setMultiSelectionEnabled(true);

            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile().getAbsoluteFile();
                openFile(f);
            }//end if

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try

    }//end fileOpen

    /**
     * check if file had already been opened and show it if it exist
     */
    private boolean fileOpened(File file) {
        Object[] files = allFiles.toArray();
        int i = 0;
        boolean found = false;
        while ((i < files.length) && (!found)) {
            File f = ((MyTextPane) files[i]).getFile();
            if ((f != null) && (file.compareTo(f) == 0)) {
                found = true;
            } else {
                i++;
            }
        }
        if (found) {
            tabbedPane.setSelectedIndex(i);
            return true;// file exist
        } else {
            return false;
        }
    }

    void fileSave() {
        try {

            MyTextPane t = allFiles.get(tabbedPane.getSelectedIndex());
            t.save();

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }//end fileSave    

    /**
     * save all open file
     */
    private void fileSaveAll() {
        int len = allFiles.size();
        for (int i = 0; i < len; i++) {
            MyTextPane f = allFiles.get(i);
            f.save();
        }
    }

    void fileSaveAs() {
        try {
            MyTextPane t = allFiles.get(tabbedPane.getSelectedIndex());
            t.saveAs(true);

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }//end fileSaveAs    

    /**
     * the frame is closed
     */
    protected void frameClosed() {

        Logger.LOG.closeLog();

        fileExit();
    }

    /**
     * @return all the files
     */
    public ArrayList<MyTextPane> getAllFiles() {
        return allFiles;
    }

    /**
     * @return the editor options
     */
    public EditorOptions getEditorOptions() {
        return options;
    }

    public void helpAbout() {

        if (this.aboutDialog == null) {
            this.aboutDialog = new AboutDialog();
        }

        this.aboutDialog.setVisible(true);

    }// end helpAbout

    public void helpCredits() {

        if (this.creditsDialog == null) {
            this.creditsDialog = new CreditsDialog();
        }

        this.creditsDialog.setVisible(true);

    }// end helpCredits

    void helpProperties() {
        try {

            xyz.zepton.zeptor.gui.SystemProps sp = new SystemProps();
            SwingUtilities.invokeLater(sp);

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }//end helpProperties    

    /**
     * open a file and highlight line
     *
     * @param file the file to be opened
     * @param line the line no;
     */
    public void highlightFile(File file, int line) {
        if (file.exists()) { // file exist
            if (!fileOpened(file)) {
                // file is not opened
                // open the file
                // do not show error msg
                openFile(file, false, false);
            }
            // find the correct text pane
            int len = allFiles.size();
            for (int j = 0; j < len; j++) {
                MyTextPane f = allFiles.get(j);
                String filename = f.getFile().toString();
                if (filename.equals(file.toString())) {
                    tabbedPane.setSelectedIndex(j);
                    f.gotoLine(line);
                }
            }
        }
    }

    /**
     * initialize the frame
     */
    private void init() {

        try {

            if (PlatformConsole.isMacOS) {
                // take the menu bar off the jframe
                System.setProperty("apple.laf.useScreenMenuBar", "true");

                // set the name of the application menu item
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "ZeptoR");

                System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");

            }

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }

        this.desktop = Desktop.getDesktop();

        allFiles = new ArrayList<>();

        try {

            File configFile = new File("zeptor-conf.yaml"); //class constant for config file name 10-19-2021

            if (configFile.exists()) {
                options = EditorOptions.fileImportYAML(configFile);
            } else {
                options = new EditorOptions();
                options.fileExportYAML(configFile);
            }//end if

        } catch (Exception ex) {

            Logger.LOG.logTrap(ex);

        }//end try

        this.setTitle(CONST_TITLE);
        this.setResizable(true);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addContainerListener(this);
        this.setContentPane(tabbedPane);

        initMenuBar();

        if (PlatformConsole.isMacOS) {
            desktop.setDefaultMenuBar(menuBar);
        }

        try {

            ZeptorLAF.initMapEnum();
            int zeplaf_index = options.getLookAndFeel();
            ZeptorLAF laf = ZeptorLAF.getZeptorLAF(zeplaf_index);
            this.setLAF(this.zeptorFrame, laf);

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }

        this.setVisible(true);

        this.helpAbout();

    }// end init

    void initBuildMenu() {
        try {

            this.buildMenu = new JMenu("Build");

            JMenuItem buildMakeMenuItem = new JMenuItem("Make"); // F11
            buildMakeMenuItem.setToolTipText("Compile ZeptoN Source");
            buildMakeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

            buildMakeMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    buildMake();

                }
            });

            buildMenu.add(buildMakeMenuItem);

            buildMenu.addSeparator();

            JMenuItem buildRunMenuItem = new JMenuItem("Run"); // Run IT' run in terminal
            buildRunMenuItem.setToolTipText("Run Externally in Console");
            buildRunMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

            buildRunMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    buildRun();
                }
            });

            buildMenu.add(buildRunMenuItem);

            JMenuItem startMenuItem = new JMenuItem("Start");
            startMenuItem.setToolTipText("Run in Zeptor Console");
            startMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

            startMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    buildStart();
                }
            });

            buildMenu.add(startMenuItem);

            buildMenu.addSeparator();

            JMenuItem buildCleanMenuItem = new JMenuItem("Clean");
            buildCleanMenuItem.setToolTipText("Clear All .class Files");
            buildCleanMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    buildClean();
                }// end actionPerformed
            });

            buildMenu.add(buildCleanMenuItem);

            menuBar.add(buildMenu);

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }//end initBuildMenu    

    /**
     * method to init the edit menu item
     */
    private void initEditMenu() {

        editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        undoMenuItem = new JMenuItem("Undo");
        undoMenuItem.setToolTipText("Undo or revert most recent action in Zeptor");
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        undoMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editUndo();
            }
        });

        undoMenuItem.setEnabled(false);
        editMenu.add(undoMenuItem);

        redoMenuItem = new JMenuItem("Redo");
        redoMenuItem.setToolTipText("Redo or repeat most recent action in Zeptor");
        redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        redoMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                editRedo();
            }
        });

        redoMenuItem.setEnabled(false);
        editMenu.add(redoMenuItem);

        editMenu.addSeparator();

        JMenuItem cutMenuItem = new JMenuItem("Cut");
        cutMenuItem.setToolTipText("Cut or extract selected text to clipboard");
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        cutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editCut();
            }
        });
        editMenu.add(cutMenuItem);

        JMenuItem copyMenuItem = new JMenuItem("Copy");
        copyMenuItem.setToolTipText("Copy or replicate selected text to clipboard");
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        copyMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editCopy();
            }
        });
        editMenu.add(copyMenuItem);

        JMenuItem pasteMenuItem = new JMenuItem("Paste");
        pasteMenuItem.setToolTipText("Paste or copy form clipboard into text pane");
        pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        pasteMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editPaste();
            }
        });
        editMenu.add(pasteMenuItem);

        editMenu.addSeparator();

        JMenuItem clearMenuItem = new JMenuItem("Delete");
        clearMenuItem.setToolTipText("Delete or clear selected text");
        clearMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        clearMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editDelete();
            }
        });
        editMenu.add(clearMenuItem);

        editMenu.addSeparator();

        JMenuItem selectAllMenuItem = new JMenuItem("Select All");
        selectAllMenuItem.setToolTipText("Select all the text in text pane");
        selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        selectAllMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editSelectAll();

            }
        });
        editMenu.add(selectAllMenuItem);

        editMenu.addSeparator();

        JMenuItem findMenuItem = new JMenuItem("Find");
        findMenuItem.setToolTipText("Find or search within the current ZeptoN source code");
        findMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        findMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editFind();
            }
        });
        editMenu.add(findMenuItem);

        JMenuItem replaceMenuItem = new JMenuItem("Replace");
        replaceMenuItem.setToolTipText("Replace text within the current ZeptoN source code");
        replaceMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                editReplace();
            }
        });
        editMenu.add(replaceMenuItem);

        editMenu.addSeparator();

        JMenuItem gotoMenuItem = new JMenuItem("Goto");
        gotoMenuItem.setToolTipText("Go to specific position with current ZeptoN source code");
        gotoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        gotoMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editGoto();
            }
        });
        editMenu.add(gotoMenuItem);

        editMenu.addSeparator();

        JMenuItem refreshMenuItem = new JMenuItem("Refresh");
        refreshMenuItem.setToolTipText("Refresh the current text pane");
        refreshMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editRefresh();
            }
        });
        editMenu.add(refreshMenuItem);
    }

    /**
     * method to init the file menu item
     */
    private void initFileMenu() {

        JMenu fileMenu = new JMenu("File");

        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.setToolTipText("Create New \"Hello, World\" Program");
        newMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()
                ));

        newMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileNew();
            }
        });

        fileMenu.add(newMenuItem);

        fileMenu.addSeparator();

        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setToolTipText("Open ZeptoN Program");

        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        openMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileOpen();
            }
        });

        fileMenu.add(openMenuItem);

        closeMenuItem = new JMenuItem("Close");
        closeMenuItem.setToolTipText("Close Current ZeptoN program");
        closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        closeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileClose();
            }
        });

        fileMenu.add(closeMenuItem);

        closeAllMenuItem = new JMenuItem("Close All");
        closeAllMenuItem.setToolTipText("Close all open current ZeptoN programs");
        closeAllMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                fileCloseAll();
            }
        });
        fileMenu.add(closeAllMenuItem);

        fileMenu.addSeparator();

        saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setToolTipText("Save current ZeptoN program to file");
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        saveMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                fileSave();
            }
        });
        fileMenu.add(saveMenuItem);

        saveAsMenuItem = new JMenuItem("Save As");
        saveAsMenuItem.setToolTipText("Save current ZeptoN program to different file");
        saveAsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                fileSaveAs();

            }
        });
        fileMenu.add(saveAsMenuItem);

        saveAllMenuItem = new JMenuItem("Save All");
        saveAllMenuItem.setToolTipText("Save all ZeptoN programs to file");
        saveAllMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileSaveAll();
            }
        });
        fileMenu.add(saveAllMenuItem);
        fileMenu.addSeparator();

        JMenuItem exitMenuItem = new JMenuItem("Quit");
        exitMenuItem.setToolTipText("Quit Zeptor");
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                fileExit();
            }

        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

    }// end initFileMenu

    private void initHelpMenu() {

        JMenu helpMenu = new JMenu("Help");

        JMenuItem helpAboutMenuItem = new JMenuItem("About");

        helpAboutMenuItem.setToolTipText("All About Zeptor");

        helpAboutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                helpAbout();
            }
        });

        helpMenu.add(helpAboutMenuItem);

        helpMenu.addSeparator();

        if (PlatformConsole.isMacOS) {
            desktop.setAboutHandler(e -> helpAbout());
        }

        JMenuItem helpCreditsMenuItem = new JMenuItem("Credits");

        helpCreditsMenuItem.setToolTipText("People That Made Zeptor Possible");

        helpCreditsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                helpCredits();
            }
        });

        helpMenu.add(helpCreditsMenuItem);

        helpMenu.addSeparator();

        JMenuItem propMenuItem = new JMenuItem("Properties");
        propMenuItem.setToolTipText("Runtime Environment Properties");
        propMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                helpProperties();
            }
        });

        helpMenu.add(propMenuItem);

        menuBar.add(helpMenu);

    }//end initHelpMenu

    /**
     * Method to initialize the menu bar
     */
    private void initMenuBar() {
        menuBar = new JMenuBar();

        initFileMenu();
        initEditMenu();
        initSyntaxMenu();
        initBuildMenu();

        initOptionsMenu();

        initHelpMenu();

        if (PlatformConsole.isMacOS) {
            desktop.setQuitHandler((e, r) -> {
                fileExit();
            });
        }

        this.setJMenuBar(menuBar);

    }// end initMenuBar

    void initOptionsMenu() {
        try {

            this.optionsMenu = new JMenu("Options");

            JMenuItem optionCompilerSettingsMenuItem = new JMenuItem("Transpiler");

            optionCompilerSettingsMenuItem.setToolTipText("Transcompiler Settings to Create Bytecode .class");

            optionCompilerSettingsMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    optionsTranspiler();
                }
            });

            optionsMenu.add(optionCompilerSettingsMenuItem);

            optionsMenu.addSeparator();

            if (PlatformConsole.isMacOS) {
                desktop.setPreferencesHandler(e -> optionsTranspiler());
            }//end if

            optionsTheme();

            menuBar.add(optionsMenu);

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }//end initOptionsMenu    

    private void initSyntaxMenu() {
        try {

            JMenu preferenceMenu = new JMenu("Syntax");
            menuBar.add(preferenceMenu);

            JMenuItem setPreferenceMenuItem = new JMenuItem("Config");
            setPreferenceMenuItem.setToolTipText("Configure settings for Zeptor syntax");
            setPreferenceMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    syntaxConfig();
                }
            });
            preferenceMenu.add(setPreferenceMenuItem);

            preferenceMenu.addSeparator();

            JMenuItem savePreferenceMenuItem = new JMenuItem("Export");
            savePreferenceMenuItem.setToolTipText("Export settings to configuration file");

            savePreferenceMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    syntaxExport();
                }//end actionPerformed
            });

            preferenceMenu.add(savePreferenceMenuItem);

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try
    }//end initSyntaxMenu    

    public boolean notSelectedIndex() {
        return tabbedPane.getSelectedIndex() < 0;
    }

    /**
     * As defined in interface OpenFileListener
     *
     * @param f the file to be open
     */
    public boolean openFile(File f) {
        return openFile(f, true, false);
    }

    /**
     * open file
     *
     * @param f the file to be open
     * @param showMsg whether to show error message
     */
    public boolean openFile(File f, boolean showMsg, boolean readFirst) {
        // check if file exist
        if (!f.exists()) {
            if (showMsg) {
                JOptionPane.showConfirmDialog(this, f + "doesn't exist.", "Error", JOptionPane.WARNING_MESSAGE);
            }
            return false;
        }
        // check if file already opened
        if (fileOpened(f)) {
            return true;
        }
        try {
            MyTextPane textPane = new MyTextPane(this, null);

            String title = f.toString();

            outputPath = f.getParent();

            if (readFirst) {
                textPane.read(f);

                tabbedPane.addTab(title, textPane);
                tabbedPane.setForeground(Color.DARK_GRAY);
                tabbedPane.setSelectedComponent(textPane);
                allFiles.add(textPane);
                textPane.setCaretPosition(0);
                this.paintAll(this.getGraphics());
                return true;
            } else {
                tabbedPane.addTab(title, textPane);
                tabbedPane.setSelectedComponent(textPane);
                tabbedPane.setForeground(Color.DARK_GRAY);
                allFiles.add(textPane);
                this.paintAll(this.getGraphics());
                textPane.read(f);
                textPane.setCaretPosition(0);
                return true;
            }
        } catch (IOException ex) {
            Logger.LOG.logTrap(ex);

            if (showMsg) {
                JOptionPane.showConfirmDialog(this, "Can not open file.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
        return false;
    }

    private void optionsTheme() {
        try {

            JMenu optionThemeSettingsMenuItem = new JMenu("Theme");

            optionThemeSettingsMenuItem.setToolTipText("Set Theme of Zeptor");

            ButtonGroup buttonGroup = new ButtonGroup();

            acrylRadioMenuItem = new JRadioButtonMenuItem("Acryl");

            acrylRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.ACRYL);
                }//end actionPerformed
            }//end new ActionListener
            );

            aeroRadioMenuItem = new JRadioButtonMenuItem("Aero");

            aeroRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.AERO);
                }//end actionPerformed
            }//end new ActionListener
            );

            aluminumRadioMenuItem = new JRadioButtonMenuItem("Aluminum");

            aluminumRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.ALUMINUM);
                }//end actionPerformed
            }//end new ActionListener
            );

//            darculaRadioMenuItem = new JRadioButtonMenuItem("Darcula");
//
//            darculaRadioMenuItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent event) {
//                    setLAF(zeptorFrame, ZeptorLAF.DARCULA);
//
//                }//end actionPerformed
//            }//end new ActionListener
//            );

            fastRadioMenuItem = new JRadioButtonMenuItem("Fast");

            fastRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.FAST);
                }//end actionPerformed
            }//end new ActionListener
            );

            graphiteRadioMenuItem = new JRadioButtonMenuItem("Graphite");

            graphiteRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.GRAPHITE);

                }//end actionPerformed
            }//end new ActionListener
            );

            hiFiRadioMenuItem = new JRadioButtonMenuItem("HiFi");

            hiFiRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.HIFI);

                }//end actionPerformed
            }//end new ActionListener
            );

            intellijRadioMenuItem = new JRadioButtonMenuItem("IntelliJ");

            intellijRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.INTELLIJ);

                }//end actionPerformed
            }//end new ActionListener
            );

            lightRadioMenuItem = new JRadioButtonMenuItem(("Light"));

            lightRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.LIGHT);

                }//end actionPerformed
            }//end new ActionListener
            );

            lunaRadioMenuItem = new JRadioButtonMenuItem("Luna");

            lunaRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.LUNA);

                }//end actionPerformed
            }//end new ActionListener
            );

            mcWinRadioMenuItem = new JRadioButtonMenuItem("McWin");

            mcWinRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.MCWIN);

                }//end actionPerformed
            }//end new ActionListener
            );

            metalRadioMenuItem = new JRadioButtonMenuItem("Metal");

            metalRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.METAL);

                }//end actionPerformed
            }//end new ActionListener
            );

            mintRadioMenuItem = new JRadioButtonMenuItem("Mint");

            mintRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.MINT);

                }//end actionPerformed
            }//end new ActionListener
            );

            motifRadioMenuItem = new JRadioButtonMenuItem("Motif");

            motifRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.MOTIF);

                }//end actionPerformed
            }//end new ActionListener
            );

            noireRadioMenuItem = new JRadioButtonMenuItem("Noire");

            noireRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.NOIRE);

                }//end actionPerformed
            }//end new ActionListener
            );

            platformRadioMenuItem = new JRadioButtonMenuItem("Platform", true);

            platformRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.PLATFORM);

                }//end actionPerformed
            }//end new ActionListener
            );

            smartRadioMenuItem = new JRadioButtonMenuItem("Smart");

            smartRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.SMART);

                }//end actionPerformed
            }//end new ActionListener
            );

            textureRadioMenuItem = new JRadioButtonMenuItem("Texture");

            textureRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.TEXTURE);

                }//end actionPerformed
            }//end new ActionListener
            );

            infoNodeRadioMenuItem = new JRadioButtonMenuItem("InfoNode");

            infoNodeRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.INFONODE);

                }//end actionPerformed
            }//end new ActionListener
            );

            pgsRadioMenuItem = new JRadioButtonMenuItem("PGS");

            pgsRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.PGS);

                }//end actionPerformed
            }//end new ActionListener
            );

            plasticRadioMenuItem = new JRadioButtonMenuItem("Plastic");

            plasticRadioMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setLAF(zeptorFrame, ZeptorLAF.PLASTIC);

                }//end actionPerformed
            }//end new ActionListener
            );

            buttonGroup.add(acrylRadioMenuItem);
            buttonGroup.add(aeroRadioMenuItem);
            buttonGroup.add(aluminumRadioMenuItem);

//            buttonGroup.add(darculaRadioMenuItem);
            buttonGroup.add(fastRadioMenuItem);

            buttonGroup.add(graphiteRadioMenuItem);

            buttonGroup.add(hiFiRadioMenuItem);
            buttonGroup.add(infoNodeRadioMenuItem);
            buttonGroup.add(intellijRadioMenuItem);
            buttonGroup.add(lightRadioMenuItem);
            buttonGroup.add(lunaRadioMenuItem);
            buttonGroup.add(mcWinRadioMenuItem);
            buttonGroup.add(metalRadioMenuItem);
            buttonGroup.add(mintRadioMenuItem);
            buttonGroup.add(motifRadioMenuItem);
            buttonGroup.add(noireRadioMenuItem);
            buttonGroup.add(plasticRadioMenuItem);

            buttonGroup.add(pgsRadioMenuItem);

            buttonGroup.add(platformRadioMenuItem);

            buttonGroup.add(smartRadioMenuItem);
            buttonGroup.add(textureRadioMenuItem);

            optionThemeSettingsMenuItem.add(acrylRadioMenuItem);
            optionThemeSettingsMenuItem.add(aeroRadioMenuItem);
            optionThemeSettingsMenuItem.add(aluminumRadioMenuItem);

//            optionThemeSettingsMenuItem.add(darculaRadioMenuItem);
            optionThemeSettingsMenuItem.add(fastRadioMenuItem);
            optionThemeSettingsMenuItem.add(graphiteRadioMenuItem);
            optionThemeSettingsMenuItem.add(hiFiRadioMenuItem);
            optionThemeSettingsMenuItem.add(infoNodeRadioMenuItem);
            optionThemeSettingsMenuItem.add(intellijRadioMenuItem);
            optionThemeSettingsMenuItem.add(lightRadioMenuItem);
            optionThemeSettingsMenuItem.add(lunaRadioMenuItem);
            optionThemeSettingsMenuItem.add(mcWinRadioMenuItem);
            optionThemeSettingsMenuItem.add(mintRadioMenuItem);
            optionThemeSettingsMenuItem.add(metalRadioMenuItem);
            optionThemeSettingsMenuItem.add(motifRadioMenuItem);
            optionThemeSettingsMenuItem.add(noireRadioMenuItem);
            optionThemeSettingsMenuItem.add(pgsRadioMenuItem);
            optionThemeSettingsMenuItem.add(plasticRadioMenuItem);
            optionThemeSettingsMenuItem.add(platformRadioMenuItem);
            optionThemeSettingsMenuItem.add(smartRadioMenuItem);
            optionThemeSettingsMenuItem.add(textureRadioMenuItem);

            optionsMenu.add(optionThemeSettingsMenuItem);

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try

    }//end optionsTheme

    private void optionsTranspiler() {

        this.compilerOptions = new CompilerOptions(this);
        this.compilerOptions.setTitle("Options");
        this.compilerOptions.setSize(500, 600);
        this.compilerOptions.setVisible(true);

    }// end optionsTranspiler
    public void redoMenuItemSetEnabled(boolean flag) {
        redoMenuItem.setEnabled(flag);
    }

    /**
     * remove a MyTextPane
     *
     * @param t the textPane to be removed
     */
    public void removeTextPane(MyTextPane t) {
        int index = tabbedPane.indexOfComponent(t);
        if (index >= 0) {
            allFiles.remove(index);
        }

        tabbedPane.remove(t);

    }// end removeTextPane
    private final void setLAF(final Zeptor zeptor, final ZeptorLAF lafTheme) {

        try {
            zeptor.options.setLookAndFeel(lafTheme.getIndex());
            UIManager.setLookAndFeel(lafTheme.getImage());
            SwingUtilities.updateComponentTreeUI(Zeptor.this);

            this.setRadioMenuSelected(lafTheme);

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try

    }//end setLAF

    private final void setRadioMenuSelected(final ZeptorLAF lafTheme) {
        
    	switch (lafTheme.getIndex()) {
            case 0:
                this.acrylRadioMenuItem.setSelected(true);
                break;

            case 1:
                this.aeroRadioMenuItem.setSelected(true);
                break;

            case 2:
                this.aluminumRadioMenuItem.setSelected(true);
                break;

//            case 3:
//                this.darculaRadioMenuItem.setSelected(true);
//                break;

            case 5:
                this.fastRadioMenuItem.setSelected(true);
                break;

            case 6:
                this.graphiteRadioMenuItem.setSelected(true);
                break;

            case 7:
                this.hiFiRadioMenuItem.setSelected(true);
                break;

            case 8:
                this.infoNodeRadioMenuItem.setSelected(true);
                break;

            case 9:
                this.intellijRadioMenuItem.setSelected(true);
                break;

            case 10:
                this.lightRadioMenuItem.setSelected(true);
                break;

            case 11:
                this.lunaRadioMenuItem.setSelected(true);
                break;

            case 12:
                this.mcWinRadioMenuItem.setSelected(true);
                break;

            case 13:
                this.metalRadioMenuItem.setSelected(true);
                break;

            case 14:
                this.mintRadioMenuItem.setSelected(true);
                break;

            case 15:
                this.motifRadioMenuItem.setSelected(true);
                break;

            case 16:
                this.noireRadioMenuItem.setSelected(true);
                break;

            case 17:
                this.pgsRadioMenuItem.setSelected(true);
                break;

            case 18:
                this.plasticRadioMenuItem.setSelected(true);
                break;

            case 19:
                this.platformRadioMenuItem.setSelected(true);
                break;

            case 20:
                this.smartRadioMenuItem.setSelected(true);
                break;

            case 21:
                this.textureRadioMenuItem.setSelected(true);
                break;

            default:
                break;

        }//end switch
    }//end setRadioMenuItem

    /**
     * select the tabbed pane
     *
     * @param index the index to be selected
     */
    public void setSelectedIndex(int index) {
        if ((index < 0) || (index > allFiles.size())) {
            return;// out of range, do nothing
        }
        tabbedPane.setSelectedIndex(index);
        JComponent c = (JComponent) tabbedPane.getComponentAt(index);
        c.grabFocus();

    }

    /**
     * set the editor options
     */
    public void syntaxConfig() {

        EditorOptionsDialog d = new EditorOptionsDialog(this);
        if (!d.isCancelled()) {
            // update all the text frames
            int len = allFiles.size();
            for (int i = 0; i < len; i++) {
                MyTextPane f = allFiles.get(i);
                f.setEditorOptions(options);

                int zeplaf_index = options.getLookAndFeel();
                ZeptorLAF laf = ZeptorLAF.getZeptorLAF(zeplaf_index);
                this.setLAF(this.zeptorFrame, laf);
                f.refresh(); // automatically refresh
            }
        }
    }//end syntaxConfig

    void syntaxExport() {
        try {

            File configFile = new File("zeptor-conf.yaml");

            if (configFile.exists()) {

                boolean deleteFlag = Files.deleteIfExists(configFile.toPath());

                if (deleteFlag) {
                    Logger.LOG.logText("Configuration File has deleted.");
                } else {
                    Logger.LOG.logText("Configuration File not deleted.");

                }//end if

            }//end if

            if (configFile.exists()) {
                Logger.LOG.logText("Error: Configuration file deleted but still exists!");

            } else {
                Logger.LOG.logText("Configuration file was deleted.");

            }//end if

            options.fileExportYAML(configFile);

            JOptionPane.showMessageDialog(
                    Zeptor.this,
                    "Syntax settings exported to file: 'zeptor-conf.yaml' success.",
                    "Export Syntax Settings",
                    JOptionPane.PLAIN_MESSAGE);

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
            JOptionPane.showMessageDialog(
                    Zeptor.this,
                    "Unable export syntax settings to file: 'zeptor-conf.yaml' failure!",
                    "Export Syntax Settings",
                    JOptionPane.WARNING_MESSAGE
            );
        }//end try

    }//end syntaxExport    

    /**
     * enable/disable the undoMenuItem
     *
     * @param flag whether to enable
     */
    public void undoMenuItemSetEnabled(boolean flag) {
        undoMenuItem.setEnabled(flag);
    }

}// end class Zeptor
