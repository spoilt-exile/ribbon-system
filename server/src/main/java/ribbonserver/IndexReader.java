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

package ribbonserver;

/**
 * Read, parse and store CSV Ribbon configurations and base index class
 * @author Stanislav Nepochatov
 * @since RibbonServer a1
 * @deprecated 
 */
public abstract class IndexReader {
    
    private static String LOG_ID = "ІНДЕКСАТОР";
    
    /**
     * Lock object for concurent safe base index operations.
     * @since RibbonServer a2
     */
    private static final Object BASE_LOCK = new Object();
    
    /**
     * Lock object for concurent safe session index operations.
     * @since RibbonServer a2
     */
    private static final Object SESSION_LOCK = new Object();
    
    /**
     * Read directories in directory index file
     * @return arraylist of dir shemas
     * @see Directories.dirSchema
     * @since RibbonServer a1
     */
    public static java.util.ArrayList<tk.freaxsoftware.ukrinform.ribbon.lib.data.directory.DirSchema> readDirectories() {
        java.util.ArrayList<tk.freaxsoftware.ukrinform.ribbon.lib.data.directory.DirSchema> Dirs = new java.util.ArrayList<>();
        java.io.BufferedReader dirIndexReader = null;
        try {
            dirIndexReader = new java.io.BufferedReader(new java.io.FileReader(RibbonServer.BASE_PATH + "/" + RibbonServer.DIR_INDEX_PATH));
            while (dirIndexReader.ready()) {
                Dirs.add(new tk.freaxsoftware.ukrinform.ribbon.lib.data.directory.DirSchema(dirIndexReader.readLine()));
            }
        } catch (java.io.FileNotFoundException ex) {
            RibbonServer.logAppend(LOG_ID, 2, "попередній файл індексу напрявків не знайдено. Створюю новий.");
            java.io.File dirIndexFile = new java.io.File(RibbonServer.BASE_PATH + "/" + RibbonServer.DIR_INDEX_PATH);
            String[] defaultDirs = new String[] {
              "СИСТЕМА,{Головний напрямок новин про розробку системи},[ALL],[GALL:100],[]",
              "СИСТЕМА.Розробка,{Новини про розробку},[UA,RU],[GALL:100],[]",
              "СИСТЕМА.Тест,{Тестовий напрямок},[UA,RU],[GALL:110],[]",
              "СИСТЕМА.Загублене,{Напрямок для загублених повідомлень},[ALL],[GALL:100],[]",
              "СИСТЕМА.Помилки,{Напрямок журналу помилок системи},[ALL],[GALL:000],[]"
            };
            java.io.FileWriter dirIndexWriter = null;
            try {
                dirIndexFile.createNewFile();
                dirIndexWriter = new java.io.FileWriter(dirIndexFile);
                for (String defString: defaultDirs) {
                    dirIndexWriter.write(defString + "\n");
                    Dirs.add(new tk.freaxsoftware.ukrinform.ribbon.lib.data.directory.DirSchema(defString));
                }
            } catch (java.io.IOException exq) {
                RibbonServer.logAppend(LOG_ID, 0, "неможливо створити новий файл індексу напрямків!");
                System.exit(4);
            } finally {
                try{
                    if (dirIndexWriter != null) {
                        dirIndexWriter.close();
                    }
                } catch (java.io.IOException exq) {
                    RibbonServer.logAppend(LOG_ID, 0, "неможливо закрити файл індексу напрямків!");
                    System.exit(4);
                }
            }
        } catch (java.io.IOException ex) {
            RibbonServer.logAppend(LOG_ID, 0, "помилка читання файлу індекса напрямків!");
            System.exit(4);
        } finally {
            try {
                if (dirIndexReader != null) {
                    dirIndexReader.close();
                }
            } catch (java.io.IOException ex) {
                RibbonServer.logAppend(LOG_ID, 0, "помилка закриття файлу індекса напрямків!");
                System.exit(4);
            }
        }
        return Dirs;
    }
    
