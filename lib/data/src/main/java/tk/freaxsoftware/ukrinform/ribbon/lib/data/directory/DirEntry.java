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
import tk.freaxsoftware.ukrinform.ribbon.lib.data.handlers.DirectoryHandler;

/**
 * Directory entry object.
 * 
 * <p>Contains directory main data and list
 * of it child directories.</p>
 * @author Stanislav Nepochatov
 */
public class DirEntry extends DirSchema {
        
    /**
     * Default constructor (empty)
     * Usually this constructor used for creating root dir
     */
    public DirEntry() {
        name = "";
        this.setFullName("");
        dirAccessEntries = null;
    }

    /**
     * Schema-based end constructor
     * @param givenSchema schema to build directory
     */
    public DirEntry(DirSchema givenSchema) {
        applySchema(givenSchema);
    }

    /**
     * Chain constuctor (adapted to a2)
     * @param upperLevel all parent directories
     * @param rest rest of the creation query
     * @param givenSchema schema of child directory
     */
    public DirEntry(String upperLevel, String rest, DirSchema givenSchema) {
        Integer joint;
        if ((joint = rest.indexOf(".")) != -1) {
            name = rest.substring(0, joint);
            if (upperLevel.isEmpty()) {
                this.setFullName(name);
            } else {
                this.setFullName(upperLevel + "." + name);
            }
            dirPath = this.getFullName().toLowerCase().replaceAll("\\.", "/") + "/";
            this.setDescription("Порожній напрямок");
            childrenDirs.add(new DirEntry(getFullName(), rest.substring(joint + 1), givenSchema));
        } else {
            applySchema(givenSchema);
        }
    }

    /**
     * Short directrory name
     */
    private String name;

    /**
     * Arraylist of inner directiries
     */
    private List<DirEntry> childrenDirs = new ArrayList<>();

    /**
     * Arraylist of messages indexes
     */
    private List<String> messageIds = new ArrayList<>();

    /**
     * Path to dir messages
     */
    private String dirPath;

    /**
     * Access array of this directory
     * @since RibbonServer a2
     */
    private List<DirPermissionEntry> dirAccessEntries;

    /**
     * Last searched directory
     * @since RibbonServer a2
     */
    private DirEntry lastEntry;
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the childrenDirs
     */
    public List<DirEntry> getChildrenDirs() {
        return childrenDirs;
    }

    /**
     * @param childrenDirs the childrenDirs to set
     */
    public void setChildrenDirs(List<DirEntry> childrenDirs) {
        this.childrenDirs = childrenDirs;
    }

    /**
     * @return the messageIds
     */
    public List<String> getMessageIds() {
        return messageIds;
    }

    /**
     * @param messageIds the messageIds to set
     */
    public void setMessageIds(List<String> messageIds) {
        this.messageIds = messageIds;
    }

    /**
     * @return the dirPath
     */
    public String getDirPath() {
        return dirPath;
    }

    /**
     * @param dirPath the dirPath to set
     */
    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    /**
     * @return the dirAccessEntries
     */
    public List<DirPermissionEntry> getDirAccessEntries() {
        return dirAccessEntries;
    }

    /**
     * @param dirAccessEntries the dirAccessEntries to set
     */
    public void setDirAccessEntries(List<DirPermissionEntry> dirAccessEntries) {
        this.dirAccessEntries = dirAccessEntries;
    }

    /**
     * @return the lastEntry
     */
    public DirEntry getLastEntry() {
        return lastEntry;
    }

