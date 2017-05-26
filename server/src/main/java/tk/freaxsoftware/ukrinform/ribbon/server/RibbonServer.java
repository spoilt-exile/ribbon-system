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
import tk.freaxsoftware.extras.faststorage.exception.EntityProcessingException;
import tk.freaxsoftware.extras.faststorage.ignition.FastStorageIgnition;
import tk.freaxsoftware.ukrinform.ribbon.lib.io.utils.IOControl;
import tk.freaxsoftware.ukrinform.ribbon.server.web.WebServer;

/**
 * Main Ribbon server class
 * @author Stanislav Nepochatov
 */
public class RibbonServer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RibbonServer.class);
    
    /**
     * Default time and date format for server.
     * @since RibbonServer a1
     */
    private static java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
    
    /**
     * ID of this component or object for loging.
     * @since RibbonServer a1
     */
    public static String LOG_ID = "СИСТЕМА";
    
    /**
     * Current dir variable.
     * @since RibbonServer a1
     */
    public static String CurrentDirectory = System.getProperty("user.dir");
    
    /**
     * RibbonServer main configuration.
     * @since RibbonServer a1
     */
    public static java.util.Properties mainConfig;
    
    /**
     * System states enumeration.
     * @since RibbonServer a1
     */
    public static enum SYS_STATES {
        
        /**
         * Initialization state.<br>
         * <b>LIMIT:</b> login, posting, IO operations.
         */
        INIT,
        
        /**
         * System is ready to recieve messages.<br>
         * <b>NO LIMIT</b>.
         */
        READY,
        
        /**
         * I/O subsystem emergency state.<br>
         * <b>LIMIT:</b> some IO operations.
         */
        DIRTY,
        
        /**
         * Maintaince state.<br>
         * <b>LIMIT:</b> posting, IO operations.
         */
        MAINTAINING,
        
        /**
         * Closing state.<br>
         * <b>LIMIT:</b> login, posting, IO operations.
         */
        CLOSING
    }
    
    /**
     * Current system state variable.
     * @since RibbonServer a1
     */
    public static SYS_STATES CURR_STATE = null;
    
    /**
     * List of IO modules strings with errors.
     * @since RibbonServer a2
     */
    public static java.util.ArrayList<String> DIRTY_LIST = new java.util.ArrayList<>();
    
    /**
     * Lock for system status concurent operations.
     * @since RibbonServer a2
     */
    protected static final Object DIRTY_LOCK = new Object();
    
    /**
     * Is system controled by administrator control console.
     * @since RibbonServer a1
     */
    public static Boolean CONTROL_IS_PRESENT = false;
    
    /** SYSTEM VARIABLES **/
    
    /**
     * Path to Ribbon base.
     * @since RibbonServer a1
     */
    public static String BASE_PATH;
    
    /**
     * Allow attachments switch <b>[not yet implemented]</b>.
     * @since RibbonServer a2
     */
    public static Boolean BASE_ALLOW_ATTACHMENTS;
    
    /**
     * Major server version id.
     * @since RibbonServer a1
     */
    public static final String RIBBON_MAJOR_VER = "a3";
    
    /**
     * Minor server version postfix.
     * @since RibbonServer a2
     */
    public static final String RIBBON_MINOR_VER = "";
    
    /**
     * Devel server version postfix.
     * @since RibbonServer a2
     */
    public static final String RIBBON_DEVEL_VER = "";
    
    /**
     * Port number for listening.
     * @since RibbonServer a1
     */
    public static Integer NETWORK_PORT;
    
    /**
     * Allow remote connection (not only localhost) switch.
     * @since RibbonServer a2
     */
    public static Boolean NETWORK_ALLOW_REMOTE;
    
    /**
     * Network connections limit variable.
     * @since RibbonServer a2
     */
    public static Integer NETWORK_MAX_CONNECTIONS;
    
    /**
     * Cache switch <b>[not yet implemented]</b>.
     * @since RibbonServer a2
     */
    public static Boolean CACHE_ENABLED;
    
    /**
     * Size of cache <b>[not yet implemented]</b>.
     * @since RibbonServer a2
     */
    public static Integer CACHE_SIZE;
    
    /**
     * Defalut ALL group permissions.
     * @since RibbonServer a2
     */
    public static String ACCESS_ALL_MASK;
    
    /**
     * Constant default ALL group permission (for validation).
     * @since RibbonServer a2
     */
    public static final String VAL_ACCESS_ALL_MASK = "100";
    
    /**
     * Allow to login user to more than one session.
     * @since RibbonServer a2
     */
    public static Boolean ACCESS_ALLOW_MULTIPLIE_LOGIN;
    
    /**
     * Allow to login by previous session hash.
     * @since RibbonServer a2
     */
    public static Boolean ACCESS_ALLOW_SESSIONS;
    
    /**
     * Max count of session hsah reusing.
     * @since RibbonServer a2
     */
    public static Integer ACCESS_SESSION_MAX_COUNT;
    
    /**
     * Allow to use remote connection mode.
     * @since RibbonServer a2
     */
    public static Boolean ACCESS_ALLOW_REMOTE;
    
    /**
     * Group of user which is allowed to create remote connections.
     * @since RibbonServer a2
     */
    public static String ACCESS_REMOTE_GROUP;
    
    /**
     * Post init message flag.
     * @since RibbonServer a2
     */
    public static Boolean OPT_POST_INIT;
    
    /**
     * Create text reports during startup.
     * @since RibbonServer a2
     */
    public static Boolean OPT_CREATE_REPORTS;
    
    /**
     * Enable/disable import and export operations.
     * @since RibbonServer a2
     */
    public static Boolean IO_ENABLED;
    
    /**
     * Ignore all attempts to set dirty status on the server.<br>
     * <b>WARNING!</b> This settings is dangerous for production installations!<br>
     * <b>ONLY FOR TEST PURPOSES!<b>
     * @since RibbonServer a2
     */
    public static Boolean IO_IGNORE_DIRTY;
    
    /**
     * Import emergency directory (in case of bad validation).
     * @since RibbonServer a2
     */
    public static String IO_IMPORT_EM_DIR;
    
    /**
     * Post system exception to specified directory.
     * @since RibbonServer a2
     */
    public static Boolean DEBUG_POST_EXCEPTIONS;
    
    /**
     * Directory to post exception messages.
     * @since RibbonServer a2
     */
    public static String DEBUG_POST_DIR;
    
    /**
     * Enable access by HTTP.
     * @since RibbonServer a3
     */
    private static Boolean HTTP_ENABLED;
    
    /**
     * Port for HTTP server.
     * @since RibbonServer a3
     */
    private static Integer HTTP_PORT;
    
    /**
     * Name of directory index file.
     * @since RibbonServer a1
     */
    public static String DIR_INDEX_PATH = "dir.index";
    
    /**
     * Name of users index file.
     * @since RibbonServer a1
     */
    public static String USERS_INDEX_PATH = "users.index";
    
    /**
     * Name of group index file.
     * @since RibbonServer a2
     */
    public static String GROUPS_INDEX_PATH = "groups.index";
    
    /**
     * Name of messages index file.
     * @since RibbonServer a1
     */
    public static String BASE_INDEX_PATH = "base.index";
    
    /**
     * Import quene object.
     * @since RibbonServer a2
     */
    public static tk.freaxsoftware.ukrinform.ribbon.lib.io.importer.Queue ImportQuene;
    
    /**
     * Export dispatcher object.
     * @since RibbonServer a2
     */
    public static tk.freaxsoftware.ukrinform.ribbon.lib.io.exporter.Dispatcher ExportDispatcher;
    
    /**
     * System wrapper for system to libRibbonIO communication.
     * @since RibbonServer a2
     */
    private static class IOWrapper extends tk.freaxsoftware.ukrinform.ribbon.lib.io.utils.SystemWrapper {
        
        public void log(String logSource, Integer logLevel, String logMessage) {}

        @Override
        public void addMessage(String schemeName, String typeName, tk.freaxsoftware.ukrinform.ribbon.lib.data.message.Message givenMessage) {
            for (int index = 0; index < givenMessage.getDirectories().length; index++) {
                if (Directories.getDirPath(givenMessage.getDirectories()[index]) == null) {
                    LOGGER.error("scheme " + schemeName + " (" + typeName + ") points on non-existing directory " + givenMessage.getDirectories()[index]);
                    givenMessage.setDirectories(new String[] {IO_IMPORT_EM_DIR});
                    break;
                }
            }
            Procedures.PROC_POST_MESSAGE(givenMessage);
            SessionManager.broadcast("RIBBON_UCTL_LOAD_INDEX:" + givenMessage.toCsv(), RibbonProtocol.CONNECTION_TYPES.CLIENT);
        }

        @Override
        public void registerPropertyName(String givenName) {
            Boolean result = tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageProperty.Types.registerTypeIfNotExist(givenName);
            if (result) {
                LOGGER.warn("new property type registered '" + givenName + "'");
            }
        }

        @Override
        public String getDate() {
            return RibbonServer.getCurrentDate();
        }

        @Override
        public String getProperty(String key) {
            return RibbonServer.mainConfig.getProperty(key);
        }

        @Override
        public void enableDirtyState(String moduleType, String moduleScheme, String modulePrint) {
            if (RibbonServer.IO_IGNORE_DIRTY) {
                return;
            }
            String moduleString = moduleType + ":" + modulePrint;
            if (RibbonServer.DIRTY_LIST.contains(moduleString)) {
                return;
            }
            if (RibbonServer.DIRTY_LIST.isEmpty()) {
                LOGGER.warn("module " + moduleType + " by scheme " + moduleScheme + " get error");
                LOGGER.error("system run in `dirty` mode!");
                //TODO add admin remote notification of such event
            }
            synchronized (RibbonServer.DIRTY_LOCK) {
                RibbonServer.CURR_STATE = RibbonServer.SYS_STATES.DIRTY;
                RibbonServer.DIRTY_LIST.add(moduleType + ":" + modulePrint);
            }
        }

        @Override
        public void disableDirtyState(String moduleType, String moduleScheme, String modulePrint) {
            if (RibbonServer.IO_IGNORE_DIRTY) {
                return;
            }
            String moduleString = moduleType + ":" + modulePrint;
            synchronized (RibbonServer.DIRTY_LOCK) {
                if (!RibbonServer.DIRTY_LIST.remove(moduleString)) {
                    LOGGER.error("module " + moduleScheme + " not present in fault module list!!");
                }
                if (RibbonServer.DIRTY_LIST.isEmpty() && RibbonServer.CURR_STATE == RibbonServer.SYS_STATES.DIRTY) {
                    LOGGER.warn("system run in normal mode.");
                    RibbonServer.CURR_STATE = RibbonServer.SYS_STATES.READY;
                }
            }
        }

        @Override
        public void updateIndex(String givenIndex) {
            IndexReader.updateBaseIndex();
            SessionManager.broadcast("RIBBON_UCTL_UPDATE_INDEX:" + Messenger.getMessageEntryByIndex(givenIndex).toCsv(), RibbonProtocol.CONNECTION_TYPES.CLIENT);
        }

        @Override
        public void postException(String desc, Throwable ex) {
            Procedures.postException(desc, ex);
        }
    }

    /**
     * Main server's function
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LOGGER.warn("System loading...");
        LOGGER.warn("Ribbon Server " + RIBBON_MAJOR_VER + RIBBON_MINOR_VER + RIBBON_DEVEL_VER);
        CURR_STATE = RibbonServer.SYS_STATES.INIT;
        setSystemVariables();
        if (IO_ENABLED) {
            LOGGER.warn("IO library configurration");
            IOControl.getInstance().initWrapper(new IOWrapper());
            IOControl.getInstance().registerPathes(BASE_PATH + "/import/", BASE_PATH + "/export/");
            ImportQuene = new tk.freaxsoftware.ukrinform.ribbon.lib.io.importer.Queue(CurrentDirectory + "/imports/", BASE_PATH + "/import/");
            ExportDispatcher = new tk.freaxsoftware.ukrinform.ribbon.lib.io.exporter.Dispatcher(CurrentDirectory + "/exports/", BASE_PATH + "/export/");
            IOControl.getInstance().registerImport(ImportQuene);
            IOControl.getInstance().registerExport(ExportDispatcher);
        }
        LOGGER.info("access control configuration");
        try {
            FastStorageIgnition.igniteOverrided(RibbonServer.class.getClassLoader().getResourceAsStream("entities.ign"), BASE_PATH + "/");
        } catch (EntityProcessingException ex) {
            LOGGER.error("Unable to ignite entities storage!", ex);
            System.exit(-1);
        }
        AccessHandler.init();
        LOGGER.info("directory configuration");
        Directories.init();
        LOGGER.info("message index reading");
        Messenger.init();
        LOGGER.info("system session index reading");
        SessionManager.init();
        CURR_STATE = RibbonServer.SYS_STATES.READY;
        if (OPT_CREATE_REPORTS) {
            Directories.dumpTree();
        }
        if (OPT_POST_INIT) {
            Procedures.postInitMessage();
        }
        if (IO_ENABLED) {
            ImportQuene.importRun();
        }
        LOGGER.info("validating system config");
        validateSystemVariables();
        LOGGER.info("network config");
        if (HTTP_ENABLED) {
            WebServer.init(HTTP_PORT);
        }
        try {
            java.net.ServerSocket RibbonServSocket = new java.net.ServerSocket(NETWORK_PORT);
            LOGGER.info("system is ready for messages");
            while (true) {
                java.net.Socket inSocket = RibbonServSocket.accept();
                if ((!inSocket.getInetAddress().getHostAddress().equals("127.0.0.1") && RibbonServer.NETWORK_ALLOW_REMOTE == false) || SessionManager.checkConnectionLimit() == true) {
                    inSocket.close();
                } else {
                    SessionManager.createNewSession(inSocket);
                }
            }
        } catch (java.io.IOException ex) {
            LOGGER.error("Unable to run network server!", ex);
            System.exit(7);
        }
    }
    
    /**
     * Get current date with default date format.
     * @return current date
     * @since RibbonServer a1
     */
    public static String getCurrentDate() {
        java.util.Date now = new java.util.Date();
        String strDate = dateFormat.format(now);
        return strDate;
    }
    
    /**
     * Read system variables from properties file 
     * and set them to local variables.
     * @since RibbonServer a1
     */
    private static void setSystemVariables() {
        mainConfig = new java.util.Properties();
        
        //Reading properties file
        try {
            mainConfig.load(new java.io.FileInputStream(new java.io.File(CurrentDirectory + "/server.properties")));
        } catch (java.io.FileNotFoundException ex) {
            LOGGER.error("Can't find server.properties file in" + CurrentDirectory);
            System.exit(2);
        } catch (java.io.IOException ex) {
            LOGGER.error("Can't read file server.properties!", ex);
            System.exit(2);
        }
        
        //Setting base variables
        try {
            BASE_PATH = new String(mainConfig.getProperty("base_path").getBytes("ISO-8859-1"), "UTF-8");
        } catch (java.io.UnsupportedEncodingException ex) {
            LOGGER.error("unable to define path for base", ex);
            System.exit(3);
        }
        //BASE_ALLOW_ATTACHMENTS = mainConfig.getProperty("base_allow_attachments").equals("0") ? false : true;
        
        //Setting network variables
        NETWORK_PORT = Integer.valueOf(mainConfig.getProperty("networking_port"));
        if (mainConfig.getProperty("networking_allow_remote").equals("0")) {
            RibbonServer.NETWORK_ALLOW_REMOTE = false;
        } else {
            RibbonServer.NETWORK_ALLOW_REMOTE = true;
        }
        NETWORK_MAX_CONNECTIONS = Integer.valueOf(mainConfig.getProperty("networking_max_connections"));
        
        //Setting cache variables
        //CACHE_ENABLED = mainConfig.getProperty("cache_enabled").equals("0") ? false : true;
        //CACHE_SIZE = Integer.valueOf(mainConfig.getProperty("cache_size"));
        
        //Setting access variables
        ACCESS_ALL_MASK = mainConfig.getProperty("access_all_mask");
        ACCESS_ALLOW_MULTIPLIE_LOGIN = mainConfig.getProperty("access_allow_multiplie_login").equals("0") ? false : true;
        ACCESS_ALLOW_SESSIONS = mainConfig.getProperty("access_enable_sessions").equals("0") ? false : true;
        ACCESS_SESSION_MAX_COUNT = Integer.valueOf(mainConfig.getProperty("access_session_count_max"));
        ACCESS_ALLOW_REMOTE = mainConfig.getProperty("access_allow_remote").equals("0") ? false : true;
        try {
            ACCESS_REMOTE_GROUP = new String(mainConfig.getProperty("access_remote_group").getBytes("ISO-8859-1"), "UTF-8");
        } catch (java.io.UnsupportedEncodingException ex) {
            LOGGER.error("Unable to define remote group name!", ex);
            System.exit(3);
        }
        
        //Setting optional variables
        OPT_POST_INIT = mainConfig.getProperty("opt_post_init").equals("0") ? false : true;
        OPT_CREATE_REPORTS = mainConfig.getProperty("opt_create_reports").equals("0") ? false : true;
        
        //Setting IO control varibales
        IO_ENABLED = mainConfig.getProperty("io_enabled").equals("0") ? false : true;
        IO_IGNORE_DIRTY = mainConfig.getProperty("io_ignore_dirty").equals("0") ? false : true;
        try {
            IO_IMPORT_EM_DIR = new String(mainConfig.getProperty("io_import_em_dir").getBytes("ISO-8859-1"), "UTF-8");
        } catch (java.io.UnsupportedEncodingException ex) {
            LOGGER.error("Unable to define emergency directory!", ex);
            System.exit(3);
        }
        //Integer loc_IO_EXPORT_QUENE_SIZE = ACCESS_SESSION_MAX_COUNT = Integer.valueOf(mainConfig.getProperty("io_export_quene_size"));
        //Integer loc_IO_EXPORT_ERRQUENE_SIZE = ACCESS_SESSION_MAX_COUNT = Integer.valueOf(mainConfig.getProperty("io_export_errquene_size"));
        
        //Setting debug variables
        DEBUG_POST_EXCEPTIONS = mainConfig.getProperty("debug_post_exceptions").equals("0") ? false : true;
        try {
            DEBUG_POST_DIR = new String(mainConfig.getProperty("debug_post_dir").getBytes("ISO-8859-1"), "UTF-8");
        } catch (java.io.UnsupportedEncodingException ex) {
            LOGGER.error("Unable to define debug directory!", ex);
            System.exit(3);
        }
        
        //Setting HTTP
        HTTP_ENABLED = mainConfig.getProperty("http_enabled", "0").equals("1");
        HTTP_PORT = Integer.valueOf(mainConfig.getProperty("http_port", "0"));
        
        LOGGER.info(
                "initial config finished.\n" + 
                "Base path: " + BASE_PATH + "\n" +
                //(RibbonServer.BASE_ALLOW_ATTACHMENTS ? "Зберігання файлів увімкнено." : "") + "\n" +
                //(RibbonServer.CACHE_ENABLED ? "Кешування бази увімкнено.\nРозмір кешу: " + RibbonServer.CACHE_SIZE + "\n" : "") +
                (RibbonServer.NETWORK_ALLOW_REMOTE ? "Network access enabled.\n"
                + "Port:" + RibbonServer.NETWORK_PORT + "\n"
                + (RibbonServer.NETWORK_MAX_CONNECTIONS == -1 ? "No limits" : "Connections limited: " 
                + RibbonServer.NETWORK_MAX_CONNECTIONS) + "\n" : "Network access disabled.\n")
                + (RibbonServer.ACCESS_ALLOW_MULTIPLIE_LOGIN ? "Multiple auth enabled.\n" : "Multiple auth disabled.\n")
                + "Mask for system group ALL:" + RibbonServer.ACCESS_ALL_MASK + "\n"
                + (RibbonServer.ACCESS_ALLOW_SESSIONS ? "Sessions enabled.\nSession max using count:" + RibbonServer.ACCESS_SESSION_MAX_COUNT + "\n" : "")
                + (RibbonServer.ACCESS_ALLOW_REMOTE ? "Remote mode enabled.\nGroup for remote mode:" + RibbonServer.ACCESS_REMOTE_GROUP + "\n" : "")
                + (RibbonServer.OPT_POST_INIT ? "Automatic hello message post enabled.\n" : "")
                + (RibbonServer.OPT_CREATE_REPORTS ? "Report creation enabled.\n" : "")
                + (RibbonServer.IO_ENABLED ? 
                    (
                    "IO enabled.\n" +
                    (RibbonServer.IO_IGNORE_DIRTY ? "WARNING! IO errors will be ignored!\n" : "") + 
                    "Emergency directory:" + RibbonServer.IO_IMPORT_EM_DIR + "\n"
                    //"Розмір черги експорту:" + loc_IO_EXPORT_QUENE_SIZE + "\n" +
                    //"Розмір черги помилок експорту:" + loc_IO_EXPORT_ERRQUENE_SIZE + "\n"
                    ):
                    ""
                )
                + (RibbonServer.DEBUG_POST_EXCEPTIONS ? "Debug mode enabled.\nDebug directory:" + RibbonServer.DEBUG_POST_DIR + "\n" : "")
                + (RibbonServer.HTTP_ENABLED ?
                    (
                    "HTTP server enabled\n" +
                    "Port:" + RibbonServer.HTTP_PORT
                    ):
                     ""
                )
                );
    }
    
    /**
     * Check correctness of some system configurations.<br>
     * <b>WARNING!</b> May stop system with error.
     * @since RibbonServer a2
     */
    private static void validateSystemVariables() {
        
        //Set constant access ALL group mask if corrupted;
        Boolean MASK_VALID = true;
        for (char curr: ACCESS_ALL_MASK.toCharArray()) {
            if (curr == '0' || curr == '1') {
                continue;
            } else {
                MASK_VALID = false;
                break;
            }
        }
        if (ACCESS_ALL_MASK.length() > 3 && !MASK_VALID) {
            LOGGER.warn("Incorrect setting for ALL mask (" + ACCESS_ALL_MASK + ")");
            ACCESS_ALL_MASK = VAL_ACCESS_ALL_MASK;
        }
        
        //Turn off sessions if session use max count is lower or equal to 0;
        if (ACCESS_ALLOW_SESSIONS && ACCESS_SESSION_MAX_COUNT <= 0) {
            LOGGER.warn("Incorrect session usage count setting (" + ACCESS_SESSION_MAX_COUNT + ")");
            ACCESS_ALLOW_SESSIONS = false;
        }
        
        //EXIT if group doesn't exist
        if (ACCESS_ALLOW_REMOTE && !AccessHandler.isGroupExisted(ACCESS_REMOTE_GROUP)) {
            LOGGER.error("remote mode error: grou " + ACCESS_REMOTE_GROUP + " doesn't exist");
            LOGGER.info("Check access_remote_group value in config file");
            System.exit(3);
        }
        
        //I/O section check
        if (IO_ENABLED) {
            
            //EXIT if emergency dir set incorrect
            if (Directories.getDirPath(IO_IMPORT_EM_DIR) == null) {
                LOGGER.error("import config error: directory " + IO_IMPORT_EM_DIR + " doesn't exist");
                LOGGER.info("Check io_import_em_dir value in config file");
                System.exit(3);
            }
            
        }
        
        //EXIT if debug directory doesn't exist
        if (DEBUG_POST_EXCEPTIONS && Directories.getDirPath(DEBUG_POST_DIR) == null) {
            LOGGER.error("debug error: directory " + DEBUG_POST_DIR + " doesn't exist");
            LOGGER.info("Check debug_post_dir value in config file");
            System.exit(3);
        }
        
        //EXIT if http port set below 1024
        if (HTTP_ENABLED && HTTP_PORT < 1024) {
            LOGGER.error("Unable to run HTTP on port below 1024");
            System.exit(3);
        }
    }
    
    /**
     * Get hash sum of given string.
     * @param givenStr given string;
     * @return md5 hash sum representation;
     * @since RibbonServer a2
     */
    public static String getHash(String givenStr) {
        StringBuffer hexString = new StringBuffer();
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(givenStr.getBytes());
            byte[] hash = md.digest();
            for (int i = 0; i < hash.length; i++) {
                if ((0xff & hash[i]) < 0x10) {
                    hexString.append("0"
                            + Integer.toHexString((0xFF & hash[i])));
                } else {
                    hexString.append(Integer.toHexString(0xFF & hash[i]));
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Unable to calculate hash!", ex);
        }
        return hexString.toString();
    }
}
