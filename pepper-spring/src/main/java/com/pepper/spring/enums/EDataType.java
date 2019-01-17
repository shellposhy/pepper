package com.pepper.spring.enums;

import java.util.Date;

/**
 * Data types that can be processed by search engines.
 * <p>
 * 
 * @author shellpo shih
 * @version 1.0
 */
public enum EDataType {
	STRING, INTEGER, LONG, FLOAT, DOUBLE, DATE, DATETIME, ENUM, OBJECT;

	public static Class<?> type(EDataType type) {
		Class<?> clazz = null;
		switch (type) {
		case STRING:
			clazz = String.class;
			break;
		case INTEGER:
			clazz = Integer.TYPE;
			break;
		case LONG:
			clazz = Long.TYPE;
			break;
		case FLOAT:
			clazz = Float.TYPE;
			break;
		case DOUBLE:
			clazz = Double.TYPE;
			break;
		case DATE:
			clazz = Date.class;
			break;
		case DATETIME:
			clazz = Date.class;
			break;
		case ENUM:
			clazz = Enum.class;
			break;
		case OBJECT:
			clazz = Object.class;
			break;
		default:
			break;
		}
		return clazz;
	}
}
