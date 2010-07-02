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
 * 
 * Created: 
 * Author: lukacu
 * 
 * Log:
 */
package org.webstrips.core.bundle;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.coffeeshop.io.Streams;
import org.webstrips.core.ComicDescription;
import org.webstrips.core.comic.ComicException;

/**
 * The class ComicBundle handles packing and unpacking of the comic data.
 */
public class ComicBundle {

	public static final String DESCRIPTION_SUFFIX = ".info";

	public static final String ENGINE_SUFFIX = ".js";
	
	public static final String IMAGE_SUFFIX = ".png";
	
	private static boolean extractEntry(ZipFile zip, String entry, File target,
			boolean overwrite) throws FileNotFoundException, IOException {

		if (target.exists() && !overwrite)
			return false;

		InputStream in = zip.getInputStream(new ZipEntry(entry));

		if (in == null)
			throw new FileNotFoundException("File not found in archive:"
					+ entry);

		FileOutputStream out = new FileOutputStream(target);

		Streams.copyStream(in, out);

		in.close();
		out.close();
		return true;

	}

	private static boolean addEntry(File src, ZipOutputStream zip, String entry)
			throws IOException {

		if (!src.exists())
			return false;

		zip.putNextEntry(new ZipEntry(entry));

		FileInputStream in = new FileInputStream(src);

		Streams.copyStream(in, zip);

		in.close();

		zip.closeEntry();

		return true;

	}

	/**
	 * The description.
	 */
	private ComicDescription description;

	private File source;

	private byte[] imageData = null;
	
	/**
	 * The Constructor.
	 * 
	 * @param bundleSource
	 *            the bundle source can be a directory or a zip file
	 * 
	 * @throws ComicException
	 *             the comic exception
	 */
	public ComicBundle(File bundleSource) throws ComicException {

		description = verifyBundle(bundleSource);

		this.source = bundleSource;

	}

	private ComicDescription verifyBundle(File f) throws ComicException {

		if (!f.exists())
			throw new ComicBundleException(f + " does not exist");

		if (f.isDirectory()) {

			File description = new File(f, "description.ini");

			if (!description.exists())
				throw new ComicBundleException("Description file not found in "
						+ f);

			try {
				return new ComicDescription(new FileInputStream(description));
			} catch (FileNotFoundException e) {
				throw new ComicBundleException("Description not readable");
			}

		} else {

			try {

				ZipFile bundle = new ZipFile(f);

				InputStream din = bundle.getInputStream(new ZipEntry(
						"description.ini"));

				if (din == null)
					throw new ComicBundleException(
							"Description file not found in " + f);

				if (bundle.getEntry("image.png") != null) {
					
					InputStream str = bundle.getInputStream(new ZipEntry(
					"image.png"));

					imageData = Streams.getStreamAsByteArray(str);
					
				}
				
				return new ComicDescription(din);

			} catch (IOException e) {

				throw new ComicBundleException("Unable to open bundle " + f);

			}

		}

	}

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public ComicDescription getDescription() {
		return description;
	}

	public InputStream imageDataStream() {
		if (imageData == null)
			return null;
		return new ByteArrayInputStream(imageData);
	}
	
	public String unpack(File destination) throws ComicBundleException {
		
		UUID uuid = UUID.randomUUID();
		
		String identifier = description.getShortName() + "-" + uuid.toString();
		
		return unpack(destination, identifier);
	}
	
	/**
	 * Unpack.
	 * 
	 * @param destination the destination directory
	 * @param identifier the identifier (comic id + "-" + uuid)
	 * 
	 * @throws ComicBundleException
	 *             the comic bundle exception
	 */
	public String unpack(File destination, String identifier) throws ComicBundleException {



		if (!destination.isDirectory())
			throw new ComicBundleException("Destination must be a directory",
					description);

		try {

			ZipFile zip = new ZipFile(source);

			extractEntry(zip, "description.ini", new File(destination, identifier + DESCRIPTION_SUFFIX), true);

			extractEntry(zip, "comic.js", new File(destination, identifier + ENGINE_SUFFIX), true);

			if (zip.getEntry("image.png") != null)
				extractEntry(zip, "image.png", new File(destination, identifier + IMAGE_SUFFIX), true);
			
		} catch (IOException e) {

			throw new ComicBundleException("Unable to unpack: "
					+ e.getMessage(), description);

		}

		return identifier;

	}

	public static File pack(File source, String identifier, File destination) throws ComicBundleException {

		ComicDescription d = null;
		
		File description = new File(source, identifier + DESCRIPTION_SUFFIX);

		if (!description.exists())
			throw new ComicBundleException("Description file not found for "
					+ identifier);

		try {
			d = new ComicDescription(new FileInputStream(description));
		} catch (FileNotFoundException e) {
			throw new ComicBundleException("Description not readable");
		} catch (ComicException e) {
			throw new ComicBundleException("Description not valid");
		}

		if (destination.exists() && destination.isDirectory()) {
			destination = new File(destination, d.getShortName()
					+ ".comic");
			
		} else if (!destination.isFile() && destination.exists())
			throw new ComicBundleException("Unable to pack to this destination", d);

		File image = new File(source, identifier + IMAGE_SUFFIX);
		
		try {

			ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(
					destination));

			addEntry(new File(source, identifier + DESCRIPTION_SUFFIX), zip,
					"description.ini");

			addEntry(new File(source, identifier + ENGINE_SUFFIX), zip, "comic.js");

			if (image.exists())
				addEntry(image, zip, "image.png");
			
			zip.close();

		} catch (IOException e) {

			throw new ComicBundleException("Unable to pack: "
					+ e.getMessage(), d);
		}



		return destination;

	}

}