    /**
     * Read message indexes in base index file
     * @return arraylist with index entries
     * @since RibbonServer a1
     */
    public static java.util.ArrayList<tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageEntry> readBaseIndex() {
        java.util.ArrayList<tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageEntry> returnedIndex = new java.util.ArrayList<>();
        java.io.BufferedReader baseIndexReader = null;
        try {
            baseIndexReader = new java.io.BufferedReader(new java.io.FileReader(RibbonServer.BASE_PATH + "/" + RibbonServer.BASE_INDEX_PATH));
            while (baseIndexReader.ready()) {
                returnedIndex.add(new tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageEntry(baseIndexReader.readLine()));
            }
        } catch (java.io.FileNotFoundException ex) {
            RibbonServer.logAppend(LOG_ID, 2, "попередній файл індексу бази не знайдено. Створюю новий.");
            java.io.File usersIndexFile = new java.io.File(RibbonServer.BASE_PATH + "/" + RibbonServer.BASE_INDEX_PATH);
            try {
                usersIndexFile.createNewFile();
            } catch (java.io.IOException exq) {
                RibbonServer.logAppend(LOG_ID, 0, "неможливо створити новий файл індексу бази!");
                System.exit(5);
            }
        } catch (java.io.IOException ex) {
            RibbonServer.logAppend(LOG_ID, 0, "помилка читання файлу індекса бази повідомлень!");
            System.exit(4);
        }
        return returnedIndex;
    }
    
    /**
     * Read session index file.
     * @return array list with session index;
     * @since RibbonServer a2
     */
    public static java.util.ArrayList<SessionManager.SessionEntry> readSessionIndex() {
        java.util.ArrayList<SessionManager.SessionEntry> returnedIndex = new java.util.ArrayList<>();
        try {
            java.io.BufferedReader baseIndexReader = new java.io.BufferedReader(new java.io.FileReader(RibbonServer.BASE_PATH + "/" + "session.index"));
            while (baseIndexReader.ready()) {
                String readedCsv = baseIndexReader.readLine();
                if (readedCsv.isEmpty()) {
                    return returnedIndex;
                }
                returnedIndex.add(new SessionManager.SessionEntry(readedCsv));
            }
        } catch (java.io.FileNotFoundException ex) {
            RibbonServer.logAppend(LOG_ID, 2, "попередній файл індексу сесій не знайдено. Створюю новий.");
            java.io.File usersIndexFile = new java.io.File(RibbonServer.BASE_PATH + "/" + "session.index");
            try {
                usersIndexFile.createNewFile();
            } catch (java.io.IOException exq) {
                RibbonServer.logAppend(LOG_ID, 0, "неможливо створити новий файл індексу сесій!");
                System.exit(5);
            }
        } catch (java.io.IOException ex) {
            RibbonServer.logAppend(LOG_ID, 0, "помилка читання файлу індекса сесій!");
            System.exit(4);
        }
        return returnedIndex;
    }
    
    /**
     * Append new message csv to session index file
     * @param csvReport csv formated string
     * @since RibbonServer a2
     */
    public synchronized static void appendToSessionIndex(String csvReport) {
        synchronized (SESSION_LOCK) {
            try {
                try (java.io.FileWriter sesWriter = new java.io.FileWriter(RibbonServer.BASE_PATH + "/" + "session.index", true)) {
                    sesWriter.write(csvReport + "\n");
                    sesWriter.close();
                }
            } catch (java.io.IOException ex) {
                RibbonServer.logAppend(LOG_ID, 0, "Неможливо записита файл индекса сесій!");
            }
        }
    }
    
    /**
     * Update session index file after session manipulations.
     * @since RibbonServer a2
     */
    public synchronized static void updateSessionIndex() {
        Thread delayExec = new Thread() {
            @Override
            public void run() {
                synchronized (SESSION_LOCK) {
                    java.util.ListIterator<SessionManager.SessionEntry> sesIter = SessionManager.sessionCookie.listIterator();
                    StringBuffer contentBuf = new StringBuffer();
                    while (sesIter.hasNext()) {
                        contentBuf.append(sesIter.next().toCsv());
                        contentBuf.append("\n");
                    }
                    java.io.FileWriter sesWriter = null;
                    try {
                    sesWriter = new java.io.FileWriter(RibbonServer.BASE_PATH + "/" + "session.index");
                    sesWriter.write(contentBuf.toString());
                    } catch (java.io.IOException ex) {
                        RibbonServer.logAppend(LOG_ID, 0, "Неможливо записита файл індекса сесій!");
                    } finally {
                        if (sesWriter != null) {
                            try {
                                sesWriter.close();
                            } catch (java.io.IOException ex) {
                                RibbonServer.logAppend(LOG_ID, 0, "Неможливо закрити файл індекса сесій!");
                            }
                        }
                    }
                }
            }
        };
        delayExec.start();
    }
    
