package org.webstrips.core.comic.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

public class WarningMethod extends JavaMethodObject {

	private static final long serialVersionUID = 1L;

	private int[] args = new int[] {
			FunctionHelper.JAVA_STRING_TYPE
	};
	
	public WarningMethod(Scriptable scope) {
		super("warning", scope);
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
		
        String s = ScriptRuntime.toString(objects, 0);

        Context.reportWarning(s);
        
        return true;
	}

}
