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

public interface IConfigure {

    //compiler error reporting options
    public void setBrief(final boolean flag);

    //build options debug or final/release mode
    public void setDebug(final boolean flag);

    //compiler options
    public void setDump(final boolean flag);

    public void setEcho(final boolean flag);

    public void setFinal(final boolean flag);

    public void setHush(final boolean flag);

    public void setMute(final boolean flag);

    //compiler build output directory path
    public void setOutputDirPath(final String path);

    public void setPanic(final boolean flag);

    public void setTime(final boolean flag);

}//end interface IConfigure
