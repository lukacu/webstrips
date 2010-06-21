package org.webstrips.core;

public interface ComicArchiveListner {

	public void contentsChanged(ComicArchive source, int i, int j); 

	public void intervalAdded(ComicArchive source, int k, int i);

	public void intervalRemoved(ComicArchive source, int k, int i);

}
