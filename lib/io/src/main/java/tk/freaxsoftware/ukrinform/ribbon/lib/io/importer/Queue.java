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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import tk.freaxsoftware.ukrinform.ribbon.lib.io.utils.IOControl;
import tk.freaxsoftware.ukrinform.ribbon.lib.io.utils.ModuleContainer;

/**
 * Import quene class.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class Queue {
    
    /**
     * List of current running instances of <code>Importer</code> class.
     */
    private List<Importer> importList = new ArrayList();
    
    /**
     * List of current registred modules.
     */
    private List<ModuleContainer> moduleList;
    
    /**
     * Path to import schemas files.
     */
    public String importDirPath;
    
    /**
     * Default constructor.
     * @param pluginPath path to search plugins.
     * @param givenImportDirPath path to search import configuration files.
     */
    public Queue (String pluginPath, String givenImportDirPath) {
        importDirPath = givenImportDirPath;
        this.moduleList = IOControl.getInstance().loadModules(pluginPath);
        File importPropsDir = new java.io.File(givenImportDirPath);
        if (!importPropsDir.exists()) {
            importPropsDir.mkdirs();
            IOControl.getInstance().getServerWrapper().log(IOControl.getIMPORT_LOGID(), 2, "Створюю теку імпорту...");
        }
        File[] importProps = importPropsDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith(".import")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        for (java.io.File importPropertyFile : importProps) {
            Properties importConfig = new Properties();
            try {
                importConfig.load(new FileReader(importPropertyFile));
            } catch (FileNotFoundException ex) {
                IOControl.getInstance().getServerWrapper().log(IOControl.getIMPORT_LOGID(), 1, "неможливо знайти файл " + importPropertyFile.getName());
            } catch (IOException ex) {
                IOControl.getInstance().getServerWrapper().log(IOControl.getIMPORT_LOGID(), 1, "помилка при зчитуванні файлу " + importPropertyFile.getName());
            }
            Importer newImport = this.getNewInstanceForType(importConfig);
            if (newImport != null) {
                this.importList.add(newImport);
            } else {
                IOControl.getInstance().getServerWrapper().log(IOControl.getIMPORT_LOGID(), 2, "заванатження модулю для " + importPropertyFile.getName() + " завершилось з помилкою.");
            }
        }
        if (this.importList.isEmpty()) {
            IOControl.getInstance().getServerWrapper().log(IOControl.getIMPORT_LOGID(), 2, "система не знайшла жодної схеми імпорту!");
        }
    }
    
    /**
     * Get new instance of importer by guvin type.
     * @param givenConfig config for importer construction;
     * @return new reference of Importer class or null if type uknown.
     */
    private Importer getNewInstanceForType(java.util.Properties givenConfig) {
        ListIterator<ModuleContainer> modIter = this.moduleList.listIterator();
        tk.freaxsoftware.ukrinform.ribbon.lib.io.utils.ModuleContainer findedMod = null;
        while (modIter.hasNext()) {
            ModuleContainer currMod = modIter.next();
            if (currMod.getModuleType().equals(givenConfig.getProperty("import_type"))) {
                findedMod = currMod;
                break;
            }
        }
        if (findedMod != null) {
            try {
                return (Importer) findedMod.getModuleClass().getConstructor(Properties.class).newInstance(givenConfig);
            } catch (InvocationTargetException ex) {
                ex.getTargetException().printStackTrace();
            } catch (Exception ex) {
                IOControl.getInstance().getServerWrapper().log(IOControl.getIMPORT_LOGID(), 1, "неможливо опрацювати класс " + findedMod.getModuleClass().getName());
                ex.printStackTrace();
            }
        } else {
            IOControl.getInstance().getServerWrapper().log(IOControl.getIMPORT_LOGID(), 2, "неможливо знайти модуль для типу " + givenConfig.getProperty("import_type"));
        }
        return null;
    }

    /**
     * Run all import schemas.
     */
    public void importRun() {
        ListIterator<Importer> importIter = this.importList.listIterator();
        while (importIter.hasNext()) {
            importIter.next().start();
        }
        IOControl.getInstance().getServerWrapper().log(IOControl.getIMPORT_LOGID(), 3, "запуск усіх схем імпорту");
    }
}
