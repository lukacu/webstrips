package org.webstrips.core.comic.javascript;

import org.mozilla.javascript.Scriptable;
import org.webstrips.core.InterruptedRuntimeException;

class SleepMethod extends JavaMethodObject {

	private static final long serialVersionUID = 1345354634L;
	
	private int[] args = new int[] {
			FunctionHelper.JAVA_INT_TYPE
	};
	
	public SleepMethod(Scriptable scope) {
		super("sleep", scope);
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
		
		Integer sleep = (Integer)objects[0];
		
		if (sleep == null)
			throw new RuntimeException("Illegal argument (sleep)");
		
		if (sleep < 1 || sleep > 10000)
			throw new RuntimeException("Illegal argument (sleep)");
		
		try {
			Thread.sleep(sleep);
			
		} catch (InterruptedException e) {
			throw new InterruptedRuntimeException(e);
		}
		
		return null;
	}

}
