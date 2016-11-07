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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Message property class. This properties may control 
 * message processing.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class MessageProperty extends tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvElder {
    
    /**
     * Type of the property.
     */
    private String type;
    
    /**
     * Owner of this property.
     */
    private String user;
    
    /**
     * Short decription message for property (one line).
     */
    private String description;
    
    /**
     * Date of marking.
     */
    private String date;
    
    /**
     * Display property with supported type.
     */
    private Boolean typeSupported;
    
    /**
     * Types of message properties.
     */
    public static class Types {
        
        /**
         * Type list storage.
         */
        private final static Set<String> typeList = new HashSet<>(Arrays.asList(new String[] {
        
        /** Generic properties **/    
        "URGENT",                       //Message was marked as urgent
        "MARK_USER",                    //User was marked message with custom text message
        "MARK_ADM",                     //Admin was marked message with custom text message
        "PROCESSING_FORBIDDEN",         //User forbid any processing of message
        "NIGHT_EMBARGO",                //Message export will be performed at night
        "COPYRIGHT",                    //Message copyright field

        /** System properties **/
        "CORRUPTED_AND_RESTORED",       //Message lost one of the link and it's link was restored by system
        "CORRUPTED_AND_LOST",           //Message lost all links and system cann't restore message
        "RELOCATED",                    //Message was relocated during deletion of directory
        }));
        
        /**
         * Register type if it doesn't exist in <code>typeList</code>.
         * @param givenType type to register;
         * @return true if type added / false if type existed
         */
        public static Boolean registerTypeIfNotExist(String givenType) {
            if(!Types.typeList.contains(givenType)) {
                Types.typeList.add(givenType);
                return true;
            } else {
                return false;
            }
        }
        
        /**
         * Find out if type existed.
         * @param givenType type to check;
         * @return true if type registered / false if not; 
         */
        public static Boolean isTypeRegistered(String givenType) {
            return Types.typeList.contains(givenType);
        }
        
    }
    
    /**
     * Empty configuration constructor.
     */
    public MessageProperty() {
        this.baseCount = 4;
        this.currentFormat = csvFormatType.SimpleCsv;
    }
    
    /**
     * Default constructor from csv form.
     * @param givenCsv given csv line;
     */
    public MessageProperty(String givenCsv) {
        this();
        java.util.ArrayList<String[]> parsedStruct = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.fromCsv(this, givenCsv);
        String[] baseArray = parsedStruct.get(0);
        this.type = baseArray[0];
        this.user = baseArray[1];
        this.description = baseArray[2];
        this.date = baseArray[3];
        this.typeSupported = Types.isTypeRegistered(type);
    }
    
    /**
     * All parametrick constructor for internal usage.
     * @param givenType
     * @param givenUser
     * @param givenMessage
     * @param givenDate 
     */
    public MessageProperty(String givenType, String givenUser, String givenMessage) {
        type = givenType;
        user = givenUser;
        description = givenMessage;
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        java.util.Date now = new java.util.Date();
        this.date = dateFormat.format(now);
        this.typeSupported = Types.isTypeRegistered(type);
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.type);
        hash = 13 * hash + Objects.hashCode(this.date);
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
        final MessageProperty other = (MessageProperty) obj;
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.typeSupported, other.typeSupported)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MessageProperty{" + "type=" + type + ", user=" + user + ", description=" + description + ", date=" + date + ", typeSupported=" + typeSupported + '}';
    }
    
    /**
     * Find oot about given type safety.
     * @return true if type registered / false if not.
     */
    public Boolean isTypeSafe() {
        return typeSupported;
    }
    
    @Override
    public String toCsv() {
        return this.type + ",{" + this.user + "},{" + (this.description != null ? this.description : "") + "}," + this.date;
    }
}
