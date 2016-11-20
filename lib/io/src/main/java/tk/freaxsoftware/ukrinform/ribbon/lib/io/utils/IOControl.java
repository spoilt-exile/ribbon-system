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

package tk.freaxsoftware.ukrinform.ribbon.lib.io.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import tk.freaxsoftware.ukrinform.ribbon.lib.io.exporter.Dispatcher;
import tk.freaxsoftware.ukrinform.ribbon.lib.io.importer.Queue;

/**
 * IO subsystem general control class.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class IOControl {
    
    /**
     * Log id for import subsystem.
     */
    private static final String IMPORT_LOGID = "ІМПОРТ";
    
    /**
     * Log id for export subsystem.
     */
    private static final String EXPORT_LOGID = "ЕКСПОРТ";

    public static String getIMPORT_LOGID() {
        return IMPORT_LOGID;
    }

    public static String getEXPORT_LOGID() {
        return EXPORT_LOGID;
    }

    public static String getLOG_ID() {
        return LOG_ID;
    }

    public static int getIO_API() {
        return IO_API;
    }
    
    /**
     * Import configs dir path.
     */
    private String IMPORT_DIR;
    
    /**
     * Export configs dir path.
     */
    private String EXPORT_DIR;
    
    /**
     * Log id for this class.
     */
    private static final String LOG_ID = "ВВІД/ВИВІД";
    
    /**
     * Numeric id of current library API.
     */
    private static final int IO_API = 2;
    
    /**
     * System wrapper object.
     */
    private SystemWrapper serverWrapper;
    
    /**
     * Current import queue object.
     */
    private Queue queue;
    
    /**
     * Current export dispathcher object.
     */
    private Dispatcher dispathcer;
    
    private static IOControl instance;
    
    private IOControl() {}
    
    public static IOControl getInstance() {
        if (instance == null) {
            instance = new IOControl();
        }
        return instance;
    }
    
    /**
     * Load wrapper to the IO subsystem.
     * @param givenWrapper wrapper to load.
     */
    public void initWrapper(SystemWrapper givenWrapper) {
        serverWrapper = givenWrapper;
    }
    
    /**
     * Register path dir variables (uses for modules initiation).
     * @param importPath path of import config folder;
     * @param exportPath path of export config folder;
     */
    public void registerPathes(String importPath, String exportPath) {
        IMPORT_DIR = importPath;
        EXPORT_DIR = exportPath;
    }
    
    /**
     * Register queue 
     * @param givenQuee queue reference to register;
     */
    public void registerImport(Queue givenQuee) {
        queue = givenQuee;
    }
    
    /**
     * Register dispatcher.
     * @param givenDispatcher dispather reference to register 
     */
    public void registerExport(Dispatcher givenDispatcher) {
        dispathcer = givenDispatcher;
    }
    
    /**
     * Load import modules.
     * @param modulePath path to the module folder.
     * @return list with module containers.
     */
    public List<ModuleContainer> loadModules(String modulePath) {
        List<ModuleContainer> classes = new ArrayList();
        File[] modulesRaw = new File(modulePath).listFiles();
        for (File moduleFile : modulesRaw) {
            processModule(moduleFile.getName(), modulePath, classes);
        }
        return classes;
    }
    
    /**
     * Process module file and add it to module store.
     * @param filename name of module file;
     * @param restOfPath full path to module directory;
     * @param classes modules store list;
     */
    private void processModule(String fileName, String restOfPath, List<ModuleContainer> classes) {
        try {
            JarFile jarFile;
            URLClassLoader loader;
            loader = URLClassLoader.newInstance(new URL[] {new URL("jar:file:" + restOfPath + fileName + "!/")});
            jarFile = new JarFile(restOfPath + "/" + fileName);
            Enumeration<JarEntry> entries = jarFile.entries();
            while(entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                String className = null;
                if(entryName.endsWith(".class")) {
                    className = entry.getName().substring(0,entry.getName().length()-6).replace('/', '.');
                }
                if (className != null) {
                    try {
                        Class loadedClass = loader.loadClass(className);
                        ModuleContainer module = tryModule(loadedClass);
                        if (module != null) {
                            serverWrapper.log(LOG_ID, 2, "завантажено модуль '" + module.getModuleClass().getName() + "'");
                            classes.add(module);
                        }
                    } catch (ClassNotFoundException ex) {
                        serverWrapper.log(LOG_ID, 1, "неможливо завантажити клас " + className + "!");
                    }
                }
            }
        } catch (java.net.MalformedURLException ex) {
            serverWrapper.log(LOG_ID, 1, "некоректний URL для файлу " + fileName + "!");
        } catch (IOException ex) {
            serverWrapper.log(LOG_ID, 1, "неможливо прочитати файл " + fileName + "!");
        }
    }
    
    /**
     * Try to define module type and build <code>ModuleContainer</code>.
     * @param givenClass class to try;
     * @return builded <code>ModuleContainer</code> instance.
     */
    private ModuleContainer tryModule(Class givenClass) {
        try {
            RibbonIOModule moduleNote = (RibbonIOModule) givenClass.getAnnotation(RibbonIOModule.class);
            if (moduleNote != null && (moduleNote.api_version() <= IOControl.IO_API)) {
                serverWrapper.registerPropertyName(moduleNote.property());
                return new ModuleContainer(moduleNote.type(), moduleNote.property(), givenClass);
            } else {
                return null;
            }
        } catch (Exception ex) {
            serverWrapper.log(LOG_ID, 1, "помилка опрацювання класу " + givenClass.getName());
            return null;
        }
    }

    public SystemWrapper getServerWrapper() {
        return serverWrapper;
    }

    public Queue getQueue() {
        return queue;
    }

    public Dispatcher getDispathcer() {
        return dispathcer;
    }

    public String getIMPORT_DIR() {
        return IMPORT_DIR;
    }

    public String getEXPORT_DIR() {
        return EXPORT_DIR;
    }
}
