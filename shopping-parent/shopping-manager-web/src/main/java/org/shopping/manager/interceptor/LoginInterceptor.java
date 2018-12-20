package org.shopping.manager.interceptor;

import java.io.PrintWriter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.shopping.common.Enumeration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import entity.Result;
import net.sf.json.JSONObject;

public class LoginInterceptor implements HandlerInterceptor{

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// 执行完毕，返回前拦截
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// 在处理过程中，执行拦截	
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		// 在拦截点执行前拦截，如果返回true则不执行拦截点后的操作（拦截成功） 返回false则不执行拦截
		Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie c : cookies) {
                if (Enumeration.CURRENT_ADMIN.equals(c.getName())) {
                    //拿到cookie名为Constants.BUYCART_COOKIE中的value
               //     String value = c.getValue(); //获取用户名
                    break;
                }
            }
            return true;
        }else {
			response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            Result result=new Result(Enumeration.CODE_LOGIN_NO,false,Enumeration.LOGIN_NO);
            JSONObject jsonObject=JSONObject.fromObject(result);
            out.println(jsonObject.toString());
            out.flush();
            out.close();
			return false;
		}
	}

}
