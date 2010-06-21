package org.webstrips.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.coffeeshop.string.StringUtils;
import org.webstrips.core.comic.ComicException;

public class ComicDescription {

	private Properties properties;
	
	private String comicName, comicAuthor, comicDescription, comicHomepage, engineAuthor, engineDescription;
	
	private String shortName;
	
	private int majorVersion, minorVersion;
	
	public ComicDescription(InputStream source) throws ComicException {
		
		properties = new Properties();
		try {
			
			properties.load(source);
		
		} catch (IOException e) {
			throw new ComicException("Unable to load description from stream (" + e.getMessage() + ")");
		}
		
		validate();
		
	}
	
	private void validate() throws ComicException {
		
		comicName = properties.getProperty("comic.name");
		
		if (StringUtils.empty(comicName))
			throw new ComicException("Comic name not defined");
		
		shortName = StringUtils.replaceAll(comicName, "[^A-Za-z0-9]", "");
		
		if (StringUtils.empty(shortName))
			throw new ComicException("Comic name does not include alphanumerical characters");	
		
		comicDescription = properties.getProperty("comic.description");
		
		comicHomepage = properties.getProperty("comic.homepage");
		
		if (StringUtils.empty(comicHomepage))
			throw new ComicException("Comic homepage not defined");
		
		comicAuthor = properties.getProperty("comic.author");
		
		if (StringUtils.empty(comicAuthor))
			throw new ComicException("Comic author not defined");
		
		engineAuthor = properties.getProperty("engine.author");
		
		if (StringUtils.empty(engineAuthor))
			throw new ComicException("Engine not defined");
		
		engineDescription = properties.getProperty("engine.description");
		
		try {
			
			majorVersion = Integer.parseInt(properties.getProperty("engine.version.major"));
		
			if (majorVersion < 0)
				throw new ComicException("Illegal major version");
			
		} catch (NumberFormatException e) {
			throw new ComicException("Unable to parse major version description");
		}
		
		try {
			
			minorVersion = Integer.parseInt(properties.getProperty("engine.version.minor"));
		
			if (minorVersion < 0)
				throw new ComicException("Illegal minor version");
			
		} catch (NumberFormatException e) {
			throw new ComicException("Unable to parse minor version description");
		}
		
		String type = properties.getProperty("engine.type");
		
		if (StringUtils.empty(type))
			throw new ComicException("Engine type not defined");
		
	}
	
	public String comicAuthor() {
		return comicAuthor;
	}

	public String comicDescription() {
		return comicDescription;
	}

	public String comicHomepage() {
		return comicHomepage;
	}

	public String comicName() {
		return comicName;
	}

	public String engineAuthor() {
		return engineAuthor;
	}

	public String engineDescription() {
		return engineDescription;
	}
	
	public int engineMajorVersion() {
		return majorVersion;
	}
	
	public int engineMinorVersion() {
		return minorVersion;
	}
	
	public String getShortName() {
		return shortName;
	}
}
