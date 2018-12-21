package org.shopping.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookUtils {
	
	/**
	 * 根据name获取cookie值
	 * @param request
	 * @param name
	 * @return
	 */
	public static String getCookieName(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		for (Cookie c : cookies) {
			if (name.equals(c.getName())) {
				return c.getValue();
			}
		}
		return null;
	}

}
