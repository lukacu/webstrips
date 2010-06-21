/* WebStrips
 * 
 * License:
 * 
 * WebStrips is a lightweight web comics browser written in Java.
 * 
 * Copyright (C) 2006 Luka Cehovin
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
 * org/webstrips/navigator/ComicDriver.java
 * 
 * Created: October 25,2006
 * Author: lukacu
 * 
 * Log:
 *  - 25/10/2006 (lukacu) moved from Navigator.java
 *  - 14/11/2006 (lukacu) added archive support
 */

package org.webstrips.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Vector;

import org.coffeeshop.log.Logger;
import org.coffeeshop.settings.SettingsNotFoundException;
import org.webstrips.core.bundle.ComicBundle;
import org.webstrips.core.comic.ArchiveEntry;
import org.webstrips.core.comic.ComicDriver;
import org.webstrips.core.comic.ComicCapabilities;
import org.webstrips.core.comic.ComicException;
import org.webstrips.core.comic.Links;
import org.webstrips.core.comic.Navigation;
import org.webstrips.core.comic.javascript.JavaScriptComic;
import org.webstrips.core.data.ContentProvider;
import org.webstrips.core.data.ObjectCache;
import org.webstrips.core.SimplePreloader;
import org.webstrips.core.WebStrips;

/**
 * 
 * @author luka
 * @since WebStrips 0.3.1
 */
public class Comic implements ComicInformationProvider {

	public final long UPDATE_TIMEOUT = 60 * 60 * 1000;
	
	public static enum State {
		IDLE, UPDATING, OFFLINE, DELETED
	};
	
	private static enum NavigationOperation {
		 NEXT, PREVIOUS, ANCHOR
	};

	/**
	 * Size of chache buffer
	 */
	public static final int CACHE_SIZE = 10;

	private ComicDriver comicEngine;

	private ComicDescription description;
	
	//TODO: disabled for now (has to be done in a more lightweight manner
	//private ComicArchive archive;
	
	private ContentProvider contentProvider;
	
	private ComicStripIdentifier firstStrip, newestStrip;
	
	private Preloader preloader;
	
	private State state = State.IDLE;
	
	private Vector<ComicListener> listeners = new Vector<ComicListener>();
	
	private Calendar lastUpdate = null;
	
	private class NavigationAsynchronousOperation extends ComicAsynchronousOperation<ComicStrip> {

		private ComicStripIdentifier previous, current, next, expected;
		
		private NavigationOperation operation;
		
		private URL image = null;
		
		private String title = null, link = null;
		
		public NavigationAsynchronousOperation(ComicStrip origin, NavigationOperation o) {
			super(origin, WebStrips.getCache());
			this.operation = o;

			beginTransaction();
		}
		
		private synchronized void beginTransaction() {
			
			previous = (getComicStrip() == null) ? null : getComicStrip().previous();
			current = (getComicStrip() == null) ? null : getComicStrip().getIdentifier();
			next = (getComicStrip() == null) ? null : getComicStrip().next();

			switch (operation) {
			case NEXT:
				expected = next;
				break;
			case PREVIOUS:
				expected = previous;
				break;
			case ANCHOR:
				expected = getAnchorIdentifier();
				break;
			}
			
		}
		
		@Override
		public ComicStripIdentifier getComicStripIdentifier() {
			return expected;
		}
		
