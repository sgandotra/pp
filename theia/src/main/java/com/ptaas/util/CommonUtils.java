package com.ptaas.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public abstract class CommonUtils {
	/**
	 * A common method for all enums since they can't have another base class
	 * 
	 * @param <T>
	 *            Enum type
	 * @param c
	 *            enum type. All enums must be all caps.
	 * @param string
	 *            case insensitive
	 * @return corresponding enum, or null
	 */
	public static <T extends Enum<T>> T getEnumFromString(Class<T> c,
			String string) {
		if (c != null && string != null) {
			try {
				return Enum.valueOf(c, string.trim().toLowerCase());
			}
			catch (IllegalArgumentException ex) {
			}
		}
		return null;
	}

	public static <T extends Enum<T>> T getEnumFromString(Class<T> c,
			String string,String defaultString) {
		if (c != null && string != null) {
			try {
				return Enum.valueOf(c, string.trim());
			}
			catch (IllegalArgumentException ex) {
				return Enum.valueOf(c, defaultString);
			}
		}
		return null;
	}
	public static String getRandomString()
	{
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	  
	}
}
