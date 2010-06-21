package org.webstrips.core.comic.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

class StringFormatMethod extends JavaMethodObject {

	private static final long serialVersionUID = 134534634L;
	
	private int[] args = new int[] {
			FunctionHelper.JAVA_STRING_TYPE,
			FunctionHelper.JAVA_OBJECT_TYPE
	};
	
	public StringFormatMethod(Scriptable scope) {
		super("format", scope);
	}
	
	@Override
	public boolean acceptsVariableArguments() {
		return true;
	}

	@Override
	protected int[] getArgumentSpec() {
		return args;
	}

	@Override
	protected Object invokeMethod(Object... objects) {
		try {
			
			return String.format((String)objects[0], (Object[])objects[1]);
		} catch (IllegalArgumentException e) {
			Context.throwAsScriptRuntimeEx(e);
			return null;
		}
	}

}
