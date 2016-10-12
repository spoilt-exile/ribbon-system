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

package UserClasses;

/**
 * User object in system. 
 * Contains all user properties.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class UserEntry extends Generic.CsvElder {
    
    /**
     * a2 endian constructor
     * @param givenCsv csv raw line
     * @since RibbonServer a2
     */
    public UserEntry(String givenCsv) {
        this.baseCount = 4;
        this.groupCount = 1;
        this.currentFormat = Generic.CsvElder.csvFormatType.ComplexCsv;
        java.util.ArrayList<String[]> parsedStruct = Generic.CsvFormat.fromCsv(this, givenCsv);
        String[] baseArray = parsedStruct.get(0);
        String[] groupsArray = parsedStruct.get(1);
        USER_NAME = baseArray[0];
        COMM = baseArray[1];
        H_PASSWORD = baseArray[2];
        IS_ENABLED = baseArray[3].equals("1") ? true : false;
        GROUPS = groupsArray;
    }
        
    /**
     * User name
     */
    public String USER_NAME;

    /**
     * MD5 hashsumm of password
     * @since RibbonServer a2
     */
    public String H_PASSWORD;

    /**
     * Comment string
     * @since RibbonServer a2
     */
    public String COMM;

    /**
     * Array with groups
     * @since RibbonServer a2
     */
    public String[] GROUPS;

    /**
     * State of users account
     * @since RibbonServer a2
     */
    public Boolean IS_ENABLED;

    @Override
    public String toCsv() {
        return "{" + this.USER_NAME + "},{" + this.COMM + "}," + Generic.CsvFormat.renderGroup(GROUPS) + "," + this.H_PASSWORD + "," + (IS_ENABLED ? "1" : "0");
    }
}
