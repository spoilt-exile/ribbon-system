/**
 * This file is part of RibbonServer application (check README).
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

package tk.freaxsoftware.ukrinform.ribbon.server;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.faststorage.storage.Handlers;
import tk.freaxsoftware.ukrinform.ribbon.lib.data.directory.DirEntry;
import tk.freaxsoftware.ukrinform.ribbon.lib.data.directory.DirPermissionEntry;
import tk.freaxsoftware.ukrinform.ribbon.lib.data.directory.DirSchema;
import tk.freaxsoftware.ukrinform.ribbon.lib.data.handlers.DirectoryHandler;
import tk.freaxsoftware.ukrinform.ribbon.lib.io.utils.IOControl;

/**
 * Directories handle class
 * @author Stanislav Nepochatov
 * @since RibbonServer a1
 */
public final class Directories {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Directories.class);
    
    /**
     * ID of this component or object for loging.
     */
    private static String LOG_ID = "НАПРЯМКИ";
    
    /**
     * Root directory.
     * @since RibbonServer a1
     */
    private static tk.freaxsoftware.ukrinform.ribbon.lib.data.directory.DirEntry rootDir;
    
    /**
     * List with pseudo directories.
     * @since RibbonServer a2
     */
    private static java.util.ArrayList<PseudoDirEntry> pseudoDirs;
    
    /**
     * Directories global lock.
     * @since RibbonServer a2
     */
    private static final Object dirLock = new Object();
    
    /**
     * Pseudo directory class.
     * @author Stanislav Nepochatov
     * @since RibbonServer a2
     */
    public static class PseudoDirEntry extends tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvElder {

        /**
         * Name of the pseudo directory.
         */
        private String name;
        
        /**
         * Commentary for this pseudo directory.
         */
        private String description;
        
        /**
         * Array of inernal directories;
         */
        private List<DirEntry> directories = new ArrayList<DirEntry>();
        
        /**
         * Default constructor.
         */
        public PseudoDirEntry() {
            this.baseCount = 2;
            this.groupCount = 1;
            this.currentFormat = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvElder.csvFormatType.ComplexCsv;
        }
        
        /**
         * Parametric constructor.
         * @param givenCsv csv representation of pseudo durectory;
         */
        public PseudoDirEntry(String givenCsv) {
            this();
            java.util.ArrayList<String[]> parsed = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.fromCsv(this, givenCsv);
            name = parsed.get(0)[0];
            description = parsed.get(0)[1];
            String[] parsedDirs = parsed.get(1);
            for (String currParsedDir : parsedDirs) {
                DirEntry addDir = Directories.rootDir.returnEndDir("", currParsedDir);
                if (addDir != null) {
                    directories.add(addDir);
                } else {
                    LOGGER.error("pseudo directory index error (directory " + currParsedDir + " doesn't exist)");
                }
            }
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

        public List<DirEntry> getDirectories() {
            return directories;
        }

        public void setDirectories(List<DirEntry> directories) {
            this.directories = directories;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 31 * hash + Objects.hashCode(this.name);
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
            final PseudoDirEntry other = (PseudoDirEntry) obj;
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            if (!Objects.equals(this.description, other.description)) {
                return false;
            }
            if (!Objects.equals(this.directories, other.directories)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "PseudoDirEntry{" + "name=" + name + ", description=" + description + ", directories=" + directories + '}';
        }
        
        /**
         * Get internal directories as string array;
         * @return 
         */
        public String[] getinternalDirectories() {
            String[] returned = new String[this.directories.size()];
            for (int index = 0; index < returned.length; index++) {
                returned[index] = this.directories.get(index).getFullName();
            }
            return returned;
        }
        
        /**
         * Check if user can use this pseudo dir.
         * @param userName name of user to check access;
         * @return true if user can use this pseudo dir;
         */
        public Boolean checkPseudoDir(String userName) {
            if (AccessHandler.checkAccessForAll(userName, this.getinternalDirectories(), 1) == null) {
                return true;
            } else {
                return false;
            }
        }
        
        @Override
        public String toCsv() {
            String returned = "{" + this.name + "},{" + this.description + "},[";
            for (tk.freaxsoftware.ukrinform.ribbon.lib.data.directory.DirEntry currDir : directories) {
                returned += currDir.getFullName();
            }
            return returned + "]";
        }
    }
    
    /**
     * Init directory's component.
     * @since RibbonServer a2
     */
    public static void init() {
        rootDir = new tk.freaxsoftware.ukrinform.ribbon.lib.data.directory.DirEntry();
        List<tk.freaxsoftware.ukrinform.ribbon.lib.data.directory.DirSchema> readedDirs = Handlers.getHandlerByClass(DirSchema.class).getAll();
        ListIterator<tk.freaxsoftware.ukrinform.ribbon.lib.data.directory.DirSchema> readIter = readedDirs.listIterator();
        while (readIter.hasNext()) {
            tk.freaxsoftware.ukrinform.ribbon.lib.data.directory.DirSchema currDir = readIter.next();
            LOGGER.info("added directory (" + currDir.getFullName() + ": " + currDir.getDescription() + ")");
            createDirs(currDir);
            if (RibbonServer.IO_ENABLED) {
                IOControl.getInstance().getDispathcer().subscribeDir(currDir.getExportList(), currDir.getFullName());
            }
        }
        rootDir.deployDir(RibbonServer.BASE_PATH);
        if (RibbonServer.ACCESS_ALLOW_REMOTE) {
            pseudoDirs = IndexReader.readPseudoDirectories();
            for (PseudoDirEntry curr: pseudoDirs) {
                LOGGER.info("added pseudo directory " + curr.getName() + " " + tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.renderGroup(curr.getinternalDirectories()));
            }
        }
    }
    
    /**
     * Create full chain of directories with given schema
     * @param givenSchema directory schema
     * @since RibbonServer a1
     */
    public static void createDirs(tk.freaxsoftware.ukrinform.ribbon.lib.data.directory.DirSchema givenSchema) {
        Directories.rootDir.insertDir("", givenSchema.getFullName(), givenSchema);
    }
    
    /**
     * Dump current tree as text report
     * @since RibbonServer a1
     */
    public static void dumpTree() {
        try {
            java.io.FileWriter treeWriter = new java.io.FileWriter(RibbonServer.BASE_PATH + "/tree");
            treeWriter.write(Directories.rootDir.treeReport(0));
            treeWriter.close();
        } catch (java.io.IOException ex) {
            LOGGER.error("Unableto create dir report!", ex);
        } finally {
            LOGGER.info("Dir report file created.");
        }
    }
    
    /**
     * Add given index to specified directory
     * @param givenDir directory in which index will be added
     * @param givenIndex index identifier
     * @since RibbonServer a1
     */
    public static void addIndexToDir(String givenDir, String givenIndex) {
        synchronized (dirLock) {
            Directories.rootDir.addIndex("", givenDir, givenIndex);
        }
    }
    
    /**
     * Remove given index from specified directory
     * @param givenDir directory from which index will be removed
     * @param givenIndex index indentifier
     * @since RibbonServer a1
     */
    public static void removeIndexFromDir(String givenDir, String givenIndex) {
        synchronized (dirLock) {
            Directories.rootDir.removeIndex("", givenDir, givenIndex);
        }
    }
    
    /**
     * Return access description array from 
     * @param givenDir dir to search;
     * @return array with permission entries;
     * @since RibbonServer a2
     */
    public static List<DirPermissionEntry> getDirAccess(String givenDir) {
        try {
            return Directories.rootDir.getAccess("", givenDir);
        } catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * Return anonymoys mode flag for specifed dir
     * @param givenDir given path to directory
     * @return path to file directory
     * @since RibbonServer a1
     */
    public static String getDirPath(String givenDir) {
        tk.freaxsoftware.ukrinform.ribbon.lib.data.directory.DirEntry returned = rootDir.returnEndDir("", givenDir);
        if (returned == null) {
            return null;
        }
        return returned.getDirPath();
    }
    
    /**
     * Return all dirs to protocol commandlet.
     * @return dirs in csv form;
     * @since RibbonServer a1
     */
    public static String PROC_GET_DIRS() {
        StringBuffer buffer = new StringBuffer();
        ListIterator<DirEntry> rootDirs = Directories.rootDir.getChildrenDirs().listIterator();
        while (rootDirs.hasNext()) {
            buffer.append(rootDirs.next().PROC_GET_DIR((DirectoryHandler) Handlers.getHandlerByType(DirEntry.TYPE), buffer));
        }
        buffer.append("END:");
        return buffer.toString();
    }
    
    /**
     * Return pseudo directory object.
     * @param pseudoName name of the pseudo dir;
     * @return pseudo dir reference or null;
     * @since RibbonServer a2
     */
    public static PseudoDirEntry getPseudoDir(String pseudoName) {
        for (PseudoDirEntry curr: pseudoDirs) {
            if (curr.getName().equals(pseudoName)) {
                return curr;
            }
        }
        return null;
    }
    
    /**
     * Return pseudo directories which specified user may use.
     * @param userName name to check;
     * @return formatted csv strings end END: command at the end;
     * @since RibbonServer a2
     */
    public static String PROC_GET_PSEUDO(String userName) {
        StringBuffer buf = new StringBuffer();
        for (PseudoDirEntry curr: pseudoDirs) {
            if (curr.checkPseudoDir(userName)) {
                buf.append(curr.toCsv());
                buf.append("\n");
            }
        }
        buf.append("END:");
        return buf.toString();
    }
}