		@Override
		protected void performOperation() throws Exception {

			if (state == State.DELETED)
				return;
			
			if (lastUpdate == null || (Calendar.getInstance().getTimeInMillis() - lastUpdate.getTimeInMillis() > UPDATE_TIMEOUT))
				updateComic();
			
			switch (operation) {
			case PREVIOUS: {
				if (previous == null)
					return;

				if (!queryCache(previous)) {
					next = current;
					current = previous;
					previous = getPrevious(current);
				}
				
				break;
			}
			case NEXT: {
				if (next == null && current.equals(getNewest()))
					return;

				if (!queryCache(next)) {
					previous = current;
					current = next;
					next = getNext(current);
				}

				break;
			}
			case ANCHOR: {

				if (current.getId() == null) {
					
					if (updateComic()) {
						
						if (newestStrip != null)
							current = newestStrip;
						else if (firstStrip != null)
							current = firstStrip;
						else throw new ComicException("Unable to find anchor");
						
					}
					
					
				}
				
				if (!queryCache(current)) {
					previous = getPrevious(current);
					next = getNext(current);
				}

				break;
			}
			}
			
			if (next == null && !current.equals(getNewest())) 
				next = getNext(current);
			
			String url = comicEngine.stripImageUrl(current);
			if (image == null) {
				try {
					image = new URL(url);
				} catch (MalformedURLException e) {
					WebStrips.getLogger().report(Logger.ERROR, "Malformed URL (%s): %s", getComicName(), url);
					return;
				}
			}
			if (title == null)
				title = ArchiveEntry.class.isInstance(current) ?
						((ArchiveEntry) current).getTitle() : comicEngine.stripTitle(current);
				
			ComicCapabilities cc = comicEngine.getCapabilities();
			
			if (cc.hasCapability(Links.LINKS) && link == null)
				link = ((Links) comicEngine).link(current);
			
			ComicStrip d = new ComicStrip(Comic.this, current, previous, next, title,
					(link != null) ? new URL(link) : null, image);
			
			stripsCache.insert(current.getId(), d);

			setResult(d);
			
		}
				
		private boolean queryCache(ComicStripIdentifier s) {

			ComicStrip c = stripsCache.query(s.getId());
			
			if (c != null) {
				current = c.getIdentifier();
				next = c.next();
				previous = c.previous();
				image = c.getImageSource();
				if (c.getSource() != null)
					link = c.getSource().toString();
				title = c.getTitle();
				
				// remove from cache (so that we can reinsert)
				stripsCache.remove(s.getId());
				
				return true;
			}
			
			return false;
	
		}
	};

/*
	private class UpdateAsynchronousOperation extends ComicAsynchronousOperation {

		public UpdateAsynchronousOperation(Comic c, ComicOperationListener ui) {
			super(c, ui, new ObjectCache<byte[]>(5));
		}
		
		@Override
		protected void performOperation() throws Exception {

			updateComic(getOperationListener());

		}
		
	}*/
	
	private boolean updateComic() throws Exception {
		if (state != State.IDLE)
			return false;
		
		changeState(State.UPDATING);
		
		stripsCache.flush();
		
		WebStrips.getLogger().report("Updating comic");
		
		try {
		
			ComicStripIdentifier newest = newest();
		
			if (firstStrip == null)
				firstStrip = first();
			
			if (newest != newestStrip || !newest.equals(newestStrip)) {
				newestStrip = newest;		
			}
			
		} catch (Exception e) {
			changeState(State.IDLE);

			throw e;
		}
		
		lastUpdate = Calendar.getInstance();
		
		changeState(State.IDLE);

		return true;
	}
	
	private NavigationAsynchronousOperation retrieveComic = null;
	
	private ObjectCache<ComicStrip> stripsCache = new ObjectCache<ComicStrip>(40);
	
	public Comic(String identifier) throws ComicException {

		try {
			InputStream is = WebStrips.getStorageManager().getInputStream(identifier + ComicBundle.DESCRIPTION_SUFFIX);
			description = new ComicDescription(is);
		} catch (IOException e) {
			throw new ComicException(e, description);
		} 
		
		contentProvider = new ContentProvider(description.getShortName());


		try {
			InputStream is = WebStrips.getStorageManager().getInputStream(identifier + ComicBundle.ENGINE_SUFFIX);
			comicEngine = new JavaScriptComic(is, contentProvider, identifier);
		} catch (IOException e) {
			throw new ComicException(e, description);
		} 
		
		ComicCapabilities cc = comicEngine.getCapabilities();

		if (cc == null) {
			throw new ComicException("Load error: comic engine "
					+ description.getShortName() + " does not specify capabilities");
		}

		if (!cc.hasCapability(Navigation.NAVIGATION_FIRST)
				&& !cc.hasCapability(Navigation.NAVIGATION_NEWEST)) {
			throw new ComicException("Load error: comic engine "
					+ description.getShortName() + " has no anchor capability");
		}
		
		preloader = new SimplePreloader(this, 3);
			
	}
	
