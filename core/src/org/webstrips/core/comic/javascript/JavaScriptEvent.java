package org.webstrips.core.comic.javascript;

import org.mozilla.javascript.RhinoException;

public class JavaScriptEvent {

	public static enum Type {RUNTIME_ERROR, ERROR, WARNING, INFO}
	
	private String message, sourceName, lineSource;
	
	private int line, lineOffset;

	private Type type;
	
	public JavaScriptEvent(RhinoException e) {
		super();
		this.message = e.getMessage();
		this.sourceName = e.sourceName();
		this.lineSource = e.lineSource();
		this.line = e.lineNumber();
		this.lineOffset = e.columnNumber();
		this.type = Type.ERROR;
	}
	
	public JavaScriptEvent(String message, String sourceName, String lineSource, int line, int lineOffset, Type type) {
		super();
		this.message = message;
		this.sourceName = sourceName;
		this.lineSource = lineSource;
		this.line = line;
		this.lineOffset = lineOffset;
		this.type = type;
	}

	public int getLine() {
		return line;
	}

	public int getLineOffset() {
		return lineOffset;
	}

	public String getLineSource() {
		return lineSource;
	}

	public String getMessage() {
		return message;
	}

	public String getSourceName() {
		return sourceName;
	}
	
	public Type getType() {
		return type;
	}
	
	
}
