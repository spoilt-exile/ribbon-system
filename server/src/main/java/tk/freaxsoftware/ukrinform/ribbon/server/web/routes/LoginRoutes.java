/**
 * This file is part of RibbonServer application (check README).
 * Copyright (C) 2017 Stanislav Nepochatov
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

package tk.freaxsoftware.ukrinform.ribbon.server.web.routes;

import io.jsonwebtoken.Claims;
import java.net.HttpURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.QueryParamsMap;
import spark.Spark;
import static spark.Spark.before;
import static spark.Spark.post;
import tk.freaxsoftware.extras.faststorage.storage.Handlers;
import tk.freaxsoftware.ukrinform.ribbon.lib.data.handlers.UserHandler;
import tk.freaxsoftware.ukrinform.ribbon.lib.data.user.User;
import tk.freaxsoftware.ukrinform.ribbon.server.AccessHandler;
import tk.freaxsoftware.ukrinform.ribbon.server.web.utils.JWTTokenService;
import tk.freaxsoftware.ukrinform.ribbon.server.web.utils.UserHolder;

/**
 * Login routing class;
 * @author Stanislav Nepochatov
 */
public class LoginRoutes {
    
    private static final String TOKEN_NAME = "X-Ribbon-Auth";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginRoutes.class);
    
    private static final UserHandler userHandler = (UserHandler) Handlers.getHandlerByClass(User.class);
    
    public static void init() {
        before("/api/*", (request, response) -> {
            User loginedUser = null;
            if (request.headers().contains(TOKEN_NAME)) {
                try {
                    Claims userClaims = JWTTokenService.getInstance().decryptToken(request.headers(TOKEN_NAME));
                    loginedUser = userHandler.getUserByLogin(userClaims.getId());
                } catch (Exception ex) {
                    LOGGER.error("Unable to finish JWT auth", ex);
                }
            }
            if (loginedUser == null) {
                Spark.halt(HttpURLConnection.HTTP_FORBIDDEN);
            } else {
                UserHolder.setUser(loginedUser);
            }
        });
        
        post("/auth", (request, response) -> {
            QueryParamsMap map = request.queryMap();
            User loginedUser = AccessHandler.loginUser(map.value("login"), map.value("password"));
            if (loginedUser != null) {
                LOGGER.debug("Proceed JWT auth: " + loginedUser.getLogin());
                JWTTokenService tokenService = JWTTokenService.getInstance();
                String token = tokenService.encryptToken(loginedUser);
                return token;
            } else {
                Spark.halt(HttpURLConnection.HTTP_FORBIDDEN);
            }
            return null;
        });
    }
    
}