	public void setAnchor(ComicStrip s) {
		
		WebStrips.getSettings().setString("anchor." + getComicIdentifier(), s.getIdentifier().getId());

		preloader.setPreloadAnchor(s);
		
	}
	
	public void displayAnchor(AsynchronousOperationListener callback) {
		if (state == State.DELETED)
			return;
		
		String id = null;
		
		try {
			id = WebStrips.getSettings().getString("anchor." + getComicIdentifier());
		}
		catch (SettingsNotFoundException e) {}
		
		if (id != null) {
			ComicStripIdentifier anchorStrip = new ComicStripIdentifier(comicEngine.getComicIdentifier(), id);
			
			displayStrip(anchorStrip, callback);
			
		} else {
			displayStrip(getAnchorIdentifier(), callback);
		}
		
	}

	public void displayNewest(AsynchronousOperationListener callback) {
		if (state == State.DELETED)
			return;
		
		if (newestStrip != null)
			displayStrip(newestStrip, callback);
	}

	public void displayFirst(AsynchronousOperationListener callback) {
		if (state == State.DELETED)
			return;
		
		if (firstStrip != null)
			displayStrip(firstStrip, callback);
	}

	public void displayNext(AsynchronousOperationListener callback, ComicStrip origin) {
		if (state == State.DELETED)
			return;
		
		ComicCapabilities cc = comicEngine.getCapabilities();
		if (cc.hasCapability(Navigation.NAVIGATION_NEXT)) { 
			retrieveComic = new NavigationAsynchronousOperation(origin, NavigationOperation.NEXT);
			retrieveComic.addListener(callback);
			retrieveComic.perform();
		}
	}

	public void displayPrevious(AsynchronousOperationListener callback, ComicStrip origin) {
		if (state == State.DELETED)
			return;
		
		ComicCapabilities cc = comicEngine.getCapabilities();
		if (cc.hasCapability(Navigation.NAVIGATION_PREVIOUS)) {
			retrieveComic = new NavigationAsynchronousOperation(origin, NavigationOperation.PREVIOUS);
			retrieveComic.addListener(callback);
			retrieveComic.perform();
		}
	}
	
	public void displayStrip(ComicStripIdentifier s, AsynchronousOperationListener callback) {		
		if (state == State.DELETED)
			return;
		
		ComicStrip si = new ComicStrip(this, s, null, null, null, null, null);
		
		retrieveComic = new NavigationAsynchronousOperation(si, NavigationOperation.ANCHOR);
		retrieveComic.addListener(callback);

		retrieveComic.perform();
		
	}
	
	public String toString() {
		return description.comicName();
	}

	/**
	 * @see org.webstrips.core.ComicInformationProvider#getComicName()
	 */
	public String getComicName() {
		return description.comicName();
	}

	/**
	 * @see org.webstrips.core.ComicInformationProvider#getComicAuthor()
	 */
	public String getComicAuthor() {
		return description.comicAuthor();
	}

	/**
	 * @see org.webstrips.core.ComicInformationProvider#getComicDescription()
	 */
	public String getComicDescription() {
		return description.comicDescription();
	}

	/**
	 * @see org.webstrips.core.ComicInformationProvider#getComicHomapage()
	 */
	public String getComicHomapage() {
		return description.comicHomepage();
	}
/*
	public ComicArchive getArchive() {
		
		ComicCapabilities cc = comicEngine.getCapabilities();
		if (!cc.hasCapability(Archive.ARCHIVE))
			return null;
		
		if (archive == null) {
			archive = ComicArchive.loadArchive(this);
			fireUnreadStripsNumberChangedEvent(archive.getUnread());
	
			if (archive != null)
				archive.addArchiveListener(new ComicArchiveListner() {

					public void contentsChanged(ComicArchive source, int i, int j) {
						fireUnreadStripsNumberChangedEvent(archive.getUnread());
						
					}

					public void intervalAdded(ComicArchive source, int k, int i) {
						fireUnreadStripsNumberChangedEvent(archive.getUnread());
						
					}

					public void intervalRemoved(ComicArchive source, int k, int i) {
						fireUnreadStripsNumberChangedEvent(archive.getUnread());
						
					}
					
				});
			
		}
		
		
		return archive;
	}
*/	
	public int getUnreadStripsCount() {
		
		return -1;
		/*
		ComicArchive a = getArchive();
		
		if (a == null)
			return -1;
		
		return a.getUnread();
		*/
	}
		
