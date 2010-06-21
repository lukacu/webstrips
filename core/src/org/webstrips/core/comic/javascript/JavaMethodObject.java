package org.webstrips.core.comic.javascript;

import java.lang.reflect.Array;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

public abstract class JavaMethodObject extends BaseFunction implements Callable {

	private static final long serialVersionUID = 1L;
	
	String methodName;
	
	public JavaMethodObject(String name, Scriptable scope) {

		methodName = name;
		
		ScriptRuntime.setFunctionProtoAndParent(this, scope);
		
	}

	
	
	/**
	 * Return the value defined by the method used to construct the object
	 * (number of parameters of the method, or 1 if the method is a "varargs"
	 * form).
	 */
	public int getArity() {
		return acceptsVariableArguments() ? 1 : getArgumentSpec().length;
	}

	/**
	 * Return the same value as {@link #getArity()}.
	 */
	public int getLength() {
		return getArity();
	}

	public String getFunctionName() {
		return (methodName == null) ? "" : methodName;
	}

	protected abstract Object invokeMethod(Object ...objects);

	protected abstract int[] getArgumentSpec();
	
	protected abstract boolean acceptsVariableArguments();
	
	protected int optionalArguments() {
		return 0;
	}
	
	protected Object defaultValue(int arg) {
		return null;
	}
	
	/**
	 * Performs conversions on argument types if needed and invokes the
	 * underlying Java method or constructor.
	 * <p>
	 * Implements Function.call.
	 * 
	 * @see org.mozilla.javascript.Function#call( Context, Scriptable,
	 *      Scriptable, Object[])
	 */
	public Object call(Context cx, Scriptable scope, Scriptable thisObj,
			Object[] args) {

		int[] typeTags = getArgumentSpec();
		Object[] invokeArgs = new Object[typeTags.length];
		int requiredArg = acceptsVariableArguments() ? typeTags.length - 1 : typeTags.length - optionalArguments();
		

		
		if (args.length < requiredArg) {
			Context.throwAsScriptRuntimeEx(new RuntimeException("Not enough arguments"));
		}
		
		int i = 0;
		
		for (i = 0; i < requiredArg; i++) {
			
			Object arg = args[i];
			Object converted = FunctionHelper.convertArg(cx, scope, arg, typeTags[i]);

			invokeArgs[i] = converted;
		}
		
		if (acceptsVariableArguments()) {
			int lastType = typeTags[typeTags.length - 1];
			
			Object vararg = Array.newInstance(FunctionHelper.getTagClass(lastType), args.length - typeTags.length + 1);
			int j = 0;
			for (; i < args.length; i++) {
				Object arg = args[i];
				
				Object converted = FunctionHelper.convertArg(cx, scope, arg, lastType);

				Array.set(vararg, j, converted);
				//invokeArgs[i] = converted;
				j++;
			}
			
			invokeArgs[invokeArgs.length - 1] = vararg;
			
		} else {
			
			for (; i < typeTags.length; i++) {
				Object arg = i < args.length ? FunctionHelper.convertArg(cx, scope, args[i], typeTags[i]) : defaultValue(i);
				
				invokeArgs[i] = arg;
			}
			
		}
		
		Object result = invokeMethod(invokeArgs);
		
		if (result == null) {
			result = Undefined.instance;
		} else if (FunctionHelper.getTypeTag(result.getClass()) == FunctionHelper.JAVA_UNSUPPORTED_TYPE) {
			result = cx.getWrapFactory().wrap(cx, scope, result, null);
			
			// XXX: the code assumes that if returnTypeTag == JAVA_OBJECT_TYPE
			// then the Java method did a proper job of converting the
			// result to JS primitive or Scriptable to avoid
			// potentially costly Context.javaToJS call.
		}

		return result;
	}

	/**
	 * Return new {@link Scriptable} instance using the default constructor for
	 * the class of the underlying Java method. Return null to indicate that the
	 * call method should be used to create new objects.
	 */
	public Scriptable createObject(Context cx, Scriptable scope) {

		Scriptable result = null;
		try {
			//result = (Scriptable) member.getDeclaringClass().newInstance();
		} catch (Exception ex) {
			throw Context.throwAsScriptRuntimeEx(ex);
		}

		//result.setPrototype(getClassPrototype());
		//result.setParentScope(getParentScope());
		return result;
	}

}