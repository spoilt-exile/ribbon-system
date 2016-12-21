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
import tk.freaxsoftware.ukrinform.ribbon.lib.data.directory.DirSchema;
import tk.freaxsoftware.ukrinform.ribbon.lib.data.utils.ListBuilder;
import tk.freaxsoftware.ukrinform.ribbon.lib.data.utils.MapBuilder;

/**
 * Directory entity handler.
 * @author Stanislav Nepochatov
 */
public class DirectoryHandler extends AbstractEntityHandler<DirSchema, String> {

    public DirectoryHandler(String filePath) {
        super(DirSchema.class, DirSchema.DEFINITION, filePath);
    }

    @Override
    public String getNewKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<DirSchema> find(String query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onStorageCreation() {
        create(new DirSchema("СИСТЕМА", "Головний напрямок новин про розробку системи", 
                ListBuilder.newInstance(String.class).add("ALL").list(), 
                MapBuilder.newInstance(String.class, String.class).put("GALL", "100").map(), 
                Collections.EMPTY_LIST));
        create(new DirSchema("СИСТЕМА.Розробка", "Новини про розробку", 
                ListBuilder.newInstance(String.class).add("UA").add("RU").list(), 
                MapBuilder.newInstance(String.class, String.class).put("GALL", "100").map(), 
                Collections.EMPTY_LIST));
        create(new DirSchema("СИСТЕМА.Тест", "Тестовий напрямок", 
                ListBuilder.newInstance(String.class).add("UA").add("RU").list(), 
                MapBuilder.newInstance(String.class, String.class).put("GALL", "110").map(), 
                Collections.EMPTY_LIST));
        create(new DirSchema("СИСТЕМА.Загублене", "Напрямок для загублених повідомлень", 
                ListBuilder.newInstance(String.class).add("ALL").list(), 
                MapBuilder.newInstance(String.class, String.class).put("GALL", "100").map(), 
                Collections.EMPTY_LIST));
        create(new DirSchema("СИСТЕМА.Помилки", "Напрямок журналу помилок системи", 
                ListBuilder.newInstance(String.class).add("ALL").list(), 
                MapBuilder.newInstance(String.class, String.class).put("GALL", "000").map(), 
                Collections.EMPTY_LIST));
    }

    @Override
    public DirSchema getNewEntity() {
        return new DirSchema();
    }

    @Override
    public String getType() {
        return DirSchema.TYPE;
    }
    
}
