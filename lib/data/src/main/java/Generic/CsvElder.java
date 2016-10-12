/**
 * This file is part of libRibbonData library (check README).
 * Copyright (C) 2012-2013 Stanislav Nepochatov
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
**/

package Generic;

/**
 * Additional fields and methods to represent
 * data structure as csv formated line.
 * 
 * @author Stanislav Nepochatov
 */
public abstract class CsvElder {
    
    /**
     * Count of arguments which may be 
     * parsed as single string.
     * 
     * <p><b>WARNING!</b> This variable should be 
     * overriden by csvElder child classes.</p>
     */
    public Integer baseCount = -1;
    
    /**
     * Count of argument which must be
     * parsed as groups;
     * 
     * <p>Like: <code>[item0,item1,...itemN]</code></p>
     * 
     * <p><b>WARNING!</b> This variable should be 
     * overriden by csvElder child classes.</p>
     */
    public Integer groupCount = -1;
    
    /**
     * Enumeration of csv format types.
     */
    public static enum csvFormatType {
        
        /**
         * Simple csv format.
         * 
         * <p>Example: <code>word1,word2...</code></p>
         */
        SimpleCsv,
        
        /**
         * Complex csv format (with groups).
         * 
         * <p>Example: <code>word1,[gword1,gword2]...</code></p>
         */
        ComplexCsv,
        
        /**
         * Double struct (identeficator and value).
         * 
         * <p>Example: <code>id:value</code></p>
         */
        DoubleStruct
    }
    
    /**
     * Current format type of object.
     * 
     * <p><b>WARNING!</b> This variable should be 
     * overriden by csvElder child classes.</p>
     */
    public csvFormatType currentFormat;
    
    /**
     * Return csv representation of data object.
     * 
     * <p><b>WARNING!</b> This method should be 
     * overriden by csvElder child classes.</p>
     * @return csv formated string
     */
    public abstract String toCsv();
    
}
