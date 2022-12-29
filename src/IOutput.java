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
package xyz.zepton.zeptor.util;

public interface IOutput {

    public void print(final String str);

    public void printf(final String fmt, final Object... args);

    public void printFailure(final String str);

    public void printfFailure(final String fmt, final Object... args);

    public void printfSuccess(final String fmt, final Object... args);

    public void println();

    public void println(final String str);

    public void printlnFailure();

    public void printlnFailure(final String str);

    public void printlnSuccess();

    public void printlnSuccess(final String str);

    public void printSuccess(final String str);

}//end interface IOutput
