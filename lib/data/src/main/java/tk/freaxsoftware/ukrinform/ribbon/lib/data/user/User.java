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

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import tk.freaxsoftware.extras.faststorage.generic.ECSVAble;
import tk.freaxsoftware.extras.faststorage.generic.ECSVDefinition;
import tk.freaxsoftware.extras.faststorage.generic.ECSVFields;
import tk.freaxsoftware.extras.faststorage.generic.EntityListReference;
import tk.freaxsoftware.extras.faststorage.reading.EntityReader;
import tk.freaxsoftware.extras.faststorage.writing.EntityWriter;

/**
 * User object in system. 
 * Contains all user properties.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class User implements ECSVAble<String> {
    
    public static final String TYPE = "RIBBON_USER";
    
    public static final ECSVDefinition DEFINITION = ECSVDefinition.createNew()
            .addKey(String.class)
            .addPrimitive(ECSVFields.PR_STRING)
            .addPrimitive(ECSVFields.PR_STRING)
            .addReferenceArray(UserGroup.class, String.class)
            .addPrimitive(ECSVFields.PR_BOOLEAN)
            .addMap(null, null);
    
    /**
     * User name
     */
    private String login;

    /**
     * MD5 hashsumm of password
     * @since RibbonServer a2
     */
    private String password;

    /**
     * Comment string
     * @since RibbonServer a2
     */
    private String description;

    /**
     * Array with groups
     * @since RibbonServer a2
     */
    private EntityListReference<UserGroup, String> groups;

    /**
     * State of users account
     * @since RibbonServer a2
     */
    private Boolean enabled;
    
    /**
     * Map of optional attributes.
     */
    private Map<String, String> attrs;

    /**
     * Empty constructor.
     */
    public User() {
    }

    /**
     * Parametr constructor.
     * @param login user login;
     * @param password hashsum of the password;
     * @param description optional description for user;
     * @param groups user groups reference;
     * @param enabled user enable flag;
     * @param attrs attribute map;
     */
    public User(String login, String password, String description, String[] groups, Boolean enabled, Map<String, String> attrs) {
        this.login = login;
        this.password = password;
        this.description = description;
        this.groups = new EntityListReference<>(Arrays.asList(groups), UserGroup.class, false);
        this.enabled = enabled;
        this.attrs = attrs;
    }
    
    @Override
    public String getEntityType() {
        return TYPE;
    }

    @Override
    public String getKey() {
        return login;
    }

    @Override
    public void setKey(String key) {
        //Do nothing
    }

    @Override
    public ECSVDefinition getDefinition() {
        return DEFINITION;
    }

    @Override
    public void readFromECSV(EntityReader<String> reader) {
        this.login = reader.readKey();
        this.password = reader.readString();
        this.description = reader.readString();
        this.groups = reader.readReferenceArray(UserGroup.class);
        this.enabled = reader.readBoolean();
        this.attrs = reader.readMap();
    }

    @Override
    public void writeToECSV(EntityWriter<String> writer) {
        writer.writeKey(this.login);
        writer.writeString(this.password);
        writer.writeString(this.description);
        writer.writeReferenceArray(groups);
        writer.writeBoolean(enabled);
        writer.writeMap(attrs);
    }

    @Override
    public void update(ECSVAble<String> updatedEntity) {
        User otherUser = (User) updatedEntity;
        this.password = otherUser.getPassword();
        this.description = otherUser.getDescription();
        this.groups = otherUser.getGroups();
        this.enabled = otherUser.isEnabled();
        this.attrs = otherUser.getAttrs();
    }
    
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EntityListReference<UserGroup, String> getGroups() {
        return groups;
    }

    public void setGroups(EntityListReference<UserGroup, String> groups) {
        this.groups = groups;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
        hash = 37 * hash + Objects.hashCode(this.login);
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
        final User other = (User) obj;
        if (!Objects.equals(this.login, other.login)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.enabled, other.enabled)) {
            return false;
        }
        if (!Objects.equals(this.attrs, other.attrs)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UserEntry{" + "login=" + login + ", password=" + password + ", description=" + description + ", groups=" + groups + ", enabled=" + enabled + ", attrs=" + attrs + '}';
    }
}
