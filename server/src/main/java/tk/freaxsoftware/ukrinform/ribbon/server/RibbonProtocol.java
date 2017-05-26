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
import tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat;
import tk.freaxsoftware.ukrinform.ribbon.lib.data.user.User;

/**
 * Ribbon protocol server side class
 * @author Stanislav Nepochatov
 * @since RibbonServer a1
 */
public class RibbonProtocol {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RibbonProtocol.class);
    
    /**
     * Tail of protocol result which should be delivered to all peers;
     * @since RibbonServer a1
     */
    public String BROADCAST_TAIL;
    
    /**
     * Type of broadcasting;
     * @since RibbonServer a1
     */
    public CONNECTION_TYPES BROADCAST_TYPE;
    
    /**
     * Remote session flag.
     */
    private Boolean IS_REMOTE = false;
    
    /**
     * Default constructor.
     * @param upperThread given session thread to link with;
     * @since RibbonServer a1
     */
    RibbonProtocol(SessionManager.SessionThread upperThread) {
        InitProtocol();
        CURR_SESSION = upperThread;
    }
    
    /**
     * Link to upper level thread
     * @since RibbonServer a1
     */
    private SessionManager.SessionThread CURR_SESSION;
    
    /**
     * Protocol revision digit.
     * @since RibbonServer a1
     */
    private Integer INT_VERSION = 2;
    
    /**
     * String protocol revision version.
     * @since RibbonServer a1
     */
    private String STR_VERSION = RibbonServer.RIBBON_MAJOR_VER;
    
    /**
     * Connection type enumeration.
     * @since RibbonServer a1
     */
    public enum CONNECTION_TYPES {
        
        /**
         * NULL connection: peer didn't call RIBBON_NCTL_INIT: yet.
         */
        NULL,
        
        /**
         * CLIENT connection for all user applications.
         */
        CLIENT,
        
        /**
         * CONTROL connection for all adm applications.
         */
        CONTROL,
        
        /**
         * Connection for any application.
         */
        ANY
    };
    
    /**
     * Current type of connection.
     * @since RibbonServer a1
     */
    public CONNECTION_TYPES CURR_TYPE = CONNECTION_TYPES.NULL;
    
    /**
     * ArrayList of commands objects.
     * @since RibbonServer a1
     */
    private java.util.ArrayList<CommandLet> RIBBON_COMMANDS = new java.util.ArrayList<CommandLet>();
    
    /**
     * Command template class.
     * @since RibbonServer a1
     */
    private class CommandLet {
        
        /**
         * Default constroctor.
         * @param givenName name of command;
         * @param givenType type of connections which may use this command;
         * @since RibbonServer a1
         */
        CommandLet(String givenName, CONNECTION_TYPES givenType) {
            this.COMMAND_NAME = givenName;
            this.COMM_TYPE = givenType;
        }
        
        /**
         * Name of command.
         * @since RibbonServer a1
         */
        public String COMMAND_NAME;
        
        /**
         * Type of connections which may use this command.
         * @since RibbonServer a1
         */
        public CONNECTION_TYPES COMM_TYPE;
        
        /**
         * Main command body.
         * @param args arguments from application <i>(may be in CSV format)</i>;
         * @return command answer;
         * @since RibbonServer a1
         */
        public String exec(String args) {return "";};
        
    }
    
    /**
     * Init protocol and load commands.
     * @since RibbonServer a1
     */
    private void InitProtocol() {
        
        /** CONNECTION CONTROL COMMANDS [LEVEL_0 SUPPORT] **/
        
        /**
         * RIBBON_NCTL_INIT: commandlet
         * Client and others application send this command to register
         * this connection.
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_NCTL_INIT", CONNECTION_TYPES.NULL) {
            @Override
            public String exec(String args) {
                String[] parsedArgs = args.split(",");
                if (CURR_TYPE == CONNECTION_TYPES.NULL) {
                    if (parsedArgs[1].equals(STR_VERSION)) {
                        try {
                            if (parsedArgs[0].equals("ANY") || parsedArgs[0].equals("NULL")) {
                                throw new IllegalArgumentException();
                            }
                            CURR_TYPE = CONNECTION_TYPES.valueOf(parsedArgs[0]);
                            if (!parsedArgs[2].equals(System.getProperty("file.encoding"))) {
                                LOGGER.warn("Netwrok session require other codepage:" + parsedArgs[2]);
                                CURR_SESSION.setReaderEncoding(parsedArgs[2]);
                            }
                            return "OK:";
                        } catch (IllegalArgumentException ex) {
                            return "RIBBON_ERROR:Unknown connection type!";
                        }
                    } else {
                        return "RIBBON_ERROR:Unknown protocol id.";
                    }
                } else {
                    return "RIBBON_WARNING:Connection already initiated!";
                }
            }
        });
        
        /**
         * RIBBON_NCTL_LOGIN: commandlet
         * Client and other applications send this command to login user.
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_NCTL_LOGIN", CONNECTION_TYPES.ANY) {
          @Override
          public String exec(String args) {
              String[] parsedArgs = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.commonParseLine(args, 2);
              if (!RibbonServer.ACCESS_ALLOW_MULTIPLIE_LOGIN && SessionManager.isAlreadyLogined(parsedArgs[0])) {
                  return "RIBBON_ERROR:User " + parsedArgs[0] + " already logined!";
              }
              if (CURR_TYPE == CONNECTION_TYPES.CONTROL && (!AccessHandler.isUserIsMemberOf(parsedArgs[0], "ADM"))) {
                  return "RIBBON_ERROR:User " + parsedArgs[0] + " is not an admin.";
              }
              String returned = AccessHandler.PROC_LOGIN_USER(parsedArgs[0], parsedArgs[1]);
              if (returned == null) {
                  if (CURR_TYPE == CONNECTION_TYPES.CLIENT) {
                      LOGGER.info("user " + parsedArgs[0] + " logined to the system.");
                  } else if (CURR_TYPE == CONNECTION_TYPES.CONTROL) {
                      LOGGER.info("admin " + parsedArgs[0] + " logined to the system.");
                      if (RibbonServer.CONTROL_IS_PRESENT == false) {
                          LOGGER.info("system control initiated!");
                          RibbonServer.CONTROL_IS_PRESENT = true;
                      }
                  }
                  CURR_SESSION.USER_NAME = parsedArgs[0];
                  if (RibbonServer.ACCESS_ALLOW_SESSIONS) {
                      CURR_SESSION.CURR_ENTRY = SessionManager.createSessionEntry(parsedArgs[0]);
                      CURR_SESSION.setSessionName();
                      return "OK:" + CURR_SESSION.CURR_ENTRY.SESSION_HASH_ID;
                  } else {
                      return "OK:";
                  }
              } else {
                  return "RIBBON_ERROR:" + returned;
              }
          }
        });
        
        /**
         * RIBBON_NCTL_GET_ID: commandlet
         * Find out session ID.
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_NCTL_GET_ID", CONNECTION_TYPES.ANY) {
            @Override
            public String exec(String args) {
                if (!RibbonServer.ACCESS_ALLOW_SESSIONS) {
                    return "RIBBON_ERROR:Sessions disabled!";
                } else if (CURR_SESSION.CURR_ENTRY == null) {
                    return "RIBBON_ERROR:Login required!";
                } else {
                    return CURR_SESSION.CURR_ENTRY.SESSION_HASH_ID;
                }
            }
        });
        
        /**
         * RIBBON_NCTL_RESUME: commandlet
         * Resume session by given hash id.
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_NCTL_RESUME", CONNECTION_TYPES.ANY) {
            @Override
            public String exec(String args) {
                if (!RibbonServer.ACCESS_ALLOW_SESSIONS) {
                    return "RIBBON_ERROR:Sessions disabled!";
                }
                SessionManager.SessionEntry exicted = SessionManager.getUserBySessionEntry(args);
                if (exicted == null) {
                    return "RIBBON_ERROR:Session not found!";
                } else {
                    String returned = AccessHandler.PROC_RESUME_USER(exicted);
                    if (returned == null) {
                        SessionManager.reniewEntry(exicted);
                          CURR_SESSION.USER_NAME = exicted.SESSION_USER_NAME;
                          CURR_SESSION.CURR_ENTRY = exicted;
                          CURR_SESSION.setSessionName();
                        return "OK:";
                    } else {
                        return "RIBBON_ERROR:" + returned;
                    }
                }
            }
        });
        
        /**
         * RIBBON_NCTL_REM_LOGIN: commandlet
         * Remote login command.
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_NCTL_REM_LOGIN", CONNECTION_TYPES.ANY) {
            @Override
            public String exec(String args) {
                if (!RibbonServer.ACCESS_ALLOW_REMOTE && !IS_REMOTE) {
                    return "RIBBON_ERROR:Remote mode disabled!";
                } else if (CURR_SESSION.USER_NAME == null) {
                    return "RIBBON_ERROR:Login required!";
                }
                String[] parsedArgs = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.commonParseLine(args, 2);
                if (CURR_TYPE == CONNECTION_TYPES.CONTROL && (!AccessHandler.isUserIsMemberOf(parsedArgs[0], "ADM"))) {
                    return "RIBBON_ERROR:User " + parsedArgs[0] + " is not an admin.";
                }
                String returned = AccessHandler.PROC_LOGIN_USER(parsedArgs[0], parsedArgs[1]);
                if (returned == null) {
                    if (CURR_TYPE == CONNECTION_TYPES.CLIENT) {
                        LOGGER.info("user " + parsedArgs[0] + " logined to the system by remote protocol.");
                    } else if (CURR_TYPE == CONNECTION_TYPES.CONTROL) {
                        LOGGER.info("admin " + parsedArgs[0] + " logined to the system by remote protocol.");
                    }
                    return "OK:";
                } else {
                    return "RIBBON_ERROR:" + returned;
                }
            }
        });
        
        /**
         * RIBBON_NCTL_GET_USERNAME: commandlet
         * Get current session username.
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_NCTL_GET_USERNAME", CONNECTION_TYPES.ANY) {
            public String exec(String args) {
                if (CURR_SESSION.USER_NAME != null) {
                    tk.freaxsoftware.ukrinform.ribbon.lib.data.user.User curr = AccessHandler.getEntryByName(CURR_SESSION.USER_NAME);
                    return "OK:{" + curr.getLogin() + "},{" + curr.getDescription() + "}," + tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.renderGroup(curr.getGroups().getKeys().toArray(new String[curr.getGroups().getKeys().size()]));
                } else {
                    return "RIBBON_ERROR:Login required!";
                }
            }
        });
        
        /**
         * RIBBON_NCTL_SET_REMOTE_MODE: commandlet
         * Set remote flag of this session.
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_NCTL_SET_REMOTE_MODE", CONNECTION_TYPES.ANY) {
            @Override
            public String exec(String args) {
                if (CURR_SESSION.USER_NAME == null) {
                    return "RIBBON_ERROR:Login required!";
                } else if (!RibbonServer.ACCESS_ALLOW_REMOTE) {
                    return "RIBBON_ERROR:Remote mode disabled!";
                } else if (!AccessHandler.isUserIsMemberOf(CURR_SESSION.USER_NAME, RibbonServer.ACCESS_REMOTE_GROUP)) {
                    return "RIBBON_ERROR:This session can't use remote mode!";
                }
                IS_REMOTE = "1".equals(args) ? true : false;
                if (IS_REMOTE) {
                    LOGGER.info("remote mode enabled (" + CURR_SESSION.SESSION_TIP + ")");
                } else {
                    LOGGER.info("remote mode disabled (" + CURR_SESSION.SESSION_TIP + ")");
                }
                return "OK:" + (IS_REMOTE ? "1" : "0");
            }
        });
        
        /**
         * RIBBON_NCTL_ACCESS_CONTEXT: commandlet
         * Change access mode of next command.
         * WARNING! this commandlet grab socket control!
         * WARNING! this commandlet calls to process() method directly!
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_NCTL_ACCESS_CONTEXT", CONNECTION_TYPES.ANY) {
            @Override
            public String exec(String args) {
                if (CURR_SESSION.USER_NAME == null) {
                    return "RIBBON_ERROR:Login required!";
                } else if (!IS_REMOTE) {
                    return "RIBBON_ERROR:Remote mode disabled!";
                }
                User overUser = AccessHandler.getEntryByName(CsvFormat.commonParseLine(args, 1)[0]);
                if (overUser == null) {
                    return "RIBBON_ERROR:User not found!";
                } else if (!overUser.isEnabled()) {
                    return "RIBBON_ERROR:User disabled!";
                }
                String oldUserName = CURR_SESSION.USER_NAME;
                CURR_SESSION.USER_NAME = overUser.getLogin();
                CURR_SESSION.printLnToPeer("PROCEED:");
                String subResult = null;
                try {
                    subResult = process(CURR_SESSION.inStream.readLine());
                } catch (java.io.IOException ex) {
                    LOGGER.error("unable to read data from socket!", ex);
                    SessionManager.closeSession(CURR_SESSION);
                }
                CURR_SESSION.USER_NAME = oldUserName;
                return subResult;
            }
        });
        
        /**
         * RIBBON_NCTL_CLOSE: commandlet
         * Exit command to close connection.
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_NCTL_CLOSE", CONNECTION_TYPES.ANY) {
            @Override
            public String exec(String args) {
                if (CURR_TYPE == CONNECTION_TYPES.CONTROL && SessionManager.hasOtherControl(CURR_SESSION) == false) {
                    LOGGER.warn("control detached!");
                    RibbonServer.CONTROL_IS_PRESENT = false;
                }
                return "COMMIT_CLOSE:";
            }
        });
        
        /** GENERAL PROTOCOL STACK [LEVEL_1 SUPPORT] **/
        
        /**
         * RIBBON_GET_DIRS: commandlet
         * Return all dirs to client in csv form.
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_GET_DIRS", CONNECTION_TYPES.CLIENT) {
            @Override
            public String exec(String args) {
                return Directories.PROC_GET_DIRS();
            }
        });
        
        /**
         * RIBBON_GET_PSEUDO: commandlet
         * Return csv list of pseudo directories which user may use.
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_GET_PSEUDO", CONNECTION_TYPES.CLIENT) {
            @Override
            public String exec(String args) {
                if (IS_REMOTE) {
                    if (CURR_SESSION.USER_NAME == null) {
                        return "RIBBON_ERROR:Login required!";
                    }
                    return Directories.PROC_GET_PSEUDO(CURR_SESSION.USER_NAME);
                } else {
                    return "RIBBON_ERROR:Remote mode disabled!";
                }
            }
        });
        
        /**
         * RIBBON_GET_TAGS: commandlet
         * Return all tags to client in csv form.
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_GET_TAGS", CONNECTION_TYPES.CLIENT) {
            @Override
            public String exec(String args) {
                return Messenger.PROC_GET_TAGS();
            }
        });
        
        /**
         * RIBBON_LOAD_BASE_FROM_INDEX: commandlet
         * Return all messages which were released later than specified index.
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_LOAD_BASE_FROM_INDEX", CONNECTION_TYPES.CLIENT) {
            @Override
            public String exec(String args) {
                return Messenger.PROC_LOAD_BASE_FROM_INDEX(args);
            }
        });
        
        /**
         * RIBBON_POST_MESSAGE: commandlet
         * Post message to the system.
         * WARNING! this commandlet grab socket control!
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_POST_MESSAGE", CONNECTION_TYPES.CLIENT) {
            @Override
            public String exec(String args) {
                tk.freaxsoftware.ukrinform.ribbon.lib.data.message.Message recievedMessage = new tk.freaxsoftware.ukrinform.ribbon.lib.data.message.Message();
                recievedMessage.createMessageForPost(args);
                recievedMessage.setAuthor(CURR_SESSION.USER_NAME);
                Boolean collectMessage = true;
                StringBuffer messageBuffer = new StringBuffer();
                String inLine;
                while (collectMessage) {
                    try {
                        inLine = CURR_SESSION.inStream.readLine();
                        if (!inLine.equals("END:")) {
                            messageBuffer.append(inLine);
                            messageBuffer.append("\n");
                        } else {
                            collectMessage = false;
                        }
                    } catch (java.io.IOException ex) {
                        LOGGER.error("Unable to read data from socket", ex);
                        return "RIBBON_ERROR:Unable to read data from socket!";
                    }
                }
                recievedMessage.setContent(messageBuffer.toString());
                String answer = Procedures.PROC_POST_MESSAGE(recievedMessage);
                if (answer.equals("OK:")) {
                    BROADCAST_TAIL = "RIBBON_UCTL_LOAD_INDEX:" + recievedMessage.returnEntry().toCsv();
                    BROADCAST_TYPE = CONNECTION_TYPES.CLIENT;
                }
                return answer;
            }
        });
        
        /**
         * RIBBON_POST_MESSAGE_BY_PSEUDO: commandlet
         * Post message from remote interface to the system by using pseudo directory.
         * WARNING! this commandlet grab socket control!
         * WARNING! this commandlet calls to RIBBON_POST_MESSAGE commandlet
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_POST_MESSAGE_BY_PSEUDO", CONNECTION_TYPES.CLIENT) {
            @Override
            public String exec(String args) {
                if (IS_REMOTE) {
                    java.util.ArrayList<String[]> parsed = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.complexParseLine(args, 4, 1);
                    Directories.PseudoDirEntry currPostPseudo = Directories.getPseudoDir(parsed.get(0)[0]);
                    if (currPostPseudo == null) {
                        return "RIBBON_ERROR:Pseudo directory " + parsed.get(0)[0] + " doesn't exist.";
                    }
                    String[] postDirs = currPostPseudo.getinternalDirectories();
                    String commandToPost = "RIBBON_POST_MESSAGE:-1," + tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.renderGroup(postDirs) + args.substring(currPostPseudo.getName().length() + 2);
                    LOGGER.info("posted message through pseudo directory '" + currPostPseudo.getName() + "'");
                    return process(commandToPost);
                } else {
                    return "RIBBON_ERROR:Remote mode disabled!";
                }
            }
        });
        
        /**
         * RIBBON_GET_MESSAGE: commandlet
         * Retrieve message body.
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_GET_MESSAGE", CONNECTION_TYPES.CLIENT) {
            @Override
            public String exec(String args) {
                String[] parsedArgs = args.split(",");
                String givenDir = parsedArgs[0];
                String givenIndex = parsedArgs[1];
                StringBuffer returnedMessage = new StringBuffer();
                if (AccessHandler.checkAccess(CURR_SESSION.USER_NAME, givenDir, 0) == false) {
                    return "RIBBON_ERROR:Directory access error for " + givenDir;
                }
                String dirPath = Directories.getDirPath(givenDir);
                if (dirPath == null) {
                    return "RIBBON_ERROR:Directory " + givenDir + " doesn't exist!";
                } else {
                    try {
                        java.io.BufferedReader messageReader = new java.io.BufferedReader(new java.io.FileReader(dirPath + givenIndex));
                        while (messageReader.ready()) {
                            returnedMessage.append(messageReader.readLine());
                            returnedMessage.append("\n");
                        }
                        return returnedMessage.append("END:").toString();
                    } catch (java.io.FileNotFoundException ex) {
                        return "RIBBON_ERROR:Message not found!";
                    } catch (java.io.IOException ex) {
                        LOGGER.error("message reading error " + givenDir + ":" + givenIndex, ex);
                        return "RIBBON_ERROR:Command execution error!";
                    }
                }
            }
        });
        
        /**
         * RIBBON_MODIFY_MESSAGE: commandlet
         * Modify text of existing message.
         * WARNING! this commandlet grab socket control!
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_MODIFY_MESSAGE", CONNECTION_TYPES.CLIENT) {
            @Override
            public String exec(String args) {
                StringBuffer messageBuffer = new StringBuffer();
                String inLine;
                Boolean collectMessage = true;
                String[] parsedArgs = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.splitCsv(args);
                tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageEntry matchedEntry = Messenger.getMessageEntryByIndex(parsedArgs[0]);
                tk.freaxsoftware.ukrinform.ribbon.lib.data.message.Message modTemplate = new tk.freaxsoftware.ukrinform.ribbon.lib.data.message.Message();
                modTemplate.createMessageForModify(parsedArgs[1]);
                while (collectMessage) {
                    try {
                        inLine = CURR_SESSION.inStream.readLine();
                        if (!inLine.equals("END:")) {
                            messageBuffer.append(inLine);
                            messageBuffer.append("\n");
                        } else {
                            collectMessage = false;
                        }
                    } catch (java.io.IOException ex) {
                        LOGGER.error("Unable to read data from socket!", ex);
                        return "RIBBON_ERROR:Unable to read data from socket!";
                    }
                }
                modTemplate.setContent(messageBuffer.toString());
                if (matchedEntry == null) {
                    return "RIBBON_ERROR:Message doesn't exist!";
                }
                Integer oldIntFlag = AccessHandler.checkAccessForAll(CURR_SESSION.USER_NAME, matchedEntry.getDirectories(), 2);
                Integer newIntFlag = AccessHandler.checkAccessForAll(CURR_SESSION.USER_NAME, modTemplate.getDirectories(), 1);
                if ((CURR_SESSION.USER_NAME.equals(matchedEntry.getAuthor()) && (newIntFlag == null)) || ((oldIntFlag == null) && (newIntFlag == null))) {
                    for (Integer dirIndex = 0; dirIndex < matchedEntry.getDirectories().length; dirIndex++) {
                        if (AccessHandler.checkAccess(CURR_SESSION.USER_NAME, matchedEntry.getDirectories()[dirIndex], 1) == true) {
                            continue;
                        } else {
                            return "RIBBON_ERROR:Directory access error for" + matchedEntry.getDirectories()[dirIndex] +  ".";
                        }
                    }
                    Procedures.PROC_MODIFY_MESSAGE(matchedEntry, modTemplate);
                    BROADCAST_TAIL = "RIBBON_UCTL_UPDATE_INDEX:" + matchedEntry.toCsv();
                    BROADCAST_TYPE = CONNECTION_TYPES.CLIENT;
                    return "OK:";
                } else {
                    if (oldIntFlag != null) {
                        return "RIBBON_ERROR:Directory access error for " + matchedEntry.getDirectories()[oldIntFlag] +  ".";
                    } else {
                        return "RIBBON_ERROR:Directory access error for " + modTemplate.getDirectories()[newIntFlag] +  ".";
                    }
                }
            }
        });
        
        /**
         * RIBBON_MODIFY_MESSAGE_BY_PSEUDO: commandlet
         * Modify existing message from remote interface to the system by using pseudo directory.
         * WARNING! this commandlet grab socket control!
         * WARNING! this commandlet calls to RIBBON_MODIFY_MESSAGE commandlet
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_MODIFY_MESSAGE_BY_PSEUDO", CONNECTION_TYPES.CLIENT) {
            @Override
            public String exec(String args) {
                if (IS_REMOTE) {
                    java.util.ArrayList<String[]> parsed = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.complexParseLine(args, 5, 1);
                    Directories.PseudoDirEntry currPostPseudo = Directories.getPseudoDir(parsed.get(0)[1]);
                    if (currPostPseudo == null) {
                        return "RIBBON_ERROR:Pseudo directory " + parsed.get(0)[1] + " doesn't exist.";
                    }
                    String[] postDirs = currPostPseudo.getinternalDirectories();
                    String commandToPost = "RIBBON_MODIFY_MESSAGE:" + parsed.get(0)[0] + "," + tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.renderGroup(postDirs) + args.substring(currPostPseudo.getName().length() + 13);
                    return process(commandToPost);
                } else {
                    return "RIBBON_ERROR:Remote mode disabled!";
                }
            }
        });
        
        /**
         * RIBBON_DELETE_MESSAGE: commandlet
         * Delete message from all directories.
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_DELETE_MESSAGE", CONNECTION_TYPES.CLIENT) {
            @Override
            public String exec(String args) {
                tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageEntry matchedEntry = Messenger.getMessageEntryByIndex(args);
                if (matchedEntry == null) {
                    return "RIBBON_ERROR:Message doesn't exist!";
                } else {
                    if (matchedEntry.getAuthor().equals(CURR_SESSION.USER_NAME) || (AccessHandler.checkAccessForAll(CURR_SESSION.USER_NAME, matchedEntry.getDirectories(), 2) == null)) {
                        Procedures.PROC_DELETE_MESSAGE(matchedEntry);
                        BROADCAST_TAIL = "RIBBON_UCTL_DELETE_INDEX:" + matchedEntry.getIndex();
                        BROADCAST_TYPE = CONNECTION_TYPES.CLIENT;
                        return "OK:";
                    } else {
                        return "RIBBON_ERROR:Message access error.";
                    }
                }
            }
        });
        
        /**
         * RIBBON_ADD_MESSAGE_PROPERTY: commandlet
         * Add custom property to message.
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_ADD_MESSAGE_PROPERTY", CONNECTION_TYPES.CLIENT) {
            @Override
            public String exec(String args) {
                String[] parsedArgs = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.commonParseLine(args, 3);
                tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageEntry matchedEntry = Messenger.getMessageEntryByIndex(parsedArgs[0]);
                if (matchedEntry == null) {
                    return "RIBBON_ERROR:Message doesn't exist!";
                }
                if ((matchedEntry.getAuthor().equals(CURR_SESSION.USER_NAME) || (AccessHandler.checkAccessForAll(CURR_SESSION.USER_NAME, matchedEntry.getDirectories(), 2) != null))) {
                    tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageProperty newProp = new tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageProperty(parsedArgs[1], CURR_SESSION.USER_NAME, parsedArgs[2]);
                    newProp.setType(parsedArgs[1]);
                    newProp.setDescription(parsedArgs[2]);
                    newProp.setDate(RibbonServer.getCurrentDate());
                    newProp.setUser(CURR_SESSION.USER_NAME);
                    matchedEntry.getProperties().add(newProp);
                    IndexReader.updateBaseIndex();
                    BROADCAST_TAIL = "RIBBON_UCTL_UPDATE_INDEX:" + matchedEntry.toCsv();
                    BROADCAST_TYPE = CONNECTION_TYPES.CLIENT;
                    return "OK:";
                } else {
                    return "RIBBON_ERROR:Message access error.";
                }
            }
        });
        
        /**
         * RIBBON_DEL_MESSAGE_PROPERTY: commandlet
         * Del custom property from specified message.
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_DEL_MESSAGE_PROPERTY", CONNECTION_TYPES.CLIENT) {
            @Override
            public String exec(String args) {
                String[] parsedArgs = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.commonParseLine(args, 3);
                tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageEntry matchedEntry = Messenger.getMessageEntryByIndex(parsedArgs[0]);
                if (matchedEntry == null) {
                    return "RIBBON_ERROR:Message doesn't exist!";
                }
                if ((matchedEntry.getAuthor().equals(CURR_SESSION.USER_NAME) || (AccessHandler.checkAccessForAll(CURR_SESSION.USER_NAME, matchedEntry.getDirectories(), 2) != null))) {
                    tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageProperty findedProp = null;
                    java.util.ListIterator<tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageProperty> propIter = matchedEntry.getProperties().listIterator();
                    while (propIter.hasNext()) {
                        tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageProperty currProp = propIter.next();
                        if (currProp.getType().equals(parsedArgs[1]) && currProp.getDate().equals(parsedArgs[2])) {
                            findedProp = currProp;
                            break;
                        }
                    }
                    if (findedProp != null) {
                        matchedEntry.getProperties().remove(findedProp);
                        IndexReader.updateBaseIndex();
                        BROADCAST_TAIL = "RIBBON_UCTL_UPDATE_INDEX:" + matchedEntry.toCsv();
                        BROADCAST_TYPE = CONNECTION_TYPES.CLIENT;
                        return "OK:";
                    } else {
                        return "RIBBON_ERROR:Message property doesn't exist";
                    }
                } else {
                    return "RIBBON_ERROR:Message access error.";
                }
            }
        });
        
        /**
         * RIBBON_GET_USERS: commandlet
         * Get all system users without ADM group members.
         */
        this.RIBBON_COMMANDS.add(new CommandLet("RIBBON_GET_USERS", CONNECTION_TYPES.CLIENT) {
            @Override
            public String exec(String args) {
                return AccessHandler.PROC_GET_USERS_UNI(false);
            }
        });
        
        /** SERVER CONTROL PROTOCOL STACK [LEVEL_2 SUPPORT] **/
        
    }
    
    /**
     * Process input from session socket and return answer;
     * @param input input line from client
     * @return answer form protocol to client
     * @since RibbonServer a1
     */
    public String process(String input) {
        String[] parsed = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.parseDoubleStruct(input);
        return this.launchCommand(parsed[0], parsed[1]);
    }
    
    /**
     * Launch command execution
     * @param command command word
     * @param args command's arguments
     * @return return form commandlet object
     * @since RibbonServer a1
     */
    private String launchCommand(String command, String args) {
        CommandLet exComm = null;
        java.util.ListIterator<CommandLet> commIter = this.RIBBON_COMMANDS.listIterator();
        while (commIter.hasNext()) {
            CommandLet currComm = commIter.next();
            if (currComm.COMMAND_NAME.equals(command)) {
                if (currComm.COMM_TYPE == this.CURR_TYPE || (currComm.COMM_TYPE == CONNECTION_TYPES.ANY && this.CURR_TYPE != CONNECTION_TYPES.NULL) || this.CURR_TYPE == CONNECTION_TYPES.CONTROL) {
                    if (this.CURR_SESSION.USER_NAME == null && (currComm.COMM_TYPE == CONNECTION_TYPES.CLIENT || currComm.COMM_TYPE == CONNECTION_TYPES.CONTROL)) {
                        return "RIBBON_ERROR:Login required!";
                    } else {
                        exComm = currComm;
                    }
                    break;
                } else {
                    return "RIBBON_ERROR:This command can't be exucuted by this sesion.";
                }
            }
        }
        if (exComm != null) {
            try {
                return exComm.exec(args);
            } catch (Exception ex) {
                Procedures.postException("Session error " + this.CURR_SESSION.SESSION_TIP
                        + "\nCommand: " + command + ":" + args + "\n\n", ex);
                /**
                if (RibbonServer.DEBUG_POST_EXCEPTIONS) {
                    StringBuffer exMesgBuf = new StringBuffer();
                    exMesgBuf.append("Помилка при роботі сесії ").append(this.CURR_SESSION.SESSION_TIP).append("(").append(RibbonServer.getCurrentDate()).append(")\n\n");
                    exMesgBuf.append("Команда:" + command + ":" + args + "\n\n");
                    exMesgBuf.append(ex.getClass().getName() + "\n");
                    StackTraceElement[] stackTrace = ex.getStackTrace();
                    for (StackTraceElement element : stackTrace) {
                        exMesgBuf.append(element.toString() + "\n");
                    }
                    MessageClasses.Message exMessage = new MessageClasses.Message(
                            "Звіт про помилку", "root", "UA", new String[] {RibbonServer.DEBUG_POST_DIR}, 
                            new String[] {"ІТУ", "ПОМИЛКИ"}, exMesgBuf.toString());
                    Procedures.PROC_POST_MESSAGE(exMessage);
                    BROADCAST_TAIL = "RIBBON_UCTL_LOAD_INDEX:" + exMessage.returnEntry().toCsv();
                    BROADCAST_TYPE = CONNECTION_TYPES.CLIENT;
                }
                RibbonServer.logAppend(LOG_ID, 1, "помилка при виконанні команди " + exComm.COMMAND_NAME + "!");
                **/
                return "RIBBON_ERROR:Command error:" + ex.toString();
            }
        } else {
            return "RIBBON_ERROR:Unknown command!";
        }
    }
}
