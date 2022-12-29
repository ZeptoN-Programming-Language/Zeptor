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
package xyz.zepton.zeptor.transpiler;

/*
 * @(#)ZepT.java
 *
 * Title: ZepT1Lib - ZeptoN programming language transcompiler.
 *                   T1 - one class for transpiler, Lib for library for lexer.
 *
 * Description: A ZeptoN transcompiler using the Java Compiler API to compile into byte code.
 *
 * @author William F. Gilreath (will.f.gilreath@gmail.com) will@zepton.xyz
 * @version 1.1  12/21/2022
 *
 * Copyright (c) © 2022 All Rights Reserved.
 *
 */
import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import xyz.zepton.lexer.ILexer;
import xyz.zepton.lexer.Lexer;
import xyz.zepton.lexer.Token;
import xyz.zepton.lexer.ZeptonLexerConstants.TokenType;
import xyz.zepton.zeptor.editor.Zeptor;
import xyz.zepton.zeptor.gui.CompilerDialog;
import xyz.zepton.zeptor.log.Logger;
import xyz.zepton.zeptor.util.IConfigure;
import xyz.zepton.zeptor.util.IOutput;

public final class ZepT implements IConfigure {

	/**
     * Java source code object as name and text as String objects. Used by the
     * Java Compiler API to compile the transpiled ZeptoN source code in Java to
     * a Java .class bytecode file.
     */
    public static class JavaSourceCodeStringObject extends SimpleJavaFileObject {

        public static final JavaSourceCodeStringObject NIL = new JavaSourceCodeStringObject();
        //source code in Java transpiled from ZeptoN
        public final String code;
        public final String name;

        public final String pack;

        private JavaSourceCodeStringObject() {
            this(EMPTY_STRING, EMPTY_STRING);
        }//end null_$constructor

        /**
         * Constructor to create a Java source code object used by the Java
         * Compiler API.
         *
         * @param name - name of the Java class for the Java source code.
         * @param code - code text of the Java source code.
         */
        public JavaSourceCodeStringObject(final String name, final String code) {

            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
            this.name = name;
            this.pack = "";

        }//end_$constructor

        public JavaSourceCodeStringObject(final String pack, final String name, final String code) {

            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
            this.name = name;
            this.pack = pack;

        }//end_$constructor

        /**
         * Get the source Java code as a character sequence.
         *
         * @param ignoreEncodingErrors - a boolean flag to ignore problems with
         * the encoding of the source code.
         * @return CharSequence - the general character sequence type as an
         * interface.
         */
        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }//end getCharContent

        /**
         * Get the source Java code as a Java string.
         *
         * @return String - the Java source code as a String object.
         */
        public String getCode() {
            return this.code;
        }

        public String getName() {
            return this.name;
        }

