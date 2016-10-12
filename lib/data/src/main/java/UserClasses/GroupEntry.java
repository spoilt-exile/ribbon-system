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
 * Group entry class. 
 * Contatins all information about single group record.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class GroupEntry extends Generic.CsvElder{
    
    /**
     * Name of the group
     */
    public String GROUP_NAME;

    /**
     * Group commentary
     */
    public String COMM;

    public GroupEntry(String givenCsv) {
        this.baseCount = 2;
        this.currentFormat = csvFormatType.SimpleCsv;
        String[] parsedStruct = Generic.CsvFormat.fromCsv(this, givenCsv).get(0);
        GROUP_NAME = parsedStruct[0];
        COMM = parsedStruct[1];
    }

    @Override
    public String toCsv() {
        return "{" + this.GROUP_NAME + "},{" + this.COMM + "}";
    }
}
