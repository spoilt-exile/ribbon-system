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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ukrinform.ribbon.lib.io.utils.IOControl;

/**
 * Main system procedures
 * @author Stanislav Nepochatov
 * @since RibbonServer a1
 */
public class Procedures {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Procedures.class);
    
    /**
     * Post given message into the system information stream
     * @param givenMessage message which should be released
     * @return processing status;
     * @since RibbonServer a1
     */
    public static synchronized String PROC_POST_MESSAGE(tk.freaxsoftware.ukrinform.ribbon.lib.data.message.Message givenMessage) {
        if (RibbonServer.CURR_STATE == RibbonServer.SYS_STATES.MAINTAINING || RibbonServer.CURR_STATE == RibbonServer.SYS_STATES.INIT || RibbonServer.CURR_STATE == RibbonServer.SYS_STATES.CLOSING) {
            LOGGER.warn("System not ready for accepting messages.");
            return "RIBBON_ERROR:System not ready. Try later.";
        } else {
           Integer failedIndex = AccessHandler.checkAccessForAll(givenMessage.getAuthor(), givenMessage.getDirectories(), 1);
            if (failedIndex != null) {
                return "RIBBON_ERROR:Access error to the directory " + givenMessage.getDirectories()[failedIndex];
            }
            if (givenMessage.getPreviousIndex().equals("-1")) {
                givenMessage.setPreviousAuthor(givenMessage.getAuthor());
            } else {
                givenMessage.setPreviousAuthor(Messenger.getMessageEntryByIndex(givenMessage.getPreviousIndex()).getAuthor());
            }
            Messenger.addMessageToIndex(givenMessage);
            if (RibbonServer.IO_ENABLED && IOControl.getInstance().getDispathcer().checkExport(givenMessage.getDirectories())) {
                IOControl.getInstance().getDispathcer().initExport(givenMessage);
            }
            writeMessage(givenMessage.getDirectories(), givenMessage.getIndex(), givenMessage.getContent());
            givenMessage.setContent(null);
            IndexReader.appendToBaseIndex(givenMessage.returnEntry().toCsv());
            for (Integer dirIndex = 0; dirIndex < givenMessage.getDirectories().length; dirIndex++) {
                if (givenMessage.getDirectories()[dirIndex] == null) {
                    LOGGER.error("unable to post message " + givenMessage.getHeader() + " in directory " + givenMessage.getDirectories()[dirIndex]);
                } else {
                    LOGGER.info(givenMessage.getDirectories()[dirIndex] + " message posted: [" + givenMessage.getHeader() + "]");
                }
            }
            return "OK:";
        }
    }
    
    /**
     * Write message content to file and create links
     * @param fullPath full path to message file
     * @param messageContent content of the message
     * @since RibbonServer a1
     */
    public static void writeMessage(String[] dirArr, String strIndex, String messageContent) {
        String currPath = "";
        try {
            for (Integer pathIndex = 0; pathIndex < dirArr.length; pathIndex++) {
                if (dirArr[pathIndex] == null) {
                    continue;
                } else {
                    currPath = Directories.getDirPath(dirArr[pathIndex]);
                    if (currPath == null) {
                        continue;
                    }
                    else {
                        java.io.FileWriter messageWriter = new java.io.FileWriter(currPath + strIndex);
                        messageWriter.write(messageContent);
                        messageWriter.close();
                    }
                }
            }
        } catch (java.io.IOException ex) {
            LOGGER.warn("Can't write file: " + currPath + strIndex, ex);
        }
    }
    
    /**
     * Modify message by given template message.
     * @param oldMessage original message to modify;
     * @param newMessage override template message;
     * @since RibbonServer a2
     */
    public static synchronized void PROC_MODIFY_MESSAGE(tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageEntry oldMessage, tk.freaxsoftware.ukrinform.ribbon.lib.data.message.Message newMessage) {
        makeCleanup(oldMessage.getDirectories(), newMessage.getDirectories(), oldMessage.getIndex());
        Messenger.modTagIndex(oldMessage, oldMessage);
        oldMessage.modifyMessageEntry(newMessage);
        writeMessage(oldMessage.getDirectories(), oldMessage.getIndex(), newMessage.getContent());
        IndexReader.updateBaseIndex();
    }
    
    /**
     * Make cleanup within old unused dirs in modifyed message;
     * @param oldDirs array with old dirs;
     * @param newDirs array with new dirs;
     * @since RibbonServer a2
     */
    private static void makeCleanup(String[] oldDirs, String[] newDirs, String strIndex) {
        for (Integer oldIndex = 0; oldIndex < oldDirs.length; oldIndex++) {
            for (Integer newIndex = 0; newIndex < newDirs.length; newIndex++) {
                if(oldDirs[oldIndex].equals(newDirs[newIndex])) {
                    oldDirs[oldIndex] = null;
                    break;
                }
            }
            if (oldDirs[oldIndex] != null) {
                String path = Directories.getDirPath(oldDirs[oldIndex]) + strIndex;
                try {
                    java.nio.file.Files.delete(new java.io.File(path).toPath());
                } catch (java.io.IOException ex) {
                    LOGGER.error("Unable to delete message: " + path, ex);
                }
            }
        }
    }
    
    /**
     * Delete message from all indexes.
     * @param givenEntry entry to delete
     * @since RibbonServer a1
     */
    public static synchronized void PROC_DELETE_MESSAGE(tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageEntry givenEntry) {
        for (Integer pathIndex = 0; pathIndex < givenEntry.getDirectories().length; pathIndex++) {
            String currPath = Directories.getDirPath(givenEntry.getDirectories()[pathIndex]) + givenEntry.getIndex();
            try {
                java.nio.file.Files.delete(new java.io.File(currPath).toPath());
            } catch (java.io.IOException ex) {
                LOGGER.error("unable to delete message: " + currPath);
            }
        }
        Messenger.deleteMessageEntryFromIndex(givenEntry);
        LOGGER.error("message with index " + givenEntry.getIndex() + " removed from the system.");
    }
    
    /**
     * Post system launch notification.
     * @since RibbonServer a1
     */
    public static void postInitMessage() {
        String formatLine = "======================================================================================";
        PROC_POST_MESSAGE(new tk.freaxsoftware.ukrinform.ribbon.lib.data.message.Message(
            "Системне повідомлення",
            "root",
            "UA",
            new String[] {"СИСТЕМА.Тест"},
            new String[] {"оголошення", "ІТУ"},
            formatLine + "\nСистема \"Стрічка\" " + RibbonServer.RIBBON_MAJOR_VER + "\n" + formatLine + "\n"
                    + "Це повідомлення автоматично генерується системою \"Стрічка\"\n"
                    + "при завантаженні. Зараз система готова для одержання повідомлень."
                    + "\n\n" + RibbonServer.getCurrentDate()));
    }
    
    /**
     * Post exception as message to the debug directory.
     * @param desc short description of exceptional situation;
     * @param ex exception object;
     * @since RibbonServer a2
     */
    public static void postException(String desc, Throwable ex) {
        if (RibbonServer.DEBUG_POST_EXCEPTIONS) {
            StringBuffer exMesgBuf = new StringBuffer();
            exMesgBuf.append(desc);
            exMesgBuf.append("\n");
            exMesgBuf.append(ex.getClass().getName() + "\n");
            exMesgBuf.append(ex.getMessage());
            StackTraceElement[] stackTrace = ex.getStackTrace();
            for (StackTraceElement element : stackTrace) {
                exMesgBuf.append(element.toString() + "\n");
            }
            tk.freaxsoftware.ukrinform.ribbon.lib.data.message.Message exMessage = new tk.freaxsoftware.ukrinform.ribbon.lib.data.message.Message(
                    "Звіт про помилку", "root", "UA", new String[] {RibbonServer.DEBUG_POST_DIR}, 
                    new String[] {"СТРІЧКА", "ПОМИЛКИ"}, exMesgBuf.toString());
            Procedures.PROC_POST_MESSAGE(exMessage);
            SessionManager.broadcast("RIBBON_UCTL_LOAD_INDEX:" + exMessage.toCsv(), RibbonProtocol.CONNECTION_TYPES.CLIENT);
        }
    }
}
