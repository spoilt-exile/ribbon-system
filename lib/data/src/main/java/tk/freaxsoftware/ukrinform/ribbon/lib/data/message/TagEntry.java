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

package tk.freaxsoftware.ukrinform.ribbon.lib.data.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Tag entry object. 
 * Contains name of the tag and indexes where this tag is present.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class TagEntry {
        
    /**
     * Name of the tag
     */
    private String name;

    /**
     * Indexes of messages which contains this tag
     */
    private List<String> messages;

    /**
     * Default costructor
     * @param givenName name of new created tag
     */
    public TagEntry(String givenName) {
        name = givenName;
        messages = new ArrayList<>();
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.name);
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
        final TagEntry other = (TagEntry) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.messages, other.messages)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TagEntry{" + "name=" + name + ", messages=" + messages + '}';
    }

    /**
     * Return csv form of tag
     * @return csv line with tag name and it's index
     */
    public String toCsv() {
        return this.name + "," + tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.renderGroup(this.messages.toArray(new String[this.messages.size()]));
    }

}
