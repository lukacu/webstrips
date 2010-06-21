package org.webstrips.core;

import java.net.URL;

public class ComicStrip {

	private Comic driver;
	
	private String title;
	
	private URL source, imageSource;
	
	private ComicStripIdentifier id, previous, next;
	
	/**
	 * Creates new object
	 * 
	 * @param comicStrip
	 * @param title
	 * @param image
	 */
	public ComicStrip(Comic driver, ComicStripIdentifier id, ComicStripIdentifier previous, ComicStripIdentifier next, String title, URL source, URL imageSource) {
		this.driver = driver;
		this.imageSource = imageSource;
		
		this.title = title;
		this.id = id;
		this.previous = previous;
		this.next = next;
		this.source = source;
	}
	
	/**
	 * Get title of the comic strip
	 * 
	 * @return title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Get the source comic engine
	 * 
	 * @return comic engine
	 */
	public Comic getComic() {
		return driver;
	}

	public Comic getComicDriver() {
		return driver;
	}
	
	/**
	 * Get the source comic
	 * 
	 * @return comic
	 */
	public ComicStripIdentifier getIdentifier() {
		return id;
	}
	
	public boolean equals(Object o) {
		
		if (ComicStrip.class.isAssignableFrom(o.getClass())) {
			return ((ComicStrip)o).id.equals(id);
		}
		
		if (ComicStripIdentifier.class.isAssignableFrom(o.getClass())) {
			return id.equals(o);
		}
		
		return false;
	}

	public ComicStripIdentifier next() {
		return next;
	}

	public ComicStripIdentifier previous() {
		return previous;
	}
	
	public URL getSource() {
		return source;
	}

	public URL getImageSource() {
		return imageSource;
	}
	
	public String toString() {

		return String.format("current: %s, prev: %s, next %s, image: %s", id, previous, next, imageSource);
		
	}
	
}
