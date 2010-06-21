package org.webstrips.core.comic.javascript;

import java.net.MalformedURLException;
import java.net.URL;

import org.mozilla.javascript.Scriptable;
import org.webstrips.core.data.ContentProvider;

class UrlExistsMethod extends JavaMethodObject {

	private static final long serialVersionUID = 134545567634L;
	
	private ContentProvider contentProvider;
	
	private int[] args = new int[] {
			FunctionHelper.JAVA_STRING_TYPE
	};
	
	public UrlExistsMethod(Scriptable scope, ContentProvider cp) {
		super("exists", scope);
		contentProvider = cp;
	}
	
	@Override
	public boolean acceptsVariableArguments() {
		return false;
	}

	@Override
	protected int[] getArgumentSpec() {
		return args;
	}

	@Override
	protected Object invokeMethod(Object... objects) {
		try {
			URL url = new URL((String)objects[0]);
			
			return contentProvider.urlExists(url);
		} catch (MalformedURLException e) {
			// TODO: error logging
			return null;
		}
		
	}

}
