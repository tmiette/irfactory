/*

Copyright (c) 2006 David Lambert

This file is part of Factory.

Factory is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

Factory is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Factory; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/FactoryFileFilter.java,v $
$Revision: 1.1 $
$Date: 2006/04/06 04:58:54 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client;

import java.io.File;

import java.util.Hashtable;
import java.util.Enumeration;

import javax.swing.filechooser.FileFilter;

/**
 *  Filter on files used for database backup and restore.
 *  @author David Lambert
 */
class FactoryFileFilter extends FileFilter {
	
	/** Filters. */
	private Hashtable filters = null;
	
	/** Description of filter. */
	private String description = null;
	
	/** Full description of filter. */
	private String fullDescription = null;
	
	/**
	 * Creates a file filter. If no filters are added, then all
	 * files are accepted.
	 */
	public FactoryFileFilter() {
		this.filters = new Hashtable();
	}
	
	/**
	 * Creates a file filter that accepts files with the given extension.
	 * Example: new FactoryFileFilter("jpg");
	 */
	public FactoryFileFilter(String extension) {
		this(extension, null);
	}
	
	/** Creates a file filter that accepts the given file type.
	 *  Example: new FactoryFileFilter("jpg", "JPEG Image Images"); */
	public FactoryFileFilter(String extension, String description) {
		this();
		
		if (extension != null)
			addExtension(extension);
		
		if (description != null)
			setDescription(description);
	}
	
	/** Creates a file filter from the given string array.
	 *  Example: new FactoryFileFilter(String {"gif", "jpg"}); */
	public FactoryFileFilter(String[] filters) {
		this(filters, null);
	}
	
	/** Creates a file filter from the given string array and description.
	 *  Example: new FactoryFileFilter(String {"gif", "jpg"}, "Gif and JPG Images"); */
	public FactoryFileFilter(String[] filters, String description) {
		this();
		
		for (int i = 0; i < filters.length; i++)
			addExtension(filters[i]);
		
		if (description != null)
			setDescription(description);
	}
	
	/** Returns true if this file should be shown in the directory pane,
	 *  false if it shouldn't. */
	public boolean accept(File f) {
		if (f != null) {
			if (f.isDirectory())
				return true;
			
			String extension = getExtension(f);
			
			if ((extension != null) && 
				(filters.get(getExtension(f)) != null))
				return true;
		}
		
		return false;
	}
	
	/** Returns the extension portion of the file's name. */
	public String getExtension(File f) {
		if (f != null) {
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			
			if ((i > 0) && (i < (filename.length() - 1))) {
				return filename.substring(i + 1).toLowerCase();
			}
		}
		
		return null;
	}
	
	/**
	 * Adds a filetype "dot" extension to filter against.
	 * For example: the following code will create a filter that filters
	 * out all files except those that end in ".jpg" and ".tif":
	 */
	public void addExtension(String extension) {
		if (filters == null)
			filters = new Hashtable(5);
		
		filters.put(extension.toLowerCase(), this);
		fullDescription = null;
	}
	
	/**
	 * Returns the human readable description of this filter. For
	 * example: "JPEG and GIF Image Files (*.jpg, *.gif)"
	 */
	public String getDescription() {
		if (fullDescription == null) {
			if (description == null) {
				fullDescription = 
				(description == null) ? "(" : (description + " (");
				
				// build the description from the extension list
				Enumeration extensions = filters.keys();
				
				if (extensions != null) {
					fullDescription += 
					("." + (String)extensions.nextElement());
					
					while (extensions.hasMoreElements()) {
						fullDescription += 
						(", ." + (String)extensions.nextElement());
					}
				}
				
				fullDescription += ")";
			} else {
				fullDescription = description;
			}
		}
		
		return fullDescription;
	}
	
	/**
	 * Sets the human readable description of this filter. For
	 * example: filter.setDescription("Gif and JPG Images");
	 */
	public void setDescription(String description) {
		this.description = description;
		fullDescription = null;
	}
}
