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

/**
 * A class that defines text utilities
 *
 * @author Tan Hong Cheong
 * @version 20040510
 */
public class MyTextUtil {

    /**
     * @param s the string being searched
     * @param searchText the search text
     * @param caseSensitive wether to be case sensitive
     * @param whole whether to find whole word only, whole word means left and
     * right have a space
     * @param start the start position
     * @return position of search string
     */
    public static int find(String s, String searchText, boolean caseSensitive, boolean whole, int start) {
        return find(s, searchText, caseSensitive, whole, start, s.length() - 1);
    }

    /**
     * @param s the string being searched
     * @param searchText the search text
     * @param caseSensitive wether to be case sensitive
     * @param whole whether to find whole word only, whole word means left and
     * right have a space
     * @param start the start position
     * @param end the end position
     * @return position of search string
     */
    public static int find(String s, String searchText, boolean caseSensitive, boolean whole, int start, int end) {
        int pos = findCaseInsensitive(s, searchText, start, end);
        StringBuilder b = new StringBuilder(s);
        if (pos == -1)//not found
        {
            return -1;
        }
        boolean temp = true;
        //check if it is a whole word
        if (whole) {
            boolean leftIsSpace = true;
            boolean rightIsSpace = true;
            //check if it is whole word
            if ((pos > 0) && (b.charAt(pos - 1) != ' ')) {
                leftIsSpace = !Character.isLetterOrDigit(b.charAt(pos - 1));
            }
            if (pos + searchText.length() < b.length()) {
                rightIsSpace = !Character.isLetterOrDigit(b.charAt(pos + searchText.length()));
            }
            if (!((leftIsSpace) && (rightIsSpace))) {
                temp = false;
            }
        }
        //either it matches whole word or do not need to check if it is whole word
        if ((caseSensitive) && (temp)) {
            //check the case
            String str = b.substring(pos, pos + searchText.length());
            if (!str.equals(searchText)) {
                temp = false;
            }
        }
        if (temp) {
            return pos;
        } else {
            //continue searching
            return find(s, searchText, caseSensitive, whole, pos + searchText.length());
        }
    }

    /**
     * Uses Knuth-Morris-Pratt algorithm
     * <b>note<b> position >=start and <=end
     *
     * @param str the string being searched
     * @param start the start position to search
     * @param end the end position to reach
     * @return the start position where search string occur, -1 if cannot find
     */
    public static int findCaseInsensitive(String str, String searchText, int start, int end) {
        StringBuilder t = new StringBuilder(str.toUpperCase());
        StringBuilder p = new StringBuilder(searchText.toUpperCase());
        int textLength = t.length();
        int searchTextLength = p.length();
        int[] fail = new int[searchTextLength];
        if (start < 0) {
            start = 0;
            //recalculate the correct start position
        }
        if (end > (textLength - 1)) { //recaculate the correct end pos
            end = textLength - 1;
        }

        if (start > end) { //cannot start search
            return -1;
        }

        if ((end - start + 1) < searchTextLength) { //not possible to find string
            return -1;
        }
        if (searchTextLength == 0) {
            //there is nothing to search for
            return -1;
        }

        int s;
        //KMP setup
        fail[0] = -1;//the first character doesn't match must restart
        for (int k = 1; k < searchTextLength; k++) {
            s = fail[k - 1];
            boolean found = false;
            while ((s >= 0) && (!found)) {
                if (p.charAt(s) == p.charAt(k - 1)) {
                    found = true;
                } else {
                    s = fail[s];
                }
            }
            fail[k] = s + 1;
        }
        //loop 2
        for (int k = 1; k < searchTextLength; k++) {
            if (p.charAt(k) == p.charAt(fail[k])) {
                fail[k] = fail[fail[k]];
            }
        }
        //KMP scan
        int j = start;//current character in t
        int k = 0;//current character in p
        while (j < textLength)//haven't reached end of text
        {
            if (k > (searchTextLength - 1)) {
                return j - searchTextLength;//success
            }
            if (k == -1) {
                j++;
                k = 0;//start pattern over
            } else if (t.charAt(j) == p.charAt(k)) {
                j++;
                k++;
            } else {
                k = fail[k];
            }
        }
        //for cases where end of string reached and found 
        if (k > (searchTextLength - 1)) {
            return j - searchTextLength;//success
        } else {
            return -1;
        }
    }
}
