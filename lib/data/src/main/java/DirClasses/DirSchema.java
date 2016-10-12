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

package DirClasses;

/**
 * Directory schema object.
 * 
 * <p>Using for transporting through directories
 * tree and making dirEntry object.</p>
 * @author Stanislav Nepochatov
 */
public class DirSchema extends Generic.CsvElder {
    
    /**
     * Default constructor.
     * 
     * <p>Using for defining csv format options.</p>
     */
    public DirSchema() {
        this.baseCount = 2;
        this.groupCount = 3;
        this.currentFormat = Generic.CsvElder.csvFormatType.ComplexCsv;
    }
    
    /**
     * Default constructor from csv form.
     * @param givenCsv given csv line
     * @since RibbonServer a2
     */
    public DirSchema(String givenCsv) {
        this();
        java.util.ArrayList<String[]> parsedStruct = Generic.CsvFormat.fromCsv(this, givenCsv);
        FULL_DIR_NAME = parsedStruct.get(0)[0];
        COMM = parsedStruct.get(0)[1];
        DIR_LANGS = parsedStruct.get(1);
        SH_ACCESS = parsedStruct.get(2);
        DIR_EXPORTS = parsedStruct.get(3);
    }

    /**
     * Parametrick costructor.
     * @param givenPath full path of directory
     * @param givenComm comment for directory
     */
    public DirSchema(String givenPath, String givenComm) {
        this();
        FULL_DIR_NAME = givenPath;
        COMM = givenComm;
        DIR_LANGS = new String[] {"ALL"};
        DIR_EXPORTS = null;
        SH_ACCESS = null;
    }

    /**
     * Full directory path
     */
    public String FULL_DIR_NAME;

    /**
     * Commentary for directory
     */
    public String COMM;

    /**
     * Directory's supported languages
     * @since RibbonServer a2
     */
    public String[] DIR_LANGS;

    /**
     * Access list for directory
     * @since RibbonServer a2
     */
    public String[] SH_ACCESS;

    /**
     * Directory's exports list
     * @since RibbonServer a2
     */
    public String[] DIR_EXPORTS;

    @Override
    public String toCsv() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
