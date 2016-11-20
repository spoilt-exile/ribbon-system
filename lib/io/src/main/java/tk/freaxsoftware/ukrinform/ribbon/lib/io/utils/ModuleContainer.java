/**
 * This file is part of libRibbonIO library (check README).
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

package tk.freaxsoftware.ukrinform.ribbon.lib.io.utils;

import java.util.Objects;

/**
 * Module class container.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class ModuleContainer<E> {
    
    /**
     * Type of import/export module.
     */
    private String moduleType;
    
    /**
     * Property of import/export module.
     */
    private String moduleProperty;
    
    /**
     * Class of import/export module.
     */
    private Class<E> moduleClass;
    
    /**
     * Default constructor.
     * @param givenType type of constructed container;
     * @param givenModule class of container's module;
     */
    public ModuleContainer(String givenType, Class givenModule) {
        moduleType = givenType;
        moduleClass = givenModule;
    }
    
    /**
     * Default constructor.
     * @param givenType type of constructed container;
     * @param givenModule class of container's module;
     */
    public ModuleContainer(String givenType, String givenProp, Class givenModule) {
        moduleType = givenType;
        moduleProperty = givenProp;
        moduleClass = givenModule;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public String getModuleProperty() {
        return moduleProperty;
    }

    public void setModuleProperty(String moduleProperty) {
        this.moduleProperty = moduleProperty;
    }

    public Class<E> getModuleClass() {
        return moduleClass;
    }

    public void setModuleClass(Class<E> moduleClass) {
        this.moduleClass = moduleClass;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.moduleType);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ModuleContainer<?> other = (ModuleContainer<?>) obj;
        if (!Objects.equals(this.moduleType, other.moduleType)) {
            return false;
        }
        if (!Objects.equals(this.moduleProperty, other.moduleProperty)) {
            return false;
        }
        if (!Objects.equals(this.moduleClass, other.moduleClass)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ModuleContainer{" + "moduleType=" + moduleType + ", moduleProperty=" + moduleProperty + ", moduleClass=" + moduleClass + '}';
    }
    
    
}
