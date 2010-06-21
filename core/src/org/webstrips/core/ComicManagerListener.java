package org.webstrips.core;

import org.webstrips.core.Comic;

public interface ComicManagerListener {

	public void comicAdded(ComicManager source, Comic newComic);
	
	public void comicRemoved(ComicManager source, Comic removedComic);
	
	public void comicReloaded(ComicManager source, Comic removedComic, Comic newComic);
	
}
