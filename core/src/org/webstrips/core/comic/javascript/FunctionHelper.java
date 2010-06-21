package org.webstrips.core.comic.javascript;

import java.util.Date;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

public class FunctionHelper {

	public static final int JAVA_UNSUPPORTED_TYPE = 0;

	public static final int JAVA_STRING_TYPE = 1;

	public static final int JAVA_INT_TYPE = 2;

	public static final int JAVA_BOOLEAN_TYPE = 3;

	public static final int JAVA_DOUBLE_TYPE = 4;

	public static final int JAVA_DATE_TYPE = 7;
	
	public static final int JAVA_SCRIPTABLE_TYPE = 5;

	public static final int JAVA_OBJECT_TYPE = 6;

	public static final int JAVA_NULL_TYPE = 8;
	
	/**
	 * @return One of <tt>JAVA_*_TYPE</tt> constants to indicate desired type
	 *         or {@link #JAVA_UNSUPPORTED_TYPE} if the convertion is not
	 *         possible
	 */
	public static int getTypeTag(Class<?> type) {
		if (type == ScriptRuntime.StringClass)
			return JAVA_STRING_TYPE;
		if (type == ScriptRuntime.IntegerClass || type == Integer.TYPE)
			return JAVA_INT_TYPE;
		if (type == ScriptRuntime.BooleanClass || type == Boolean.TYPE)
			return JAVA_BOOLEAN_TYPE;
		if (type == ScriptRuntime.DoubleClass || type == Double.TYPE)
			return JAVA_DOUBLE_TYPE;
		if (type == ScriptRuntime.DateClass || type == Date.class)
			return JAVA_DATE_TYPE;
		if (type == Undefined.class)
			return JAVA_OBJECT_TYPE;
		if (((Class<?>) (ScriptRuntime.ScriptableClass)).isAssignableFrom(type))
			return JAVA_SCRIPTABLE_TYPE;
		if (type == ScriptRuntime.ObjectClass)
			return JAVA_OBJECT_TYPE;

		// Note that the long type is not supported; see the javadoc for
		// the constructor for this class

		return JAVA_UNSUPPORTED_TYPE;
	}

	public static Class<?> getTagClass(int typeTag) {
		switch (typeTag) {
		case JAVA_STRING_TYPE:
			return ScriptRuntime.StringClass;
		case JAVA_INT_TYPE:
			return ScriptRuntime.StringClass;
		case JAVA_BOOLEAN_TYPE:
			return ScriptRuntime.StringClass;
		case JAVA_DOUBLE_TYPE:
			return ScriptRuntime.StringClass;
		case JAVA_DATE_TYPE:
			return ScriptRuntime.StringClass;
		case JAVA_SCRIPTABLE_TYPE:
			return ScriptRuntime.ScriptableClass;
		case JAVA_OBJECT_TYPE:
			return ScriptRuntime.ObjectClass;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public static Object convertArg(Context cx, Scriptable scope, Object arg,
			int typeTag) {
		switch (typeTag) {
		case JAVA_STRING_TYPE:
			if (arg instanceof String)
				return arg;
			return ScriptRuntime.toString(arg);
		case JAVA_INT_TYPE:
			if (arg instanceof Integer)
				return arg;
			return new Integer(ScriptRuntime.toInt32(arg));
		case JAVA_BOOLEAN_TYPE:
			if (arg instanceof Boolean)
				return arg;
			return ScriptRuntime.toBoolean(arg) ? Boolean.TRUE : Boolean.FALSE;
		case JAVA_DOUBLE_TYPE:
			if (arg instanceof Double)
				return arg;
			return new Double(ScriptRuntime.toNumber(arg));
		case JAVA_DATE_TYPE:
			if (arg == null) return null;
			if (arg.getClass().getName().equals("org.mozilla.javascript.NativeDate"))
				return convertToJavaDate(arg);
		case JAVA_SCRIPTABLE_TYPE:
			if (arg instanceof Scriptable)
				return arg;
			return ScriptRuntime.toObject(cx, scope, arg);
		case JAVA_OBJECT_TYPE: {
			if (arg == null) return null;
			if (arg.getClass().getName().equals("org.mozilla.javascript.NativeDate"))
				return convertToJavaDate(arg);	
			return convertToJavaObject(arg, cx, scope);
		}
		case JAVA_NULL_TYPE:
			return null;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public static Date convertToJavaDate(Object jsDate) {
// 
		//double v = ((NativeDate)(jsDate)).getJSTimeValue();
		
	//	return new Date(Math.round(v));
		return new java.util.Date((long) ScriptRuntime.toNumber(jsDate));
	}
	
	public static Object convertToJavaObject(Object obj, Context cx, Scriptable scope) {

		int tag = getTypeTag(obj.getClass());

		if (tag == JAVA_UNSUPPORTED_TYPE)
			throw new IllegalArgumentException("Unsupported");
		
		return convertArg(cx, scope, obj, tag);
		
	}
	
	
	
	
}
