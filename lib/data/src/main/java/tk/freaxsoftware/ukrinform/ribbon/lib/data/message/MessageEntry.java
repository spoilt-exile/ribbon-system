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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Message index entry class. 
 * Contains all message fields but it's content.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class MessageEntry extends tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvElder {

    /**
     * Index of the original message
     * @since RibbonServer a2
     */
    protected String previousIndex;
    
    /**
     * Index of message.
     */
    protected String index;

    /**
     * Message's directories.
     */
    protected String[] directories;

    /**
     * Header of message.
     */
    protected String header;

    /**
     * Date of message release.
     */
    protected String date;

    /**
     * Name of the original author. 
     * 
     * <p>Once message appears in the system name of original author 
     * cannot be changed by ordinary user.</p>
     * @since RibbonServer a2
     */
    protected String previousAuthor;
    
    /**
     * Author of message.
     */
    protected String author;

    /**
     * Message's tags.
     */
    protected String[] tags;
    
    /**
     * System properties of this message.
     * @since RibbonServer a2
     */
    protected List<MessageProperty> properties = new ArrayList<>();
    
    /**
     * Message language.
     * @since RibbonServer a2
     */
    protected String language;

    /**
     * Default empty constructor.
     */
    public MessageEntry() {
        this.baseCount = 8;
        this.groupCount = 2;
        this.currentFormat = csvFormatType.ComplexCsv;
    }

    /**
     * Default constructor from csv form.
     * @param givenCsv csv line
     */
    public MessageEntry(String givenCsv) {
        this();
        java.util.ArrayList<String[]> parsedStruct = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.fromCsv(this, givenCsv);
        String[] baseArray = parsedStruct.get(0);
        this.index = baseArray[0];
        this.previousIndex = baseArray[1];
        this.directories = parsedStruct.get(1);
        this.language = baseArray[2];
        this.header = baseArray[3];
        this.date = baseArray[4];
        this.previousAuthor = baseArray[5];
        this.author = baseArray[6];
        this.tags = parsedStruct.get(2);
        String[] rawPropertiesArray = baseArray[7].split("\\$");
        if ((rawPropertiesArray.length > 1) || (!rawPropertiesArray[0].isEmpty())) {
            for (String rawProperty : rawPropertiesArray) {
                this.properties.add(new tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageProperty(rawProperty));
            }
        }
    }

    /**
     * @return the previousIndex
     */
    public String getPreviousIndex() {
        return previousIndex;
    }

    /**
     * @param previousIndex the previousIndex to set
     */
    public void setPreviousIndex(String previousIndex) {
        this.previousIndex = previousIndex;
    }

    /**
     * @return the index
     */
    public String getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * @return the directories
     */
    public String[] getDirectories() {
        return directories;
    }

    /**
     * @param directories the directories to set
     */
    public void setDirectories(String[] directories) {
        this.directories = directories;
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the previousAuthor
     */
    public String getPreviousAuthor() {
        return previousAuthor;
    }

    /**
     * @param previousAuthor the previousAuthor to set
     */
    public void setPreviousAuthor(String previousAuthor) {
        this.previousAuthor = previousAuthor;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the tags
     */
    public String[] getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(String[] tags) {
        this.tags = tags;
    }

    /**
     * @return the properties
     */
    public List<MessageProperty> getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(List<MessageProperty> properties) {
        this.properties = properties;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
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
        final MessageEntry other = (MessageEntry) obj;
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
        return true;
    }

    @Override
    public String toString() {
        return "MessageEntry{" + "previousIndex=" + previousIndex + ", index=" + index + ", directories=" + directories + ", header=" + header + ", date=" + date + ", previousAuthor=" + previousAuthor + ", author=" + author + ", tags=" + tags + ", properties=" + properties + ", language=" + language + '}';
    }
    
    @Override
    public String toCsv() {
        return this.getIndex() + "," + this.getPreviousIndex() + "," + tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.renderGroup(getDirectories()) + "," 
                + this.getLanguage() + ",{" + this.getHeader() + "}," + this.getDate() + ",{" + this.getPreviousAuthor() + "},{" + this.getAuthor() + "}," 
                + tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.renderGroup(getTags()) + "," + tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.renderMessageProperties(getProperties());
    }
    
    /**
     * Create message template for RIBBON_POST_MESSAGE command;<br>
     * <br>
     * CSV format:<br>
     * ORIGINAL_INDEX,[DIR_1,DIR_2],LANG,{HEADER},[TAG_1,TAG_2]
     * @param PostCsv given csv line for post command;
     */
    public void createMessageForPost(String PostCsv) {
        this.baseCount = 4;
        java.util.ArrayList<String[]> parsedStruct = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.fromCsv(this, PostCsv);
        String[] baseArray = parsedStruct.get(0);
        this.setPreviousIndex(baseArray[0]);
        this.setLanguage(baseArray[1]);
        this.setHeader(baseArray[2]);
        String[] rawPropertiesArray = baseArray[3].split("\\$");
        if ((rawPropertiesArray.length > 1) || (!rawPropertiesArray[0].isEmpty())) {
            for (String rawProperty : rawPropertiesArray) {
                this.getProperties().add(new tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageProperty(rawProperty));
            }
        }
        this.setDirectories(parsedStruct.get(1));
        this.setTags(parsedStruct.get(2));
    }
    
    /**
     * Create message template for RIBBON_MODIFY_MESSAGE command;<br>
     * <br>
     * CSV format:<br>
     * [DIR_1,DIR_2],LANG,{HEADER},[TAG_1,TAG_2]
     * @param modCsv given csv line for post command;
     */
    public void createMessageForModify(String modCsv) {
        this.baseCount = 3;
        java.util.ArrayList<String[]> parsedStruct = tk.freaxsoftware.ukrinform.ribbon.lib.data.csv.CsvFormat.fromCsv(this, modCsv);
        String[] baseArray = parsedStruct.get(0);
        this.setLanguage(baseArray[0]);
        this.setHeader(baseArray[1]);
        String[] rawPropertiesArray = baseArray[2].split("\\$");
        if ((rawPropertiesArray.length > 1) || (!rawPropertiesArray[0].isEmpty())) {
            for (String rawProperty : rawPropertiesArray) {
                this.getProperties().add(new tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageProperty(rawProperty));
            }
        }
        this.setDirectories(parsedStruct.get(1));
        this.setTags(parsedStruct.get(2));
    }
    
    /**
     * Modify message's fileds;
     * @param givenMessage message template;
     */
    public void modifyMessageEntry(MessageEntry givenMessage) {
        this.setLanguage(givenMessage.getLanguage());
        this.setHeader(givenMessage.getHeader());
        this.setDirectories(givenMessage.getDirectories());
        this.setTags(givenMessage.getTags());
        this.setProperties(givenMessage.getProperties());
    }
    
    /**
     * Get single property by type.
     * @param propType type to search;
     * @return founded property or null.
     */
    public MessageProperty getProperty(String propType) {
        MessageProperty returned = null;
        java.util.ListIterator<MessageProperty> propIter = this.getProperties().listIterator();
        while (propIter.hasNext()) {
            MessageProperty currProp = propIter.next();
            if (currProp.getType().equals(propType)) {
                returned = currProp;
            }
        }
        return returned;
    }
    
    /**
     * Delete all properties excluding COPYRIGHT.
     */
    public void cleanProperties() {
        MessageProperty copyright = this.getProperty("COPYRIGHT");
        MessageProperty urgent = this.getProperty("URGENT");
        this.getProperties().clear();
        if (copyright != null) {
            this.getProperties().add(copyright);
        }
        if (urgent != null) {
            this.getProperties().add(urgent);
        }
    }
    
    /**
     * Return copyright string of the message entry.
     * @return copyright string or null if there is no copyright;
     */
    public String getCopyright() {
        MessageProperty copyright = this.getProperty("COPYRIGHT");
        if (copyright != null) {
            return copyright.getDescription();
        } else {
            return null;
        }
    }
    
    /**
     * Add property to the message;
     * @param user creator of property;
     * @param propType type of property;
     * @param propText text of property (may be null)
     */
    public void addProperty(String user, String propType, String propText) {
        this.getProperties().add(new tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageProperty(propType, user, propText));
    }
    
    /**
     * Set copyright to the message.
     * @param givenUser user which create copyright;
     * @param givenCopyright name of the author of message;
     */
    public void setCopyright(String givenUser, String givenCopyright) {
        this.getProperties().remove(this.getProperty("COPYRIGHT"));
        this.getProperties().add(new tk.freaxsoftware.ukrinform.ribbon.lib.data.message.MessageProperty("COPYRIGHT", givenUser, givenCopyright));
    }
}
