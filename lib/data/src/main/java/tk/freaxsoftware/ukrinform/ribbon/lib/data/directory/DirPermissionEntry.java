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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import tk.freaxsoftware.extras.faststorage.generic.ECSVAble;
import tk.freaxsoftware.extras.faststorage.generic.ECSVDefinition;
import tk.freaxsoftware.extras.faststorage.generic.ECSVFields;
import tk.freaxsoftware.extras.faststorage.generic.FieldConverter;
import tk.freaxsoftware.extras.faststorage.reading.EntityReader;
import tk.freaxsoftware.extras.faststorage.writing.EntityWriter;

/**
 * Permission description object.
 * 
 * <p>Display permission to information
 * directory which granted to user or group.</p>
 * @author Stanislav Nepochatov
 * @since RibbonServer a2
 */
public class DirPermissionEntry implements ECSVAble<String> {
    
    public static Boolean[] DIR_READ_ONLY = new Boolean[] {true, false, false};
    
    public static Boolean[] DIR_READ_WRITE = new Boolean[] {true, true, false};
    
    public static Boolean[] DIR_FULL_ACCESS = new Boolean[] {true, true, true};
    
    public final static String TYPE = "RIBBON_DIRECTORY_PERM_ENTRY";
    
    public final static ECSVDefinition DEFINITION = ECSVDefinition.createNew()
            .addKey(String.class)
            .addPrimitive(ECSVFields.PR_BOOLEAN)
            .addArray(FieldConverter.CONVERTER_BOOLEAN);
    
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
    private List<Boolean> permissionDescriptor;
    
    /**
     * Default constructor.
     */
    public DirPermissionEntry() {
    }
    
    /**
     * Compatibility constructor.
     * @param isGroupEntry flag for group entries;
     * @param key key of entry;
     * @param rawDescriptor raw descriptor of permissions like {@code 100} or {@code 110};
     */
    public DirPermissionEntry(Boolean isGroupEntry, String key, String rawDescriptor) {
        this.key = key;
        this.groupPermission = isGroupEntry;
        char[] rawPermissions = rawDescriptor.toCharArray();
        this.permissionDescriptor = new ArrayList<>();
        for (char entry: rawPermissions) {
            this.permissionDescriptor.add(entry == '1');
        }
    }
    
    /**
     * Parametric constructor.
     * @param isGroupEntry flag for group entries.
     * @param key key of entry.
     * @param descriptor descriptor of permissions
     */
    public DirPermissionEntry(Boolean isGroupEntry, String key, Boolean[] descriptor) {
        this.key = key;
        this.groupPermission = isGroupEntry;
        this.permissionDescriptor = Arrays.asList(descriptor);
    }
    
    @Override
    public String getKey() {
        return key;
    }

    @Override
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
        if (!Objects.equals(this.permissionDescriptor, other.permissionDescriptor)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DirPermissionEntry{" + "key=" + key + ", groupPermission=" + groupPermission + ", permissionDescriptor=" + permissionDescriptor + '}';
    }

    /**
     * Check permission with specified attempt action mode;
     * @param attemptMode integer identefication of type of the action 
     * which was initiated by user.
     * @return result of checking.
     */
    public Boolean checkByMode(Integer attemptMode) {
        if (attemptMode > (permissionDescriptor.size() - 1)) {
            return false;
        }
        return permissionDescriptor.get(attemptMode);
    }

    @Override
    public String getEntityType() {
        return TYPE;
    }

    @Override
    public ECSVDefinition getDefinition() {
        return DEFINITION;
    }

    @Override
    public void readFromECSV(EntityReader<String> reader) {
        this.key = reader.readKey();
        this.groupPermission = reader.readBoolean();
        this.permissionDescriptor = reader.readArray();
    }

    @Override
    public void writeToECSV(EntityWriter<String> writer) {
        writer.writeKey(key);
        writer.writeBoolean(groupPermission);
        writer.writeArray(permissionDescriptor);
    }

    @Override
    public void update(ECSVAble<String> updatedEntity) {
        DirPermissionEntry otherEntry = (DirPermissionEntry) updatedEntity;
        this.groupPermission = otherEntry.groupPermission;
        this.permissionDescriptor = otherEntry.permissionDescriptor;
    }
}
