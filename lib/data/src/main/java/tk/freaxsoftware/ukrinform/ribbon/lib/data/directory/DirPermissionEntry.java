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

package tk.freaxsoftware.ukrinform.ribbon.lib.data.directory;

import java.util.Arrays;
import java.util.Objects;

/**
 * Permission description object.
 * 
 * <p>Display permission to information
 * directory which granted to user or group.</p>
 * @author Stanislav Nepochatov
 * @since RibbonServer a2
 */
public class DirPermissionEntry extends tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvElder {
    
    /**
     * Access key (user or group)
     */
    private String key;
    
    /**
     * Show is current permission granted for the group
     */
    private Boolean groupPermission;

    /**
     * Permission array
     */
    private char[] permissionDescriptor;
    
    /**
     * Default constructor.
     * 
     * <p>Using for defining csv format options.</p>
     */
    public DirPermissionEntry() {
        this.currentFormat = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvElder.csvFormatType.DoubleStruct;
    }
    
    /**
     * Default constructor
     * @param key
     * @param descriptor
     */
    public DirPermissionEntry(String key, String descriptor) {
        this();
        this.key = key;
        groupPermission = key.charAt(0) == 'G' ? true : false;
        permissionDescriptor = descriptor.toCharArray();
    }
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean isGroupPermission() {
        return groupPermission;
    }

    public void setGroupPermission(Boolean groupPermission) {
        this.groupPermission = groupPermission;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.key);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DirPermissionEntry other = (DirPermissionEntry) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        if (!Objects.equals(this.groupPermission, other.groupPermission)) {
            return false;
        }
        if (!Arrays.equals(this.permissionDescriptor, other.permissionDescriptor)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DirPermissionEntry{" + "key=" + key + ", groupPermission=" + groupPermission + ", permissionDescriptor=" + permissionDescriptor + '}';
    }

    /**
     * Return csv representation of permission object
     * @return formated string representation of this permission object
     */
    @Override
    public String toCsv() {
        String returned = (groupPermission ? "G" : "U") + key + ":" + String.valueOf(permissionDescriptor);
        return returned;
    }

    /**
     * Check permission with specified attempt action mode;
     * @param attemptMode integer identefication of type of the action 
     * which was initiated by user.
     * @return result of checking.
     */
    public Boolean checkByMode(Integer attemptMode) {
        if (attemptMode > (permissionDescriptor.length - 1)) {
            return false;
        }
        if (permissionDescriptor[attemptMode] == '0') {
            return false;
        } else {
            return true;
        }
    }
}
