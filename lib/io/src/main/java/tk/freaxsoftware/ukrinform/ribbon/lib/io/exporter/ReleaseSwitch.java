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

package tk.freaxsoftware.ukrinform.ribbon.lib.io.exporter;

import java.util.ArrayList;
import java.util.List;
import tk.freaxsoftware.ukrinform.ribbon.lib.io.utils.IOControl;

/**
 * ReleaseSwitch is created for prevent massive concurent 
 * calling to index writer methods. ReleaseSwitch creates for 
 * each exporting message. Only when all exports were done withot 
 * errors then ReleaseSwitch will call index writer update base index.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class ReleaseSwitch {
    
    /**
     * Default constructor.
     * @param givenIndex index of exported message;
     */
    public ReleaseSwitch(String givenIndex) {
        index = givenIndex;
    }
    
    /**
     * Index of exported message.
     */
    private final String index;
    
    /**
     * Array with schemas names.
     */
    private List<String> schemeList = new ArrayList<>();
    
    /**
     * Add schemas into switch.
     * @param givenList list to add;
     */
    public void addSchemas(List givenList) {
        this.schemeList.addAll(givenList);
    }
    
    /**
     * Mark given scheme name as exported or 
     * recieve error.
     * @param givenName name to mark.
     */
    public void markSchema(String givenName) {
        schemeList.remove(givenName);
        if (schemeList.isEmpty()) {
            this.markSwitch();
        }
    }
    
    /**
     * Call to index update method after all schemas export.
     */
    private void markSwitch() {
        IOControl.getInstance().getServerWrapper().updateIndex(index);
    }
}
