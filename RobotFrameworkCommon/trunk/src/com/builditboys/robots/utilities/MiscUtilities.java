package com.builditboys.robots.utilities;

public class MiscUtilities {
	
	public static String objectShortName (Object obj) {
		return obj.getClass().getSimpleName() + '@' + Integer.toHexString(obj.hashCode());
	}

}
