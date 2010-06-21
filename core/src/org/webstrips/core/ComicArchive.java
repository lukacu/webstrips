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
 * org/webstrips/navigator/ComicArchive.java
 * 
 * Created: November 7, 2006
 * Author: lukacu
 * 
 * Log:
 * 	- 07/11/2006 (lukacu) created
 *  - 21/11/2006 (lukacu) javadoc
 */

package org.webstrips.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.coffeeshop.log.Logger;
import org.webstrips.core.comic.ArchiveEntry;
import org.webstrips.core.WebStrips;

/**
 * Comic archive handles all archive related tasks such as adding entries,
 * notifying the presentation, savin and loading entries...
 * 
 * @author luka
 * 
 */
public class ComicArchive implements Saveable {

	private static final long serialVersionUID = 1L;

	public static String ARCHIVE_SUFFIX = ".archive";

	public static boolean COMPRESS_DATA = true;

	private ArrayList<ArchiveEntry> archive = new ArrayList<ArchiveEntry>();

	private File file = null;

	private Comic comic;

	private boolean modified = false;

	private int unread = 0;

	/**
	 * Loads an archive and returnes the archive object
	 * 
	 * @param c
	 *            comic that is the owner of this archive
	 * @return
	 */
	public static ComicArchive loadArchive(Comic cd) {

		if (cd == null)
			return null;

		ComicArchive result = new ComicArchive(cd);

		try {

			InputStream inStream = (COMPRESS_DATA) ? new GZIPInputStream(
					StorageManager.getStorageManager().getInputStream(
							cd.getComicIdentifier() + ARCHIVE_SUFFIX))
					: StorageManager.getStorageManager().getInputStream(
							cd.getComicIdentifier() + ARCHIVE_SUFFIX);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					inStream));

			int lineCounter = 0;

			ArchiveEntry previous = null;

			while (true) {

				String line = in.readLine();

				lineCounter++;

				if (line == null)
					break;

				try {

					ArchiveEntry e = ArchiveEntry.parseArchiveEntry(cd
							.getComicIdentifier(), line);

					if (previous != null && previous.equals(e)) {
						WebStrips.getLogger().report(
								Logger.WARNING,
								"Duplicate archive entry: " + result.file
										+ " (" + lineCounter + ")");
						continue;
					}

					if (!e.isVisited())
						result.unread++;

					result.archive.add(e);

					previous = e;

				} catch (ParseException e) {
					WebStrips.getLogger().report(
							Logger.WARNING,
							"Archive parse error: " + result.file + " ("
									+ lineCounter + ")");

				}

			}

			in.close();

			result.modified = false;

