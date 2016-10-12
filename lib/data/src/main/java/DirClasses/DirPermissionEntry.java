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

package DirClasses;

/**
 * Permission description object.
 * 
 * <p>Display permission to information
 * directory which granted to user or group.</p>
 * @author Stanislav Nepochatov
 * @since RibbonServer a2
 */
public class DirPermissionEntry extends Generic.CsvElder {
    
    /**
     * Default constructor.
     * 
     * <p>Using for defining csv format options.</p>
     */
    public DirPermissionEntry() {
        this.currentFormat = Generic.CsvElder.csvFormatType.DoubleStruct;
    }
    
    /**
     * Default constructor
     * @param rawDescriptor string descriptor of permission to directory
     */
    public DirPermissionEntry(String rawDescriptor) {
        this();
        java.util.ArrayList<String[]> parsedStruct = Generic.CsvFormat.fromCsv(this, rawDescriptor);
        KEY = parsedStruct.get(0)[0].substring(1);
        IS_GROUP = parsedStruct.get(0)[0].charAt(0) == 'G' ? true : false;
        PERM_ARRAY = parsedStruct.get(0)[1].toCharArray();
    }

    /**
     * Access key (user or group)
     */
    public String KEY;
    
    /**
     * Show is current permission granted for the group
     */
    public Boolean IS_GROUP;

    /**
     * Permission array
     */
    private char[] PERM_ARRAY;

    /**
     * Return csv representation of permission object
     * @return formated string representation of this permission object
     */
    @Override
    public String toCsv() {
        String returned = (IS_GROUP ? "G" : "U") + KEY + ":" + String.valueOf(PERM_ARRAY);
        return returned;
    }

    /**
     * Check permission with specified attempt action mode;
     * @param attemptMode integer identefication of type of the action 
     * which was initiated by user.
     * @return result of checking.
     */
    public Boolean checkByMode(Integer attemptMode) {
        if (attemptMode > (PERM_ARRAY.length - 1)) {
            return false;
        }
        if (PERM_ARRAY[attemptMode] == '0') {
            return false;
        } else {
            return true;
        }
    }
}
