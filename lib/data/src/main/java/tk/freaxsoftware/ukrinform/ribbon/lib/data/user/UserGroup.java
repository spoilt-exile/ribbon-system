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

package tk.freaxsoftware.ukrinform.ribbon.lib.data.user;

import java.util.Objects;

/**
 * Group entry class. 
 * Contatins all information about single group record.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class UserGroup extends tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvElder{
    
    /**
     * Name of the group
     */
    private String name;

    /**
     * Group commentary
     */
    private String description;

    public UserGroup(String givenCsv) {
        this.baseCount = 2;
        this.currentFormat = csvFormatType.SimpleCsv;
        String[] parsedStruct = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.fromCsv(this, givenCsv).get(0);
        name = parsedStruct[0];
        description = parsedStruct[1];
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.name);
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
        final UserGroup other = (UserGroup) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GroupEntry{" + "name=" + name + ", description=" + description + '}';
    }

    @Override
    public String toCsv() {
        return "{" + this.name + "},{" + this.description + "}";
    }


}