    /**
     * Append new message csv to base index file
     * @param csvReport csv formated string
     * @since RibbonServer a1
     */
    public synchronized static void appendToBaseIndex(String csvReport) {
        synchronized (BASE_LOCK) {
            try {
                try (java.io.FileWriter messageWriter = new java.io.FileWriter(RibbonServer.BASE_PATH + "/" + RibbonServer.BASE_INDEX_PATH, true)) {
                    messageWriter.write(csvReport + "\n");
                    messageWriter.close();
                }
            } catch (java.io.IOException ex) {
                RibbonServer.logAppend(LOG_ID, 0, "Неможливо записита файл индекса бази повідомлень!");
            }
        }
    }
    
    /**
     * Update base index file after message manipulations.
     * @since RibbonServer a1
     */
    public synchronized static void updateBaseIndex() {
        Thread delayExec = new Thread() {
            @Override
            public void run() {
                synchronized (BASE_LOCK) {
                    java.util.ListIterator<tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageEntry> storeIter = Messenger.messageIndex.listIterator();
                    StringBuffer contentBuf = new StringBuffer();
                    while (storeIter.hasNext()) {
                        contentBuf.append(storeIter.next().toCsv());
                        contentBuf.append("\n");
                    }
                    try {
                        try (java.io.FileWriter messageWriter = new java.io.FileWriter(RibbonServer.BASE_PATH + "/" + RibbonServer.BASE_INDEX_PATH)) {
                            messageWriter.write(contentBuf.toString());
                        }
                    } catch (java.io.IOException ex) {
                        RibbonServer.logAppend(LOG_ID, 0, "Неможливо записита файл индекса бази повідомлень!");
                    }
                }
            }
        };
        delayExec.start();
    }
    
    /**
     * Read pseudo directoris from index;
     * @return array list with pseudo directories objects;
     */
    public static java.util.ArrayList<Directories.PseudoDirEntry> readPseudoDirectories() {
        String[] readed = readIndex("pseudo.index");
        if (readed == null) {
            writeIndex("pseudo.index", null, false, "{Тест},{Випуск тестового повідомлення},[СИСТЕМА.Тест]\n");
            readed = readIndex("pseudo.index");
        }
        java.util.ArrayList<Directories.PseudoDirEntry> psDirs = new java.util.ArrayList<Directories.PseudoDirEntry>(readed.length);
        for (String currLine : readed) {
            psDirs.add(new Directories.PseudoDirEntry(currLine));
        }
        return psDirs;
    }
    
    /**
     * Write some csv to index file.
     * @param indexFile name of file;
     * @param indexLock lock to sync;
     * @param appendFlag write and erase or just append;
     */
    public static void writeIndex(String indexFile, Object indexLock, Boolean appendFlag, String content) {
        java.io.FileWriter indexWriter = null;
        try {
            indexWriter = new java.io.FileWriter(RibbonServer.BASE_PATH + "/" + indexFile);
            if (indexLock == null) {
                indexWriter.write(content);
            } else {
                synchronized (indexLock) {
                    indexWriter.write(content);
                }
            }
        } catch (java.io.IOException ex) {
            RibbonServer.logAppend(LOG_ID, 0, "помилка запису до файлу " + indexFile);
            System.exit(4);
        } finally {
            try {
                indexWriter.close();
            } catch (java.io.IOException ex) {}
        }
    }
    
    /**
     * Read inde file.
     * @param indexFile name of index file;
     * @return string array of index lines or null if index does'nt exist;
     */
    public static String[] readIndex(String indexFile) {
        StringBuffer buf = new StringBuffer();
        java.io.BufferedReader indxReader = null;
        try {
            indxReader = new java.io.BufferedReader(new java.io.FileReader(RibbonServer.BASE_PATH + "/" + indexFile));
            while (indxReader.ready()) {
                buf.append(indxReader.readLine());
                buf.append("\n");
            }
            indxReader.close();
        } catch (java.io.FileNotFoundException ex) {
            return null;
        } catch (java.io.IOException ex) {
            RibbonServer.logAppend(LOG_ID, 0, "помилка читання файлу " + indexFile);
            System.exit(4);
        }
        return buf.toString().split("\n");
    }
    
    
}