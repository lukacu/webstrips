package org.webstrips.core.comic.javascript;

import org.coffeeshop.log.Logger;
import org.mozilla.javascript.Scriptable;
import org.webstrips.core.WebStrips;

class PrintMethod extends JavaMethodObject {

	private static final long serialVersionUID = 1345354634L;
	
	private int[] args = new int[] {
			FunctionHelper.JAVA_STRING_TYPE
	};
	
	public PrintMethod(Scriptable scope) {
		super("print", scope);
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
		
		String s = (String)objects[0];
		
		WebStrips.getLogger().report(Logger.APPLICATION_INTERNAL_4, s);
		
		return null;
	}

}
