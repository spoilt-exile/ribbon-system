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

package tk.freaxsoftware.ukrinform.ribbon.server.web;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;
import tk.freaxsoftware.ukrinform.ribbon.server.Messenger;

/**
 * Web server for HTTP access.
 * @author Stanislav Nepochatov
 */
public class WebServer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);
    
    public static void init(Integer port) {
        LOGGER.info("Starting web server on port {}", port);
        Spark.staticFileLocation("web");
        Spark.port(port);
        Gson gson = new Gson();
        Spark.get("api/message/last", (req,res) -> Messenger.messageIndex.get(Messenger.messageIndex.size() - 1), gson::toJson);
    }
    
}
