package org.webstrips.core.comic.javascript;

import org.mozilla.javascript.RhinoException;
import org.webstrips.core.ComicDescription;

public class JavaScriptError {

	private int line, column;
	
	private String description;
	
	private String sourceLine;
	
	private ComicDescription comic;
	
	public JavaScriptError(RhinoException e, ComicDescription d) {
		this.line = e.lineNumber();
		this.column = e.columnNumber();
		this.description = e.details();
		this.sourceLine = e.lineSource();
		this.comic = d;
	}

	public int getColumn() {
		return column;
	}

	public String getDescription() {
		return description;
	}

	public int getLine() {
		return line;
	}

	public String getSourceLine() {
		return sourceLine;
	}
	
	public String getPluginName() {
		return comic.getShortName();
	}
	
}
