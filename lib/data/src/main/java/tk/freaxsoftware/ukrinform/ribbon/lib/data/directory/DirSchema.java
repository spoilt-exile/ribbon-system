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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import tk.freaxsoftware.extras.faststorage.generic.ECSVAble;
import tk.freaxsoftware.extras.faststorage.generic.ECSVDefinition;
import tk.freaxsoftware.extras.faststorage.generic.ECSVFields;
import tk.freaxsoftware.extras.faststorage.generic.ECSVFormat;
import tk.freaxsoftware.extras.faststorage.reading.EntityReader;
import tk.freaxsoftware.extras.faststorage.writing.EntityWriter;

/**
 * Directory schema object.
 * 
 * <p>Using for transporting through directories
 * tree and making dirEntry object.</p>
 * @author Stanislav Nepochatov
 */
public class DirSchema implements ECSVAble<String> {
    
    public final static String TYPE = "RIBBON_DIRECTORY";
    
    public final static ECSVDefinition DEFINITION = ECSVDefinition.createNew()
            .addKey(String.class)
            .addPrimitive(ECSVFields.PR_STRING)
            .addArray(null)
            .addInternalArray(DirPermissionEntry.class, String.valueOf(ECSVFormat.INTERNAL_DEFAULT_SEPARATOR), ECSVFormat.INTERNAL_DEFAULT_SEPARATOR_EXPR)
            .addArray(null);
    
    /**
     * Full directory path
     */
    private String fullName;

    /**
     * Commentary for directory
     */
    private String description;

    /**
     * Directory's supported languages
     * @since RibbonServer a2
     */
    private List<String> languages;

    /**
     * Access list for directory
     * @since RibbonServer a2
     */
    private List<DirPermissionEntry> accessEntries;

    /**
     * Directory's exports list
     * @since RibbonServer a2
     */
    private List<String> exportList;
    
    /**
     * Default constructor.
     * 
     * <p>Using for defining csv format options.</p>
     */
    public DirSchema() {
    }

    /**
     * Parametrick costructor.
     * @param givenPath full path of directory
     * @param givenComm comment for directory
     */
    public DirSchema(String givenPath, String givenComm) {
        fullName = givenPath;
        description = givenComm;
        languages = new ArrayList<>();
        languages.add("ALL");
        exportList = null;
        accessEntries = null;
    }

    /**
     * Full parametric constructor.
     * @param fullName full name of directory;
     * @param description description of directory;
     * @param languages languages of direcotry;
     * @param accessEntries access entries;
     * @param exportList export list;
     */
    public DirSchema(String fullName, String description, List<String> languages, List<DirPermissionEntry> accessEntries, List<String> exportList) {
        this.fullName = fullName;
        this.description = description;
        this.languages = languages;
        this.accessEntries = accessEntries;
        this.exportList = exportList;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the languages
     */
    public List<String> getLanguages() {
        return languages;
    }

    /**
     * @param languages the languages to set
     */
    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<DirPermissionEntry> getAccessEntries() {
        return accessEntries;
    }

    public void setAccessEntries(List<DirPermissionEntry> accessEntries) {
        this.accessEntries = accessEntries;
    }
    
    /**
     * @return the exportList
     */
    public List<String> getExportList() {
        return exportList;
    }

    /**
     * @param exportList the exportList to set
     */
    public void setExportList(List<String> exportList) {
        this.exportList = exportList;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.fullName);
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
        final DirSchema other = (DirSchema) obj;
        if (!Objects.equals(this.fullName, other.fullName)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!!Objects.equals(this.languages, other.languages)) {
            return false;
        }
        if (!!Objects.equals(this.accessEntries, other.accessEntries)) {
            return false;
        }
        if (!!Objects.equals(this.exportList, other.exportList)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DirSchema{" + "fullName=" + fullName + ", description=" + description + ", languages=" + languages + ", rawAccessEntries=" + accessEntries + ", exportList=" + exportList + '}';
    }

    @Override
    public String getEntityType() {
        return TYPE;
    }

    @Override
    public String getKey() {
        return fullName;
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
        fullName = reader.readKey();
        description = reader.readString();
        languages = reader.readArray();
        accessEntries = reader.readInternalArray(DirPermissionEntry.class);
        exportList = reader.readArray();
    }

    @Override
    public void writeToECSV(EntityWriter<String> writer) {
        writer.writeKey(fullName);
        writer.writeString(description);
        writer.writeArray(languages);
        writer.writeInternalArray(accessEntries);
        writer.writeArray(exportList);
    }

    @Override
    public void update(ECSVAble<String> updatedEntity) {
        DirSchema otherDir = (DirSchema) updatedEntity;
        description = otherDir.getDescription();
        languages = otherDir.getLanguages();
        accessEntries = otherDir.getAccessEntries();
        exportList = otherDir.getExportList();
    }
    
}
