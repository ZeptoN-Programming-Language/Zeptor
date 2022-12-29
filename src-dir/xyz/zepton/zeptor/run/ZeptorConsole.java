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
package xyz.zepton.zeptor.run;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

import xyz.zepton.zeptor.log.Logger;

public final class ZeptorConsole {

    public class AppendTask implements Runnable {

        private Terminal terminal;
        private String text;

        public AppendTask(Terminal textArea, String text) {
            this.terminal = textArea;
            this.text = text;
        }

        @Override
        public void run() {
            terminal.appendText(text);
        }
    }

    @SuppressWarnings("serial")
    public static final class BlockCaret extends DefaultCaret {

        private static final String CURSOR = 
        //"\u007C"; //unicode vertical bar 
        "\u005F";//Unicode underline 
        //"\u2588"; //Unicode block character

        public BlockCaret() {
            setBlinkRate(0); //0 no blink, solid;  original 500
        }//end constructor

        //@Override
        protected synchronized void damage(Rectangle2D rectangle2D) {
            if (rectangle2D == null) {
                return;
            }//end if

            JTextComponent comp = getComponent();
            FontMetrics fm = comp.getFontMetrics(comp.getFont());
            int textWidth = fm.stringWidth(CURSOR);
            int textHeight = fm.getHeight();
            x = (int) rectangle2D.getX();
            y = (int) rectangle2D.getY();
            width = textWidth;
            height = textHeight;
            repaint();
        }//end damage

        @Override
        public void paint(Graphics g) {
            JTextComponent comp = getComponent();
            if (comp == null) {
                return;
            }//end if

            int dot = getDot();
            Rectangle2D rect;
            try {
                rect = comp.modelToView2D(dot);
            } catch (BadLocationException e) {
                return;
            }//end try

            if (rect == null) {
                return;
            }//end if

            if ((x != rect.getX()) || (y != rect.getY())) {
                repaint(); // erase previous location of caret
                damage(rect);
            }//end if

            if (isVisible()) {

                FontMetrics fm = comp.getFontMetrics(comp.getFont());

                g.setColor(comp.getCaretColor());
                g.drawString(CURSOR, x, y + fm.getAscent());
            }//end if

        }//end paint

    }//end class BlockCaret

    public class Command {

        private CommandListener listener;
        private ProcessRunner runner;

        public Command(CommandListener listener) {
            this.listener = listener;
        }
        
        public void execute(String cmd) {

        	  if (cmd.contentEquals("exit")) {
                ZeptorConsole.this.frame.dispose();
                return;
              }//end if
        	
            if (!cmd.trim().isEmpty()) {

                List<String> values = new ArrayList<>(25);
                if (cmd.contains("\"")) {

                    while (cmd.contains("\"")) {

                        String start = cmd.substring(0, cmd.indexOf("\""));
                        cmd = cmd.substring(start.length());
                        String quote = cmd.substring(cmd.indexOf("\"") + 1);
                        cmd = cmd.substring(cmd.indexOf("\"") + 1);
                        quote = quote.substring(0, cmd.indexOf("\""));
                        cmd = cmd.substring(cmd.indexOf("\"") + 1);

                        if (!start.trim().isEmpty()) {
                            String[] parts = start.trim().split(" ");
                            values.addAll(Arrays.asList(parts));
                        }
                        values.add(quote.trim());

                    }

                    if (!cmd.trim().isEmpty()) {
                        String parts[] = cmd.trim().split(" ");
                        values.addAll(Arrays.asList(parts));
                    }

                } else {

                    if (!cmd.trim().isEmpty()) {
                        String parts[] = cmd.trim().split(" ");
                        values.addAll(Arrays.asList(parts));
                    }

                } // end if

                runner = new ProcessRunner(listener, values);
                	
            }

        }

        public boolean isRunning() {

            return runner != null && runner.isAlive();

        }

        public void send(String cmd) throws IOException {
            runner.write(cmd); 
        }
    }

    public interface CommandListener {

        public void commandCompleted(String cmd, int result);

        public void commandFailed(Exception exp);

        public void commandOutput(String text);

    }// end interface CommandListener

    @SuppressWarnings("serial")
    public class ConsolePane extends JPanel implements CommandListener, Terminal {

        private Command cmd;
        private JTextArea textArea;
        private int userInputStart = 0;

