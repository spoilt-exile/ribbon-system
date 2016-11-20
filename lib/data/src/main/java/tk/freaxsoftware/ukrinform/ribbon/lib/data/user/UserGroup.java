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

import java.util.Map;
import java.util.Objects;
import tk.freaxsoftware.extras.faststorage.generic.ECSVAble;
import tk.freaxsoftware.extras.faststorage.generic.ECSVDefinition;
import tk.freaxsoftware.extras.faststorage.generic.ECSVFields;
import tk.freaxsoftware.extras.faststorage.reading.EntityReader;
import tk.freaxsoftware.extras.faststorage.writing.EntityWriter;

/**
 * Group entry class. 
 * Contatins all information about single group record.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class UserGroup implements ECSVAble<String>{
    
    public static final String TYPE = "RIBBON_USER_GROUP";
    
    public static final ECSVDefinition DEFINITION = ECSVDefinition.createNew()
            .addKey(String.class)
            .addPrimitive(ECSVFields.PR_STRING)
            .addMap(null, null);
    
    /**
     * Name of the group.
     */
    private String name;

    /**
     * Group commentary.
     */
    private String description;
    
    /**
     * Map of optional attributes.
     */
    private Map<String, String> attrs;

    /**
     * Empty constructor.
     */
    public UserGroup() {
    }

    /**
     * Parametr constructor.
     * @param name user group name;
     * @param description description of group;
     * @param attrs attribute map;
     */
    public UserGroup(String name, String description, Map<String, String> attrs) {
        this.name = name;
        this.description = description;
        this.attrs = attrs;
    }
    
    @Override
    public String getEntityType() {
        return TYPE;
    }

    @Override
    public String getKey() {
        return this.name;
    }

    @Override
    public void setKey(String key) {
        //Do nothing;
    }

    @Override
    public ECSVDefinition getDefinition() {
        return DEFINITION;
    }

    @Override
    public void readFromECSV(EntityReader<String> reader) {
        this.name = reader.readKey();
        this.description = reader.readString();
        this.attrs = reader.readMap();
    }

    @Override
    public void writeToECSV(EntityWriter<String> writer) {
        writer.writeKey(name);
        writer.writeString(description);
        writer.writeMap(attrs);
    }

    @Override
    public void update(ECSVAble<String> updatedEntity) {
        UserGroup group = (UserGroup) updatedEntity;
        this.description = group.getDescription();
        this.attrs = group.getAttrs();
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

    public Map<String, String> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, String> attrs) {
        this.attrs = attrs;
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
        if (!Objects.equals(this.attrs, other.attrs)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GroupEntry{" + "name=" + name + ", description=" + description + '}';
    }

}
