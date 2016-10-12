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

package MessageClasses;

/**
 * Tag entry object. 
 * Contains name of the tag and indexes where this tag is present.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class TagEntry {
        
    /**
     * Name of the tag
     */
    public String NAME;

    /**
     * Indexes of messages which contains this tag
     */
    public java.util.ArrayList<String> INDEXES;

    /**
     * Default costructor
     * @param givenName name of new created tag
     */
    public TagEntry(String givenName) {
        NAME = givenName;
        INDEXES = new java.util.ArrayList<String>();
    }

    /**
     * Return csv form of tag
     * @return csv line with tag name and it's index
     */
    public String toCsv() {
        return this.NAME + "," + Generic.CsvFormat.renderGroup(this.INDEXES.toArray(new String[this.INDEXES.size()]));
    }
}
