/* WebStrips
 * 
 * License:
 * 
 * WebStrips is a lightweight web comics browser written in Java.
 * 
 * Copyright (C) 2007 Luka Cehovin
 * 
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; either version 2 of the License, or (at 
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 * 
 * http://www.opensource.org/licenses/gpl-license.php
 * 
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, write to the Free Software Foundation, 
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * -----------------------------------------------------------------------
 * 
 * File info:
 * 
 * org/webstrips/navigator/ComicList.java
 * 
 * Created: November 9, 2006
 * Author: lukacu
 * 
 * Log:
 *  - 09/11/2006 (lukacu) created
 *  - 23/11/2006 (lukacu) javadoc
 *  - 07/12/2006 (lukacu) visual improvements
 *  - 01/04/2007 (lukacu) dynamic installing
 */

package org.webstrips.core;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

import org.webstrips.core.Comic;
import org.webstrips.core.Saveable;
import org.webstrips.core.bundle.ComicBundle;
import org.webstrips.core.comic.ComicException;
import org.webstrips.core.WebStrips;

/**
 * A GUI comic list representation that supports comic choosing and displays
 * various comic information.
 * 
 * @author luka
 * @since WebStrips 0.3.2
 * 
 */
public class ComicManager implements Iterable<Comic>, Saveable {

	/**
	 * Serialization...
	 */
	public static final long serialVersionUID = 1;

	public static final String INSTALLED_COMICS_FILE = "installed";

	private Vector<Comic> comics = new Vector<Comic>();

	private Vector<ComicManagerListener> listeners = new Vector<ComicManagerListener>();

	/**
	 * Creates new component.
	 * 
	 */
	public ComicManager() {
				
	}

	/**
	 * Lists available comic engines from the specific directory
	 * 
	 * @return
	 * @throws ComicException
	 * 
	 */
	public void loadComics() throws ComicException {

		File comicsDir = StorageManager.getStorageManager().getStorageDirectory();

		WebStrips.getLogger().report(WebStrips.COMICS, "Searching for installed comic engines...");

		if (!comicsDir.exists() || !comicsDir.isDirectory()) {
			throw new ComicException("Unable to locate settings directory");
		}

		FileFilter filter = new FileFilter() {
			public boolean accept(File file) {
				if (file.isDirectory())
					return false;

				if (file.getName().endsWith(ComicBundle.DESCRIPTION_SUFFIX))
					return true;

				return false;
			}
		};

		File[] elements = comicsDir.listFiles(filter);

		for (int i = 0; i < elements.length; i++) {

			String identifier = elements[i].getName().substring(0, 
					elements[i].getName().length() - ComicBundle.DESCRIPTION_SUFFIX.length());

			Comic d = loadComic(identifier);
			
			if (d != null) {
				addComic(d);
			}

		}

	}

	
	private Comic loadComic(String identifier) {
		
		try {

			WebStrips.getLogger().report(WebStrips.COMICS, "Checking: " + identifier);

			Comic d = new Comic(identifier);

			return d;
			
		} catch (Exception e) {
			WebStrips.getLogger().report(e);
		}
		
		
		return null;
	}
	
	
	/**
	 * Add a new comic to the list.
	 * 
	 * TODO: alphabetical sorting, duplicate removal.
	 * 
	 * @param comic
	 *            new comic
	 */
	private void addComic(Comic comic) {

		if (comic == null)
			return;

		comics.add(comic);

		for (ComicManagerListener l : listeners) {
			l.comicAdded(this, comic);
		}
		
	}

	/**
	 * Add a selection listener.
	 * 
	 * @param l
	 *            new selection listener
	 */
	public void addComicManagerListener(ComicManagerListener l) {

		if (l == null)
			return;

		listeners.add(l);

	}

	/**
	 * Remove selection listener
	 * 
	 * @param l
	 *            listener to be removed
	 */
	public void removeComicManagerListener(ComicManagerListener l) {

		listeners.remove(l);

	}
	
	/**
	 * Returns the number of comics in this list.
	 * 
	 * @return number of comics
	 */
	public synchronized int getComicCount() {
		return comics.size();
	}

