package org.shopping.seller.controller;
/**
 * 商家入驻
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.sellergoods.service.SellerService;
import org.shopping.common.Encrypt;
import org.shopping.common.Enumeration;
import org.shopping.pojo.TbSeller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Password;
import entity.Result;

@RestController
@RequestMapping("/Seller")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SellerController {

	@Reference
	private SellerService Service;

	/**
	 * 获取全部信息
	 * 
	 * @return
	 */

	@RequestMapping("/findAll")
	public List<TbSeller> findAll() {
		return Service.queryAll();
	}

	/**
	 * 返回分页列表
	 * 
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows) {
		return Service.queryPageListByWhere(new TbSeller(), page, rows);
	}

	/**
	 * 增加
	 * 
	 * @param bean
	 * @return
	 */

	@RequestMapping("/add")
	public Result add(@RequestBody TbSeller bean) {
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
	 * 批量删除
	 * 
	 * @param ids
	 */
	@RequestMapping("/delete")
	public Result delete(Long[] ids) {
		if (Service.delete(ids)) {
			return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.DELETE_SUCCESS);
		}
		return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.DELETE_FAIL);

	}

	/**
	 * 修改对象
	 * 
	 * @param Seller
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbSeller bean) {
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
	public TbSeller findOne(String id) {
		return Service.findOne(id);
	}

	/**
	 * 查询+分页
	 * 
	 * @param Seller
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(int page, int rows, @RequestBody TbSeller bean) {
		return Service.findPage(bean, page, rows);
	}

	/**
	 * 更改状态
	 * 
	 * @param sellerId
	 *            商家ID
	 * @param status
	 *            状态
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(String sellerId, String status) {
		try {
			Service.updateStatus(sellerId, status);
			return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.FAIL);
		}
	}

	/**
	 * 商家登录接口
	 * 
	 * @param bean
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/login")
	public Result sellerLogin(@RequestBody TbSeller bean, HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {
		TbSeller seller = Service.login(bean.getSellerId());
		if (seller != null) {
			if (Encrypt.md5AndSha(bean.getPassword()).equals(seller.getPassword())) {
				// 登录成功
				if (seller.getStatus().equals("0")) {
					return new Result(Enumeration.CODE_LOGIN_NO, false, Enumeration.SELLER_AUDIT);
				}
				if (seller.getStatus().equals("2")) {
					return new Result(Enumeration.CODE_LOGIN_NO, false, Enumeration.SELLER_AUDIT_FAILED);
				}
				if (seller.getStatus().equals("3")) {
					return new Result(Enumeration.CODE_LOGIN_NO, false, Enumeration.SELLER_COLSE);
				}
				Cookie cookie = new Cookie(Enumeration.CURRENT_SELLER,
						URLEncoder.encode(seller.getSellerId(), "utf-8"));
				cookie.setMaxAge(3600 * 1);
				cookie.setPath("/");
				response.addCookie(cookie);
				Cookie cookie2 = new Cookie(Enumeration.CURRENT_SELLER_NAME,
						URLEncoder.encode(seller.getName(), "utf-8"));
				cookie2.setMaxAge(3600 * 1);
				cookie2.setPath("/");
				response.addCookie(cookie2);
				return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.SUCCESS);
			}
			return new Result(Enumeration.CODE_LOGIN_NO, false, Enumeration.PASSWORD_NO);
		}
		return new Result(Enumeration.CODE_LOGIN_NO, false, Enumeration.USERNAME_NO);
	}

	/**
	 * 注销
	 * 
	 * @param sellerId
	 * @param name
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/exit")
	public Result Exit(String sellerId, String name, HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {
		Cookie cookie = new Cookie(Enumeration.CURRENT_SELLER, URLEncoder.encode(sellerId, "utf-8"));
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);
		Cookie cookie2 = new Cookie(Enumeration.CURRENT_SELLER_NAME, URLEncoder.encode(name, "utf-8"));
		cookie2.setMaxAge(0);
		cookie2.setPath("/");
		response.addCookie(cookie2);
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
		TbSeller user=findOne(bean.getName());
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
