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

import java.util.HashMap;
import java.util.Map;

import javax.swing.UIManager;

public enum ZeptorLAF {
	
    ACRYL("com.jtattoo.plaf.acryl.AcrylLookAndFeel", 0),
    AERO("com.jtattoo.plaf.aero.AeroLookAndFeel", 1),
    ALUMINUM("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel", 2),
    //DARCULA("com.formdev.flatlaf.FlatDarculaLaf", 4),
    FAST("com.jtattoo.plaf.fast.FastLookAndFeel", 3),
    GRAPHITE("com.jtattoo.plaf.graphite.GraphiteLookAndFeel", 4),
    HIFI("com.jtattoo.plaf.hifi.HiFiLookAndFeel", 5),
    INFONODE("net.infonode.gui.laf.InfoNodeLookAndFeel", 6),
    INTELLIJ("com.formdev.flatlaf.FlatIntelliJLaf", 7),
    LIGHT("com.formdev.flatlaf.FlatLightLaf", 8),
    LUNA("com.jtattoo.plaf.luna.LunaLookAndFeel", 9),
    MCWIN("com.jtattoo.plaf.mcwin.McWinLookAndFeel", 10),
    METAL("javax.swing.plaf.metal.MetalLookAndFeel", 11),
    MINT("com.jtattoo.plaf.mint.MintLookAndFeel", 12),
    MOTIF("com.sun.java.swing.plaf.motif.MotifLookAndFeel", 13),
    NOIRE("com.jtattoo.plaf.noire.NoireLookAndFeel", 14),
    PGS("com.pagosoft.plaf.PgsLookAndFeel", 15),
    PLASTIC("com.jgoodies.looks.plastic.PlasticLookAndFeel", 16),
    PLATFORM(UIManager.getSystemLookAndFeelClassName(), 17),
    SMART("com.jtattoo.plaf.smart.SmartLookAndFeel", 18),
    TEXTURE("com.jtattoo.plaf.texture.TextureLookAndFeel", 19);

    private static final Map<Integer, ZeptorLAF> mapIZ = new HashMap<>();
    public static final ZeptorLAF getZeptorLAF(final Integer idx) {
        ZeptorLAF result = mapIZ.get(idx);
        if (result == null) {
            result = ZeptorLAF.PLATFORM;
           
        }
        return result;
    }//end getZeptorLAF

    public static final void initMapEnum() {

        for (ZeptorLAF lafValue : ZeptorLAF.values()) {
            mapIZ.put(lafValue.getIndex(), lafValue);
        }//end for

    }//end initMapEnum

    private String lafImage = "Platform";

    private Integer lafIndex = 17;

    private ZeptorLAF(final String laf, final int idx) {
        this.lafImage = laf;
        this.lafIndex = idx;
    }//end constructor

    public String getImage() {
        return this.lafImage;
    }

    public Integer getIndex() {
        return this.lafIndex;
    }

}//end enum ZeptorLAF
