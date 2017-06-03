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

package tk.freaxsoftware.ukrinform.ribbon.server.web.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import tk.freaxsoftware.ukrinform.ribbon.lib.data.user.User;
import tk.freaxsoftware.ukrinform.ribbon.server.RibbonServer;

/**
 * JWT tokenizer.
 * @author Stanislav Nepochatov
 */
public class JWTTokenService {
    
    private static JWTTokenService instance;
    
    /**
     * Security key for JWT signing.
     */
    private final Key jwtKey;
    
    /**
     * Valid hours of JWT token.
     */
    private final Integer validHours;
    
    /**
     * Private controller.
     * @param secret JWT secret;
     * @param validHours valid hours value;
     */
    private JWTTokenService(String secret, Integer validHours) {
        byte[] jwtSecret = DatatypeConverter.parseBase64Binary(DatatypeConverter.printBase64Binary(secret.getBytes()));
        jwtKey = new SecretKeySpec(jwtSecret, SignatureAlgorithm.HS256.getJcaName());
        this.validHours = validHours;
    }
    
    /**
     * Encrypt token for certain user.
     * @param user given user;
     * @return encrypted token;
     */
    public String encryptToken(User user) {
        return Jwts.builder().setId(user.getLogin()).setExpiration(Date.from(ZonedDateTime.now().plusHours(validHours).toInstant())).signWith(SignatureAlgorithm.HS256, jwtKey).compact();
    }
    
    /**
     * Decrypt token from string to claims instance. May throws unchecked exceptions.
     * @param tokenValue raw token value;
     * @return claims instance;
     */
    public Claims decryptToken(String tokenValue) {
        return Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(tokenValue).getBody();
    }
    
    /**
     * Get instance of JWT token service.
     * @return initiated service instance;
     */
    public static JWTTokenService getInstance() {
        if (instance == null) {
            instance = new JWTTokenService(RibbonServer.HTTP_JWT_TOKEN_SECRET, RibbonServer.HTTP_JWT_TOKEN_HOURS);
        }
        return instance;
    }
    
}
