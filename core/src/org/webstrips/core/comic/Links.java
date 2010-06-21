package org.webstrips.core.comic;

import org.webstrips.core.ComicStripIdentifier;

public interface Links {

	public static final String LINKS = "links";
	
	public String link(ComicStripIdentifier cs);
	
}