			return result;

		} catch (IOException e) {

			WebStrips.getLogger().report(e);

			return result;
		}

	}

	/**
	 * Creates a new archive
	 * 
	 * @param comic
	 *            parent comic
	 */
	protected ComicArchive(Comic comic) {
		this.comic = comic;
	}

	/**
	 * @see Saveable#save()
	 */
	public void save() {

		if (file == null || !modified)
			return;

		try {

			OutputStream outStream = (COMPRESS_DATA) ? new GZIPOutputStream(
					new FileOutputStream(file)) : new FileOutputStream(file);

			PrintWriter out = new PrintWriter(outStream);

			for (int i = 0; i < archive.size(); i++) {

				out.println(archive.get(i));

			}

			out.close();

		} catch (IOException e) {

		}

	}

	/**
	 * Appends an array of archive entries to the end of the archive. The
	 * algorithm works like this: first the first entry of the sequence to be
	 * added is located in the existing archive. If it exists the amount of
	 * entries that already are in the archive is located using this information
	 * ant the entry sequence is truncated. The remaining entries are then
	 * inserted in the archive.
	 * 
	 * <b>Note:</b> the sequence should be ordered in a way that the oldest
	 * entry comes first.
	 * 
	 * @param entries
	 */
	public void append(ArchiveEntry[] entries) {

		if (entries == null || entries.length == 0)
			return;

		int i = archive.indexOf(entries[0]);

		int ignore = (i == -1) ? 0 : archive.size() - i;

		int k = archive.size();

		archive.ensureCapacity(archive.size() + entries.length - ignore);

		ArchiveEntry previous = getLast();

		for (int j = ignore; j < entries.length; j++) {
			if (previous != null && previous.equals(entries[j])) {
				WebStrips.getLogger().report(Logger.WARNING,
						"Duplicate archive entry: " + entries[j]);
				continue;
			}

			if (!entries[j].isVisited())
				unread++;
			archive.add(entries[j]);
			previous = entries[j];
		}

		modified = true;

		fireIntervalAdded(this, k, k + entries.length - 1 - ignore);

		return;

	}

	private void fireIntervalAdded(ComicArchive comicArchive, int k, int i) {

		synchronized (listeners) {

			for (ComicArchiveListner l : listeners) {
				try {
					l.intervalAdded(this, k, i);
				} catch (Exception e) {
					WebStrips.getLogger().report(e);
				}
			}

		}

	}

	/**
	 * Calls the <code>visit()</code> method of the entry that maches the given
	 * strip.
	 * 
	 * <b>Note:</b> The <code>visit()</code> method should not be called in any
	 * other way to keep the unread counter right.
	 * 
	 * @param s
	 *            the reference strip
	 * @return the appropriate archive entry or <code>null</code> if the strip
	 *         does not exist in this archive.
	 * @see ArchiveEntry#visit()
	 */
	public ArchiveEntry visit(ComicStripIdentifier s) {

		int i = archive.indexOf(s);

		if (i == -1)
			return null;

		ArchiveEntry a = archive.get(i);

		if (!a.isVisited())
			unread--;

		a.visit();

		modified = true;
		fireContentsChanged(this, i, i + 1);

		return a;
	}

	private void fireContentsChanged(ComicArchive comicArchive, int i, int j) {
		synchronized (listeners) {

			for (ComicArchiveListner l : listeners) {
				try {
					l.contentsChanged(this, i, j);
				} catch (Exception e) {
					WebStrips.getLogger().report(e);
				}
			}

		}
	}

	/**
	 * Markes the strips at the indexes provided in the argument
	 * <code>items</code> as read. That means that the view counter ov every
	 * unvisited comic is increased to 1.
	 * 
	 * @param items
	 *            indices of the strips to be marked as visited
	 */
	public void markAsRead(int[] items) {

		if (items == null || items.length == 0)
			return;

		boolean change = false;

		int begin = archive.size() - 1, end = 0;

		for (int i = 0; i < items.length; i++) {

			if (items[i] < 0 || items[i] >= archive.size())
				continue;

			ArchiveEntry e = archive.get(items[i]);

			if (!e.isVisited()) {
				e.visit();
				change = true;
				unread--;
				begin = Math.min(begin, items[i]);
				end = Math.max(end, items[i]);
			}
		}

		if (change) {

			if (begin == end)
				end++;

			modified = true;
			fireContentsChanged(this, begin, end);

		}

	}

	/**
	 * Markes the strips at the indexes provided in the argument
	 * <code>items</code> as unread. That means that the view counter ov every
	 * visited comic is reset to 0.
	 * 
	 * @param items
	 *            indices of the strips to be marked as not visited
	 */
	public void markAsUnread(int[] items) {

		if (items == null || items.length == 0)
			return;

		boolean change = false;

		int begin = archive.size() - 1, end = 0;

		for (int i = 0; i < items.length; i++) {

			if (items[i] < 0 || items[i] >= archive.size())
				continue;

			ArchiveEntry e = archive.get(items[i]);

			if (e.isVisited()) {
				e.reset();
				change = true;
				unread++;
				begin = Math.min(begin, items[i]);
				end = Math.max(end, items[i]);
			}
		}

		if (change) {

			if (begin == end)
				end++;

			modified = true;
			fireContentsChanged(this, begin, end);

		}

	}

	/**
	 * Get the first entry in the archive
	 * 
	 * @return the first entry on <code>null</code> if the archive is empty.
	 */
	public ArchiveEntry getFirst() {

		if (archive.isEmpty())
			return null;

		return archive.get(0);
	}

	/**
	 * Get the strip entry that is located in front of the given strip entry in
	 * this archive.
	 * 
	 * @param s
	 *            reference strip
	 * @return the archive strip entry or <code>null</code> if the archive is
	 *         empty, the strip is the first entry or is not found in this
	 *         archive.
	 */
	public ArchiveEntry getPrevious(ComicStripIdentifier s) {

		if (archive.isEmpty())
			return null;

		int i = archive.indexOf(s);

		if (i < 1 || i >= archive.size())
			return null;

		return archive.get(i - 1);
	}

	/**
	 * Get the strip entry that is located after the given strip entry in this
	 * archive.
	 * 
	 * @param s
	 *            reference strip
	 * @return the archive strip entry or <code>null</code> if the archive is
	 *         empty, the strip is the last entry or is not found in this
	 *         archive.
	 */
	public ArchiveEntry getNext(ComicStripIdentifier s) {

		if (archive.isEmpty())
			return null;

		int i = archive.indexOf(s);

		if (i == -1 || i >= archive.size() - 1)
			return null;

		return archive.get(i + 1);
	}

	/**
	 * Get the last entry in the archive
	 * 
	 * @return the last entry on <code>null</code> if the archive is empty.
	 */
	public ArchiveEntry getLast() {

		if (archive.isEmpty())
			return null;

		return archive.get(archive.size() - 1);
	}

	/**
	 * Gets the position of the strip in the archive
	 * 
	 * @param s
	 *            comic strip
	 * @return index number or -1 if the strip is not in the archive
	 */
	public int indexOf(ComicStripIdentifier s) {
		return archive.indexOf(s);
	}

	/**
	 * Checks if any change has been made to this archive sice last save. The
	 * change includes appending new entries and marking entries as read/unread.
	 * 
	 * @return <code>true</code> if changes have been made, <code>false</code>
	 *         otherwise.
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * @see ListModel#getElementAt(int)
	 */
	public Object getElementAt(int arg0) {
		return archive.get(arg0);
	}

	/**
	 * @see ListModel#getSize()
	 */
	public int getSize() {
		return archive.size();
	}

	/**
	 * Get the parent comic engine
	 * 
	 * @return parent comic
	 */
	public Comic getComic() {
		return comic;
	}

	/**
	 * Get the number of unread strip entries
	 * 
	 * @return number of unread strip entires
	 */
	public int getUnread() {
		return unread;
	}

	/**
	 * Checks if the archive contains no elements
	 * 
	 * @return <code>true</code> if the archive is empty, <code>false</code>
	 *         otherwise.
	 */
	public boolean isEmpty() {
		return archive.size() == 0;
	}

	private ArrayList<ComicArchiveListner> listeners;

	public void addArchiveListener(ComicArchiveListner listner) {
		if (listner != null && !listeners.contains(listeners))
			listeners.remove(listner);
	}

	public void removeArchiveListener(ComicArchiveListner listner) {
		listeners.remove(listner);
	}

}
