package org.shopping.manager.controller;

/**
 * 运营商登录
 */
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.sellergoods.service.AdminUserService;
import org.shopping.common.Encrypt;
import org.shopping.common.Enumeration;
import org.shopping.pojo.TbAdminUser;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;

import entity.PageResult;
import entity.Password;
import entity.Result;

@RestController
@RequestMapping("/AdminUser")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminUserController {

	@Reference
	private AdminUserService Service;

	/**
	 * 增加
	 * 
	 * @param bean
	 * @return
	 */

	@RequestMapping("/add")
	public Result add(@RequestBody TbAdminUser bean) {
		try {
			bean.setPassword(Encrypt.md5AndSha(bean.getPassword()));
			Service.save(bean);
			return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.INSETR_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.INSETR_FAIL);
		}
	}
	/**
	 * 修改对象
	 * 
	 * @param AdminUser
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbAdminUser bean) {
		try {
			Service.update(bean);
			return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.UPDATA_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.UPDATA_FAIL);
		}
	}



	/**
	 * 运营商登录接口
	 * 
	 * @param bean
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/login")
	public Result sellerLogin(@RequestBody TbAdminUser bean, HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {
		TbAdminUser adminUser = Service.findOne(bean.getUsername());
		if (adminUser != null) {
			if (Encrypt.md5AndSha(bean.getPassword()).equals(adminUser.getPassword())) {
				// request.getSession().setAttribute("admin",
				// URLEncoder.encode(adminUser.getUsername(),"utf-8"));
				Cookie cookie = new Cookie(Enumeration.CURRENT_ADMIN,
						URLEncoder.encode(adminUser.getUsername(), "utf-8"));
				cookie.setMaxAge(3600 * 1);
				cookie.setPath("/");
				response.addCookie(cookie);
				return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.SUCCESS);
			}
			return new Result(Enumeration.CODE_LOGIN_NO, false, Enumeration.PASSWORD_NO);
		}
		return new Result(Enumeration.CODE_LOGIN_NO, false, Enumeration.USERNAME_NO);
	}

	/**
	 * 注销
	 * 
	 * @param name
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/exit")
	public Result exit(String name, HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {
		Cookie cookie = new Cookie(Enumeration.CURRENT_ADMIN, URLEncoder.encode(name, "utf-8"));
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);
		return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.SUCCESS);
	}
	
	/**
	 * 更改密码
	 * @param bean
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/changepasswd")
	public Result changepasswd(@RequestBody Password bean)throws UnsupportedEncodingException {
		TbAdminUser user=Service.findOne(bean.getName());
		if (user==null) {
			return new Result(Enumeration.CODE_LOGIN_NO, true, Enumeration.LOGIN_NO);
		}
		//校验原密码是否正确	
		if(user.getPassword().equals(Encrypt.md5AndSha(bean.getOldPassword()))){
			user.setPassword(Encrypt.md5AndSha(bean.getNewPassword()));
			return update(user);
		}
		return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.PASSWORD_ERROR);
	}

}
