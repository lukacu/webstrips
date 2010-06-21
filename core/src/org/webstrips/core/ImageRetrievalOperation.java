package org.webstrips.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.webstrips.core.data.ContentProvider;

public class ImageRetrievalOperation extends ComicAsynchronousOperation<InputStream> {

	private static final ContentProvider contentProvider = new ContentProvider("--ui--");
	
	public ImageRetrievalOperation(ComicStrip strip) {
		super(strip, WebStrips.getCache());
	}

	@Override
	protected void performOperation() throws Exception {
	
		if (getComicStrip() != null && getComicStrip().getImageSource() != null) {
		
			byte[] data = contentProvider.retrive(getComicStrip().getImageSource());
			
			if (data != null)
				setResult(new ByteArrayInputStream(data));
			else throw new IOException("No data returned");
		}
	}
	
}