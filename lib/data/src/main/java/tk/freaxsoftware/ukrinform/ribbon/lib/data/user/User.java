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
import java.util.Objects;

/**
 * User object in system. 
 * Contains all user properties.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class User extends tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvElder {
    
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
    private String[] groups;

    /**
     * State of users account
     * @since RibbonServer a2
     */
    private Boolean enabled;
    
    /**
     * a2 endian constructor
     * @param givenCsv csv raw line
     * @since RibbonServer a2
     */
    public User(String givenCsv) {
        this.baseCount = 4;
        this.groupCount = 1;
        this.currentFormat = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvElder.csvFormatType.ComplexCsv;
        java.util.ArrayList<String[]> parsedStruct = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.fromCsv(this, givenCsv);
        String[] baseArray = parsedStruct.get(0);
        String[] groupsArray = parsedStruct.get(1);
        login = baseArray[0];
        description = baseArray[1];
        password = baseArray[2];
        enabled = baseArray[3].equals("1") ? true : false;
        groups = groupsArray;
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

    public String[] getGroups() {
        return groups;
    }

    public void setGroups(String[] groups) {
        this.groups = groups;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
        if (!Arrays.deepEquals(this.groups, other.groups)) {
            return false;
        }
        if (!Objects.equals(this.enabled, other.enabled)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UserEntry{" + "login=" + login + ", password=" + password + ", description=" + description + ", groups=" + groups + ", enabled=" + enabled + '}';
    }

    @Override
    public String toCsv() {
        return "{" + this.login + "},{" + this.description + "}," + tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.renderGroup(groups) + "," + this.password + "," + (enabled ? "1" : "0");
    }
}
