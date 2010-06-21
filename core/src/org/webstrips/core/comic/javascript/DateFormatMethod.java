package org.webstrips.core.comic.javascript;

import java.util.Date;

import org.mozilla.javascript.Scriptable;

class DateFormatMethod extends JavaMethodObject {

	private static final long serialVersionUID = 134534634L;
		
	private int[] args = new int[] {
			FunctionHelper.JAVA_DATE_TYPE
	};
	
	public DateFormatMethod(Scriptable scope) {
		super("formatDate", scope);
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
		
		return String.format("%1$tB %1$te, %1$tY", (Date)objects[0]);
	}

}