	public void addComicListener(ComicListener l) {
		if (l == null)
			return;
		
		listeners.add(l);
	}
	
	public void removeComicListener(ComicListener l) {
		if (l == null)
			return;
		
		listeners.remove(l);
	}
	/*
	private void fireUnreadStripsNumberChangedEvent(int unread) {
		
		for (int i = 0; i < listeners.size(); i++) 
			listeners.get(i).unreadStripsNumberChanged(this, unread);
		
	}
	*/
	public ComicDescription getDescription() {
		return description;
	}
	
	private ComicStripIdentifier first() {
		
		ComicCapabilities cc = comicEngine.getCapabilities();
		
		if (cc.hasCapability(Navigation.NAVIGATION_FIRST))
			return ((Navigation) comicEngine).first();
		
		return null;
	}
	
	private ComicStripIdentifier newest() {
		
		ComicCapabilities cc = comicEngine.getCapabilities();
		
		if (cc.hasCapability(Navigation.NAVIGATION_NEWEST))
			return ((Navigation) comicEngine).newest();
		
		return null;
	}
	
	public ComicStripIdentifier getFirst() {
		return firstStrip;
	}
	
	public ComicStripIdentifier getNewest() {
		return newestStrip;
	}
	
	private ComicStripIdentifier getPrevious(ComicStripIdentifier s) {
		
		ComicCapabilities cc = comicEngine.getCapabilities();
		
		ComicStrip i = preloader.getPreloaded(s);
		
		if (i != null)
			return i.previous();
		if (cc.hasCapability(Navigation.NAVIGATION_PREVIOUS))
			return ((Navigation) comicEngine).previous(s);
		
		return null;
	}
	
	private ComicStripIdentifier getNext(ComicStripIdentifier s) {
		
		ComicCapabilities cc = comicEngine.getCapabilities();
		
		ComicStrip i = preloader.getPreloaded(s);
		
		if (i != null)
			return i.next();
		if (cc.hasCapability(Navigation.NAVIGATION_PREVIOUS))
			return ((Navigation) comicEngine).next(s);
		
		return null;
	}
	
	public boolean delete(boolean cleanData, boolean cleanBinary) {
		
		if (cleanBinary) {
			
			try {
				StorageManager.getStorageManager().delete(getComicIdentifier() + ComicBundle.DESCRIPTION_SUFFIX);
			} catch (IOException e) {}
			
			try {
				StorageManager.getStorageManager().delete(getComicIdentifier() + ComicBundle.ENGINE_SUFFIX);
			} catch (IOException e) {}
			
		}
		
		if (cleanData) {
			
			// TODO: implement clearing data
			
		}
		
		state = State.DELETED;
		
		return true;
		
	}
	
	private void changeState(State newState) {
		
		State oldState = state;
		state = newState;
		
		for (ComicListener l : listeners) {
			
			try {
				l.stateChanged(this, newState, oldState);
				
			} catch (Exception e) {
				WebStrips.getLogger().report(e);
			}
			
		}
		
		
	}
	
	public String getComicIdentifier() {
		return comicEngine.getComicIdentifier();
	}
	
	public ComicStripIdentifier getAnchorIdentifier() {
		return new ComicStripIdentifier(getComicIdentifier(), null);
	}
	
	public File getImageFile() {
		File file = new File(WebStrips.getStorageManager().getStorageDirectory(), getComicIdentifier() + ComicBundle.IMAGE_SUFFIX);
		
		if (file.exists())
			return file;
		
		return null;
		
	}
	
}