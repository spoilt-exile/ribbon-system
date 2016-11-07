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

package tk.freaxsoftware.ukrinform.ribbon.lib.data.message;

import java.util.Arrays;
import java.util.Objects;

/**
 * Message class.
 * Extends messageEntry class with additional field for message content.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 * @see messageEntry
 */
public class Message extends tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageEntry {
    
    /**
     * Message's content
     */
    protected String content;
    
    /**
     * Empty constructor.
     */
    public Message() {
        super();
    }
    
    /**
     * Default constructor from csv line.
     * @param givenCsv 
     */
    public Message(String givenCsv) {
        super(givenCsv);
    }
    
    /**
     * Constructor for internal post procedure;
     * @param givenHeader header of message;
     * @param givenAuthor author of message;
     * @param givenLang language of message;
     * @param givenDirs destination dirs for message;
     * @param givenTags tag marks for message;
     * @param givenContent content of the message;
     */
    public Message (String givenHeader, String givenAuthor, String givenLang, String[] givenDirs, String[] givenTags, String givenContent) {
        header = givenHeader;
        author = givenAuthor;
        previousAuthor = "";
        language = givenLang;
        directories = givenDirs;
        tags = givenTags;
        content = givenContent;
        previousIndex = "-1";
    }
    
    /**
     * Construct Message from Messageentry;
     * @param givenEntry message entry;
     * @param givenContent content for message;
     */
    public Message (MessageEntry givenEntry, String givenContent) {
        index = givenEntry.index;
        previousIndex = givenEntry.previousIndex;
        header = givenEntry.header;
        author = givenEntry.author;
        previousAuthor = givenEntry.previousAuthor;
        language = givenEntry.language;
        directories = givenEntry.directories;
        tags = givenEntry.tags;
        date = givenEntry.date;
        properties = givenEntry.properties;
        content = givenContent;
    }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.index);
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
        final Message other = (Message) obj;
        if (!Objects.equals(this.previousIndex, other.previousIndex)) {
            return false;
        }
        if (!Objects.equals(this.index, other.index)) {
            return false;
        }
        if (!Arrays.deepEquals(this.directories, other.directories)) {
            return false;
        }
        if (!Objects.equals(this.header, other.header)) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.previousAuthor, other.previousAuthor)) {
            return false;
        }
        if (!Objects.equals(this.author, other.author)) {
            return false;
        }
        if (!Arrays.deepEquals(this.tags, other.tags)) {
            return false;
        }
        if (!Objects.equals(this.properties, other.properties)) {
            return false;
        }
        if (!Objects.equals(this.language, other.language)) {
            return false;
        }
        if (!Objects.equals(this.content, other.content)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "Message{" + "previousIndex=" + previousIndex + ", index=" + index + ", directories=" + directories + ", header=" + header + ", date=" + date + ", previousAuthor=" + previousAuthor + ", author=" + author + ", tags=" + tags + ", properties=" + properties + ", language=" + language + '}';
    }
    
    /**
     * Return message entry from Message
     * @return messageEntry object
     */
    public tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageEntry returnEntry() {
        return (tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageEntry) this;
    }


}