        public ConsolePane() {

            cmd = new Command(this);

            setLayout(new BorderLayout());
            textArea = new JTextArea(40, 80);

            Font font = new Font("Courier", Font.PLAIN, 20);

            textArea.setFont(font);

            BlockCaret blockCursor = new BlockCaret();

            textArea.setCaret(blockCursor);

            textArea.setCaretColor(Color.BLACK); //cursor is invisible

            ((AbstractDocument) textArea.getDocument()).setDocumentFilter(new ProtectedDocumentFilter(this));
            add(new JScrollPane(textArea));

            ActionMap am = textArea.getActionMap();

            Action oldAction = am.get("insert-break");
            am.put("insert-break", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int range = textArea.getCaretPosition() - userInputStart;
                    try {
                        String text = textArea.getText(userInputStart, range).trim();
                        userInputStart += range;
                        if (!cmd.isRunning()) {
                            cmd.execute(text);
                        } else {
                            try {
                                cmd.send(text + "\n"); // platform EOLN??
                            } catch (IOException ex) {
                                appendText("!! Failed to send command to process: " + ex.getMessage() + "\n");
                            }
                        }
                    } catch (BadLocationException ex) {
                    } // end try
                    oldAction.actionPerformed(e);
                }// end actionPerformed
            });

        }// end ConsolePane

        @Override
        public void appendText(String text) {
            textArea.append(text);
            updateUserInputPos();
        }

        @Override
        public void commandCompleted(String cmd, int result) {
        }

        @Override
        public void commandFailed(Exception exp) {
        }

        @Override
        public void commandOutput(String text) {
            SwingUtilities.invokeLater(new AppendTask(this, text));
        }

        public void exec(final String cmd) {
            this.cmd.execute(cmd);
        }

        public JTextArea getTextArea() {
            return this.textArea;
        }

        @Override
        public int getUserInputStart() {
            return userInputStart;
        }

        protected void updateUserInputPos() {
            int pos = textArea.getCaretPosition();
            textArea.setCaretPosition(textArea.getText().length());
            userInputStart = pos;

        }// end updateUserInputPos
    }

    public class ProcessRunner extends Thread {

        private List<String> cmds;
        private CommandListener listener;

        private Process process;

        public ProcessRunner(CommandListener listener, List<String> cmds) {
            this.cmds = cmds;
            this.listener = listener;
            start();
        }

        @Override
        public void run() {
            try {
                ProcessBuilder pb = new ProcessBuilder(cmds);
                pb.redirectErrorStream();
                StreamReader reader = null;
                int result = -1;
                try {
                    process = pb.start();

                    reader = new StreamReader(listener, process.getInputStream());
                    result = process.waitFor();

                    // Terminate the stream writer
                    reader.join();

                } catch (Exception ignored) {
                }

                StringJoiner sj = new StringJoiner(" ");
                cmds.stream().forEach((cmd) -> {
                    sj.add(cmd);
                });

                listener.commandCompleted(sj.toString(), result);
            } catch (Exception exp) {
                exp.printStackTrace();
                listener.commandFailed(exp);
            } // end try
        }// end run

        public void write(String text) throws IOException {
            if (process != null && process.isAlive()) {
                process.getOutputStream().write(text.getBytes());
                process.getOutputStream().flush();
            } // end if
        }// end write
    }// end ProcessRunner

    public class ProtectedDocumentFilter extends DocumentFilter {

        private UserInput userInput;

        public ProtectedDocumentFilter(UserInput userInput) {
            this.userInput = userInput;
        }

        public UserInput getUserInput() {
            return userInput;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (offset >= getUserInput().getUserInputStart()) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if (offset >= getUserInput().getUserInputStart()) {
                super.remove(fb, offset, length); // To change body of generated methods, choose Tools | Templates.
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (offset >= getUserInput().getUserInputStart()) {
                super.replace(fb, offset, length, text, attrs); // To change body of generated methods, choose Tools |
                // Templates.
            } // end if
        }// end replace
    }// end class ProtectedDocumentFilter

    public class StreamReader extends Thread {

        private InputStream is;
        private CommandListener listener;
        
        public StreamReader(CommandListener listener, InputStream is) {
            this.is = is;
            this.listener = listener;
            start();
        }

        @Override
        public void run() {
            try {
                int value = -1;
                while ((value = is.read()) != -1) {
                    listener.commandOutput(Character.toString((char) value));
                }

                listener.commandOutput(System.lineSeparator());
                listener.commandOutput("Type 'exit' to close console . . .");
                listener.commandOutput(System.lineSeparator());
                listener.commandOutput(System.lineSeparator());
                
            } catch (IOException exp) {
                exp.printStackTrace();
            }
        }//end run
    }

    public interface Terminal extends UserInput {

        public void appendText(String text);
    }

    public interface UserInput {

        public int getUserInputStart();
    }

    public static final void doCommand(final String cmd) {
        ZeptorConsole con = new ZeptorConsole();
        con.exec(cmd);
    }//end doCommand

    public static final ZeptorConsole getConsole(final String cmd) {
        ZeptorConsole con = new ZeptorConsole();
        con.exec(cmd);
        return con;
    }//end getConsole

    public static void runCommand(final String outputPath,
            final String packageName,
            final String programName) {

        String execCmd = "";

        String outPath = outputPath;

        if (PlatformConsole.isWindows) {
            outPath = String.format("\"%s\"", outPath);
        }

        if (packageName == null || packageName.equals("")) {
            execCmd = outPath + " " + programName;
        } else {
            execCmd = outPath + " " + packageName + "." + programName;
        }//end if

        try {
            Thread.sleep(1100);
        } catch (Exception ignore) {
        }

        try {

            ZeptorConsole.doCommand("java -cp " + execCmd);

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
        }//end try

    }//end runCommand

    public ConsolePane console = new ConsolePane();

    public JFrame frame = null;

    public ZeptorConsole() {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                try {

                    frame = new JFrame("Zeptor Console");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setLayout(new BorderLayout());
                    frame.add(ZeptorConsole.this.console);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);

                } catch (Exception ex) {
                    Logger.LOG.logTrap(ex);
                }

            }// end run
        });

    }// end constructor

    public void exec(final String cmd) {
        this.console.exec(cmd);
    }

}//end class ZeptorConsole
