package org.shopping.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * Cookie 工具类
 *
 */
public class CookUtils {
	
	/**
	 * 根据name获取cookie值
	 * @param request
	 * @param name
	 * @return
	 */
	public static String getCookieName(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies==null) {
			return null;
		}
		for (Cookie c : cookies) {
			if (name.equals(c.getName())) {
				return c.getValue();
			}
		}
		return null;
	}
}
