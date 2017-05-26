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

import java.io.StringWriter;
import java.util.List;
import tk.freaxsoftware.extras.faststorage.reading.EntityReaderImpl;
import tk.freaxsoftware.extras.faststorage.storage.EntityHandler;
import tk.freaxsoftware.extras.faststorage.writing.EntityWriterImpl;
import tk.freaxsoftware.ukrinform.ribbon.lib.data.directory.DirPermissionEntry;

/**
 * Handler for directory permission entry.
 * @author Stanislav Nepochatiov
 */
public class DirPermissionEntryHandler implements EntityHandler<DirPermissionEntry, String>{

    @Override
    public DirPermissionEntry getNewEntity() {
        return new DirPermissionEntry();
    }

    @Override
    public String getType() {
        return DirPermissionEntry.TYPE;
    }

    @Override
    public DirPermissionEntry readFromString(String rawString) {
        EntityReaderImpl<String> reader = new EntityReaderImpl<>(DirPermissionEntry.DEFINITION);
        reader.parseInit(rawString);
        DirPermissionEntry newEntity = getNewEntity();
        newEntity.readFromECSV(reader);
        return newEntity;
    }

    @Override
    public String writeToString(DirPermissionEntry entity) {
        StringWriter buffer = new StringWriter();
        EntityWriterImpl<String> writer = new EntityWriterImpl<>(DirPermissionEntry.DEFINITION, buffer);
        writer.reset();
        entity.writeToECSV(writer);
        return buffer.toString();
    }

    @Override
    public String getNewKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void create(DirPermissionEntry entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save(DirPermissionEntry entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DirPermissionEntry get(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<DirPermissionEntry> get(List<String> keys) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<DirPermissionEntry> getAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<DirPermissionEntry> find(String query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(DirPermissionEntry entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onStorageCreation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
