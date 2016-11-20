/**
 * This file is part of libRibbonIO library (check README).
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

package tk.freaxsoftware.ukrinform.ribbon.lib.io.importer;

import java.util.Properties;
import tk.freaxsoftware.ukrinform.ribbon.lib.io.utils.IOControl;

/**
 * Import operation class.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public abstract class Importer extends Thread {
    
    /**
     * Name of this import schema.
     */
    protected final String importerName;
    
    /**
     * Unique importer scheme ID.
     */
    protected final String importerPrint;
    
    /**
     * Dirty status of module.
     */
    protected Boolean dirtyStatus = false;
    
    /**
     * Is <code>Importer</code> is valid.
     */
    protected Boolean status = true;
    
    /**
     * String representation of status.
     */
    protected String strStatus;
    
    /**
     * Exception in the <code>Importer</code>.
     */
    protected Exception exStatus;
    
    /**
     * Configuration of this <code>Importer</code>.
     */
    protected final Properties currConfig;
    
    /**
     * Timeout for check.
     */
    protected final Integer timeout;
    
    /**
     * Default constructor.
     * @param givenConfig configuration to init with;
     */
    public Importer(Properties givenConfig) {
        this.currConfig = givenConfig;
        this.importerName = currConfig.getProperty("import_name");
        this.importerPrint = currConfig.getProperty("import_print");
        this.timeout = Integer.parseInt(currConfig.getProperty("import_timeout")) * 60 * 1000;
        IOControl.getInstance().getServerWrapper().log(IOControl.getInstance().getIMPORT_LOGID(), 3, "завантажено схему імпорту '" + this.importerName + "'");
    }
    
    @Override
    public void run() {
        while (status) {
            if (dirtyStatus) {
                tryRecover();
            }
            try {
                doImport();
            } catch (Exception ex) {
                this.exStatus = ex;
                IOControl.getInstance().getServerWrapper().postException("Помилка при імпорті: " + this.importerName, ex);
                IOControl.getInstance().getServerWrapper().enableDirtyState(this.currConfig.getProperty("import_type"), importerName, importerPrint);
            }
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ex) {
                
            }
        }
    }
    
    /**
     * Body of import method.
     */
    protected abstract void doImport();
    
    /**
     * Reset state of the importer.
     */
    protected abstract void resetState();
    
    /**
     * Try to recover module internal normal state.
     */
    public abstract void tryRecover();
}
