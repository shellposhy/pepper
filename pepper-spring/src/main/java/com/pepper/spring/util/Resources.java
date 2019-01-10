package com.pepper.spring.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static cn.com.lemon.base.Strings.isNullOrEmpty;

/**
 * Get properties file value
 * 
 * @author shishb
 * @version 1.0
 */
public class Resources {
	public ResourceBundle resourceBundle;

	public Resources(String name) {
		resourceBundle = ResourceBundle.getBundle(!isNullOrEmpty(name) ? name : "pepper");
	}

	public void close() {
		resourceBundle = null;
	}

	public String value(String key) {
		if (resourceBundle == null)
			return "";
		if (!resourceBundle.containsKey(key))
			return "";
		try {
			return resourceBundle.getString(key);
		} catch (MissingResourceException e) {
			e.printStackTrace();
			return "";
		}
	}
}
