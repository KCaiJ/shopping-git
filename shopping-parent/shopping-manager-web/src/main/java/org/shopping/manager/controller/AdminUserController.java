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
import entity.Result;

@RestController
@RequestMapping("/AdminUser")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminUserController {

	@Reference
	private AdminUserService Service;
	
	/**
	 * 获取全部信息
	 * @return
	 */
	 
	@RequestMapping("/findAll")
	public List<TbAdminUser> findAll(){			
		return Service.queryAll();
	}
	
	/**
	 * 返回分页列表
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){
		return Service.queryPageListByWhere(new TbAdminUser(),page, rows);
	}

	/**
	 * 增加
	 * @param bean
	 * @return
	 */
	
	@RequestMapping("/add")
	public Result  add(@RequestBody TbAdminUser bean){			
		try {
			bean.setPassword(Encrypt.md5AndSha(bean.getPassword()));
			Service.save(bean);
			return new Result(Enumeration.CODE_SUCCESS,true, Enumeration.INSETR_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Enumeration.CODE_SUCCESS,false, Enumeration.INSETR_FAIL);
		}
	}
	/**
	 * 批量删除
	 * @param ids
	 */
	@RequestMapping("/delete")
	public Result  delete(Long[] ids) {
		if (Service.delete(ids)) {
			return new Result(Enumeration.CODE_SUCCESS,true, Enumeration.DELETE_SUCCESS); 
		}
		return new Result(Enumeration.CODE_SUCCESS,false, Enumeration.DELETE_FAIL);

	}
	/**
	 * 修改对象
	 * @param AdminUser
	 * @return
	 */
	@RequestMapping("/update")
	public Result  update(@RequestBody TbAdminUser bean){			
		try {
			Service.update(bean);
			return new Result(Enumeration.CODE_SUCCESS,true, Enumeration.UPDATA_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Enumeration.CODE_SUCCESS,false, Enumeration.UPDATA_FAIL);
		}
	}
	
	/**
	 * 根据id获取对象
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbAdminUser findOne(String name){
		return Service.findOne(name);		
	}

	/**
	 * 查询+分页
	 * @param AdminUser
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(int page, int rows ,@RequestBody TbAdminUser bean ){
		return Service.findPage(bean, page, rows);		
	}
	
	/**
	 * 运营商登录接口
	 * @param bean
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/login")
	public Result sellerLogin(@RequestBody TbAdminUser bean,HttpServletRequest  request, HttpServletResponse response ) throws UnsupportedEncodingException{
		TbAdminUser adminUser=findOne(bean.getUsername());
		if (adminUser!=null) {
			if (Encrypt.md5AndSha(bean.getPassword()).equals(adminUser.getPassword())) {
			//	request.getSession().setAttribute("admin", URLEncoder.encode(adminUser.getUsername(),"utf-8"));
				Cookie cookie = new Cookie(Enumeration.CURRENT_ADMIN, URLEncoder.encode(adminUser.getUsername(),"utf-8"));  
				cookie.setMaxAge(3600 * 1);
				cookie.setPath("/");
				response.addCookie(cookie);
				return new Result(Enumeration.CODE_SUCCESS,true,Enumeration.SUCCESS);
			}
			return new Result(Enumeration.CODE_LOGIN_NO,false, Enumeration.PASSWORD_NO);
		}
		return new Result(Enumeration.CODE_LOGIN_NO,false, Enumeration.USERNAME_NO);
	}
	/**
	 * 注销
	 * @param name
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/exit")
	public Result Exit(String name,HttpServletRequest  request, HttpServletResponse response ) throws UnsupportedEncodingException{
		Cookie cookie = new Cookie(Enumeration.CURRENT_ADMIN, URLEncoder.encode(name,"utf-8"));  
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);
		return new Result(Enumeration.CODE_SUCCESS,true, Enumeration.SUCCESS);
	}
	
	
}