        public String getPack() {
            return this.pack;
        }

    }//end class JavaSourceCodeStringObject

    public static boolean briefFlag = false;  //set brief error reporting a count of error diagnostics

    //internal constants used by the compiler
    public static final Charset CHARSET = Charset.defaultCharset();

    public static final String CODE_SPC = " "; 

    public static final String CWD = Zeptor.getOutputPath();   

    public static boolean dumpFlag = false;  //set dump raw Java source code to external file

    public static boolean echoFlag = false;  //set echo ZeptoN compiler parameters and compiler status

    private static final String EMPTY_STRING = "";

    public static final String ENDLN = System.getProperty("line.separator");

    public static final String EOL = System.lineSeparator();

    public static final String ERROR_DIR_PARAM = "Directory output path parameter is compiler parameter.";

    public static final String ERROR_DIR_PATH = "Directory output path parameter unspecified outside length of parameter list.";

    public static final String ERROR_DIR_ZEP = "Directory output path parameter is ZeptoN '*.ZepC' source file.";

    public static final String ERROR_FILE_EXIST = "File: '%s' does not exist.";
    public static final String ERROR_FILE_EXTEN = "File: '%s' does not have '.ZepC' extension.";

    public static final String ERROR_FILE_READ = "File: '%s' is unreadable.";

    public static final String ERROR_FILE_SMALL = "File: '%s' is too small.";

    public static final String ERROR_NO_FILES = "No input source files given! Compilation is terminated.";

    public static final String ERROR_NO_INPUT = "No compiler options or files given! Use -help for options.";

    public static final String ERROR_OPT_BRIEF = "Option -brief ambiguous with option -hush and/or -mute option.";

    public static final String ERROR_OPT_HUSH = "Option -hush ambiguous with option -brief and/or -mute option.";

    public static final String ERROR_OPT_MUTE = "Option -mute ambiguous with -brief and/or -hush option.";
    public static final String ERROR_PARAM_FILES = "ZepC option: '%s' must precede ZeptoN source code files list.";
    public static final String ERROR_PARAM_WRONG = "ZepC option: '%s' is not recognized.";

    public static final int EXIT_CODE_FAILURE = 1; //failure - compiler failure in compiling ZeptoN source file.
    public static final int EXIT_CODE_PROBLEM = 2; //problem - compiler failure with a problem for ZeptoN source file.
    public static final int EXIT_CODE_SUCCESS = 0; //success - compiler success in compiling ZeptoN source file.
    public static final long FILE_SIZE_MIN = 15;         //smallest ZeptoN file size is 15-bytes
    public static final String FILE_SOURCE_EXT = ".zep"; //ZeptoN source file extension

    public static final ArrayList<String> files = new ArrayList<>(); //Javac compiler ZeptoN source files
    public static boolean finalFlag = false;  //set final compilation with no debug information

    public static boolean hushFlag = false;  //set hush compiler diagnostics except errors
    public static final String JAVAC_DEBUG = "-g";
    public static final String JAVAC_FINAL = "-g:none";
    public static final Locale LOCALE = Locale.getDefault();
    public static boolean muteFlag = false;  //set mute all compiler diagnostics are silenced

    public static final Iterable<String> NO_ANNOTATION_PROC = Collections.emptyList();

    public static final String OPTIONS = "                                                                              \n\r"
            + "                                                                               " + EOL
            + " Compiler Params:                                                              " + EOL
            + "                                                                               " + EOL
            + "  -dir <path>  Specify the output directory path otherwise the current         " + EOL
            + "               working directory is used implicitly.                           " + EOL
            + "                                                                               " + EOL
            + " Compiler Flags: [-dump]|[-echo]|[-final]|[-javac (param)+]|[-panic]|[-time]   " + EOL
            + "                                                                               " + EOL
            + "  -dump          Dump raw transpiled Java source to '.java' file.              " + EOL
            + "  -echo          Print ZeptoN compiler options and success or failure.         " + EOL
            + "  -final         Compile final release without debug information.              " + EOL
            + "  -javac <param> Pass javac compiler param as-is to the compiler.              " + EOL
            + "  -panic         Panic on any error and terminate compilation.                 " + EOL
            + "  -time          Print total time for success compiling of a source file.      " + EOL
            + "                                                                               " + EOL
            + "  Error Flags: [-brief|-hush|-mute]                                            " + EOL
            + "                                                                               " + EOL
            + "  -brief       Print only a brief count of compiler messages.                  " + EOL
            + "  -hush        Disable all compiler messages except errors.                    " + EOL
            + "  -mute        Disable all compiler messages.                                  " + EOL
            + "                                                                               " + EOL
            + " Help Options: (-help|-info)                                                   " + EOL
            + "                                                                               " + EOL
            + "  -help        Print list of compiler options and exit.                        " + EOL
            + "  -info        Print compiler version information and exit.                    " + EOL
            + "                                                                               " + EOL
            + "                                                                               " + EOL;

    public static IOutput out;
    public static final Map<String, String> packageMap = new HashMap<>();

    public static boolean panicFlag = false;  //set panic on error and terminate compiler
    public static final String RELEASE = "ZepT - ZeptoN Lex Transcompiler" + EOL + "Copyright (c) 2023 William F. Gilreath. All Rights Reserved";

    //predefined environment methods used in the transcompile of ZeptoN to Java source code.
    public static final String SOURCE_CODE_BODY
            = EOL
            + "public static final char[]      EMPTY_CHAR   = new char[0];" + EOL
            + "public static final String      EMPTY_STRING = new String();" + EOL
            + "public static final String      EOL          = System.getProperty(\"line.separator\");" + EOL
            + "public static final char        NULL_CHAR    = Character.MIN_VALUE;" + EOL
            + EOL
            //internal attributes
            + "public static       String[]    _$argv       = new String[0];" + EOL
            + "public static final PrintStream _$out_str    = System.out;" + EOL
            + "public static final InputStream _$inp_str    = System.in;" + EOL
            + "public static final PrintStream _$err_str    = System.err;" + EOL
            + "public static final Console     _$con        = System.console();" + EOL
            + "public static final Runtime     _$run        = Runtime.getRuntime();" + EOL
            + "public static final Scanner     _$scan       = new Scanner(System.in);" + EOL
            + EOL
            //internal methods
            + "public static final void _$start(final String[] args){_$argv=args;}" + EOL
            + "public static final void _$close(){try{ _$out_str.flush();_$out_str.close();_$err_str.flush();_$err_str.close();_$inp_str.close();}catch (Exception ex){_$err_str.println(ex.getMessage());ex.printStackTrace(_$err_str);}}" + EOL
            + EOL
            + "public static final void    arraycopy(final Object src,final int srcPos,Object dst,final int dstPost,final int len){System.arraycopy(src,srcPos,dst,dstPost,len);}" + EOL
            + "public static final int     availableProcessors() { return _$run.availableProcessors(); }" + EOL
            + "public static final String  clearProperty(final String param) { return System.clearProperty(param); }" + EOL
            + "public static final Console console(){return _$con;}" + EOL
            + "public static final long    currentTimeMillis(){return System.currentTimeMillis();}" + EOL
            + "public static final Charset defaultCharset() { return Charset.defaultCharset(); }" + EOL
            + "public static final void    exit(final int code){ _$run.exit(code);}" + EOL
            + "public static final long    freeMemory(){return _$run.freeMemory();}" + EOL
            + "public static final void    gc(){ _$run.gc();}" + EOL
            + "public static final String  getenv(final String param){return System.getenv(param);}" + EOL
            + "public static final Locale  getLocale(){return _$scan.locale();}" + EOL
            + "public static final String  getProperty(final String param){return System.getProperty(param);}" + EOL
            + "public static final Runtime getRuntime(){return _$run;}" + EOL
            + "public static final void    halt(final int param) { _$run.halt(param); }" + EOL
            + "public static final int     identityHashCode(final Object obj){return System.identityHashCode(obj);}" + EOL
            + "public static final String  lineSeparator() { return System.lineSeparator(); };" + EOL
            + "public static final long    maxMemory(){return _$run.maxMemory();}" + EOL
            + "public static final long    nanoTime(){return System.nanoTime();}" + EOL
            + EOL
            + "public static final BigDecimal readBigDecimal(){return _$scan.nextBigDecimal();}" + EOL
            + "public static final BigInteger readBigIntegr(){return _$scan.nextBigInteger();}" + EOL
            + "public static final boolean    readBoolean(){return _$scan.nextBoolean();}" + EOL
            + "public static final byte       readByte(){return _$scan.nextByte();}" + EOL
            + "public static final char       readChar(){char chr;try{chr = (char) _$inp_str.read();}catch (Exception ex){chr = NULL_CHAR;} return chr;}" + EOL
            + "public static final double     readDouble(){return _$scan.nextDouble();}" + EOL
            + "public static final float      readFloat(){return _$scan.nextFloat();}" + EOL
            + "public static final int        readInt(){return _$scan.nextInt();}" + EOL
            + "public static final long       readLong(){return _$scan.nextLong();}" + EOL
            + "public static final short      readShort(){return _$scan.nextShort();}" + EOL
            + "public static final String     readLine(){String line = EMPTY_STRING;try{line = _$scan.nextLine();}catch (Exception ex){line = EMPTY_STRING;} return line;}" + EOL
            + "public static final String     readLine(final String fmt, final Object... args){if( _$con==null){return EMPTY_STRING;} return _$con.readLine(fmt,args);}" + EOL
            + "public static final char[]     readPassword(){if( _$con==null){return EMPTY_CHAR;} return _$con.readPassword();}" + EOL
            + "public static final char[]     readPassword(String fmt,Object... args){if (_$con==null){return EMPTY_CHAR;} return _$con.readPassword(fmt,args);}" + EOL
            + "public static final String     readString(){try{return _$scan.next();}catch (Exception ex){_$err_str.println(ex.getMessage());ex.printStackTrace(_$err_str);} return EMPTY_STRING;}" + EOL
            + EOL
            + "public static final void printf(final String fmt,final Object... param){_$out_str.printf(fmt,param);}" + EOL
            + "public static final void print(final char[] param){_$out_str.print(param);}" + EOL
            + "public static final void print(final BigDecimal param){_$out_str.print(param.toPlainString());}" + EOL
            + "public static final void print(final BigInteger param){_$out_str.print(param.toString());}" + EOL
            + "public static final void print(final boolean param){_$out_str.print(param);}" + EOL
            + "public static final void print(final byte param){_$out_str.print(param);}" + EOL
            + "public static final void print(final char param){_$out_str.print(param);}" + EOL
            + "public static final void print(final double param){_$out_str.print(param);}" + EOL
            + "public static final void print(final float param){_$out_str.print(param);}" + EOL
            + "public static final void print(final int param){_$out_str.print(param);}" + EOL
            + "public static final void print(final long param){_$out_str.print(param);}" + EOL
            + "public static final void print(final Object param){_$out_str.print(param);}" + EOL
            + "public static final void print(final short param){_$out_str.print(param);}" + EOL
            + "public static final void print(final String param){_$out_str.print(param);}" + EOL
            + "public static final void println(){_$out_str.println();}" + EOL
            + "public static final void println(final char[] param){_$out_str.println(param);}" + EOL
            + "public static final void println(final BigDecimal param){_$out_str.print(param.toPlainString());}" + EOL
            + "public static final void println(final BigInteger param){_$out_str.print(param.toString());}" + EOL
            + "public static final void println(final boolean param){_$out_str.println(param);}" + EOL
            + "public static final void println(final byte param){_$out_str.println(param);}" + EOL
            + "public static final void println(final char param){_$out_str.println(param);}" + EOL
            + "public static final void println(final double param){_$out_str.println(param);}" + EOL
            + "public static final void println(final float param){_$out_str.println(param);}" + EOL
            + "public static final void println(final int param){_$out_str.println(param);}" + EOL
            + "public static final void println(final long param){_$out_str.println(param);}" + EOL
            + "public static final void println(final Object param){_$out_str.println(param);}" + EOL
            + "public static final void println(final short param){_$out_str.println(param);}" + EOL
            + "public static final void println(final String param){_$out_str.println(param);}" + EOL
            + EOL
            + "public static final String setProperty(final String key, final String value) { return System.setProperty(key, value); }" + EOL
            + "public static final long   totalMemory(){return _$run.totalMemory();}" + EOL
            + EOL
            + "public static final String toString(final boolean[] param){return Arrays.toString(param);}" + EOL
            + "public static final String toString(final byte[] param){return Arrays.toString(param);}" + EOL
            + "public static final String toString(final char[] param){return Arrays.toString(param);}" + EOL
            + "public static final String toString(final double[] param){return Arrays.toString(param);}" + EOL
            + "public static final String toString(final float[] param){return Arrays.toString(param);}" + EOL
            + "public static final String toString(final int[] param){return Arrays.toString(param);}" + EOL
            + "public static final String toString(final long[] param){return Arrays.toString(param);}" + EOL
            + "public static final String toString(final short[] param){return Arrays.toString(param);}" + EOL
            + "public static final String toString(final Object[] param){return Arrays.toString(param);}" + EOL
            + "public static final String valueOf(final char[] param){return String.valueOf(param);}" + EOL
            + EOL
            //non-standard methods
            + "public static final String[] getArgs(){return _$argv;}" + EOL
            + "public static final void     errorf(final String fmt,final Object...param){_$err_str.printf(fmt,param);}" + EOL
            + "public static final void     nop(){;}" + EOL
            + EOL //add code for FileIO, urlIO
            ;
    //predefined imports used in the transcompile of ZeptoN to Java source code.
    public static final String SOURCE_CODE_HEAD
            = "import java.io.*;" + CODE_SPC
            + "import java.math.*;" + CODE_SPC
            + "import java.nio.charset.*;" + CODE_SPC
            + "import java.net.*;" + CODE_SPC
            + "import java.util.*;" + CODE_SPC;
    public static final Writer SYS_ERR = new PrintWriter(System.err, true);

    public static boolean timeFlag = false;  //set to time overall time to compile a ZeptoN source file
    public static final String USEINFO = "Use: ZepC (param|flag|option)* [-javac (javac-options)+] (ZeptoN-file)+ | (-help|-info)     ";

    public static final String VERSION = "Version 2.2 Released January 2023";
    public static final String XLINT_DO_NONE = "-Xlint:none";

    public static final String XLINT_RUN_ALL = "-Xlint:all";
    private static void addList(final int idx, final Map<Integer, StringBuilder> list, final String str) {

        StringBuilder strb = ZepT.getLineStrBAt(idx, list);
        strb.append(str);
        strb.append(CODE_SPC);
        list.put(idx, strb);

    }//end addList

    private static void addList(final Map<Integer, StringBuilder> list, final Token tok) {

        ZepT.addList(tok.getBeginLine(), list, tok.getImage());

    }//end addList
    /**
     * Compile using the command line arguments of compiler parameters and
     * ZeptoN source files.
     *
     * @param args - command line arguments passed to the ZeptoN transcompiler.
     */
    public static void compile(final String[] args) {

        final ZepT comp = new ZepT();

//        if (args.length == 0) {
//            ZepT.error(ERROR_NO_INPUT);
//            System.exit(EXIT_CODE_FAILURE);
//        }//end if

        comp.processCommandLineArgs(args);

        if (files.isEmpty()) {
            ZepT.error(ERROR_NO_FILES);
        }//end if

        comp.configureParams();

        JavaSourceCodeStringObject javaCodeObject = JavaSourceCodeStringObject.NIL;

        for (String sourceFile : files) {

            javaCodeObject = ZepT.transpile(new File(sourceFile));

            comp.compileZeptoN(javaCodeObject, sourceFile);

        }//end for

    }//end compile
    public static String encodeLiteral(final String literal) {

        final StringBuilder str = new StringBuilder();

        for (char chr : literal.toCharArray()) {
            str.append("\\u").append(String.format("%04X", (int) chr));
        }//end for

        return str.toString();

    }//end encodeLiteral
    /**
     * Report a compiler error and then exit with status code of failure with a
     * problem.
     *
     * @param text - error message to report to the user.
     * @param args - error message arguments to report.
     */
    public static void error(final String text, final Object... args) {

        out.printf("%nError! ");
        out.printf(text, args);
        out.printf("%n%n");
        
//        if (panicFlag) {
//            System.exit(EXIT_CODE_PROBLEM);
//        }//end if

    }//end error

    /**
     * @return JavaCompiler - get system Java Compiler instance
     */
    public static final JavaCompiler getJavac() {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        if (compiler == null) {

            try {
                Class<?> javacTool = Class.forName("com.sun.tools.javac.api.JavacTool");
                Method create = javacTool.getMethod("create");
                compiler = (JavaCompiler) create.invoke(null);
            } catch (Exception e) {
                throw new RuntimeException(String.format("getJavac() Error: %s%n%n", e));
            }//end try

        }//end if

        return compiler;

    }//end getJavac
    private static StringBuilder getLineStrBAt(final int idx, final Map<Integer, StringBuilder> list) {

        StringBuilder strb = list.get(idx);
        if (strb == null) {
            strb = new StringBuilder();
        }//end if

        return strb;

    }//end getLineStrBAt
    public static final String getPackName(final String progName) {

        String packName = packageMap.get(progName);

        return packName;

    }//end getPackName

    public static final String getProgramName(final String zepSource) {

        String prog = EMPTY_STRING;

        try {

            int progIdent = zepSource.indexOf("prog ");

            int openBrace = zepSource.indexOf("{", progIdent);

            prog = zepSource.substring(progIdent + 5, openBrace);

            prog = prog.trim();

        } catch (Exception ex) {
            System.out.printf("Error: Unable to determine the program name identifier using the 'prog' keyword!%n");
        }//end try

        return prog;

    }//end getProgramName
    public static final String getProgramName(final String zepSource, final IOutput out) {
        String prog = EMPTY_STRING;
        try {

            int progIdent = zepSource.indexOf("prog ");

            int openBrace = zepSource.indexOf("{", progIdent);

            prog = zepSource.substring(progIdent + 5, openBrace);

            prog = prog.trim();

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
            out.printfFailure("Error: Unable to determine the program name identifier using the 'prog' keyword!%n");
        }//end try

        return prog;

    }//end getProgramName
    private static boolean isComment(final Token tok) {

        TokenType type = tok.getType();
        return (type == TokenType.SINGLE_LINE_COMMENT
                || type == TokenType.SINGLE_LINE_COMMENT_START
                || type == TokenType.MULTI_LINE_COMMENT
                || type == TokenType.MULTI_LINE_COMMENT_START);

    }//end isComment

    private static boolean isIdent(final Token tok) {

        TokenType type = tok.getType();
        return (type == TokenType.IDENTIFIER);

    }//end isIdent

    /**
     * Pad the multiple line comments with whitespace but preserve \n and \r to
     * maintain line number
     *
     * @param code - raw ZeptoN source code with comments.
     */
    public static String padCommentStar(final String code) {

        StringBuilder text = new StringBuilder(code);

        int head = 0;
        int tail = -1;

        for (;;) {

            head = code.indexOf("/*", head);

            tail = code.indexOf("*/", head + 2);

            if (head == -1) {
                break;
            }//end if

            for (int idx = head; idx < tail + 2; idx++) {

                char chr = code.charAt(idx);
                if (!Character.isWhitespace(chr)) {
                    text.replace(idx, idx + 1, CODE_SPC);
                }//end if

            }//end for idx

            head = tail + 2;

        }//end for ;;

        return text.toString();
    }//end padCommentStar
    /**
     * Print compiler USEINFO and OPTIONS and then exit without invoking
     * compiler.
     */
