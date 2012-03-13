package com.builditboys.robots.utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MiscUtilities {
	
	public static int booleanToInt (boolean val) {
		return val ? 1 : 0;
	}
	
	public static String shortObjectName (Object obj) {
		return obj.getClass().getSimpleName() + '@' + Integer.toHexString(obj.hashCode());
	}
	
	public static String bestObjectName (Object obj) {
		String name;
		
		name = directToStringObjectName(obj);
		if (name != null) {return name;}
		
		name = namedObjectObjectName(obj);
		if (name != null) {return name;}
		
		return shortObjectName(obj);
	}

	public static String namedObjectObjectName (Object obj) {
		Class<?> objClass = obj.getClass();
		Method method;
		try {
			 method = objClass.getMethod("getName", new Class<?>[] {});
			 if (method != null) {
				return objClass.getSimpleName() + ": \"" + (String) method.invoke(obj, new Object[] {}) + "\"";
			}
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return null;
	}

	public static String directToStringObjectName (Object obj) {
		Class<?> objClass = obj.getClass();
		Method method;
		try {
			 method = objClass.getDeclaredMethod("toString", new Class<?>[] {});
			 if (method != null) {
				return obj.toString();
			}
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		return null;
	}
	
	/*
		Class<?> objClass = obj.getClass();

	try {
		if (objClass.getMethod("getName", new Class<?>[] {}) != null) {
			return obj.toString();
		}
		else {
			return shortObjectName(obj);
		}
	} catch (SecurityException e) {
		return shortObjectName(obj);
	} catch (NoSuchMethodException e) {
		return shortObjectName(obj);
	}	
}

*/

		
}
