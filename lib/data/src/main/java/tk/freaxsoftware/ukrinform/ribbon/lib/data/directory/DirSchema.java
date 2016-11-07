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

import java.util.Arrays;
import java.util.Objects;

/**
 * Directory schema object.
 * 
 * <p>Using for transporting through directories
 * tree and making dirEntry object.</p>
 * @author Stanislav Nepochatov
 */
public class DirSchema extends tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvElder {
    
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
    private String[] languages;

    /**
     * Access list for directory
     * @since RibbonServer a2
     */
    private String[] rawAccessEntries;

    /**
     * Directory's exports list
     * @since RibbonServer a2
     */
    private String[] exportList;
    
    /**
     * Default constructor.
     * 
     * <p>Using for defining csv format options.</p>
     */
    public DirSchema() {
        this.baseCount = 2;
        this.groupCount = 3;
        this.currentFormat = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvElder.csvFormatType.ComplexCsv;
    }
    
    /**
     * Default constructor from csv form.
     * @param givenCsv given csv line
     * @since RibbonServer a2
     */
    public DirSchema(String givenCsv) {
        this();
        java.util.ArrayList<String[]> parsedStruct = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.fromCsv(this, givenCsv);
        fullName = parsedStruct.get(0)[0];
        description = parsedStruct.get(0)[1];
        languages = parsedStruct.get(1);
        rawAccessEntries = parsedStruct.get(2);
        exportList = parsedStruct.get(3);
    }

    /**
     * Parametrick costructor.
     * @param givenPath full path of directory
     * @param givenComm comment for directory
     */
    public DirSchema(String givenPath, String givenComm) {
        this();
        fullName = givenPath;
        description = givenComm;
        languages = new String[] {"ALL"};
        exportList = null;
        rawAccessEntries = null;
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
    public String[] getLanguages() {
        return languages;
    }

    /**
     * @param languages the languages to set
     */
    public void setLanguages(String[] languages) {
        this.languages = languages;
    }

    /**
     * @return the rawAccessEntries
     */
    public String[] getRawAccessEntries() {
        return rawAccessEntries;
    }

    /**
     * @param rawAccessEntries the rawAccessEntries to set
     */
    public void setRawAccessEntries(String[] rawAccessEntries) {
        this.rawAccessEntries = rawAccessEntries;
    }

    /**
     * @return the exportList
     */
    public String[] getExportList() {
        return exportList;
    }

    /**
     * @param exportList the exportList to set
     */
    public void setExportList(String[] exportList) {
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
        if (!Arrays.deepEquals(this.languages, other.languages)) {
            return false;
        }
        if (!Arrays.deepEquals(this.rawAccessEntries, other.rawAccessEntries)) {
            return false;
        }
        if (!Arrays.deepEquals(this.exportList, other.exportList)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DirSchema{" + "fullName=" + fullName + ", description=" + description + ", languages=" + languages + ", rawAccessEntries=" + rawAccessEntries + ", exportList=" + exportList + '}';
    }

    @Override
    public String toCsv() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
