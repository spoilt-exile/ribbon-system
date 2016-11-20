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

package tk.freaxsoftware.ukrinform.ribbon.lib.io.exporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import tk.freaxsoftware.ukrinform.ribbon.lib.data.message.Message;
import tk.freaxsoftware.ukrinform.ribbon.lib.io.utils.IOControl;
import tk.freaxsoftware.ukrinform.ribbon.lib.io.utils.ModuleContainer;

/**
 * Export dispatcher (exporter factory) class.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class Dispatcher {
    
    /**
     * Path to export modules.
     */
    public final String exportModulePath;
    
    /**
     * List of export schemas.
     */
    private List<ModuleContainer> moduleList = new ArrayList<>();
    
    /**
     * List of export schemas.
     */
    private final List<Schema> schemaList = new ArrayList<>();
    
    /**
     * Error quene.
     */
    private final List<Exporter> errQuene = new ArrayList<>();
    
    /**
     * Sync lock for error quene.
     */
    private final Object errQueneLock = new Object();
    
    /**
     * System directory export subscribes.<br>
     * <br>
     * Subsribe hash map has next structure:<br>
     * <b>DIR_NAME, ArrayList(SCHEME_NAME)</b>
     */
    private Map<String, List<String>> subscribes = new HashMap();
    
    /**
     * Current error quene worker thread.
     */
    private ErrorQueneWorker currWorker = new ErrorQueneWorker();
    
    /**
     * Error quene worker thread.
     */
    private class ErrorQueneWorker extends Thread {
        
        @Override
        public void run() {
            while (true) {
                processErrQuene();
                try {
                    Thread.sleep(2 * 60 * 1000);        //Default timeout 2 min will be changed in the feature;
                } catch (InterruptedException ex) {}
            }
        }
        
        /**
         * Launch broken export task recovery.
         */
        public void processErrQuene() {
            synchronized (errQueneLock) {
                for (int index = 0; index < errQuene.size(); index++) {
                    Exporter curr = errQuene.get(index);
                    Boolean result = curr.tryRecovery();
                    if (result) {
                        errQuene.remove(curr);
                    }
                }
            }
        }
    }
    
    /**
     * Default constructor.
     * @param givenDirPath path to directory with export schemas;
     * @param givenModulePath path to modules to load;
     */
    public Dispatcher(String givenModulePath, String givenDirPath) {
        exportModulePath = givenModulePath;
        moduleList = IOControl.getInstance().loadModules(givenModulePath);
        java.io.File exportPropsDir = new java.io.File(givenDirPath);
        if (!exportPropsDir.exists()) {
            exportPropsDir.mkdirs();
            IOControl.getInstance().getServerWrapper().log(IOControl.getEXPORT_LOGID(), 2, "Створюю теку експорту...");
        }
        java.io.File[] exportsProps = exportPropsDir.listFiles(new java.io.FilenameFilter() {

            @Override
            public boolean accept(java.io.File dir, String name) {
                if (name.endsWith(".export")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        for (java.io.File exportFile : exportsProps) {
            java.util.Properties exportConfig = new java.util.Properties();
            try {
                exportConfig.load(new java.io.FileReader(exportFile));
            } catch (java.io.FileNotFoundException ex) {
                IOControl.getInstance().getServerWrapper().log(IOControl.getEXPORT_LOGID(), 1, "неможливо знайти файл " + exportFile.getName());
            } catch (java.io.IOException ex) {
                IOControl.getInstance().getServerWrapper().log(IOControl.getEXPORT_LOGID(), 1, "помилка при зчитуванні файлу " + exportFile.getName());
            }
            Schema newExport = getNewSchema(exportConfig);
            if (newExport != null) {
                this.schemaList.add(newExport);
            } else {
                IOControl.getInstance().getServerWrapper().log(IOControl.getEXPORT_LOGID(), 2, "заванатження модулю для " + exportFile.getName() + " завершилось з помилкою.");
            }
        }
        if (this.schemaList.isEmpty()) {
            IOControl.getInstance().getServerWrapper().log(IOControl.getEXPORT_LOGID(), 2, "система не знайшла жодної схеми экспорту!");
        }
    }
    
    /**
     * Subscribe directory to export.
     * @param givenSchemas array with schemas names;
     * @param givenDirName name of directory;
     */
    public void subscribeDir(String[] givenSchemas, String givenDirName) {
	if (givenSchemas == null) {
	    return;
	}
        List<String> putList = new ArrayList();
        for (String currScheme : givenSchemas) {
            if (currScheme.isEmpty()) {
                continue;
            }
            if (isSchemaExists(currScheme)) {
                putList.add(currScheme);
            } else {
                IOControl.getInstance().getServerWrapper().log(IOControl.getEXPORT_LOGID(), 2, "схему експорту " + currScheme + " не існує (" + givenDirName + ")");
            }
        }
        if (!putList.isEmpty()) {
            this.subscribes.put(givenDirName, putList);
        }
    }
    
    /**
     * Find out if schema existed.
     * @param givenSchema schema to search;
     * @return true if existed / false if not.
     */
    private Boolean isSchemaExists(String givenSchema) {
        ListIterator<Schema> shIter = this.schemaList.listIterator();
        while (shIter.hasNext()) {
            if (shIter.next().name.equals(givenSchema)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Return new export schema.
     * @param givenConfig schema's config;
     * @return new reference of Scheme or null if type is uknown;
     */
    private Schema getNewSchema(java.util.Properties givenConfig) {
        ListIterator<ModuleContainer> modIter = this.moduleList.listIterator();
        ModuleContainer findedMod = null;
        while (modIter.hasNext()) {
            ModuleContainer currMod = modIter.next();
            if (currMod.getModuleType().equals(givenConfig.getProperty("export_type"))) {
                findedMod = currMod;
                break;
            }
        }
        if (findedMod != null) {
            return new Schema(givenConfig, findedMod.getModuleClass());
        } else {
            IOControl.getInstance().getServerWrapper().log(IOControl.getEXPORT_LOGID(), 2, "неможливо знайти модуль для типу " + givenConfig.getProperty("export_type"));
        }
        return null;
    }
    
    /**
     * Find out if there is subscriptions for given directories.
     * @param givenDirs array with directory names;
     * @return true if export needed / false if no subscriptions.
     */
    public Boolean checkExport(String[] givenDirs) {
        for (String currDir : givenDirs) {
            if (this.subscribes.containsKey(currDir)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Init export sequence.
     * @param exportedMessage message to export;
     */
    public void initExport(Message exportedMessage) {
        if (exportedMessage.getProperty("PROCESSING_FORBIDDEN") != null) {
            IOControl.getInstance().getServerWrapper().log(IOControl.getEXPORT_LOGID(), 2, "повідомлення " + exportedMessage.getIndex() + " заборонено випускати з системи");
            return;
        }
        if (!currWorker.isAlive()) {
            currWorker.start();
        }
        ReleaseSwitch newSwitch = new ReleaseSwitch(exportedMessage.getIndex());
        for (String currDir : exportedMessage.getDirectories()) {
            if (this.subscribes.containsKey(currDir)) {
                newSwitch.addSchemas(subscribes.get(currDir));
                ListIterator<String> schemeIter = subscribes.get(currDir).listIterator();
                while (schemeIter.hasNext()) {
                    Schema currSchema = this.getScheme(schemeIter.next());
                    Exporter newExport = currSchema.getNewExportTask(exportedMessage, newSwitch, currDir);
                    newExport.start();
                }
            }
        }
    }
    
    /**
     * Get scheme by name.
     * @param givenName scheme's name;
     * @return scheme or null.
     */
    private Schema getScheme(String givenName) {
        ListIterator<Schema> schemeIter = this.schemaList.listIterator();
        while (schemeIter.hasNext()) {
            Schema currScheme = schemeIter.next();
            if (currScheme.name.equals(givenName)) {
                return currScheme;
            }
        }
        return null;
    }
    
    /**
     * Add broken export task to export quene.
     * @param brokenExport exporter object;
     */
    public void addToQuene(Exporter brokenExport) {
        synchronized (errQueneLock) {
            this.errQuene.add(brokenExport);
        }
    }
}
