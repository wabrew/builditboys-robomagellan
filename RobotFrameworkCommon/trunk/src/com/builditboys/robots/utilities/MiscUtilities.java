package com.builditboys.robots.utilities;

public class MiscUtilities {
	
	public static String shortObjectName (Object obj) {
		return obj.getClass().getSimpleName() + '@' + Integer.toHexString(obj.hashCode());
	}
	
	public static String bestObjectName (Object obj) {
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

}
