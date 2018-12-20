package org.shopping.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookUtils {
	public static String  getCookieName(HttpServletRequest request,String name) {
		Cookie[] cookies = request.getCookies();
	    for (Cookie c : cookies) {
	        if (name.equals(c.getName())) {
	        	return c.getValue();
	        }
	    }
	    return null;
	}


}