    /**
     * @param lastEntry the lastEntry to set
     */
    public void setLastEntry(DirEntry lastEntry) {
        this.lastEntry = lastEntry;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.dirPath);
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
        final DirEntry other = (DirEntry) obj;
        if (!Objects.equals(this.getFullName(), other.getFullName())) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.childrenDirs, other.childrenDirs)) {
            return false;
        }
        if (!Objects.equals(this.messageIds, other.messageIds)) {
            return false;
        }
        if (!Objects.equals(this.dirPath, other.dirPath)) {
            return false;
        }
        if (!Objects.equals(this.dirAccessEntries, other.dirAccessEntries)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DirEntry{" + "name=" + name + ", childrenDirs=" + childrenDirs + ", messageIds=" + messageIds + ", dirPath=" + dirPath + ", dirAccessEntries=" + dirAccessEntries + ", lastEntry=" + lastEntry + '}';
    }

    /**
     * Insert chain of directories in current directory
     * @param upperLevel all parent directories
     * @param rest rest of the creation query
     * @param givenSchema schema to apply
     */
    public void insertDir(String upperLevel, String rest, tk.freaxsoftware.ukrinform.ribbon.lib.data.directory.DirSchema givenSchema) {
        Integer joint;
        if ((joint = rest.indexOf(".")) != -1) {
            String inserted_DIR_NAME = rest.substring(0, joint);
            String inserted_FULL_DIR_NAME;
            if (upperLevel.isEmpty()) {
                inserted_FULL_DIR_NAME = upperLevel + inserted_DIR_NAME;
            } else {
                inserted_FULL_DIR_NAME = upperLevel + "." + inserted_DIR_NAME;
            }
            if (this.hasFoldDir(inserted_DIR_NAME)) {
                getLastEntry().insertDir(inserted_FULL_DIR_NAME, rest.substring(joint + 1), givenSchema);
            } else {
                if (this.getName().isEmpty()) {
                    this.getChildrenDirs().add(new DirEntry("", rest, givenSchema));
                } else {
                    this.getChildrenDirs().add(new DirEntry(inserted_FULL_DIR_NAME, rest.substring(joint + 1), givenSchema));
                }
            }
        } else {
            String inserted_DIR_NAME = rest;
            if (hasFoldDir(inserted_DIR_NAME)) {
                getLastEntry().applySchema(givenSchema);
            } else {
                getChildrenDirs().add(new DirEntry(upperLevel, rest, givenSchema));
            }
        }
    }

    /**
     * Find out if there is a specified dir in FOLDED_DIR
     * @param foldedDirName name of directory
     * @return true if this entry contain such directory in it's children
     */
    protected Boolean hasFoldDir(String foldedDirName) {
        java.util.ListIterator<DirEntry> dirIter = getChildrenDirs().listIterator();
        while (dirIter.hasNext()) {
            DirEntry foldedDir = dirIter.next();
            if (foldedDir.getName().equals(foldedDirName)) {
                setLastEntry(foldedDir);
                return true;
            }
        }
        return false;
    }

    /**
     * Apply schema to given directory;
     * @param givenSchema schema to apply
     */
    public final void applySchema(DirSchema givenSchema) {
        this.setFullName(givenSchema.getFullName());
        this.setDescription(givenSchema.getDescription());
        this.setLanguages(givenSchema.getLanguages());
        this.setExportList(givenSchema.getExportList());
        this.setDirAccessEntries(new ArrayList<>());
        this.dirAccessEntries = givenSchema.getAccessEntries();
        String[] chunks = this.getFullName().split("\\.");
        this.setName(chunks[chunks.length - 1]);
        this.setDirPath(getFullName().toLowerCase().replaceAll("\\.", "/") + "/");
    }

    /**
     * Build tree from specifed inner level
     * @param inLevel inner folding level
     * @return tree formated string
     */
    public String treeReport(Integer inLevel) {
        String spaceStr = "";
        for (Integer space = 0; space < inLevel; space++) {
            spaceStr += "   ";
        }
        String returned = spaceStr + this.toString() + "\n";
        if (!this.childrenDirs.isEmpty()) {
            java.util.ListIterator<DirEntry> foldedIter = this.getChildrenDirs().listIterator();
            while (foldedIter.hasNext()) {
                DirEntry foldDir = foldedIter.next();
                returned += foldDir.treeReport(inLevel + 1);
            }
        }
        return returned;
    }

    /**
     * Add index to folded directory
     * @param upperLevel upper level
     * @param rest rest of add query
     * @param givenIndex index of message
     */
    public void addIndex(String upperLevel, String rest, String givenIndex) {
        Integer joint;
        if ((joint = rest.indexOf(".")) != -1) {
            String indxed_DIR_NAME = rest.substring(0, joint);
            if (this.hasFoldDir(indxed_DIR_NAME) == false) {
                return;
            } else {
                getLastEntry().addIndex(this.getFullName(), rest.substring(joint + 1), givenIndex);
            }
        } else {
            String indxed_DIR_NAME = rest;
            if (this.hasFoldDir(indxed_DIR_NAME) == false) {
                return;
            } else {
                getLastEntry().getMessageIds().add(givenIndex);
            }
        }
    }

    /**
     * Remove index from folded directory
     * @param upperLevel upper level
     * @param rest rest of remove query
     * @param givenIndex index of message
     */
    public void removeIndex(String upperLevel, String rest, String givenIndex) {
        Integer joint;
        if ((joint = rest.indexOf(".")) != -1) {
            String indxed_DIR_NAME = rest.substring(0, joint);
            if (this.hasFoldDir(indxed_DIR_NAME) == false) {
                return;
            } else {
                getLastEntry().removeIndex(this.getFullName(), rest.substring(joint + 1), givenIndex);
            }
        } else {
            String indxed_DIR_NAME = rest;
            if (this.hasFoldDir(indxed_DIR_NAME) == false) {
                return;
            } else {
                getLastEntry().getMessageIds().remove(givenIndex);
            }
        }
    }

    /**
     * Get cascade of foded diretories.
     * @return formated string for net protocol
     */
    public String PROC_GET_DIR(DirectoryHandler handler, StringBuffer buffer) {
        buffer.append("RIBBON_UCTL_LOAD_DIR:");
        buffer.append(handler.writeToString(this));
        buffer.append("\n");
        
        List<DirEntry> dirList = new ArrayList<>();
        this.getDirs(dirList);
        for (DirEntry child: dirList) {
            buffer.append("RIBBON_UCTL_LOAD_DIR:");
            buffer.append(handler.writeToString(child));
            buffer.append("\n");
        }
        return null;
    }
    
    private void getDirs(List<DirEntry> dirList) {
        dirList.add(this);
        if (!this.childrenDirs.isEmpty()) {
            for (DirEntry childDir: this.childrenDirs) {
                childDir.getDirs(dirList);
            }
        }
    }

    /**
     * Return dir with specified path
     * @param upperLevel upper level of path
     * @param rest rest of path
     * @return folded directory object
     */
    public DirEntry returnEndDir(String upperLevel, String rest) {
        Integer joint;
        if ((joint = rest.indexOf(".")) != -1) {
            String indxed_DIR_NAME = rest.substring(0, joint);
            if (this.hasFoldDir(indxed_DIR_NAME) == false) {
                return null;
            } else {
                return getLastEntry().returnEndDir(this.getFullName(), rest.substring(joint + 1));
            }
        } else {
            String indxed_DIR_NAME = rest;
            if (this.hasFoldDir(indxed_DIR_NAME) == false) {
                return null;
            } else {
                return getLastEntry();
            }
        }
    }

    /**
     * Return topmost access description array
     * @param upperLevel upper level of path
     * @param rest rest of path
     * @return array with dirEntry.dirPermissionEntry
     * @see dirEntry.dirPermissionEntry
     * @throws Exception 
     */
    public List<DirPermissionEntry> getAccess(String upperLevel, String rest) throws Exception {
        Integer joint;
        if ((joint = rest.indexOf(".")) != -1) {
            String indxed_DIR_NAME = rest.substring(0, joint);
            if (this.hasFoldDir(indxed_DIR_NAME) == false) {
                throw new Exception("Неможливо знайти шлях до напрямку!");
            } else {
                List<DirPermissionEntry> gainedAccess = getLastEntry().getAccess(this.getFullName(), rest.substring(joint + 1));
                if (gainedAccess == null) {
                    return getLastEntry().getDirAccessEntries();
                } else {
                    return gainedAccess;
                }
            }
        } else {
            String indxed_DIR_NAME = rest;
            if (this.hasFoldDir(indxed_DIR_NAME) == false) {
                throw new Exception("Неможливо знайти шлях до напрямку!");
            } else {
                return getLastEntry().getDirAccessEntries();
            }
        }
    }
    
    /**
     * Deploy directory in base directory.
     * @param basePath path to Ribbon base.
     */
    public void deployDir(String basePath) {
        if (!this.name.isEmpty()) {
            this.setDirPath(basePath + "/" + this.getDirPath());
            new java.io.File(getDirPath()).mkdirs();
        }
        java.util.ListIterator<DirEntry> foldedIter = this.getChildrenDirs().listIterator();
        while (foldedIter.hasNext()) {
            foldedIter.next().deployDir(basePath);
        }
    }
}
