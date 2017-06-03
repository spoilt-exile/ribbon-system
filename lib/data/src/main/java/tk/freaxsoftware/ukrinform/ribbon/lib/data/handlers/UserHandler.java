/**
 * This file is part of libRibbonData library (check README).
 * Copyright (C) 2012-2016 Stanislav Nepochatov
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

package tk.freaxsoftware.ukrinform.ribbon.lib.data.handlers;

import java.util.Collections;
import java.util.List;
import tk.freaxsoftware.extras.faststorage.storage.AbstractEntityHandler;
import tk.freaxsoftware.ukrinform.ribbon.lib.data.user.User;

/**
 * Handler for user entity.
 * @author Stanislav Nepochatov
 */
public class UserHandler extends AbstractEntityHandler<User, String>{

    public UserHandler(String filePath) {
        super(User.class, User.DEFINITION, filePath);
    }

    @Override
    public String getNewKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<User> find(String query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onStorageCreation() {
        create(new User("root", "63a9f0ea7bb98050796b649e85481845", "Root administrator, pass: root", new String[] {"ADM"}, true, Collections.EMPTY_MAP));
        create(new User("test", "098f6bcd4621d373cade4e832627b4f6", "Test user, pass: test", new String[] {"test"}, true, Collections.EMPTY_MAP));
    }

    @Override
    public User getNewEntity() {
        return new User();
    }

    @Override
    public String getType() {
        return User.TYPE;
    }
    
    /**
     * Gets user instance by login.
     * @param login
     * @return 
     */
    public User getUserByLogin(String login) {
        synchronized(entitiesLock) {
            for (User entity: entitiesStore) {
                if (entity.getLogin().equals(login)) {
                    return entity;
                }
            }
        }
        return null;
    }

}
