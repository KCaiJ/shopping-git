package org.user.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
/**
 *用户登录注册
 */
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shopping.common.Encrypt;
import org.shopping.common.Enumeration;
import org.shopping.pojo.TbSeller;
import org.shopping.pojo.TbUser;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.user.service.UserService;

import com.alibaba.dubbo.config.annotation.Reference;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/User")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

	@Reference
	private UserService Service;

	/**
	 * 增加
	 * 
	 * @param bean
	 * @return
	 */

	@RequestMapping("/add")
	public Result add(@RequestBody TbUser bean,String smscode) {
		boolean checkSmsCode = Service.checkSmsCode(bean.getPhone(), smscode);
		System.out.println(smscode+"   "+checkSmsCode);
		if(!checkSmsCode){
			return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.CODE_SEND_ERROR);
		}
		/**
		 * 检查用户名是否存在
		 */
		if (Service.check("username",bean.getUsername())) {
			return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.USER_USERNAME_YES);
		}
		try {
			bean.setCreated(new Date());//创建日期
			bean.setUpdated(new Date());//修改日期
			String password = Encrypt.md5AndSha(bean.getPassword());//对密码加密
			bean.setPassword(password);
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
	 * @param User
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser bean) {
		try {
			Service.update(bean);
			return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.UPDATA_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.UPDATA_FAIL);
		}
	}

	/**
	 * 根据id获取对象
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbUser findOne(Long id) {
		return Service.queryById(id);
	}

	/**
	 * 发送短信验证码
	 * @param phone
	 * @return
	 */
	@RequestMapping("/sendCode")
	public Result sendCode(String phone){
		//判断手机号格式
	/*	if(!PhoneFormatCheckUtils.isPhoneLegal(phone)){
			return new Result(false, "手机号格式不正确");
		}*/		
		try {
			Service.createSmsCode(phone);//生成验证码
			return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.CODE_SEND_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return	new Result(Enumeration.CODE_SUCCESS, true, Enumeration.CODE_SEND_FAIL);
		}		
	}
	/**
	 * 用户登录
	 * @param bean
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/login")
	public Result Login(@RequestBody TbUser bean, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		TbUser tbUser=Service.findOneByName(bean.getUsername());
		if (tbUser != null) {
			String password = Encrypt.md5AndSha(bean.getPassword());//对密码加密
			if (password.equals(tbUser.getPassword())) {
				Cookie cookie = new Cookie(Enumeration.CURRENT_USER,
						URLEncoder.encode(tbUser.getUsername(), "utf-8"));
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
	 * @param username
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/exit")
	public Result Exit(String username, HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {
		Cookie cookie = new Cookie(Enumeration.CURRENT_USER, URLEncoder.encode(username, "utf-8"));
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);
		return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.SUCCESS);
	}
	


}
