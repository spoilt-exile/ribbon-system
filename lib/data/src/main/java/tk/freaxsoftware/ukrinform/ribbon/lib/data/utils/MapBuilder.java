/**
 * This file is part of libRibbonData library (check README).
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

package tk.freaxsoftware.ukrinform.ribbon.lib.data.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Map builder class.
 * @author Stanislav Nepochatov
 */
public class MapBuilder<K, V> {
    
    private final Map<K, V> map = new HashMap<>();
    
    public MapBuilder<K,V> put(K argKey, V argValue) {
        this.map.put(argKey, argValue);
        return this;
    }
    
    public Map<K, V> map() {
        return this.map;
    }
    
    public static final <K,V> MapBuilder newInstance(Class<K> keyClass, Class<V> valueClass) {
        return new MapBuilder<>();
    }
    
}
