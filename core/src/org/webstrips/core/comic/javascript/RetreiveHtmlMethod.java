package org.webstrips.core.comic.javascript;

import java.net.MalformedURLException;
import java.net.URL;

import org.mozilla.javascript.Scriptable;
import org.webstrips.core.data.ContentProvider;

class RetreiveHtmlMethod extends JavaMethodObject {

	private static final long serialVersionUID = 132234567634L;
	
	private ContentProvider contentProvider;
	
	private int[] args = new int[] {
			FunctionHelper.JAVA_STRING_TYPE
	};
	
	public RetreiveHtmlMethod(Scriptable scope, ContentProvider cp) {
		super("get", scope);
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
			
			return contentProvider.retriveHtml(url);
		} catch (MalformedURLException e) {
			// TODO: error logging
			return null;
		}
		
	}

}
