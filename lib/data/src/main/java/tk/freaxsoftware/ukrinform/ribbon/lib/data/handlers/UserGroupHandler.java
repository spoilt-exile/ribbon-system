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
import tk.freaxsoftware.ukrinform.ribbon.lib.data.user.UserGroup;

/**
 * User group entity handler.
 * @author Stanislav Nepochatov
 */
public class UserGroupHandler extends AbstractEntityHandler<UserGroup, String> {

    public UserGroupHandler(String filePath) {
        super(UserGroup.class, UserGroup.DEFINITION, filePath);
    }

    @Override
    public String getNewKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<UserGroup> find(String query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onStorageCreation() {
        create(new UserGroup("ADM", "Admin group", Collections.EMPTY_MAP));
        create(new UserGroup("test", "Test group", Collections.EMPTY_MAP));
    }

    @Override
    public UserGroup getNewEntity() {
        return new UserGroup();
    }

    @Override
    public String getType() {
        return User.TYPE;
    }
    
}
