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

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

/**
 * DirEntry adopted to Swing UI.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class DirEntryUI extends DirClasses.DirEntry implements javax.swing.tree.TreeModel {

    /**
     * Static root entry.
     */
    public static DirEntryUI rootDir = new DirEntryUI();
    
    /**
     * Add given index to specified directory
     * @param givenDir directory in which index will be added
     * @param givenIndex index identifier
     */
    public static void addIndexToDir(String givenDir, String givenIndex) {
        DirEntryUI.rootDir.addIndex("", givenDir, givenIndex);
    }
    
    /**
     * Remove given index from specified directory
     * @param givenDir directory from which index will be removed
     * @param givenIndex index indentifier
     */
    public static void removeIndexFromDir(String givenDir, String givenIndex) {
        DirEntryUI.rootDir.removeIndex("", givenDir, givenIndex);
    }
    
    /**
     * Get end directory from TreePath selection;
     * @param givenPath selection path;
     * @return end directory object reference;
     */
    public static DirEntryUI getEndDir(javax.swing.tree.TreePath givenPath) {
        return (DirEntryUI) givenPath.getPath()[givenPath.getPath().length - 1];
    }
    
    /**
     * Empty constructor.
     */
    public DirEntryUI() {
       super();
    }
    
    /**
     * Chain constuctor (adapted to a2)
     * @param upperLevel all parent directories
     * @param rest rest of the creation query
     * @param givenSchema schema to apply
     */
    public DirEntryUI(String upperLevel, String rest, DirClasses.DirSchema givenSchema) {
        Integer joint;
        if ((joint = rest.indexOf(".")) != -1) {
            DIR_NAME = rest.substring(0, joint);
            if (upperLevel.isEmpty()) {
                FULL_DIR_NAME = DIR_NAME;
            } else {
                FULL_DIR_NAME = upperLevel + "." + DIR_NAME;
            }
            DIR_PATH = FULL_DIR_NAME.toLowerCase().replaceAll("\\.", "/") + "/";;
            COMM = "Порожній напрямок";
            FOLDED_DIR.add(new DirEntryUI(FULL_DIR_NAME, rest.substring(joint + 1), givenSchema));
        } else {
            applySchema(givenSchema);
        }
    }

    @Override
    public Object getRoot() {
        return rootDir;
    }

    @Override
    public Object getChild(Object parent, int index) {
        DirEntryUI SwParentDir = (DirEntryUI) parent;
        DirEntryUI retDir = (DirEntryUI) rootDir.returnEndDir("", SwParentDir.FULL_DIR_NAME);
        if (retDir != null) {
            return retDir.FOLDED_DIR.get(index);
        } else {
            return this.FOLDED_DIR.get(index);
        }
    }

    @Override
    public int getChildCount(Object parent) {
        DirEntryUI SwParentDir = (DirEntryUI) parent;
        return SwParentDir.FOLDED_DIR.size();
    }

    @Override
    public boolean isLeaf(Object node) {
        DirEntryUI SwParentDir = null;
        if (node instanceof String) {
            SwParentDir = (DirEntryUI) this.returnEndDir("", (String) node);
        } else {
            SwParentDir = (DirEntryUI) node;
        }
        if (SwParentDir == null) {
            return true;
        }
        if (SwParentDir.FOLDED_DIR.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        DirEntryUI SwParentDir = (DirEntryUI) parent;
        return SwParentDir.FOLDED_DIR.indexOf(child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Insert chain of directories in current directory (DirEntryUI version)
     * @param upperLevel all parent directories
     * @param rest rest of the creation query
     * @param givenSchema schema to apply
     */
    @Override
    public void insertDir(String upperLevel, String rest, DirClasses.DirSchema givenSchema) {
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
                lastEntry.insertDir(inserted_FULL_DIR_NAME, rest.substring(joint + 1), givenSchema);
            } else {
                if (this.DIR_NAME.isEmpty()) {
                    this.FOLDED_DIR.add(new DirEntryUI("", rest, givenSchema));
                } else {
                    this.FOLDED_DIR.add(new DirEntryUI(inserted_FULL_DIR_NAME, rest.substring(joint + 1), givenSchema));
                }
            }
        } else {
            String inserted_DIR_NAME = rest;
            if (this.hasFoldDir(inserted_DIR_NAME)) {
                lastEntry.applySchema(givenSchema);
            } else {
                FOLDED_DIR.add(new DirEntryUI(upperLevel, rest, givenSchema));
            }
        }
    }
    
    @Override
    public String toString() {
        return this.DIR_NAME;
    }
}
