package org.webstrips.core.bundle;

import org.webstrips.core.ComicDescription;
import org.webstrips.core.comic.ComicException;

public class ComicBundleException extends ComicException {

	private static final long serialVersionUID = 135464764L;

	public ComicBundleException(String message) {
		super(message);
	}
	
	public ComicBundleException(String message, ComicDescription comic) {
		super(message, comic);
	}
	
	public ComicBundleException(Throwable t, ComicDescription comic) {
		super(t, comic);
	}
}