	/**
	 * Returns the comic at the certaion location in the list
	 * 
	 * @param i
	 *            index of the comic in the list. Starts with 0.
	 * @return comic object or <code>null</code> if the index does not exist.
	 */
	public synchronized Comic getComic(int i) {
		Comic c = comics.get(i);

		return c;
	}


	public synchronized Comic findComicByName(String name) {
		
		for (Comic c : comics) {
			
			if (c.getDescription().getShortName().compareTo(name) == 0)
				return c;
			
		}
		
		return null;
	}
	
	public synchronized Comic findComicByIdentifier(String identifier) {
		
		for (Comic c : comics) {
			
			if (c.getComicIdentifier().compareTo(identifier) == 0)
				return c;
			
		}
		
		return null;
	}
	
	public synchronized boolean importBundle(URL location) {
		try {
		
			WebStrips.getLogger().report(WebStrips.COMICS, "Attempting to open bundle at: " + location.getPath());
			
			ComicBundle bundle = new ComicBundle(new File(location.getPath()));

			Comic installed = findComicByName(bundle.getDescription().getShortName());
			
			if (installed == null) {
				
				String identifier = bundle.unpack(StorageManager.getStorageManager().getStorageDirectory());
				
				Comic d = loadComic(identifier);
				
				if (d == null)
					return false;

				addComic(d);
				
				return true;
			} else {

				if (installed.getDescription().engineMajorVersion() > bundle.getDescription().engineMajorVersion() ||
						(installed.getDescription().engineMajorVersion() == bundle.getDescription().engineMajorVersion() &&
						installed.getDescription().engineMinorVersion() > bundle.getDescription().engineMinorVersion())) {
					
					return false;
					
				} else {
				
					bundle.unpack(StorageManager.getStorageManager().getStorageDirectory(), installed.getComicIdentifier());
					
					reloadComic(installed);
					
					return true;
				}
				
				
				
			}
			
		} 
		catch (ComicException e) {
			WebStrips.getLogger().report(e);
		}
		catch (Exception e) {
			WebStrips.getLogger().report(e);
		}
		
		return false;
	}
	
	public synchronized boolean removeComic(Comic comic) {
		
		if (comic == null)
			return false;
		
	
		comics.remove(comic);
		
		for (ComicManagerListener l : listeners) {
			try {
				l.comicRemoved(this, comic);
			} catch (Exception e) {
				WebStrips.getLogger().report(e);
			}
		}
		
		WebStrips.getLogger().report(WebStrips.COMICS, "Removed comic: " + comic.getComicName());
		
		return true;
		
	} 

	public synchronized boolean reloadComic(Comic comic) {
		
		if (comic == null)
			return false;
		
		Comic old = comic;
		
		Comic reloaded = loadComic(comic.getComicIdentifier());
		
		if (reloaded == null)
			return false;
		
		try {
			for (ComicManagerListener l : listeners) {
				l.comicReloaded(this, old, reloaded);
			}
		} finally {
			comics.remove(old);
			old.delete(false, false);
			comics.add(reloaded);
		}
		
		WebStrips.getLogger().report(WebStrips.COMICS, "Reloaded comic: " + reloaded.getComicName());
		
		return true;
		
	}
	
	
	public Iterator<Comic> iterator() {
		return comics.iterator();
	}

	public void save() {
		/*
		for (int i = 0; i < getComicCount(); i++) {
			
			Comic d = getComic(i);
			
			ComicArchive a = d.getArchive();
			
			if (a == null)
				continue;
			
			a.save();
		}
		*/
	}
	
	
	public synchronized boolean exportBundle(Comic comic, File file) {
		try {
		
			WebStrips.getLogger().report(WebStrips.COMICS, 
					"Exporting comic %s to %s ", comic.getComicName(), file);
			
			ComicBundle.pack(StorageManager.getStorageManager().getStorageDirectory(), 
					comic.getComicIdentifier(), file);
			
			return true;
		} 
		catch (ComicException e) {
			WebStrips.getLogger().report(e);
		}
		
		return false;
	}
	
	
}