//    public static void printOptions() {
//
//        out.printf("%n%s%n%s%n", USEINFO, OPTIONS);
//        System.exit(EXIT_CODE_SUCCESS);
//
//    }//end printOptions

    /**
     * Print compiler RELEASE and VERSION and then exit without invoking
     * compiler.
     */
//    public static void printVersion() {
//
//        out.printf("%s%n%s%n", RELEASE, VERSION);
//        System.exit(EXIT_CODE_SUCCESS);
//
//    }//end printVersion

    /**
     * Process the comments in the ZeptoN source code but preserve line
     * structure for error reporting.
     *
     * @param code - raw commented ZeptoN source code.
     */
    public static String processComments(final String code) {

        String result = padCommentStar(code);

        result = result.replaceAll("//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/", EMPTY_STRING);

        return result;

    }//end processComments

    public static String processStringLiterals(final String text) {

        String code = text;

        final String DQUOTE = Character.toString('\u0022');

        int idx = 0;
        for (;;) {

            int head = code.indexOf(DQUOTE, idx);
            if (head == -1) {
                break;
            }
            int tail = code.indexOf(DQUOTE, head + 1);
            if (tail == -1) {
                break;
            }

            String litString = code.substring(head, tail + 1);

            String unicodeString = ZepT.encodeLiteral(litString);

            code = code.replace(litString, unicodeString);

            idx = tail + 1;

        }//end for

        return code;

    }//end processStringLiterals

    public static JavaSourceCodeStringObject transpile(final File file) {

        JavaSourceCodeStringObject javaObject = JavaSourceCodeStringObject.NIL;

        final Map<Integer, StringBuilder> list = new HashMap<>();

        ILexer lex = new Lexer(file); //JavaCC lexer from library

        String progName = ""; //program name used in code object
        String packName = ""; //package name used in code object

        int lastLineNumber = 0;

        int braceCount = 0, lbraceCount = 0, rbraceCount = 0, balance = 0;

        boolean importFlag = true;  //need to inject imports
        boolean progBlockFlag = false; //inside program block begin { ... }
        boolean packageFlag = false; //ZeptoN program has package namespace

        while (lex.hasTok()) {

            Token tok = lex.getTok();

            if (isComment(tok)) {

                for (int idx = tok.getBeginLine(); idx < tok.getEndLine(); idx++) {

                    ZepT.addList(idx, list, CODE_SPC);

                }//end for

                continue;

            }//end if

            if (isIdent(tok)) {

                //check for leading '_' or '$' char
                char chr = tok.getImage().charAt(0);
                switch (chr) {

                    case '_':
                    case '$': //error

                        out.printf("Error: Identifier invalid; identifier '%s' contains illegal character '%c' for ZeptoN identifier prefix on line: %d position: %d.%n", tok.getImage(), chr, tok.getBeginLine(), tok.getBeginColumn());
                        break;

                    default: ;
                        break;

                }//end switch

            }//end if

            int idx = 0;

            switch (tok.getImage()) {

                case "{":
                    //count braces to determine balance correctly
                    braceCount++;
                    lbraceCount++;

                    break;

                case "}":

                    braceCount++;
                    rbraceCount++;

                    balance = lbraceCount - rbraceCount;

                    if (progBlockFlag && braceCount > 1) {

                        switch (balance) {

                            //last closing brace insert predefined environment source code
                            case 0:

                                lastLineNumber = tok.getBeginLine();
                                tok.setImage("} " + EOL + EOL + SOURCE_CODE_BODY + CODE_SPC + EOL + EOL + "}");
                                break;

                            //next to last closing brace insert catch-finally clause from predefined try-statement
                            case 1:
                                //MAIN_TRY_CATCH
                                tok.setImage("} catch(Exception _$ex) {" + EOL + "  System.out.printf(\"Uncaught ZeptoN Program Exception: '%s' is '%s'.%n\", _$ex.getClass().getName(), _$ex.getMessage()); \n} finally {  " + CODE_SPC + "  _$close();" + CODE_SPC + "}" + CODE_SPC + EOL + CODE_SPC + CODE_SPC + "System.exit(0);" + CODE_SPC);

                                break;

                        }//end switch

                    }//end if

                    break;

                case "begin":

                    progBlockFlag = true;

                    idx = tok.getBeginLine();
                    Token beginTok = tok;
                    ZepT.addList(idx, list, "public " + progName + "(){ ; } " + "public static final void main(String[] _$args)"); //PROG_BLOCK_HEAD

                    tok = lex.getTok();

                    if (tok.getType() == TokenType.LBRACE) {

                        lbraceCount++;
                        braceCount++;

                        //MAIN_TRY_HEAD
                        tok.setImage("{ try { _$start(_$args); " + progName + " _$me = new " + progName + "(); ");

                    } else {

                        out.printf("Error: Opening right brace '{' must follow keyword 'begin' on line: %d position: %d.%n", beginTok.getBeginLine(), beginTok.getBeginColumn());
                    }//end if

                    break;

                case "main":

                    //modify token image with _$ to avoid conflict with predefined 'main' identifier
                    tok.setImage("_$main");

                    break;

                case "me":

                    //check if 'me' predefined variable is within program block
                    if (!progBlockFlag) {
                        out.printf("Error: Keyword 'me' only used within program block begin { ... } on line: %d position: %d.%n", tok.getBeginLine(), tok.getBeginColumn());
                    }//end if

                    tok.setImage("_$me");

                    break;

                case "package":

                    packageFlag = true;
                    ZepT.addList(list, tok);

                    final StringBuilder packageName = new StringBuilder();

                    for (;;) {

                        tok = lex.getTok();
                        ZepT.addList(list, tok);

                        if (tok.getType() != TokenType.SEMICOLON) {

                            packageName.append(tok.getImage());

                        } else {

                            //based on JDK, add implicit imports java.time.*; for JDK >= 8 ??
                            //ZepT.addList(tok.getBeginLine(), list, "import java.math.*; import java.net.*; import java.util.*;");
                            ZepT.addList(tok.getBeginLine(), list, SOURCE_CODE_HEAD);

                            importFlag = false;
                            break;

                        }//end if

                    }//end for

                    packName = packageName.toString();

                    continue;

                case "prog":

                    idx = tok.getBeginLine();

                    if (importFlag) {

                        importFlag = false;

                        ZepT.addList(idx, list, SOURCE_CODE_HEAD);

                    }//end if

                    ZepT.addList(idx, list, "public final class"); //PROG_HEAD

                    //check lexeme identifier follows "prog" keyword
                    tok = lex.getTok();

                    if (tok.getType() != TokenType.IDENTIFIER) {

                        out.printf("Error: Identifier must follow keyword 'prog' on line: %d position: %d.%n", tok.getBeginLine(), tok.getBeginColumn());

                    }//end if

                    progName = tok.getImage();

                    break;

                default: ;
                    break;

            }//end switch

            ZepT.addList(list, tok);

        }//end while

        StringBuilder javaCode = new StringBuilder();

        for (int pos = 1; pos <= lastLineNumber; pos++) {

            final StringBuilder lineCode = list.get(pos);

            if (lineCode == null) {

                //insert space with platform end of line to maintain line numbering
                javaCode.append(CODE_SPC);
                javaCode.append(EOL);

            } else {

                javaCode.append(lineCode.toString());
                javaCode.append(EOL);

            }//end if

        }//end for

        if (packageFlag) {

            javaObject = new JavaSourceCodeStringObject(packName, progName, javaCode.toString());

        } else {

            javaObject = new JavaSourceCodeStringObject(progName, javaCode.toString());

        }//end if

        //add package namespace under program name to table/map used for running code in Zeptor
        ZepT.packageMap.put(progName, packName);

        return javaObject;

    }//end transpile

    public static JavaSourceCodeStringObject transpile(final String fileName) {
        return ZepT.transpile(new File(fileName));
    }//end transpile

    public static JavaSourceCodeStringObject transpileFromFile(final String fileName) {

        JavaSourceCodeStringObject javaCodeObject = JavaSourceCodeStringObject.NIL;

        try {

            String zepSource = new String(Files.readAllBytes(Paths.get(fileName)), ZepT.CHARSET);

            javaCodeObject = ZepT.transpileString(fileName, zepSource);

        } catch (Exception ex) {
            error("Transcompiler Exception: '%s' is '%s'%n", ex.getClass().getName(), ex.getMessage());
        }//end try

        return javaCodeObject;

    }//end transpileFile

    /**
     * Transcompile single ZeptoN source code file into Java source code for
     * compilation
     *
     * @param fileName - name of the external file_$containing the ZeptoN source
     * code.
     * @return JavaSouceCodeStringObject - subclass of SimpleJavaFileObject that
     * is the name and code of the ZeptoN source code.
     */
    public static JavaSourceCodeStringObject transpileString(final String fileName, final String sourceCodeZeptoN) {

        String packName = EMPTY_STRING;
        String prgmName = EMPTY_STRING;

        JavaSourceCodeStringObject javaObject = JavaSourceCodeStringObject.NIL;

        //abort transpile flag if ZeptoN source code 
        //  does not have 'prog' and 'begin' keyword
        boolean stopTranspileFlag = false;

        try {

            String zepSource = sourceCodeZeptoN;

            zepSource = ZepT.processStringLiterals(zepSource);

            zepSource = processComments(zepSource);

            if (!zepSource.contains("prog ")) {
                stopTranspileFlag = true;
                out.printf("%nFatal Error: ZeptoN source code '%s' missing 'prog' keyword.%n", fileName);
                if (panicFlag) {
                    return JavaSourceCodeStringObject.NIL;
                }//end if
            }//end if

            if (!zepSource.contains("begin")) {
                stopTranspileFlag = true;
                out.printf("%nFatal Error: ZeptoN source code '%s' missing 'begin' keyword.%n", fileName);
                if (panicFlag) {
                    return JavaSourceCodeStringObject.NIL;
                }//end if

            } else {
                //normalize begin { => begin{
                if (zepSource.contains("begin {")) {
                    //replace 'begin {' => 'begin{'
                    zepSource = zepSource.replace("begin {", "begin{");
                }//end if
            }//end if

            zepSource = zepSource.replaceAll("\\{", " {");

            //check for identifiers  containing _$, all ZeptoN identifiers must begin with letter character
            if (zepSource.contains(" _$")) {
                out.printf("ZeptoN source code '%s' contains an invalid identifier that begins with '_$' must begin with letter character.%n", fileName);
                if (panicFlag) {
                    return JavaSourceCodeStringObject.NIL;
                }//end if                
            }//end if

            //check for main method, change main method to mangled name
            if (zepSource.contains("void main")) {
                zepSource = zepSource.replace("main", "_$main");
            }//end if

            final StringBuilder javaSource = new StringBuilder(zepSource);

            boolean hasPackageName = false;

            if (zepSource.contains("package")) {
                int name = javaSource.indexOf("package");
                int semi = javaSource.indexOf(";");

                packName = javaSource.substring(name + 8, semi);
                hasPackageName = true;

                javaSource.delete(name, semi);

            }//end if

            int prgmIdent = javaSource.indexOf("prog ");

            if (prgmIdent == -1) {
                stopTranspileFlag = true;
                out.printf("ZeptoN source code '%s' 'prog' keyword is always followed with whitespace.%n", fileName);
                if (panicFlag) {
                    return JavaSourceCodeStringObject.NIL;
                }//end if

            } else {

                int openBrace = javaSource.indexOf("{", prgmIdent);

                prgmName = javaSource.substring(prgmIdent + 5, openBrace);

                prgmName = prgmName.trim();

                //check fileName in path is programName + ".ZepC"
                if (!fileName.contains(prgmName + ZepT.FILE_SOURCE_EXT)) {
                    stopTranspileFlag = true;
                    if (panicFlag) {
                        return JavaSourceCodeStringObject.NIL;
                    }//end if

                }//end if

            }//end if

            //fatal errors, but no panic, do not synthesize Java intermediate code
            if (stopTranspileFlag) {
                return JavaSourceCodeStringObject.NIL;
            }//end if

            int tailBrace = javaSource.lastIndexOf("}");

            //MAIN_FOOT                                           "                                                                                                                                                                                                                                            " PROG_FOOT
            javaSource.replace(tailBrace, tailBrace + 1, "} catch(Exception _$ex) {" + EOL + "  System.out.printf(\"Uncaught ZeptoN Program Exception: '%s' is '%s'.%n\", _$ex.getClass().getName(), _$ex.getMessage()); \n} finally {  " + CODE_SPC + "  _$close();" + CODE_SPC + "}" + CODE_SPC + "  System.exit(0);" + CODE_SPC + "}" + EOL + ZepT.SOURCE_CODE_BODY + "  " + EOL + "} " + EOL);

            String javaCode = javaSource.toString();

            javaCode = javaCode.replace("prog", "public final class");
            if (hasPackageName) {
                javaCode = String.format("package %s; ", packName) + ZepT.SOURCE_CODE_HEAD + javaCode;
            } else {
                javaCode = ZepT.SOURCE_CODE_HEAD + javaCode;
            }//end if

            javaCode = javaCode.replace("begin", "public " + prgmName + "(){ ; }" + CODE_SPC + CODE_SPC + "public static void main(String[] _$args){" + CODE_SPC + "  final " + prgmName + " me = new " + prgmName + "();" + CODE_SPC + "  try {" + CODE_SPC + "    _$start(_$args); ");

            //export transpiled code to external source file
            if (dumpFlag) {
                Path javaPath = Paths.get(prgmName + ".java");
                Files.write(javaPath, javaCode.getBytes());

                if (echoFlag) {
                    System.out.printf("ZeptoN Compiler: Dump raw Java source code as: '%s' external file.%n", javaPath);  //create dumpFile method??
                }//end if

            }//end if

            if (!hasPackageName) {
                javaObject = new JavaSourceCodeStringObject(prgmName, javaCode);
            } else {
                javaObject = new JavaSourceCodeStringObject(packName, prgmName, javaCode);
            }//end if

        } catch (Exception ex) {
            error("%nTranspiler Exception: '%s' is '%s'%n", ex.getClass().getName(), ex.getMessage());
            //ex.printStackTrace();
        }//end try

        return javaObject;

    }//end transpileString

    /**
     * Verify a ZeptoN source file in given path exists, is readable, and is of
     * minimum file size to compile.
     *
     * @param filePath - file path to ZeptoN source file to compiler.
     */
    public static void verifyFile(final String filePath) {

        Path path = Paths.get(filePath);

        try {

            if (!Files.exists(path)) {
                error(ERROR_FILE_EXIST, path.toString());
            } else if (!Files.isReadable(path)) {
                error(ERROR_FILE_READ, path.toString());
            } else if (Files.size(path) < FILE_SIZE_MIN) {
                error(ERROR_FILE_SMALL, path.toString());
            }//end if

        } catch (Exception ex) {
            error("Verify File Exception: '%s' is '%s'.%n", ex.getClass().getName(), ex.getMessage());
        }//end try

    }//end verifyFile

    public String dirPathOutput = ZepT.CWD;

    public final ArrayList<String> param = new ArrayList<>(); //Javac compiler parameters implicit and explicit

    public ZepT() {
        this(new CompilerDialog("Transpile ZeptoN Source Code"));
    }//end_$constructor

    public ZepT(final IOutput iprinter) {
        ZepT.out = iprinter;
    }//end consturctor

    public boolean compileFile(final String fileName) {

        if (!ZepT.files.contains(fileName)) {
            ZepT.files.add(fileName);
        }

        JavaSourceCodeStringObject code = ZepT.transpile(fileName);

        boolean status = this.compileZeptoN(code, fileName);

        return status;

    }//end compileFile

    /**
     * Compile single ZeptoN source code file using the Java API with compiler
     * parameters.
     *
     * @param fileName - name of the external file_$containing the ZeptoN source
     * code.
     */
    public boolean compileZeptoN(final JavaSourceCodeStringObject zepSrc, final String fileName) {

        if (zepSrc == JavaSourceCodeStringObject.NIL) {
            //zepSrc is default NIL, no point to compile
            return false;
        }//end if

        if (echoFlag) {
            out.printf("%nZeptoN Compiler Options: %s Files: %s Encoding: %s%n%n",
                    param.isEmpty() ? "None." : param.toString(), files.toString(), CHARSET);
        }//end if

        //0 - error, 1 - mandatory warning, 2 - note, 3 - other, 4 - warning, 5 - total diagnostic
        final int[] diagnosticCounter = new int[]{0, 0, 0, 0, 0, 0};

        boolean statusFlag = false;

        long timeStart = 0, timeClose = 0;

        try {

            Iterable<? extends JavaFileObject> list = Arrays.asList(zepSrc);

            JavaCompiler comp = getJavac();
            if (comp == null) {
                out.println("Fatal Internal Error: JavaCompiler is null!");
                System.exit(ZepT.EXIT_CODE_PROBLEM);
            }//end if

            DiagnosticCollector<JavaFileObject> diag = new DiagnosticCollector<>();

            StandardJavaFileManager file = comp.getStandardFileManager(diag, LOCALE, CHARSET);

            if (file == null) {
                out.println("Fatal Internal Error: StandardJavaFileManager is null!");
                System.exit(ZepT.EXIT_CODE_PROBLEM);
            }//end if

            JavaCompiler.CompilationTask task = comp.getTask(
                    SYS_ERR,
                    file,
                    diag,
                    param,
                    NO_ANNOTATION_PROC,
                    list);

            if (task == null) {
                out.println("Fatal Internal Error: JavaCompiler.CompilationTask is null!");
                System.exit(ZepT.EXIT_CODE_PROBLEM);
            }//end if

            if (timeFlag) {
                timeStart = System.currentTimeMillis();
                statusFlag = task.call();
                timeClose = System.currentTimeMillis();
            } else {
                statusFlag = task.call();
            }//end if

            if (!muteFlag) {

                if (briefFlag) {

                    for (Diagnostic<? extends JavaFileObject> diagnostic : diag.getDiagnostics()) {

                        Diagnostic.Kind kind = diagnostic.getKind();

                        switch (kind) {
                            case ERROR:
                                diagnosticCounter[0]++;
                                diagnosticCounter[5]++;
                                break;
                            case MANDATORY_WARNING:
                                diagnosticCounter[1]++;
                                diagnosticCounter[5]++;
                                break;
                            case NOTE:
                                diagnosticCounter[2]++;
                                diagnosticCounter[5]++;
                                break;
                            case OTHER:
                                diagnosticCounter[3]++;
                                diagnosticCounter[5]++;
                                break;
                            case WARNING:
                                diagnosticCounter[4]++;
                                diagnosticCounter[5]++;
                                break;
                        }//end switch

                    }//end for

                } else {
                    List<String> javaFileCode = new ArrayList<String>(Arrays.asList(zepSrc.getCode().split(EOL)));
                    this.diagnose(fileName, diag, javaFileCode);
                }//end if (briefFlag)

            }//end if (!ZepC.muteFlag)

            file.close();

        } catch (Exception ex) {
            Logger.LOG.logTrap(ex);
            out.printfFailure("ZeptoN Compiler Exception: '%s' is '%s'.%n", ex.getClass().getName(), ex.getMessage());
            statusFlag = false;

        } finally {

            if (briefFlag) {
                if (diagnosticCounter[5] > 0) {
                    out.printf("%3d Diagnostic messages:%n", diagnosticCounter[5]);
                    for (int x = 0; x < diagnosticCounter.length - 1; x++) {
                        if (diagnosticCounter[x] > 0) {

                            //0 - error, 1 - mandatory warning, 2 - note, 3 - other, 4 - warning, 5 - total diagnostic
                            switch (x) {
                                case 0:
                                    out.printf("  %3d Error!!!%n", diagnosticCounter[x]);
                                    break;
                                case 1:
                                    out.printf("  %3d Caution %n", diagnosticCounter[x]);
                                    break;
                                case 2:
                                    out.printf("  %3d Note    %n", diagnosticCounter[x]);
                                    break;
                                case 3:
                                    out.printf("  %3d Other   %n", diagnosticCounter[x]);
                                    break;
                                case 4:
                                    out.printf("  %3d Warning %n", diagnosticCounter[x]);
                                    break;
                                default:
                                    out.printf("  %3d Unknown class of error!%n", diagnosticCounter[x]);
                                    break;
                            }//end switch

                        }//end if

                    }//end for

                    out.println();

                } else {
                    out.println("No compiler diagnostic messages.");
                }//end if

            }//end if( briefFlag )

            if (timeFlag) {
                out.printf("Time: %d-ms for: %s%n", (timeClose - timeStart), fileName);
            }//end if

            if (echoFlag) {
                out.printf("ZeptoN Compiler result for file: '%s' is: ", fileName);
                if (statusFlag) {
                    out.println("Success.");
                } else {
                    out.println("Failure!");
                }
            }//end if

        }//end try

        return statusFlag;

    }//end compileZeptoN

    /**
     * Configure underlying Javac compiler parameters the WEJAC parameters
     * passed as command line arguments.
     */
    public void configureParams() {

        for (String sourceFile : files) {
            verifyFile(sourceFile);
        }//end for

        param.add("-d");
        param.add(dirPathOutput);

        param.add(finalFlag ? JAVAC_FINAL : JAVAC_DEBUG);

        //lint if reporting warnings, not just errors
        param.add(hushFlag ? XLINT_DO_NONE : XLINT_RUN_ALL);

    }//end configureParams

    /**
     * Diagnose compiler errors with error, position, and illustrative source
     * code line.
     *
     * @param fileName - name of the external file_$containing the ZeptoN source
     * code.
     * @param diagnostics - diagnostic information from compile of Java source
     * code.
     * @param javaFileCode - list_$containing the lines of Java/ZeptoN source
     * code from file.
     */
    public void diagnose(final String fileName,
            final DiagnosticCollector<JavaFileObject> diagnostics,
            final List<String> javaFileCode) {

        for (Diagnostic<?> diag : diagnostics.getDiagnostics()) {

            if (hushFlag) {
                if (diag.getKind() != Diagnostic.Kind.ERROR) {
                    continue;
                }//end if
            }//end if

            final String diagnosticText = diag.toString();

            switch (diag.getKind()) {
                case ERROR:
                    out.printf("Error:   %s.%n", fileName);
                    break;
                case MANDATORY_WARNING:
                    out.printf("Caution: %s.%n", fileName);
                    break;
                case NOTE: ;
                    break;
                case OTHER:
                    out.printf("Other:   %s.%n", fileName);
                    break;
                case WARNING:
                    out.printf("Warning: %s.%n", fileName);
                    break;

            }//end switch

            if (diag.getKind() != Diagnostic.Kind.NOTE) {

                out.printf("Line %d ", diag.getLineNumber());
                out.printf("At %d:", diag.getColumnNumber());

                String diagText = diagnosticText.split(":")[3];
                String diagLine = diagText.split(ENDLN)[0];

                out.printf("%s%n", diagLine);

                String codeLine = this.getCodeLine(javaFileCode, diag.getLineNumber(), diag.getColumnNumber());
                out.printf("%s%n", codeLine);

            } else {
                out.println(diag.getMessage(LOCALE));
            }//end if

            out.println();

        }//end for

    }//end diagnose

    /**
     * Get the source line of Java/ZeptoN source code and format with indicator
     * of the point of diagnostic error.
     *
     * @param srcCode - a list_$containing the Java/ZeptoN source code.
     * @param lineNum - the line number within the Java/ZeptoN source code to
     * retrieve.
     * @param colNum - the column position within the line for the diagnostic
     * error.
     * @return String - the line of source code formatted to indicate point of
     * error.
     */
    public String getCodeLine(final List<String> srcCode, final long lineNum, final long colNum) {

        try {

            String line = srcCode.get(((int) lineNum - 1));

            StringBuilder codeLine = new StringBuilder(line);
            codeLine.append(ENDLN);

            for (int x = 0; x < colNum - 1; x++) {
                codeLine.append(ZepT.CODE_SPC);
            }//end for

            codeLine.append("^");

            return codeLine.toString();

        } catch (Exception ex) {
            error("getCodeLine() Exception: '%s' is '%s'.%n", ex.getClass().getName(), ex.getMessage());
        }//end try

        return EMPTY_STRING;

    }//end getCodeLine

    /**
     * Process the command line arguments to set the internal parameters for
     * compilation.
     *
     * @param args - command line arguments to set compiler parameters during
     * compilation.
     */
    public void processCommandLineArgs(final String[] args) {

        int x; //external for-loop index used after exiting the loop structure
        for (x = 0; x < args.length; x++) {

            if (args[x].contains("-")) {

                switch (args[x]) {   //-dir or -home or -dest

                    case "-dump":
                        dumpFlag = true;
                        break;

                    case "-dir":
                        if (x + 1 >= args.length) {
                            error(ERROR_DIR_PATH);
                        } else if (args[x + 1].startsWith("-")) {
                            error(ERROR_DIR_PARAM);
                        } else if (args[x + 1].endsWith(".ZepC")) {
                            error(ERROR_DIR_ZEP);
                        } else {
                            dirPathOutput = args[x + 1]; //check if dirPathOutput exists?? or auto-created by backend Javac
                            //check if args[x+1] = '.' then do nothing, use default cwd ??
                            x++;
                            continue;
                        }//end if
                        break;

                    case "-panic":
                        panicFlag = true;
                        break;
                    case "-time":
                        timeFlag = true;
                        break;
                    case "-echo":
                        echoFlag = true;
                        break;
                    case "-final":
                        finalFlag = true;
                        break;
                    case "-brief":
                        if (hushFlag || muteFlag) {
                            error(ERROR_OPT_BRIEF);
                        }
                        briefFlag = true;
                        break;
                    case "-hush":
                        if (briefFlag || muteFlag) {
                            error(ERROR_OPT_HUSH);
                        }
                        hushFlag = true;
                        break;
                    case "-mute":
                        if (briefFlag || hushFlag) {
                            error(ERROR_OPT_MUTE);
                        }
                        muteFlag = true;
                        break;
                    case "-javac":
                        x = processJavacArguments(args, x);
                        break;
                    default:
                        error(ERROR_PARAM_WRONG, args[x]);
                        break;
                }//end switch

            } else {

                for (; x < args.length; x++) {

                    if (args[x].contains(FILE_SOURCE_EXT)) {
                        files.add(args[x]);
                    } else {

                        if (args[x].contains("-")) {
                            error(ERROR_PARAM_FILES, args[x]);
                        } else {
                            error(ERROR_FILE_EXTEN, args[x]);
                        }//end if

                    }//end if

                }//end for

                break;

            }//end if

        }//end for

    }//end processCommandLineArgs

    /**
     * Add any Javac compiler parameters to the compiler parameters passed to
     * the Java Compiler API.
     *
     * @param args - command line arguments for the ZeptoN compiler passed to
     * Java Compiler API.
     * @param idx - starting index position within array of command line
     * arguments.
     * @return int - closing index position within the array of command line
     * arguments.
     */
    public int processJavacArguments(final String[] args, final int idx) {

        int pos;

        for (pos = idx + 1; pos < args.length; pos++) {

            if (args[pos].contains(FILE_SOURCE_EXT)) {
                break;
            } else {
                param.add(args[pos]);
            }//end if

        }//end for

        return pos - 1;

    }//end processJavacArguments

    public void setBrief(final boolean flag) {
        ZepT.briefFlag = flag;
    } //-brief

    public void setDebug(final boolean flag) {
        ZepT.finalFlag = !flag;
    } //-final

    public void setDump(final boolean flag) {
        ZepT.dumpFlag = flag;
    }  //-dump

    public void setEcho(final boolean flag) {
        ZepT.echoFlag = flag;
    }  //-echo

    public void setFinal(final boolean flag) {
        ZepT.finalFlag = flag;
    } //-final

    public void setHush(final boolean flag) {
        ZepT.hushFlag = flag;
    }  //-hush

    public void setMute(final boolean flag) {
        ZepT.muteFlag = flag;
    }  //-mute

    public void setOutputDirPath(final String path) {
        this.dirPathOutput = path;
    } //-dir

    public void setPanic(final boolean flag) {
        ZepT.panicFlag = flag;
    } //-panic

    public void setParams() {
        param.add(finalFlag ? JAVAC_FINAL : JAVAC_DEBUG);

        param.add("-d");
        param.add(dirPathOutput);

    }//end setParams

    public void setTime(final boolean flag) {
        ZepT.timeFlag = flag;
    }  //-time

}//end class ZepC
